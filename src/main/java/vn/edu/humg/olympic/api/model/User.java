package vn.edu.humg.olympic.api.model;

import java.sql.Date;
import java.sql.Timestamp;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private Gender gender;
    private Date birthday;
    private Role role;
    private String phone;
    private String universityName;
    private String facultyName;
    private String avatarUrl;
    private Boolean isActive;
    private Timestamp created;
    private Timestamp updated;
}
