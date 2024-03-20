package fr.uge.chargepointconfiguration.logs;

import fr.uge.chargepointconfiguration.logs.business.BusinessLogRepository;
import fr.uge.chargepointconfiguration.logs.sealed.BusinessLogEntity;
import fr.uge.chargepointconfiguration.logs.sealed.LogEntity;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import fr.uge.chargepointconfiguration.logs.technical.TechnicalLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A CustomLoggerService doing database manipulations.
 */
@Service
public class CustomLoggerService {

  private final BusinessLogRepository businessLogRepository;

  private final TechnicalLogRepository technicalLogRepository;

  /**
   * CustomLoggerService's constructor.
   *
   * @param businessLogRepository A BusinessLogRepository accessing to database.
   * @param technicalLogRepository A TechnicalLogRepository accessing to database.
   */
  @Autowired
  public CustomLoggerService(
      BusinessLogRepository businessLogRepository, TechnicalLogRepository technicalLogRepository) {
    this.businessLogRepository = businessLogRepository;
    this.technicalLogRepository = technicalLogRepository;
  }

  /**
   * Saves a log into the database in the corresponding table according to the log type.
   *
   * @param log {@link LogEntity}
   * @return The used log.
   */
  public LogEntity save(LogEntity log) {
    return switch (log) {
      case BusinessLogEntity businessLogEntity -> businessLogRepository.save(businessLogEntity);
      case TechnicalLogEntity technicalLogEntity -> technicalLogRepository.save(technicalLogEntity);
    };
  }
}
