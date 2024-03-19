package fr.uge.chargepointconfiguration.typeallowed;

import fr.uge.chargepointconfiguration.firmware.Firmware;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Set;

/**
 * TypeAllowed class represents a type allowed in the database via JPA.<br>
 * A TypeAllowed has an ID, a constructor, and a type.
 */
@Entity
@Table(name = "type_allowed")
public class TypeAllowed {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_type_allowed")
  private int id;

  @Column(name = "constructor", nullable = false, length = 45)
  private String constructor;

  @Column(name = "type", nullable = false, length = 45)
  private String type;

  /**
   * TypeAllowed's constructor.
   *
   * @param constructor A chargepoint's manufacturer where a firmware is working on.
   * @param type The commercial name of a chargepoint where a firmware is working on.
   */
  public TypeAllowed(String constructor,
                     String type) {
    this.constructor = Objects.requireNonNull(constructor);
    this.type = Objects.requireNonNull(type);
  }

  /**
   * Empty constructor. Should not be called.
   */
  public TypeAllowed() {

  }

  public String getConstructor() {
    return constructor;
  }

  public void setConstructor(String constructor) {
    this.constructor = constructor;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @ManyToMany(mappedBy = "typesAllowed")
  private Set<Firmware> firmwares;

  public TypeAllowedDto toDto() {
    return new TypeAllowedDto(
        id,
        constructor,
        type);
  }

  @Override
  public String toString() {
    return "TypeAllowed{"
           + "id=" + id
           + ", constructor='" + constructor + '\''
           + ", type='" + type + '\''
           + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TypeAllowed that = (TypeAllowed) o;
    return id == that.id
           && Objects.equals(constructor, that.constructor)
           && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, constructor, type);
  }
}
