package fr.uge.chargepointconfiguration.configuration;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the charge point configuration.
 */
@Repository
public interface ConfigurationRepository
    extends CrudRepository<Configuration, Integer>,
        PagingAndSortingRepository<Configuration, Integer>,
        JpaSpecificationExecutor<Configuration> {
  List<Configuration> findAllByOrderByIdDesc();

  Page<Configuration> findAllByOrderByIdDesc(Pageable pageable);
}
