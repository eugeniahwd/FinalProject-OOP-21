package com.finpro.backend.service;

import com.finpro.backend.model.Player;
import com.finpro.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public Player createPlayer(Player player) {
        if (playerRepository.existsByUsername(player.getUsername())) {
            throw new RuntimeException("Username sudah digunakan: " + player.getUsername());
        }
        return playerRepository.save(player);
    }

    public Optional<Player> getPlayerById(UUID playerId) {
        return playerRepository.findById(playerId);
    }

    public Optional<Player> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public boolean isUsernameExists(String username) {
        return playerRepository.existsByUsername(username);
    }

    @Transactional
    public Player getOrCreatePlayer(String username) {
        Optional<Player> existing = playerRepository.findByUsername(username);
        if (existing.isPresent()) {
            return existing.get();
        }
        Player newPlayer = new Player(username);
        return playerRepository.save(newPlayer);
    }

    @Transactional
    public Player updatePlayer(UUID playerId, Player updatedPlayer) {
        Player existingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player tidak ditemukan: " + playerId));

        if (updatedPlayer.getUsername() != null &&
                !existingPlayer.getUsername().equals(updatedPlayer.getUsername())) {
            if (playerRepository.existsByUsername(updatedPlayer.getUsername())) {
                throw new RuntimeException("Username sudah digunakan: " + updatedPlayer.getUsername());
            }
            existingPlayer.setUsername(updatedPlayer.getUsername());
        }

        if (updatedPlayer.getHighScore() != null) {
            existingPlayer.setHighScore(updatedPlayer.getHighScore());
        }

        return playerRepository.save(existingPlayer);
    }

    @Transactional
    public void deletePlayer(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new RuntimeException("Player tidak ditemukan: " + playerId);
        }
        playerRepository.deleteById(playerId);
    }

    // Leaderboards
    public List<Player> getLeaderboardByScore() {
        return playerRepository.findTop10ByOrderByHighScoreDesc();
    }

    // DIHAPUS: getLeaderboardByWins() - tidak relevan untuk teamwork game

    public List<Player> getLeaderboardByDiamonds() {
        return playerRepository.findTop10ByTotalDiamonds();
    }

    public List<Player> getLeaderboardByProgress() {
        return playerRepository.findTop10ByProgress();
    }

    // Update stats after game - TEAMWORK version
    @Transactional
    public void updatePlayerStatsAfterGame(UUID playerId, Integer score, Integer redDiamonds,
                                           Integer blueDiamonds, Integer level) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player tidak ditemukan: " + playerId));

        player.updateHighScore(score);

        if (redDiamonds != null && redDiamonds > 0) {
            player.addRedDiamonds(redDiamonds);
        }

        if (blueDiamonds != null && blueDiamonds > 0) {
            player.addBlueDiamonds(blueDiamonds);
        }

        player.incrementGamesPlayed();

        // DIHAPUS: tidak ada update wins karena ini teamwork

        if (level != null) {
            player.updateLevelsCompleted(level);
        }

        player.updateLastPlayed();

        playerRepository.save(player);
    }
}