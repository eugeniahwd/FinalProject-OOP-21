package com.finpro.backend.repository;

import com.finpro.backend.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {

    // Find by player
    @Query("SELECT gs FROM GameSession gs WHERE gs.player1Id = :playerId OR gs.player2Id = :playerId ORDER BY gs.createdAt DESC")
    List<GameSession> findByPlayerId(@Param("playerId") UUID playerId);

    // Find by both players
    @Query("SELECT gs FROM GameSession gs WHERE (gs.player1Id = :p1 AND gs.player2Id = :p2) OR (gs.player1Id = :p2 AND gs.player2Id = :p1) ORDER BY gs.createdAt DESC")
    List<GameSession> findByBothPlayers(@Param("p1") UUID p1, @Param("p2") UUID p2);

    // Find by level
    List<GameSession> findByLevelNumberOrderByTotalScoreDesc(Integer levelNumber);

    // Completed sessions
    List<GameSession> findByCompletedTrueOrderByTotalScoreDesc();

    // Recent sessions
    List<GameSession> findTop20ByOrderByCreatedAtDesc();

    // Leaderboard - best sessions
    @Query("SELECT gs FROM GameSession gs WHERE gs.completed = true ORDER BY gs.totalScore DESC, gs.timeSeconds ASC")
    List<GameSession> findTopSessions();

    // Count wins
    @Query("SELECT COUNT(gs) FROM GameSession gs WHERE gs.winnerId = :playerId AND gs.completed = true")
    Long countWinsByPlayerId(@Param("playerId") UUID playerId);

    // Average time per level
    @Query("SELECT AVG(gs.timeSeconds) FROM GameSession gs WHERE gs.levelNumber = :level AND gs.completed = true")
    Double getAverageTimeForLevel(@Param("level") Integer level);
}