package com.finpro.backend.controller;

import com.finpro.backend.model.Player;
import com.finpro.backend.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ResponseEntity<?> createPlayer(@RequestBody Player player) {
        try {
            Player created = playerService.createPlayer(player);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayerById(@PathVariable UUID playerId) {
        Optional<Player> player = playerService.getPlayerById(playerId);
        if (player.isPresent()) {
            return ResponseEntity.ok(player.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Player tidak ditemukan"));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getPlayerByUsername(@PathVariable String username) {
        Optional<Player> player = playerService.getPlayerByUsername(username);
        if (player.isPresent()) {
            return ResponseEntity.ok(player.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Player tidak ditemukan"));
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        boolean exists = playerService.isUsernameExists(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<?> updatePlayer(@PathVariable UUID playerId,
                                          @RequestBody Player player) {
        try {
            Player updated = playerService.updatePlayer(playerId, player);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{playerId}")
    public ResponseEntity<?> deletePlayer(@PathVariable UUID playerId) {
        try {
            playerService.deletePlayer(playerId);
            return ResponseEntity.ok(Map.of("message", "Player berhasil dihapus"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Leaderboards - TEAMWORK based
    @GetMapping("/leaderboard/score")
    public ResponseEntity<List<Player>> getLeaderboardByScore() {
        return ResponseEntity.ok(playerService.getLeaderboardByScore());
    }

    // DIHAPUS: getLeaderboardByWins() - tidak relevan untuk teamwork game

    @GetMapping("/leaderboard/diamonds")
    public ResponseEntity<List<Player>> getLeaderboardByDiamonds() {
        return ResponseEntity.ok(playerService.getLeaderboardByDiamonds());
    }

    @GetMapping("/leaderboard/progress")
    public ResponseEntity<List<Player>> getLeaderboardByProgress() {
        return ResponseEntity.ok(playerService.getLeaderboardByProgress());
    }
}