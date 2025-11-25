package vn.edu.humg.olympic.api.model;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class AuthenticatedUser implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public String getAuthority() {
    return authorities.stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);
  }

  public boolean isAdmin() {
    return this.getAuthority().equals(Role.ADMIN.getAuthority());
  }

  public boolean isTeacher() {
    return this.getAuthority().equals(Role.TEACHER.getAuthority());
  }

  public boolean isStudent() {
    return this.getAuthority().equals(Role.STUDENT.getAuthority());
  }

  public boolean isOwner(Long id) {
    return this.getId().equals(id);
  }
}
