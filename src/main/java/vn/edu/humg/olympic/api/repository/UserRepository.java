package vn.edu.humg.olympic.api.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import vn.edu.humg.olympic.api.model.User;

@Mapper
public interface UserRepository {
  @Insert(
      """
            INSERT INTO user_humg (
                        first_name, last_name, email, password_hash,
                        gender, birthday, role,
                        phone, university_name, faculty_name, avatar_url,
                        is_active, created, updated
                    ) VALUES (
                        #{firstName},
                        #{lastName},
                        #{email},
                        #{passwordHash},
                        #{gender}::gender,
                        #{birthday},
                        #{role}::role,
                        #{phone},
                        #{universityName},
                        #{facultyName},
                        #{avatarUrl},
                        #{isActive},
                        NOW(),
                        NOW()
                    )
            """)
  void save(User user);

  @Select(
      """
                SELECT *
                FROM user_humg
                WHERE email = #{email}
                LIMIT 1
            """)
  Optional<User> findByEmail(String email);

  @Select(
      """
                SELECT *
                FROM user_humg
                WHERE id = #{id}
                LIMIT 1
            """)
  Optional<User> findById(Long id);

  @Update(
      """
           UPDATE user_humg
           SET
               first_name = #{firstName},
               last_name = #{lastName},
               gender = #{gender}::gender,
               birthday = #{birthday},
               role = #{role}::role,
               phone = #{phone},
               university_name = #{universityName},
               faculty_name = #{facultyName},
               avatar_url = #{avatarUrl},
               is_active = #{isActive},
               updated = NOW()
           WHERE id = #{id}
           """)
  void update(User user);

  @Select(
      """
          SELECT *
          FROM user_humg
          WHERE ((LOWER(first_name) LIKE LOWER(#{keyword})) OR (LOWER(last_name) LIKE LOWER(#{keyword}))) AND
                role::text LIKE #{role} AND is_active = true
          ORDER BY created DESC
          LIMIT #{limit} OFFSET #{offset}
      """)
  List<User> search(int offset, int limit, String keyword, String role);

  @Select(
      """
              SELECT COUNT(id)
              FROM user_humg
              WHERE ((LOWER(first_name) LIKE LOWER(#{keyword})) OR (LOWER(last_name) LIKE LOWER(#{keyword}))) AND
                role::text LIKE #{role} AND is_active = true
          """)
  int countByKeywordAndRole(String keyword, String role);
}
