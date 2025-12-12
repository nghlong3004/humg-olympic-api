package vn.edu.humg.olympic.api.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.humg.olympic.api.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query(
      """
      SELECT user
      FROM User user
      WHERE (LOWER(user.firstName) LIKE LOWER(:keyword)
          OR LOWER(user.lastName)  LIKE LOWER(:keyword))
        AND CAST(user.role AS string) = :role
        AND user.isActive = true
      """)
  Page<User> search(@Param("keyword") String keyword, @Param("role") String role, Pageable pageable);
}
