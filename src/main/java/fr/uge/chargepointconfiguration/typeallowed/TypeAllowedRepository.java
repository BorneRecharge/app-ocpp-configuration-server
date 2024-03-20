package fr.uge.chargepointconfiguration.typeallowed;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for type allowed.
 */
public interface TypeAllowedRepository extends CrudRepository<TypeAllowed, Integer> {
  List<TypeAllowed> findAll();
}
