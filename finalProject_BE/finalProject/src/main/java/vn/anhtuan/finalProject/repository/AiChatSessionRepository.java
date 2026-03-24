package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.AiChatSession;
import vn.anhtuan.finalProject.entity.User;

import java.util.Optional;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {
    Optional<AiChatSession> findTopByUserAndExerciseIdOrderByCreatedAtDesc(User user, Long exerciseId);
}
