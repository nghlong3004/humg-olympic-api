package vn.edu.humg.olympic.api.repository;

import java.util.List;
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
        SELECT
            id,
            title,
            description,
            subject_name,
            owner_id,
            start_time,
            end_time,
            is_active,
            created,
            updated
        FROM assignment
        ORDER BY updated DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
  List<Assignment> findAllPaging(int offset, int limit);

  @Select(
      """
        SELECT COUNT(*)
        FROM assignment
        """)
  long countAll();

  @Select(
      """
        SELECT
            id,
            title,
            description,
            subject_name,
            owner_id,
            start_time,
            end_time,
            is_active,
            created,
            updated
        FROM assignment
        WHERE LOWER(title) LIKE CONCAT('%', LOWER(#{keyword}), '%')
        ORDER BY updated DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
  List<Assignment> searchByTitlePaging(int offset, int limit, String keyword);

  @Select(
      """
        SELECT COUNT(*)
        FROM assignment
        WHERE LOWER(title) LIKE CONCAT('%', LOWER(#{keyword}), '%')
        """)
  long countByTitle(String keyword);
}
