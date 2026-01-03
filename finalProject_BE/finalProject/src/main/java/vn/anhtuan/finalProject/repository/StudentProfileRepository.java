package vn.anhtuan.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.finalProject.entity.StudentProfile;
import vn.anhtuan.finalProject.entity.User;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    // Tìm profile dựa theo User
    Optional<StudentProfile> findByUser(User user);
}