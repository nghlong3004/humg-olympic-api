package vn.edu.humg.olympic.api.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
        WHERE is_active = true
        ORDER BY updated DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
  List<Assignment> findAllPaging(int offset, int limit);

  @Select(
      """
        SELECT COUNT(*)
        FROM assignment
        WHERE is_active = true
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
        WHERE LOWER(title) LIKE LOWER(#{pattern}) AND is_active = true
        ORDER BY updated DESC
        LIMIT #{limit} OFFSET #{offset}
        """)
  List<Assignment> searchByTitlePaging(int offset, int limit, String pattern);

  @Select(
      """
        SELECT COUNT(*)
        FROM assignment
        WHERE LOWER(title) LIKE LOWER(#{pattern}) AND is_active = true
        """)
  long countByTitle(String pattern);

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
        WHERE id = #{id}
        """)
  Optional<Assignment> findById(Long id);

  @Insert(
      """
            UPDATE assignment
            SET
                title = #{title},
                owner_id = #{ownerId},
                description = #{description},
                subject_name = #{subjectName},
                start_time = #{startTime},
                end_time = #{endTime}
            WHERE id = #{id}
          """)
  void update(Assignment assignment);

  @Update(
      """
        DELETE FROM assignment
        WHERE id = #{id}
        """)
  void delete(Long id);
}
