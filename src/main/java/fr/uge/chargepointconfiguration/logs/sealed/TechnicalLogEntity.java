package fr.uge.chargepointconfiguration.logs.sealed;

import fr.uge.chargepointconfiguration.logs.technical.TechnicalLogDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Technical class represents a technical log in the database via JPA.<br>
 * A technical log has an id, a date, a component, a criticality and the complete log.
 */
@Entity
@Table(name = "technical_logs")
public final class TechnicalLogEntity implements LogEntity {

  /**
   * Component enum represents different components where a log can be created.<br>
   * The component can be :<br>
   * - BACKEND ;<br>
   * - FRONTEND :<br>
   * - WEBSOCKET :<br>
   * - DATABASE.
   */
  public enum Component {
    BACKEND,
    FRONTEND,
    WEBSOCKET,
    DATABASE
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "date", nullable = false, columnDefinition = "datetime default current_timestamp")
  @CreationTimestamp
  private LocalDateTime date;

  @Enumerated(EnumType.STRING)
  @Column(name = "component", nullable = false)
  private Component component;

  @Column(name = "level", nullable = false)
  private String level;

  @Column(name = "complete_log", nullable = false)
  private String completeLog;

  /**
   * TechnicalLog's constructor.
   *
   * @param component {@link Component}
   * @param level String version of {@link Level}
   * @param completeLog All the log in a String.
   */
  public TechnicalLogEntity(Component component, String level, String completeLog) {
    this.component = Objects.requireNonNull(component);
    this.level = Objects.requireNonNull(level);
    this.completeLog = Objects.requireNonNull(completeLog);
    date = LocalDateTime.now();
  }

  /**
   * Empty constructor. Should not be called.
   */
  public TechnicalLogEntity() {}

  /**
   * Get the id of the log.
   *
   * @return id, int.
   */
  public int getId() {
    return id;
  }

  /**
   * Set the id of the log.
   *
   * @param id an int.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Get the date of the log.
   *
   * @return date, Timestamp.
   */
  public LocalDateTime getDate() {
    return date;
  }

  /**
   * Set the date of the log.
   *
   * @param date a Timestamp.
   */
  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  /**
   * Get the component of the log.
   *
   * @return component, String.
   */
  public Component getComponent() {
    return component;
  }

  /**
   * Set the component of the log.
   *
   * @param component a String.
   */
  public void setComponent(Component component) {
    this.component = component;
  }

  /**
   * Get the level of the log.
   *
   * @return {@link Level}
   */
  public String getLevel() {
    return level;
  }

  /**
   * Set the level of the log.
   *
   * @param level a {@link Level}.
   */
  public void setLevel(String level) {
    this.level = level;
  }

  /**
   * Get the complete log.
   *
   * @return completeLog, String.
   */
  public String getCompleteLog() {
    return completeLog;
  }

  /**
   * Set the complete log.
   *
   * @param completeLog a String.
   */
  public void setCompleteLog(String completeLog) {
    this.completeLog = completeLog;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TechnicalLogEntity that)) {
      return false;
    }
    return getId() == that.getId()
        && Objects.equals(getDate(), that.getDate())
        && getComponent() == that.getComponent()
        && getLevel().equals(that.getLevel())
        && Objects.equals(getCompleteLog(), that.getCompleteLog());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getDate(), getComponent(), getLevel(), getCompleteLog());
  }

  @Override
  public String text() {
    return date + " " + "{" + component + "} " + "{" + level + "} " + "(" + id + ") " + completeLog;
  }

  @Override
  public String toString() {
    return "TechnicalLog{"
        + "id=" + id
        + ", date=" + date
        + ", component=" + component
        + ", level=" + level
        + ", completeLog='" + completeLog + '\''
        + '}';
  }

  public TechnicalLogDto toDto() {
    return new TechnicalLogDto(id, Timestamp.valueOf(date), component, level, completeLog);
  }
}
