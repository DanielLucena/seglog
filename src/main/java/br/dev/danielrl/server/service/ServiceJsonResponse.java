package br.dev.danielrl.server.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ServiceJsonResponse {

    private static final Gson GSON = new Gson();

    private ServiceJsonResponse() {
    }

    public static String success(String code, String message, JsonElement data) {
        JsonObject root = new JsonObject();
        root.addProperty("success", true);
        root.addProperty("code", code);
        root.addProperty("message", message);
        root.add("data", data == null ? new JsonObject() : data);
        return GSON.toJson(root);
    }

    public static String error(String code, String message) {
        JsonObject root = new JsonObject();
        root.addProperty("success", false);
        root.addProperty("code", code);
        root.addProperty("message", message);
        return GSON.toJson(root);
    }
}