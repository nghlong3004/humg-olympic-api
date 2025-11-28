package vn.edu.humg.olympic.api.model;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_humg")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String email;

  @Column(name = "password_hash")
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "gender")
  private Gender gender;

  private Date birthday;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "role")
  private Role role;

  private String phone;
  private String universityName;
  private String facultyName;
  private String avatarUrl;
  private Boolean isActive;
  private Timestamp created;
  private Timestamp updated;
}
