package br.dev.danielrl.server.service;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.dev.danielrl.domain.LogQuery;

public class SegmentedLogReadService {

    private final Gson gson = new Gson();

    public String read(LogQuery query) {
        if (query == null) {
            return ServiceJsonResponse.error("INVALID_QUERY", "Query payload is required.");
        }

        if (query.getEndTimestamp() > System.currentTimeMillis() - SegmentedLogConstants.SEGMENT_ROTATION_MS) {
            return ServiceJsonResponse.error("QUERY_NOT_AVAILABLE",
                    "The requested window is still being written. Try again after the current 10s segment closes.");
        }

        Path consolidatedPath = SegmentedLogConsolidationService.buildConsolidatedPath(
                query.getStartTimestamp(),
                query.getEndTimestamp());

        if (!Files.exists(consolidatedPath)) {
            return ServiceJsonResponse.error("NOT_CONSOLIDATED", "No consolidated file exists for this query window.");
        }

        try {
            String content = Files.readString(consolidatedPath);
            JsonArray array = JsonParser.parseString(content).getAsJsonArray();
            JsonArray filtered = new JsonArray();

            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject event = element.getAsJsonObject();
                if (matches(event, query)) {
                    filtered.add(event);
                }
            }

            JsonObject data = new JsonObject();
            data.addProperty("consolidatedFile", consolidatedPath.toString());
            data.addProperty("resultCount", filtered.size());
            data.add("events", filtered);
            return ServiceJsonResponse.success("READ_OK", "Consolidated events loaded.", data);
        } catch (Exception e) {
            return ServiceJsonResponse.error("READ_ERROR", "Could not read consolidated file: " + e.getMessage());
        }
    }

    private boolean matches(JsonObject event, LogQuery query) {
        long timestamp = readLong(event, "timestamp", -1L);
        if (timestamp < query.getStartTimestamp() || timestamp > query.getEndTimestamp()) {
            return false;
        }

        if (query.getEventType() != null) {
            String eventType = readString(event, "type");
            if (eventType == null || !query.getEventType().name().equalsIgnoreCase(eventType)) {
                return false;
            }
        }

        if (query.getPlayerId() != null && !query.getPlayerId().isBlank()) {
            String playerId = readString(event, "playerId");
            String killerId = readString(event, "killerPlayerId");
            String victimId = readString(event, "victimPlayerId");
            boolean found = query.getPlayerId().equals(playerId)
                    || query.getPlayerId().equals(killerId)
                    || query.getPlayerId().equals(victimId);
            if (!found) {
                return false;
            }
        }

        if (query.getLobbyId() != null && !query.getLobbyId().isBlank()) {
            String lobbyId = readString(event, "lobbyId");
            if (!query.getLobbyId().equals(lobbyId)) {
                return false;
            }
        }

        return true;
    }

    private long readLong(JsonObject obj, String key, long fallback) {
        try {
            return obj.has(key) ? obj.get(key).getAsLong() : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private String readString(JsonObject obj, String key) {
        try {
            return obj.has(key) ? obj.get(key).getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}