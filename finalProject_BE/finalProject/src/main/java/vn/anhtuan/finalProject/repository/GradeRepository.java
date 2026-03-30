package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
