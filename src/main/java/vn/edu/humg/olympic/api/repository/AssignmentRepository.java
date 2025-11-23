package vn.edu.humg.olympic.api.repository;

import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import vn.edu.humg.olympic.api.model.Assignment;

@Mapper
public interface AssignmentRepository {

  @Insert(
      """
        INSERT INTO assignment
            (title, owner_id, description, subject_name, start_time, end_time)
        VALUES
            (#{title},
             #{ownerId},
             #{description},
             #{subjectName},
             #{startTime},
             #{endTime})
      """)
  void save(Assignment assignment);

  @Select(
"""
        SELECT id, title, description, subject_name AS subjectName, owner_id AS ownerId,
             start_time AS startTime, end_time AS endTime, is_active AS isActive, created, updated
        FROM assignment
        WHERE title = #{title}
        LIMIT 1
""")
  Optional<Assignment> findByTitle(String title);
}
