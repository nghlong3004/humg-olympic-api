package vn.edu.humg.olympic.api.model;

import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Assignment {
  private Long id;
  private String title;
  private String description;
  private String subjectName;
  private Long ownerId;
  private Timestamp startTime;
  private Timestamp endTime;
  private Boolean isActive;
  private Timestamp created;
  private Timestamp updated;
}
