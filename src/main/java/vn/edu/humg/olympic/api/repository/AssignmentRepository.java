package vn.edu.humg.olympic.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.humg.olympic.api.model.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

  Page<Assignment> findByIsActiveTrue(Pageable pageable);

  Page<Assignment> findByIsActiveTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
}
