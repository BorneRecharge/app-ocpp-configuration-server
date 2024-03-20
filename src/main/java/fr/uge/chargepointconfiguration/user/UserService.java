package fr.uge.chargepointconfiguration.user;

import fr.uge.chargepointconfiguration.errors.exceptions.BadRequestException;
import fr.uge.chargepointconfiguration.errors.exceptions.EntityAlreadyExistingException;
import fr.uge.chargepointconfiguration.errors.exceptions.EntityNotFoundException;
import fr.uge.chargepointconfiguration.errors.exceptions.IllegalOperationException;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLog;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import fr.uge.chargepointconfiguration.shared.SearchUtils;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * A UserService doing database manipulations.
 */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CustomLogger logger;

  /**
   * Constructor for {@link UserService}.
   *
   * @param userRepository {@link UserRepository}
   * @param passwordEncoder {@link PasswordEncoder}
   * @param logger {@link CustomLogger}
   */
  @Autowired
  public UserService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, CustomLogger logger) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.logger = logger;
  }

  /**
   * Update the password of the user id.
   *
   * @param changePasswordUserDto of the user.
   * @return a User.
   */
  public User updatePassword(int id, ChangePasswordUserDto changePasswordUserDto) {
    var user = getById(id);

    if (user.getId() != getAuthenticatedUser().getId()) {
      throw new IllegalOperationException(
          "Impossible de changer le mot de passe d'un autre " + "utilisateur.");
    }

    if (!passwordEncoder.matches(changePasswordUserDto.oldPassword(), user.getPassword())) {
      throw new BadRequestException("Impossible de changer le mot de passe.");
    }

    if (!validatePassword(changePasswordUserDto.newPassword())) {
      throw new BadRequestException("Le nouveau mot de passe ne respecte pas les contraintes");
    }

    user.setPassword(passwordEncoder.encode(changePasswordUserDto.newPassword()));

    var result = userRepository.save(user);
    logger.info(new TechnicalLog(
        TechnicalLogEntity.Component.BACKEND, "Password updated for user : " + user.getId()));

    return result;
  }

  private boolean validatePassword(String s) {
    var regex =
        "^(?=.*\\d)(?=.*[!@#$%^&*~\"'{(-|`_\\\\)\\]}+°£µ§/:;.,?<>])(?=.*[a-z])(?=.*[A-Z]).{8,30}$";
    var pattern = Pattern.compile(regex);
    return pattern.matcher(s).matches();
  }

  /**
   * Retrieve info in database for all users.
   *
   * @return Details about all the users.
   */
  public List<User> getAllUsers() {
    return userRepository.findAll().stream().toList();
  }

  /**
   * Retrieve info in database for the current user.
   *
   * @return Details about the current user.
   */
  public User getAuthenticatedUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    var email = authentication.getName();
    return userRepository.findByEmail(email);
  }

  /**
   * Update the role of the user.
   *
   * @param id   the id of the user to change.
   * @param role the new role to be applied.
   * @return a {@link User} updated.
   */
  public User updateRole(int id, User.Role role) {
    var user = getById(id);

    if (user.getId() == getAuthenticatedUser().getId()) {
      throw new IllegalOperationException("Vous ne pouvez pas changer votre propre rôle.");
    }
    user.setRole(role);
    var result = userRepository.save(user);

    logger.info(new TechnicalLog(
        TechnicalLogEntity.Component.BACKEND,
        "Role updated for user : " + user.getId() + " " + user.getRole()));

    return result;
  }

  /**
   * Count with filters.
   *
   * @param request the request used to search
   * @return the count of entities with the request
   */
  public long countWithFilters(String request) {
    try {
      var condition = SearchUtils.computeSpecification(request, User.class);
      return userRepository.count(condition);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Requête invalide pour les filtres : " + request, e);
    }
  }

  public long count() {
    return userRepository.count();
  }

  /**
   * Search for {@link User} with a pagination.
   *
   * @param request  the request used to search
   * @param pageable The page requested
   * @return the list of corresponding {@link User}
   */
  public List<User> search(String request, PageRequest pageable) {
    try {
      var condition = SearchUtils.computeSpecification(request, User.class);
      return userRepository.findAll(condition, pageable).stream().toList();
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Requête invalide pour les filtres : " + request, e);
    }
  }

  /**
   * Create a new user in the database.
   *
   * @param createUserDto contains parameters of the new user.
   * @return the new User.
   * @throws EntityAlreadyExistingException when the user is already created.
   */
  public User createUser(CreateUserDto createUserDto) {
    if (createUserDto.email().isBlank()) {
      throw new BadRequestException("L'email est requis.");
    }
    if (userRepository.findByEmail(createUserDto.email()) != null) {
      throw new EntityAlreadyExistingException(
          "L'utilisateur existe déjà : " + createUserDto.email());
    }

    if (createUserDto.password().isBlank()) {
      throw new BadRequestException("Un mot de passe est requis.");
    }

    if (createUserDto.firstName().isBlank()) {
      throw new BadRequestException("Le prénom est requis.");
    }

    if (createUserDto.lastName().isBlank()) {
      throw new BadRequestException("Le nom est requis.");
    }

    var user = new User(
        createUserDto.firstName(),
        createUserDto.lastName(),
        createUserDto.email(),
        passwordEncoder.encode(createUserDto.password()),
        createUserDto.role());

    var result = userRepository.save(user);

    logger.info(new TechnicalLog(
        TechnicalLogEntity.Component.BACKEND,
        "New user saved : " + user.getId() + " " + user.getRole()));

    return result;
  }

  /**
   * Deletes a user from its id.
   *
   * @param id the id of the user to be deleted
   */
  public void delete(int id) {
    if (getAuthenticatedUser().getId() == id) {
      throw new IllegalOperationException("Impossible de se supprimer soi-même");
    }
    var user = getById(id);
    userRepository.delete(user);
  }

  private User getById(int id) {
    var user = userRepository.findById(id);
    if (user == null) {
      throw new EntityNotFoundException("Pas d'utilisateur avec l'id : " + id);
    }
    return user;
  }
}
