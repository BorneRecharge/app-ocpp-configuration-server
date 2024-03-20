package fr.uge.chargepointconfiguration.logs.business;

import fr.uge.chargepointconfiguration.chargepoint.ChargepointRepository;
import fr.uge.chargepointconfiguration.errors.exceptions.BadRequestException;
import fr.uge.chargepointconfiguration.errors.exceptions.EntityNotFoundException;
import fr.uge.chargepointconfiguration.logs.sealed.BusinessLogEntity;
import fr.uge.chargepointconfiguration.shared.SearchUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * A BusinessLogService doing database manipulations.
 */
@Service
public class BusinessLogService {

  private final BusinessLogRepository businessLogRepository;

  private final ChargepointRepository chargepointRepository;

  /**
   * BusinessLogService's constructor.
   *
   * @param businessLogRepository A BusinessLogRepository accessing to database.
   * @param chargepointRepository A ChargepointRepository accessing to database.
   */
  @Autowired
  public BusinessLogService(
      BusinessLogRepository businessLogRepository, ChargepointRepository chargepointRepository) {
    this.businessLogRepository = businessLogRepository;
    this.chargepointRepository = chargepointRepository;
  }

  /**
   * Returns a list of business logs filtered by a chargepoint id.
   *
   * @param chargepointId the id of the chargepoint.
   * @return a list of business logs by chargepoint.
   */
  public List<BusinessLogEntity> getAllByChargepointId(int chargepointId) {
    var chargepoint = chargepointRepository
        .findById(chargepointId)
        .orElseThrow(() -> new EntityNotFoundException("Aucune borne avec l'id " + chargepointId));
    return businessLogRepository.findAllByChargepointOrderByIdDesc(chargepoint);
  }

  /**
   * Count the number of entities with the constraint of the given request.
   *
   * @param request the request used to search
   * @return the amount of entities with the constraint of the given request
   */
  public long countTotalWithFilter(String request) {
    try {
      var condition = SearchUtils.computeSpecification(request, BusinessLogEntity.class);
      return businessLogRepository.count(condition);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Requête invalide pour les filtres : " + request, e);
    }
  }

  public long count() {
    return businessLogRepository.count();
  }

  /**
   * Search for {@link BusinessLogEntity} with a pagination.
   *
   * @param request the request used to search
   * @param pageable The page requested
   * @return the list of corresponding {@link BusinessLogEntity}
   */
  public List<BusinessLogEntity> search(String request, PageRequest pageable) {
    try {
      var condition = SearchUtils.computeSpecification(request, BusinessLogEntity.class);
      return businessLogRepository.findAll(condition, pageable).stream().toList();
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Requête invalide pour les filtres : " + request, e);
    }
  }
}
