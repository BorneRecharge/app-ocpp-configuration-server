package fr.uge.chargepointconfiguration.chargepoint;

import fr.uge.chargepointconfiguration.configuration.Configuration;
import fr.uge.chargepointconfiguration.firmware.Firmware;
import fr.uge.chargepointconfiguration.status.StatusDto;
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
import org.hibernate.annotations.CreationTimestamp;

/**
 * Chargepoint class represents a charge point in the database via JPA.<br>
 * A Chargepoint has an ID, a serial_number_chargepoint, a type, a constructor, a client_id,
 * a server_address, a configuration, a last_edit and a {@link Firmware}.
 */
@Entity
@Table(name = "chargepoint")
public class Chargepoint {

  /**
   * The mods which a machine can be.<br>
   * FIRMWARE : the chargepoint will update his firmware thanks to a given url.<br>
   * CONFIGURATION : the chargepoint will change values in his configuration.
   */
  public enum Step {
    FIRMWARE,
    CONFIGURATION
  }

  /**
   * The status of the process.<br>
   * PENDING : the process is not launched.<br>
   * PROCESSING : the process has been accepted by the chargepoint and is now in process.<br>
   * FINISHED : the process has been done successfully.<br>
   * FAILED : the process has failed because of a wrong configuration or a bug.
   */
  public enum StatusProcess {
    PENDING,
    PROCESSING,
    FINISHED,
    FAILED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_chargepoint")
  private int id;

  @Column(name = "serial_number_chargepoint", nullable = false, length = 45)
  private String serialNumberChargepoint;

  @Column(name = "type", nullable = false, length = 45)
  private String type;

  @Column(name = "constructor", nullable = false, length = 45)
  private String constructor;

  @Column(name = "client_id", nullable = false, length = 45)
  private String clientId;

  @Column(
      name = "last_update",
      nullable = false,
      columnDefinition = "datetime default current_timestamp")
  @CreationTimestamp
  private LocalDateTime lastUpdate = LocalDateTime.now();

  @Column(name = "error", nullable = false, length = 65_535)
  private String error = "";

  @Column(name = "state", nullable = false, columnDefinition = "boolean default false")
  private boolean state = false;

  @Column(name = "step", nullable = false, columnDefinition = "varchar(32) default 'FIRMWARE'")
  @Enumerated(EnumType.STRING)
  private Step step = Step.FIRMWARE;

  @Column(
      name = "step_status",
      nullable = false,
      columnDefinition = "varchar(32) default 'PENDING'")
  @Enumerated(EnumType.STRING)
  private StatusProcess status = StatusProcess.PENDING;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id_configuration", referencedColumnName = "id_configuration")
  private Configuration configuration;

  /**
   * {@link Chargepoint}'s constructor.
   *
   * @param serialNumberChargepoint The chargepoint's serial number.
   * @param type                    The chargepoint's model (commercial name).
   * @param constructor             The chargepoint's constructor.
   * @param clientId                The client's name.
   * @param configuration           {@link Configuration}.
   * @param lastUpdate              The last time when the chargepoint has been updated.
   * @param error                   The error message in case of a failure.
   * @param state                   If the chargepoint is disconnected (false) or connected (true).
   * @param step                    {@link Step}.
   * @param status           {@link StatusProcess}.
   */
  public Chargepoint(
      String serialNumberChargepoint,
      String type,
      String constructor,
      String clientId,
      Configuration configuration,
      LocalDateTime lastUpdate,
      String error,
      boolean state,
      Step step,
      StatusProcess status) {
    this.serialNumberChargepoint = Objects.requireNonNull(serialNumberChargepoint);
    this.type = Objects.requireNonNull(type);
    this.constructor = Objects.requireNonNull(constructor);
    this.clientId = Objects.requireNonNull(clientId);
    this.lastUpdate = Objects.requireNonNull(lastUpdate);
    this.error = Objects.requireNonNull(error);
    this.state = state;
    this.step = Objects.requireNonNull(step);
    this.status = Objects.requireNonNull(status);
    this.configuration = Objects.requireNonNull(configuration);
  }

  /**
   * {@link Chargepoint}'s shorter constructor.<br>
   * The default parameters are not required here.
   *
   * @param serialNumberChargepoint The chargepoint's serial number.
   * @param type                    The chargepoint's model (commercial name).
   * @param constructor             The chargepoint's constructor.
   * @param clientId                The client's name.
   * @param configuration           {@link Configuration}.
   */
  public Chargepoint(
      String serialNumberChargepoint,
      String type,
      String constructor,
      String clientId,
      Configuration configuration) {
    this.serialNumberChargepoint = Objects.requireNonNull(serialNumberChargepoint);
    this.type = Objects.requireNonNull(type);
    this.constructor = Objects.requireNonNull(constructor);
    this.clientId = Objects.requireNonNull(clientId);
    this.error = Objects.requireNonNull(error);
    this.configuration = configuration;
  }

  /**
   * Empty constructor. Should not be called.
   */
  public Chargepoint() {}

  public int getId() {
    return id;
  }

  public String getSerialNumberChargepoint() {
    return serialNumberChargepoint;
  }

  public void setSerialNumberChargepoint(String serialNumberChargepoint) {
    this.serialNumberChargepoint = serialNumberChargepoint;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getConstructor() {
    return constructor;
  }

  public void setConstructor(String constructor) {
    this.constructor = constructor;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    updateLastUpdate();
    this.error = error;
  }

  public LocalDateTime getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(LocalDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public boolean isState() {
    return state;
  }

  public void setState(boolean state) {
    updateLastUpdate();
    this.state = state;
  }

  public Step getStep() {
    return step;
  }

  public void setStep(Step step) {
    updateLastUpdate();
    this.step = step;
  }

  public StatusProcess getStatus() {
    return status;
  }

  public void setStatus(StatusProcess statusProcess) {
    updateLastUpdate();
    this.status = statusProcess;
  }

  private void updateLastUpdate() {
    lastUpdate = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Chargepoint that = (Chargepoint) o;
    return id == that.id
        && state == that.state
        && Objects.equals(serialNumberChargepoint, that.serialNumberChargepoint)
        && Objects.equals(type, that.type)
        && Objects.equals(constructor, that.constructor)
        && Objects.equals(clientId, that.clientId)
        && Objects.equals(lastUpdate, that.lastUpdate)
        && Objects.equals(error, that.error)
        && step == that.step
        && status == that.status
        && Objects.equals(configuration, that.configuration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        serialNumberChargepoint,
        type,
        constructor,
        clientId,
        lastUpdate,
        error,
        state,
        step,
        status,
        configuration);
  }

  @Override
  public String toString() {
    return "Chargepoint{"
        + "id=" + id
        + ", serialNumberChargepoint='" + serialNumberChargepoint + '\''
        + ", type='" + type + '\''
        + ", constructor='" + constructor + '\''
        + ", clientId='" + clientId + '\''
        + ", lastUpdate=" + lastUpdate
        + ", error='" + error + '\''
        + ", state=" + state
        + ", step=" + step
        + ", status=" + status
        + ", configuration=" + configuration
        + '}';
  }

  public ChargepointDto toDto() {
    var statusDto = new StatusDto(Timestamp.valueOf(lastUpdate), error, state, step, status);
    return new ChargepointDto(
        id,
        serialNumberChargepoint,
        type,
        constructor,
        clientId,
        configuration != null ? configuration.toDto() : null,
        statusDto);
  }
}
