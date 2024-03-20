package fr.uge.chargepointconfiguration.logs.technical;

import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for technical log.
 */
@Repository
public interface TechnicalLogRepository
    extends CrudRepository<TechnicalLogEntity, Integer>,
        PagingAndSortingRepository<TechnicalLogEntity, Integer>,
        JpaSpecificationExecutor<TechnicalLogEntity> {

  /**
   * Method to return all technical logs.
   *
   * @return a list of all technical logs.
   */
  List<TechnicalLogEntity> findAllByOrderByIdDesc();

  Page<TechnicalLogEntity> findAllByOrderByIdDesc(Pageable pageable);

  /**
   * Method to return all technical logs by the component.
   *
   * @param component the type of component in the system.
   * @param level {@link Level}
   * @return the list of technical logs by component.
   */
  List<TechnicalLogEntity> findAllByComponentAndLevelOrderByIdDesc(
      TechnicalLogEntity.Component component, String level);

  /**
   * Method to return all technical logs by the criticality.
   *
   * @param level {@link Level}
   * @return the list of technical logs by criticality.
   */
  List<TechnicalLogEntity> findAllByLevelOrderByIdDesc(String level);
}
