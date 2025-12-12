// ===== GameSessionController.java =====
package com.finpro.backend.controller;

import com.finpro.backend.dto.GameFinishRequest;
import com.finpro.backend.dto.GameStartRequest;
import com.finpro.backend.model.GameSession;
import com.finpro.backend.model.Player;
import com.finpro.backend.service.GameSessionService;
import com.finpro.backend.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GameSessionController {

    @Autowired
    private GameSessionService sessionService;

    @Autowired
    private PlayerService playerService;

    // ENDPOINT UTAMA: Start Game - Auto create players dan session
    @PostMapping("/game/start")
    public ResponseEntity<?> startGame(@RequestBody GameStartRequest request) {
        try {
            // Get or create player 1
            Player player1 = playerService.getOrCreatePlayer(request.getPlayer1Username());

            // Get or create player 2
            Player player2 = playerService.getOrCreatePlayer(request.getPlayer2Username());

            // Create game session
            GameSession session = new GameSession(
                    player1.getPlayerId(),
                    player1.getUsername(),
                    player2.getPlayerId(),
                    player2.getUsername(),
                    request.getLevelNumber()
            );

            GameSession createdSession = sessionService.createSession(session);

            // Return response dengan session ID
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", createdSession.getSessionId());
            response.put("player1", player1);
            response.put("player2", player2);
            response.put("levelNumber", createdSession.getLevelNumber());
            response.put("message", "Game started successfully!");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ENDPOINT UTAMA: Finish Game
    @PutMapping("/sessions/{sessionId}/finish")
    public ResponseEntity<?> finishGame(@PathVariable UUID sessionId,
                                        @RequestBody GameFinishRequest request) {
        try {
            GameSession updateData = new GameSession();
            updateData.setPlayer1RedDiamonds(request.getPlayer1RedDiamonds());
            updateData.setPlayer1Score(request.getPlayer1Score());
            updateData.setPlayer2BlueDiamonds(request.getPlayer2BlueDiamonds());
            updateData.setPlayer2Score(request.getPlayer2Score());
            updateData.setKeysCollected(request.getKeysCollected());
            updateData.setTotalKeys(request.getTotalKeys());
            updateData.setTimeSeconds(request.getTimeSeconds());

            GameSession finishedSession = sessionService.finishSession(sessionId, updateData);

            return ResponseEntity.ok(finishedSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all sessions
    @GetMapping("/sessions")
    public ResponseEntity<List<GameSession>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    // Get session by ID
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<?> getSessionById(@PathVariable UUID sessionId) {
        Optional<GameSession> session = sessionService.getSessionById(sessionId);
        if (session.isPresent()) {
            return ResponseEntity.ok(session.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Session tidak ditemukan"));
    }

    // Get sessions by player
    @GetMapping("/sessions/player/{playerId}")
    public ResponseEntity<List<GameSession>> getSessionsByPlayer(@PathVariable UUID playerId) {
        return ResponseEntity.ok(sessionService.getSessionsByPlayerId(playerId));
    }

    // Get sessions by both players
    @GetMapping("/sessions/players/{player1Id}/{player2Id}")
    public ResponseEntity<List<GameSession>> getSessionsByBothPlayers(
            @PathVariable UUID player1Id, @PathVariable UUID player2Id) {
        return ResponseEntity.ok(sessionService.getSessionsByBothPlayers(player1Id, player2Id));
    }

    // Get sessions by level
    @GetMapping("/sessions/level/{level}")
    public ResponseEntity<List<GameSession>> getSessionsByLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(sessionService.getSessionsByLevel(level));
    }

    // Leaderboard
    @GetMapping("/sessions/leaderboard")
    public ResponseEntity<List<GameSession>> getLeaderboard() {
        return ResponseEntity.ok(sessionService.getLeaderboard());
    }

    // Recent sessions
    @GetMapping("/sessions/recent")
    public ResponseEntity<List<GameSession>> getRecentSessions() {
        return ResponseEntity.ok(sessionService.getRecentSessions());
    }

    // Get player wins
    @GetMapping("/sessions/player/{playerId}/wins")
    public ResponseEntity<?> getPlayerWins(@PathVariable UUID playerId) {
        Long wins = sessionService.getWinCount(playerId);
        return ResponseEntity.ok(Map.of("wins", wins));
    }

    // Average time for level
    @GetMapping("/sessions/level/{level}/average-time")
    public ResponseEntity<?> getAverageTime(@PathVariable Integer level) {
        Double avgTime = sessionService.getAverageTimeForLevel(level);
        return ResponseEntity.ok(Map.of(
                "level", level,
                "averageTimeSeconds", avgTime != null ? avgTime : 0
        ));
    }

    // Delete session
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable UUID sessionId) {
        try {
            sessionService.deleteSession(sessionId);
            return ResponseEntity.ok(Map.of("message", "Session berhasil dihapus"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}