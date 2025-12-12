package com.finpro.backend.service;

import com.finpro.backend.model.GameSession;
import com.finpro.backend.repository.GameSessionRepository;
import com.finpro.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameSessionService {

    @Autowired
    private GameSessionRepository sessionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @Transactional
    public GameSession createSession(GameSession session) {
        // Validasi players exist
        if (!playerRepository.existsById(session.getPlayer1Id())) {
            throw new RuntimeException("Player 1 tidak ditemukan: " + session.getPlayer1Id());
        }
        if (!playerRepository.existsById(session.getPlayer2Id())) {
            throw new RuntimeException("Player 2 tidak ditemukan: " + session.getPlayer2Id());
        }

        return sessionRepository.save(session);
    }

    @Transactional
    public GameSession finishSession(UUID sessionId, GameSession updatedSession) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session tidak ditemukan: " + sessionId));

        // Update game data
        session.setPlayer1RedDiamonds(updatedSession.getPlayer1RedDiamonds());
        session.setPlayer1Score(updatedSession.getPlayer1Score());
        session.setPlayer2BlueDiamonds(updatedSession.getPlayer2BlueDiamonds());
        session.setPlayer2Score(updatedSession.getPlayer2Score());
        session.setKeysCollected(updatedSession.getKeysCollected());
        session.setTotalKeys(updatedSession.getTotalKeys());
        session.setTimeSeconds(updatedSession.getTimeSeconds());

        // Finish game
        session.finishGame();
        GameSession savedSession = sessionRepository.save(session);

        // Update player stats
        boolean player1Wins = session.getWinnerId() != null &&
                session.getWinnerId().equals(session.getPlayer1Id());
        boolean player2Wins = session.getWinnerId() != null &&
                session.getWinnerId().equals(session.getPlayer2Id());

        playerService.updatePlayerStatsAfterGame(
                session.getPlayer1Id(),
                session.getPlayer1Score(),
                session.getPlayer1RedDiamonds(),
                0,
                player1Wins,
                session.getLevelNumber()
        );

        playerService.updatePlayerStatsAfterGame(
                session.getPlayer2Id(),
                session.getPlayer2Score(),
                0,
                session.getPlayer2BlueDiamonds(),
                player2Wins,
                session.getLevelNumber()
        );

        return savedSession;
    }

    public Optional<GameSession> getSessionById(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }

    public List<GameSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public List<GameSession> getSessionsByPlayerId(UUID playerId) {
        return sessionRepository.findByPlayerId(playerId);
    }

    public List<GameSession> getSessionsByBothPlayers(UUID p1, UUID p2) {
        return sessionRepository.findByBothPlayers(p1, p2);
    }

    public List<GameSession> getSessionsByLevel(Integer level) {
        return sessionRepository.findByLevelNumberOrderByTotalScoreDesc(level);
    }

    public List<GameSession> getLeaderboard() {
        return sessionRepository.findTopSessions();
    }

    public List<GameSession> getRecentSessions() {
        return sessionRepository.findTop20ByOrderByCreatedAtDesc();
    }

    public Long getWinCount(UUID playerId) {
        return sessionRepository.countWinsByPlayerId(playerId);
    }

    public Double getAverageTimeForLevel(Integer level) {
        return sessionRepository.getAverageTimeForLevel(level);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new RuntimeException("Session tidak ditemukan: " + sessionId);
        }
        sessionRepository.deleteById(sessionId);
    }
}