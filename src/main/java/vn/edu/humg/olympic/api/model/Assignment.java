package vn.edu.humg.olympic.api.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "assignment")
public class Assignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 55)
  private String title;

  private String description;

  @Column(name = "subject_name")
  private String subjectName;

  @Column(name = "owner_id", nullable = false)
  private Long ownerId;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "end_time")
  private Timestamp endTime;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(updatable = false)
  private Timestamp created;

  private Timestamp updated;

  @PrePersist
  protected void onCreate() {
    Timestamp now = new Timestamp(System.currentTimeMillis());
    this.created = now;
    this.updated = now;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updated = new Timestamp(System.currentTimeMillis());
  }
}
