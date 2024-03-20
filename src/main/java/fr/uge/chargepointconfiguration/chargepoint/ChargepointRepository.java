package fr.uge.chargepointconfiguration.chargepoint;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the charge point.
 */
@Repository
public interface ChargepointRepository
    extends CrudRepository<Chargepoint, Integer>,
        PagingAndSortingRepository<Chargepoint, Integer>,
        JpaSpecificationExecutor<Chargepoint> {

  /**
   * Returns a Chargepoint from the database according to the serial number and vendor.
   *
   * @param serialNumber Chargepoint's unique serial number.
   * @param constructor Chargepoint's vendor.
   * @return The correct Chargepoint or null if the chargepoint couldn't be found.
   */
  Chargepoint findBySerialNumberChargepointAndConstructor(String serialNumber, String constructor);

  /**
   * Return a list of registered Chargepoints from database.
   *
   * @return A list of Chargepoints or an empty list if no chargepoints are registered.
   */
  List<Chargepoint> findAllByOrderByIdDesc();

  Page<Chargepoint> findAllByClientIdContainingIgnoreCaseOrderByIdDesc(
      Pageable pageable, String clientId);
}
