package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.finalProject.entity.Quiz;
import vn.anhtuan.finalProject.entity.QuizType;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    boolean existsByCode(String code);
}