package fr.uge.chargepointconfiguration.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * User class represents a User in the database via JPA.<br>
 * A user has an ID, an email, a lastname, a firstname, a password and a role.
 */
@Entity
@Table(name = "app_user")
@SQLDelete(sql = """
      update app_user 
      set is_deleted = true
      where id = ?
      """)
@SQLRestriction("not is_deleted")
public class User {

  /**
   * Role enum represents different roles that a User can have in the application.<br>
   * The role can be :<br>
   * - VISUALIZER ;<br>
   * - EDITOR :<br>
   * - ADMINISTRATOR.
   */
  public enum Role {
    VISUALIZER, EDITOR, ADMINISTRATOR
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "email", nullable = false, unique = true, length = 45)
  private String email;

  @Column(name = "lastname", nullable = false, length = 45)
  private String lastName;

  @Column(name = "firstname", nullable = false, length = 45)
  private String firstName;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
  private boolean isDeleted = false;

  /**
   * User's constructor.
   *
   * @param firstName String.
   * @param lastName String.
   * @param email String.
   * @param password String.
   * @param role Role.
   */
  public User(String firstName,
              String lastName,
              String email,
              String password,
              Role role) {
    Objects.requireNonNull(firstName);
    Objects.requireNonNull(lastName);
    Objects.requireNonNull(email);
    Objects.requireNonNull(password);
    Objects.requireNonNull(role);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  /**
   * No args User's constructor.<br>
   * It should not be called.
   */
  public User() {
  }

  /**
   * Returns the user's ID.
   *
   * @return id, int.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the user's id.
   *
   * @param id An int.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the user's email.
   *
   * @return email, String.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the user's email.
   *
   * @param email A String.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the user's lastname.
   *
   * @return lastName, String.
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the user's lastname.
   *
   * @param lastName a String.
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Returns the user's firstname.
   *
   * @return firstname, String.
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the user's firstName.
   *
   * @param firstName String.
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Returns the user's password.
   *
   * @return password, String.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the user's password.
   *
   * @param password String.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Returns the user's role.<br>
   *
   * @return role, Role.
   */
  public Role getRole() {
    return role;
  }

  /**
   * Sets the user's role.
   *
   * @param role Role.
   */
  public void setRole(Role role) {
    this.role = role;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return id == user.id && Objects.equals(email, user.email) && isDeleted == user.isDeleted;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, lastName, firstName, password, role, isDeleted);
  }

  public UserDto toDto() {
    return new UserDto(
        id,
        firstName,
        lastName,
        email,
        role);
  }

  @Override
  public String toString() {
    return "User{"
            + "id=" + id
            + ", email='" + email + '\''
            + ", lastName='" + lastName + '\''
            + ", firstName='" + firstName + '\''
            + ", role=" + role
            + ", isDeleted=" + isDeleted
            + '}';
  }
}