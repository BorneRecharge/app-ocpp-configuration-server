package fr.uge.chargepointconfiguration.typeallowed;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TypeAllowedService manage database manipulations.
 */
@Service
public class TypeAllowedService {

  private final TypeAllowedRepository typeAllowedRepository;

  /**
   * TypeAllowedService's constructor.
   *
   * @param typeAllowedRepository A ConfigurationRepository accessing to database.
   */
  @Autowired
  public TypeAllowedService(TypeAllowedRepository typeAllowedRepository) {
    this.typeAllowedRepository = typeAllowedRepository;
  }

  /**
   * Get all type allowed entries from the database.
   *
   * @return The list of all type allowed.
   */
  public List<TypeAllowed> getAll() {
    return typeAllowedRepository.findAll();
  }

  /**
   * Create a new type allowed.
   *
   * @param typeAllowedDto the type allowed to be saved.
   * @return the saved type allowed
   */
  public TypeAllowed save(CreateTypeAllowedDto typeAllowedDto) {
    return typeAllowedRepository.save(
        new TypeAllowed(typeAllowedDto.constructor(), typeAllowedDto.type()));
  }
}
