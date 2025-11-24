package vn.edu.humg.olympic.api.model;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public String getRole() {
    return authorities.stream().findFirst().map(GrantedAuthority::getAuthority).orElse(null);
  }
}
