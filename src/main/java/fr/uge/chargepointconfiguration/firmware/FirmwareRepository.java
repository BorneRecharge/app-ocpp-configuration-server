package fr.uge.chargepointconfiguration.firmware;

import fr.uge.chargepointconfiguration.typeallowed.TypeAllowed;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Repository for the firmware.
 */
public interface FirmwareRepository
    extends CrudRepository<Firmware, Integer>,
        PagingAndSortingRepository<Firmware, Integer>,
        JpaSpecificationExecutor<Firmware> {

  /**
   * Returns a Firmware from the database according to the version.
   *
   * @param version Firmware's unique version.
   * @return The correct Firmware or null if the firmware couldn't be found.
   */
  Firmware findByVersion(String version);

  /**
   * Returns all the firmwares from the database according to the compatibility table.
   *
   * @param typeAllowed {@link TypeAllowed} which is the compatibility for a firmware.
   * @return {@link Firmware}.
   */
  @Query(
      """
          select f from Firmware f \
          join f.typesAllowed \
          where :typeAllowed member of f.typesAllowed order by f.version asc""")
  List<Firmware> findAllByTypeAllowedAsc(@Param("typeAllowed") TypeAllowed typeAllowed);

  /**
   * Returns all the firmwares from the database according to the compatibility table.
   *
   * @param typeAllowed {@link TypeAllowed} which is the compatibility for a firmware.
   * @return {@link Firmware}.
   */
  @Query(
      """
          select f from Firmware f \
          join f.typesAllowed \
          where :typeAllowed member of f.typesAllowed order by f.version desc""")
  List<Firmware> findAllByTypeAllowedDesc(@Param("typeAllowed") TypeAllowed typeAllowed);

  /**
   * Return a list of registered Firmwares from database.
   *
   * @return A list of Firmwares or an empty list if no firmwares are registered.
   */
  List<Firmware> findAllByOrderByIdDesc();

  Page<Firmware> findAllByOrderByIdDesc(Pageable pageable);

  Optional<Firmware> findByUrl(String url);
}
