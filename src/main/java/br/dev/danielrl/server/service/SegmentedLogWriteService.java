package br.dev.danielrl.server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SegmentedLogWriteService {

    private final Gson gson = new Gson();
    private final String writerId;

    public SegmentedLogWriteService(String writerId) {
        this.writerId = writerId;
        ensureBaseDirs();
    }

    public synchronized String registerEvent(String rawPayload) {
        try {
            JsonObject event = parsePayload(rawPayload);
            long eventTimestamp = event.has("timestamp") && event.get("timestamp").isJsonPrimitive()
                    ? event.get("timestamp").getAsLong()
                    : System.currentTimeMillis();

            event.addProperty("timestamp", eventTimestamp);
            if (!event.has("eventId")) {
                event.addProperty("eventId", UUID.randomUUID().toString());
            }
            if (!event.has("sourceWriterId")) {
                event.addProperty("sourceWriterId", writerId);
            }

            long segmentStart = SegmentedLogConstants.alignWindowStart(eventTimestamp);
            long segmentEnd = SegmentedLogConstants.windowEnd(segmentStart);
            Path segmentPath = buildSegmentPath(writerId, segmentStart, segmentEnd);
            Files.createDirectories(segmentPath.getParent());

            Files.writeString(
                    segmentPath,
                    gson.toJson(event) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            JsonObject data = new JsonObject();
            data.addProperty("writerId", writerId);
            data.addProperty("segmentFile", segmentPath.toString());
            data.addProperty("timestamp", eventTimestamp);

            return ServiceJsonResponse.success("WRITE_OK", "Event persisted in active segment.", data);
        } catch (Exception e) {
            return ServiceJsonResponse.error("WRITE_ERROR", "Could not persist event: " + e.getMessage());
        }
    }

    private JsonObject parsePayload(String rawPayload) {
        try {
            return JsonParser.parseString(rawPayload).getAsJsonObject();
        } catch (Exception ignored) {
            JsonObject fallback = new JsonObject();
            fallback.addProperty("rawPayload", rawPayload);
            return fallback;
        }
    }

    private void ensureBaseDirs() {
        try {
            Files.createDirectories(Path.of(SegmentedLogConstants.SEGMENTS_DIR));
            Files.createDirectories(Path.of(SegmentedLogConstants.CONSOLIDATED_DIR));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot initialize segmented log directories", e);
        }
    }

    static Path buildSegmentPath(String writerId, long windowStart, long windowEnd) {
        String fileName = "segment_" + windowStart + "_" + windowEnd + ".logseg";
        return Path.of(SegmentedLogConstants.SEGMENTS_DIR, writerId, fileName);
    }
}