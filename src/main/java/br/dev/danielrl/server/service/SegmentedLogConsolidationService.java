package br.dev.danielrl.server.service;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SegmentedLogConsolidationService {

    private final Gson gson = new Gson();

    public synchronized String consolidate(long startTimestamp, long endTimestamp) {
        if (startTimestamp <= 0 || endTimestamp <= 0 || endTimestamp < startTimestamp) {
            return ServiceJsonResponse.error("INVALID_WINDOW", "Invalid consolidation window.");
        }

        Path consolidatedPath = buildConsolidatedPath(startTimestamp, endTimestamp);
        if (Files.exists(consolidatedPath)) {
            return ServiceJsonResponse.error("ALREADY_CONSOLIDATED", "A consolidated file already exists for this window.");
        }

        List<JsonObject> collected = new ArrayList<>();
        long closedThreshold = System.currentTimeMillis() - SegmentedLogConstants.SEGMENT_ROTATION_MS;

        try (Stream<Path> paths = Files.walk(Path.of(SegmentedLogConstants.SEGMENTS_DIR), 3)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".logseg"))
                    .forEach(path -> collectSegment(path, startTimestamp, endTimestamp, closedThreshold, collected));
        } catch (IOException e) {
            return ServiceJsonResponse.error("CONSOLIDATION_SCAN_ERROR", "Could not scan segment files: " + e.getMessage());
        }

        collected.sort(Comparator.comparingLong(this::extractTimestamp));

        JsonArray consolidated = new JsonArray();
        for (JsonObject event : collected) {
            consolidated.add(event);
        }

        try {
            Files.createDirectories(consolidatedPath.getParent());
            Files.writeString(consolidatedPath, gson.toJson(consolidated), StandardOpenOption.CREATE_NEW);
        } catch (FileAlreadyExistsException e) {
            return ServiceJsonResponse.error("ALREADY_CONSOLIDATED", "A consolidated file already exists for this window.");
        } catch (IOException e) {
            return ServiceJsonResponse.error("CONSOLIDATION_WRITE_ERROR",
                    "Could not create consolidated file: " + e.getMessage());
        }

        JsonObject data = new JsonObject();
        data.addProperty("consolidatedFile", consolidatedPath.toString());
        data.addProperty("eventCount", collected.size());
        data.addProperty("startTimestamp", startTimestamp);
        data.addProperty("endTimestamp", endTimestamp);

        return ServiceJsonResponse.success("CONSOLIDATION_OK", "Consolidated file created.", data);
    }

    private void collectSegment(Path segmentPath, long requestedStart, long requestedEnd, long closedThreshold,
            List<JsonObject> output) {
        long[] window = parseWindowFromSegmentFileName(segmentPath.getFileName().toString());
        if (window == null) {
            return;
        }

        long segmentStart = window[0];
        long segmentEnd = window[1];

        if (segmentStart < requestedStart || segmentEnd > requestedEnd) {
            return;
        }

        if (segmentEnd > closedThreshold) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(segmentPath);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                JsonObject obj = JsonParser.parseString(line).getAsJsonObject();
                long ts = extractTimestamp(obj);
                if (ts >= requestedStart && ts <= requestedEnd) {
                    output.add(obj);
                }
            }
        } catch (Exception ignored) {
            // Skips malformed files without failing the whole consolidation.
        }
    }

    private long[] parseWindowFromSegmentFileName(String fileName) {
        try {
            if (!fileName.startsWith("segment_") || !fileName.endsWith(".logseg")) {
                return null;
            }
            String noPrefix = fileName.substring("segment_".length(), fileName.length() - ".logseg".length());
            String[] parts = noPrefix.split("_");
            if (parts.length != 2) {
                return null;
            }
            return new long[] { Long.parseLong(parts[0]), Long.parseLong(parts[1]) };
        } catch (Exception ignored) {
            return null;
        }
    }

    private long extractTimestamp(JsonObject event) {
        if (event != null && event.has("timestamp")) {
            try {
                return event.get("timestamp").getAsLong();
            } catch (Exception ignored) {
                return 0L;
            }
        }
        return 0L;
    }

    private long extractTimestamp(JsonElement event) {
        return event != null && event.isJsonObject() ? extractTimestamp(event.getAsJsonObject()) : 0L;
    }

    public static Path buildConsolidatedPath(long startTimestamp, long endTimestamp) {
        String fileName = "consolidated_" + startTimestamp + "_" + endTimestamp + ".json";
        return Path.of(SegmentedLogConstants.CONSOLIDATED_DIR, fileName);
    }
}