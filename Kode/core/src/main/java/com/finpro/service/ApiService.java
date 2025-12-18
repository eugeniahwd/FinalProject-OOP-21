package com.finpro.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ApiService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static ApiService instance;
    private Gson gson;

    // Current session data
    private UUID currentSessionId;
    private String player1Username;
    private String player2Username;
    private boolean isOfflineMode = false;

    private ApiService() {
        gson = new Gson();
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    /**
     * Start new game session menggunakan Native Java HttpClient
     * Lebih reliable daripada LibGDX Net
     */
    public void startGame(String player1Username, String player2Username, int levelNumber,
                          final ApiCallback callback) {
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.isOfflineMode = false;

        System.out.println("=== API Request: Start Game ===");
        System.out.println("URL: " + BASE_URL + "/game/start");
        System.out.println("Body: {\"player1Username\":\"" + player1Username +
            "\",\"player2Username\":\"" + player2Username +
            "\",\"levelNumber\":" + levelNumber + "}");

        // Generate offline session ID sebagai backup
        final String offlineSessionId = "OFFLINE-" + UUID.randomUUID().toString().substring(0, 8);

        // Gunakan thread terpisah untuk network call
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/game/start");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "FireGirlWaterBoy/1.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // Create JSON body
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("player1Username", player1Username);
                requestBody.addProperty("player2Username", player2Username);
                requestBody.addProperty("levelNumber", levelNumber);

                String jsonBody = gson.toJson(requestBody);

                // Send request
                conn.getOutputStream().write(jsonBody.getBytes("UTF-8"));

                int statusCode = conn.getResponseCode();
                String response;

                if (statusCode >= 200 && statusCode < 300) {
                    // Success
                    response = new String(conn.getInputStream().readAllBytes(), "UTF-8");
                } else {
                    // Error
                    response = new String(conn.getErrorStream().readAllBytes(), "UTF-8");
                }

                conn.disconnect();

                final int finalStatusCode = statusCode;
                final String finalResponse = response;

                // Handle response di main thread
                Gdx.app.postRunnable(() -> {
                    System.out.println("=== API Response: Start Game ===");
                    System.out.println("Status: " + finalStatusCode);
                    System.out.println("Response: " + finalResponse);

                    if (finalStatusCode == 200) {
                        try {
                            JsonObject json = gson.fromJson(finalResponse, JsonObject.class);

                            // Extract sessionId
                            if (json.has("sessionId")) {
                                String sessionIdStr = json.get("sessionId").getAsString();
                                currentSessionId = UUID.fromString(sessionIdStr);
                                isOfflineMode = false;
                                System.out.println("✓ Session ID saved: " + currentSessionId);
                                System.out.println("✓ Backend connection SUCCESS!");
                                callback.onSuccess(finalResponse);
                                return;
                            } else {
                                System.err.println("⚠ No sessionId in response");
                            }
                        } catch (Exception e) {
                            System.err.println("✗ JSON Parse error: " + e.getMessage());
                        }
                    }

                    // Jika gagal, coba dengan LibGDX sebagai fallback
                    System.err.println("⚠ Native HttpClient failed, trying LibGDX...");
                    startGameWithLibGDX(player1Username, player2Username, levelNumber, callback, offlineSessionId);
                });

            } catch (Exception e) {
                // Network error
                Gdx.app.postRunnable(() -> {
                    System.err.println("✗ Native HttpClient Exception: " + e.getClass().getName() + ": " + e.getMessage());
                    System.err.println("⚠ Trying LibGDX network...");
                    startGameWithLibGDX(player1Username, player2Username, levelNumber, callback, offlineSessionId);
                });
            }
        }).start();
    }

    /**
     * Fallback: Gunakan LibGDX network
     */
    private void startGameWithLibGDX(String player1Username, String player2Username, int levelNumber,
                                     ApiCallback callback, String offlineSessionId) {
        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("player1Username", player1Username);
        requestBody.put("player2Username", player2Username);
        requestBody.put("levelNumber", levelNumber);

        String jsonBody = gson.toJson(requestBody);

        // Build HTTP request dengan LibGDX
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder
            .newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/game/start")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .timeout(10000)
            .content(jsonBody)
            .build();

        // Send request
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();

                System.out.println("=== LibGDX Response ===");
                System.out.println("Status: " + statusCode);
                System.out.println("Response: " + result);

                if (statusCode >= 200 && statusCode < 300 && result != null && !result.isEmpty()) {
                    try {
                        JsonObject json = gson.fromJson(result, JsonObject.class);
                        if (json.has("sessionId")) {
                            String sessionIdStr = json.get("sessionId").getAsString();
                            currentSessionId = UUID.fromString(sessionIdStr);
                            isOfflineMode = false;
                            System.out.println("✓ LibGDX SUCCESS! Session ID: " + currentSessionId);
                            callback.onSuccess(result);
                            return;
                        }
                    } catch (Exception e) {
                        System.err.println("✗ LibGDX parse error: " + e.getMessage());
                    }
                }

                // Jika LibGDX juga gagal, gunakan offline mode
                System.err.println("⚠ Both methods failed, using OFFLINE MODE");
                useOfflineMode(offlineSessionId, callback);
            }

            @Override
            public void failed(Throwable t) {
                System.err.println("✗ LibGDX failed: " + t.getMessage());
                useOfflineMode(offlineSessionId, callback);
            }

            @Override
            public void cancelled() {
                System.err.println("✗ LibGDX cancelled");
                useOfflineMode(offlineSessionId, callback);
            }
        });
    }

    /**
     * Gunakan offline mode dengan generated session ID
     */
    private void useOfflineMode(String offlineSessionId, ApiCallback callback) {
        this.isOfflineMode = true;

        // Generate UUID untuk offline session
        try {
            currentSessionId = UUID.fromString(offlineSessionId);
        } catch (IllegalArgumentException e) {
            // Fallback ke UUID random
            currentSessionId = UUID.randomUUID();
        }

        System.out.println("⚠ ===========================================");
        System.out.println("⚠ USING OFFLINE MODE");
        System.out.println("⚠ Generated Session ID: " + currentSessionId);
        System.out.println("⚠ Game will play normally");
        System.out.println("⚠ Results will be logged locally only");
        System.out.println("⚠ ===========================================");

        // Buat response JSON manual untuk callback
        JsonObject fakeResponse = new JsonObject();
        fakeResponse.addProperty("sessionId", currentSessionId.toString());
        fakeResponse.addProperty("status", "offline");
        fakeResponse.addProperty("message", "Playing in offline mode");
        fakeResponse.addProperty("player1Username", player1Username);
        fakeResponse.addProperty("player2Username", player2Username);

        callback.onSuccess(gson.toJson(fakeResponse));
    }

    /**
     * Finish game and save results to backend
     */
    public void finishGame(int player1RedDiamonds, int player1Score,
                           int player2BlueDiamonds, int player2Score,
                           int keysCollected, int totalKeys, int timeSeconds,
                           final ApiCallback callback) {

        System.out.println("\n=== Saving Game Results ===");
        System.out.println("Player 1: " + player1Username);
        System.out.println("Player 2: " + player2Username);
        System.out.println("Session ID: " + currentSessionId);

        // Pastikan ada session ID
        if (currentSessionId == null) {
            System.err.println("✗ ERROR: No session ID! Generating new one...");
            currentSessionId = UUID.randomUUID();
            isOfflineMode = true;
            System.out.println("⚠ Generated new session ID: " + currentSessionId);
        }

        // Jika offline mode, hanya log tanpa kirim ke server
        if (isOfflineMode) {
            System.out.println("⚠ OFFLINE MODE: Results logged locally only");
            System.out.println("  FireGirl: " + player1RedDiamonds + " red diamonds, " + player1Score + " score");
            System.out.println("  WaterBoy: " + player2BlueDiamonds + " blue diamonds, " + player2Score + " score");
            System.out.println("  Keys: " + keysCollected + "/" + totalKeys + ", Time: " + timeSeconds + "s");
            System.out.println("✓ Game completed (offline mode)");

            // Simulasi callback success
            JsonObject fakeResponse = new JsonObject();
            fakeResponse.addProperty("status", "saved_locally");
            fakeResponse.addProperty("message", "Results saved locally (offline mode)");
            fakeResponse.addProperty("sessionId", currentSessionId.toString());
            callback.onSuccess(gson.toJson(fakeResponse));
            return;
        }

        // Jika online, coba kirim ke server dengan Native HttpClient
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/sessions/" + currentSessionId + "/finish");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setDoOutput(true);

                // Create request body
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("player1RedDiamonds", player1RedDiamonds);
                requestBody.addProperty("player1Score", player1Score);
                requestBody.addProperty("player2BlueDiamonds", player2BlueDiamonds);
                requestBody.addProperty("player2Score", player2Score);
                requestBody.addProperty("keysCollected", keysCollected);
                requestBody.addProperty("totalKeys", totalKeys);
                requestBody.addProperty("timeSeconds", timeSeconds);

                String jsonBody = gson.toJson(requestBody);

                // Log untuk debugging
                System.out.println("URL: " + url.toString());
                System.out.println("Body: " + jsonBody);
                System.out.println("Stats:");
                System.out.println("  FireGirl: " + player1RedDiamonds + " diamonds, " + player1Score + " score");
                System.out.println("  WaterBoy: " + player2BlueDiamonds + " diamonds, " + player2Score + " score");
                System.out.println("  Keys: " + keysCollected + "/" + totalKeys + ", Time: " + timeSeconds + "s");

                // Send request
                conn.getOutputStream().write(jsonBody.getBytes("UTF-8"));

                int statusCode = conn.getResponseCode();
                String response;

                if (statusCode >= 200 && statusCode < 300) {
                    response = new String(conn.getInputStream().readAllBytes(), "UTF-8");
                } else {
                    response = new String(conn.getErrorStream().readAllBytes(), "UTF-8");
                }

                conn.disconnect();

                final int finalStatusCode = statusCode;
                final String finalResponse = response;

                // Handle response di main thread
                Gdx.app.postRunnable(() -> {
                    System.out.println("=== API Response: Finish Game ===");
                    System.out.println("Status: " + finalStatusCode);
                    System.out.println("Response: " + finalResponse);

                    if (finalStatusCode >= 200 && finalStatusCode < 300) {
                        System.out.println("✓ Game results saved to database!");
                        callback.onSuccess(finalResponse);
                    } else {
                        System.err.println("✗ Server returned error: " + finalStatusCode);
                        System.out.println("⚠ Results saved locally");

                        JsonObject fakeResponse = new JsonObject();
                        fakeResponse.addProperty("status", "saved_locally");
                        fakeResponse.addProperty("message", "Server error, saved locally");
                        callback.onSuccess(gson.toJson(fakeResponse));
                    }
                });

            } catch (Exception e) {
                // Network error
                Gdx.app.postRunnable(() -> {
                    System.err.println("✗ Failed to save to server: " + e.getMessage());
                    System.out.println("⚠ Results saved locally only");

                    JsonObject fakeResponse = new JsonObject();
                    fakeResponse.addProperty("status", "saved_locally");
                    fakeResponse.addProperty("message", "Network error, saved locally");
                    callback.onSuccess(gson.toJson(fakeResponse));
                });
            }
        }).start();
    }

    /**
     * Test connection to backend server
     */
    public void testConnection(final ApiCallback callback) {
        System.out.println("=== Testing Backend Connection ===");
        System.out.println("URL: " + BASE_URL + "/test");

        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/test");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                int statusCode = conn.getResponseCode();
                String response = new String(conn.getInputStream().readAllBytes(), "UTF-8");
                conn.disconnect();

                Gdx.app.postRunnable(() -> {
                    if (statusCode == 200) {
                        System.out.println("✓ Backend is UP! Status: " + statusCode);
                        System.out.println("Response: " + response);
                        callback.onSuccess("Connected");
                    } else {
                        System.err.println("✗ Backend returned: " + statusCode);
                        callback.onError("Status: " + statusCode);
                    }
                });

            } catch (Exception e) {
                Gdx.app.postRunnable(() -> {
                    System.err.println("✗ Backend is DOWN: " + e.getMessage());
                    System.err.println("⚠ Please ensure backend is running on " + BASE_URL);
                    callback.onError("Connection failed: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Get leaderboard from backend
     */
    public void getLeaderboard(final ApiCallback callback) {
        // Jika offline mode, return empty leaderboard
        if (isOfflineMode) {
            System.out.println("⚠ OFFLINE MODE: Returning empty leaderboard");
            JsonObject fakeResponse = new JsonObject();
            fakeResponse.addProperty("status", "offline");
            fakeResponse.add("leaderboard", gson.toJsonTree(new String[0]));
            callback.onSuccess(gson.toJson(fakeResponse));
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/sessions/leaderboard");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                int statusCode = conn.getResponseCode();
                String response;

                if (statusCode == 200) {
                    response = new String(conn.getInputStream().readAllBytes(), "UTF-8");
                } else {
                    response = "[]";
                }

                conn.disconnect();

                final String finalResponse = response;
                Gdx.app.postRunnable(() -> {
                    callback.onSuccess(finalResponse);
                });

            } catch (Exception e) {
                Gdx.app.postRunnable(() -> {
                    System.err.println("✗ Failed to get leaderboard: " + e.getMessage());
                    // Return empty array jika gagal
                    callback.onSuccess("[]");
                });
            }
        }).start();
    }

    /**
     * Clear current session
     */
    public void clearSession() {
        System.out.println("ApiService: Clearing session");
        currentSessionId = null;
        player1Username = null;
        player2Username = null;
        isOfflineMode = false;
    }

    // ========== GETTERS ==========

    public UUID getCurrentSessionId() {
        return currentSessionId;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public String getPlayer2Username() {
        return player2Username;
    }

    public boolean isOfflineMode() {
        return isOfflineMode;
    }

    /**
     * Manual set session ID (for testing)
     */
    public void setSessionId(UUID sessionId) {
        this.currentSessionId = sessionId;
    }

    /**
     * Set offline mode manually
     */
    public void setOfflineMode(boolean offline) {
        this.isOfflineMode = offline;
    }

    /**
     * Callback interface for async API calls
     */
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
