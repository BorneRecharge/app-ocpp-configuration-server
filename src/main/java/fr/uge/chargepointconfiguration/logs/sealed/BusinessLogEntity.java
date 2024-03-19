package fr.uge.chargepointconfiguration.logs.sealed;

import fr.uge.chargepointconfiguration.chargepoint.Chargepoint;
import fr.uge.chargepointconfiguration.logs.business.BusinessLogDto;
import fr.uge.chargepointconfiguration.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.hibernate.annotations.CreationTimestamp;

/**
 * BusinessLog class represents a Business log in the database via JPA.<br>
 * A business log has an id, a date, an user,
 * a charge point, a firmware version and the complete log.
 */
@Entity
@Table(name = "business_logs")
public final class BusinessLogEntity implements LogEntity {

  /**
   * Category attach to this log.<br>
   * - LOGIN : log in and out of chargepoints.<br>
   * - STATUS : any creation/modification/delete of the status of a chargepoint.<br>
   * - FIRM : any creation/modification/delete of a firmware.<br>
   * - CONFIG : any creation/modification/delete of a configuration.<br>
   */
  public enum Category {
    LOGIN, STATUS, FIRM, CONFIG
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "date", nullable = false,
          columnDefinition = "datetime default current_timestamp")
  @CreationTimestamp
  private LocalDateTime date;

  /*
   * The quote for user ("user") are here to specify the database H2 that
   * user isn't the key word user, but a field user in the database.
   */
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_id", referencedColumnName = "id",
      columnDefinition = "int default NULL")
  private User user = null;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "chargepoint_id", referencedColumnName = "id_chargepoint",
      columnDefinition = "int default NULL")
  private Chargepoint chargepoint = null;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private Category category;

  @Column(name = "level", nullable = false)
  private String level;

  @Column(name = "complete_log", nullable = false)
  private String completeLog;

  /**
   * BusinessLog's constructor.
   *
   * @param user User logged currently implied with this log, null if not.
   * @param chargepoint Chargepoint implied with this log, null if not.
   * @param category {@link TechnicalLogEntity.Component}
   * @param level String version of {@link Level}.
   * @param completeLog All the log in a String.
   */
  public BusinessLogEntity(User user,
                           Chargepoint chargepoint,
                           Category category,
                           String level,
                           String completeLog) {
    this.user = user;
    this.chargepoint = chargepoint;
    this.category = Objects.requireNonNull(category);
    this.level = Objects.requireNonNull(level);
    this.completeLog = Objects.requireNonNull(completeLog);
    date = LocalDateTime.now();
  }

  /**
   * BusinessLog's constructor.
   *
   * @param category {@link TechnicalLogEntity.Component}
   * @param level String version of {@link Level}.
   * @param completeLog All the log in a String.
   */
  public BusinessLogEntity(Category category, String level, String completeLog) {
    this.category = Objects.requireNonNull(category);
    this.level = Objects.requireNonNull(level);
    this.completeLog = Objects.requireNonNull(completeLog);
    date = LocalDateTime.now();
  }

  /**
   * Empty constructor. Should not be called.
   */
  public BusinessLogEntity() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Chargepoint getChargepoint() {
    return chargepoint;
  }

  public void setChargepoint(Chargepoint chargepoint) {
    this.chargepoint = chargepoint;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getCompleteLog() {
    return completeLog;
  }

  public void setCompleteLog(String completeLog) {
    this.completeLog = completeLog;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BusinessLogEntity that)) {
      return false;
    }
    return getId() == that.getId()
           && getChargepoint() == that.getChargepoint()
           && Objects.equals(getDate(), that.getDate())
           && Objects.equals(getUser(), that.getUser())
           && getCategory() == that.getCategory()
           && Objects.equals(getLevel(), that.getLevel())
           && Objects.equals(getCompleteLog(), that.getCompleteLog());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(),
            getDate(),
            getUser(),
            getChargepoint(),
            getCategory(),
            getLevel(),
            getCompleteLog());
  }

  @SuppressWarnings({"checkstyle:MissingJavadocMethod"})
  public BusinessLogDto toDto() {
    return new BusinessLogDto(this.id,
        Timestamp.valueOf(date),
        user != null ? this.user.toDto() : null,
        chargepoint != null ? this.chargepoint.toDto() : null,
        this.category,
        this.level,
        this.completeLog);
  }

  @Override
  public String text() {
    return date + " "
           + "{" + category + "} "
           + "{" + level + "} "
           + "(" + id + ") "
           + "user " + (user == null ? "null" : user.getId()) + " "
           + "chargepoint " + (chargepoint == null ? "null" : chargepoint.getId()) + " "
           + completeLog;
  }

  @Override
  public String toString() {
    return "BusinessLog{"
           + "id=" + id
           + ", date=" + date
           + ", user='" + user + '\''
           + ", chargePoint='" + chargepoint + '\''
           + ", category='" + category + '\''
           + ", level='" + level + '\''
           + ", completeLog='" + completeLog + '\''
           + '}';
  }
}
