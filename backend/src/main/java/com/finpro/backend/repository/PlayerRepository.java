package com.finpro.backend.repository;

import com.finpro.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByUsername(String username);
    boolean existsByUsername(String username);

    List<Player> findTop10ByOrderByHighScoreDesc();


    @Query("SELECT p FROM Player p ORDER BY (p.totalRedDiamonds + p.totalBlueDiamonds) DESC")
    List<Player> findTop10ByTotalDiamonds();

    @Query("SELECT p FROM Player p ORDER BY p.levelsCompleted DESC, p.highScore DESC")
    List<Player> findTop10ByProgress();
}