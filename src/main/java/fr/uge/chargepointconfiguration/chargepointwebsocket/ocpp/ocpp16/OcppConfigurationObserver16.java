package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uge.chargepointconfiguration.chargepoint.Chargepoint;
import fr.uge.chargepointconfiguration.chargepoint.ChargepointRepository;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ChargePointManager;
import fr.uge.chargepointconfiguration.chargepointwebsocket.OcppMessageSender;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessage;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppObserver;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.FirmwareStatus;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.RegistrationStatus;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.ResetType;
import fr.uge.chargepointconfiguration.configuration.ConfigurationTranscriptor;
import fr.uge.chargepointconfiguration.firmware.FirmwareRepository;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import fr.uge.chargepointconfiguration.logs.sealed.BusinessLog;
import fr.uge.chargepointconfiguration.logs.sealed.BusinessLogEntity;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLog;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import fr.uge.chargepointconfiguration.typeallowed.TypeAllowed;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

/**
 * Defines the OCPP 1.6 observer.<br>
 * An observer is defined by the interface {@link OcppObserver}.
 */
public class OcppConfigurationObserver16 implements OcppObserver {
  private final OcppMessageSender sender;
  private final ChargePointManager chargePointManager;
  private final ChargepointRepository chargepointRepository;
  private final FirmwareRepository firmwareRepository;
  private final Queue<ChangeConfigurationRequest16> queue = new LinkedList<>();
  private final CustomLogger logger;
  private String firmwareVersion;
  private String targetFirmwareVersion;
  private boolean loaded = false;
  private boolean lastOrderModeOn = false;


  /**
   * {@link OcppConfigurationObserver16}'s constructor.
   *
   * @param sender                {@link OcppMessageSender}.
   * @param chargePointManager    {@link ChargePointManager}.
   * @param chargepointRepository {@link ChargepointRepository}.
   * @param firmwareRepository    {@link FirmwareRepository}.
   * @param logger                {@link CustomLogger}.
   */
  public OcppConfigurationObserver16(OcppMessageSender sender,
                                     ChargePointManager chargePointManager,
                                     ChargepointRepository chargepointRepository,
                                     FirmwareRepository firmwareRepository,
                                     CustomLogger logger) {
    this.sender = Objects.requireNonNull(sender);
    this.chargePointManager = Objects.requireNonNull(chargePointManager);
    this.chargepointRepository = Objects.requireNonNull(chargepointRepository);
    this.firmwareRepository = Objects.requireNonNull(firmwareRepository);
    this.logger = Objects.requireNonNull(logger);
  }

  @Override
  public Optional<OcppMessage> onMessage(OcppMessage ocppMessage) {
    if (ocppMessage == null) {
      return processDefaultMessage();
    }
    return switch (ocppMessage) {
      case BootNotificationRequest16 b -> processBootNotification(b);
      case ChangeConfigurationResponse16 c -> processConfigurationResponse(c);
      case ResetResponse16 ignored -> processResetResponse();
      case FirmwareStatusNotificationRequest16 f -> processFirmwareStatusResponse(f);
      default -> processDefaultMessage();
    };
  }

  @Override
  public void onConnection(ChargePointManager chargePointManager) {
    Objects.requireNonNull(chargePointManager);
    // TODO : Is this method really useful ?
  }

  @Override
  public void onDisconnection(ChargePointManager chargePointManager) {
    if (chargePointManager.getCurrentChargepoint() == null) {
      lastOrderModeOn = false;
      loaded = false;
      var reset = new ResetRequest16(ResetType.Hard);
      sender.sendMessage(reset, chargePointManager);
    }
  }

  /**
   * Processes the received {@link BootNotificationRequest16} sent by the chargepoint.<br>
   * It is here where we start the automation of the configuration/firmware update.
   *
   * @param bootNotificationRequest16 {@link BootNotificationRequest16}.
   */
  private Optional<OcppMessage> processBootNotification(
          BootNotificationRequest16 bootNotificationRequest16) {
    firmwareVersion = bootNotificationRequest16.firmwareVersion();
    // Get charge point from database
    chargePointManager.setCurrentChargepoint(
            chargepointRepository.findBySerialNumberChargepointAndConstructor(
                    bootNotificationRequest16.chargePointSerialNumber(),
                    bootNotificationRequest16.chargePointVendor()
            ));
    // If charge point is not found then skip it
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    if (currentChargepoint == null) {
      logger.warn(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
              "unknown chargepoint, serial number : "
                      + bootNotificationRequest16.chargePointSerialNumber()));
      var response = new BootNotificationResponse16(
              LocalDateTime.now().toString(),
              10,
              RegistrationStatus.Rejected
      );
      sender.sendMessage(response, chargePointManager);
      return Optional.of(response);
    }
    var config = currentChargepoint.getConfiguration();
    if (currentChargepoint.getConfiguration() == null) {
      currentChargepoint.setStatus(Chargepoint.StatusProcess.PENDING);
      chargepointRepository.save(currentChargepoint);
      chargePointManager.notifyStatusUpdate();
      logger.info(new BusinessLog(null,
              currentChargepoint,
              BusinessLogEntity.Category.LOGIN,
              "chargepoint ("
                      + currentChargepoint.getSerialNumberChargepoint()
                      + ") is authenticated"));
      var response = new BootNotificationResponse16(
              LocalDateTime.now().toString(),
              5,
              RegistrationStatus.Accepted
      );
      sender.sendMessage(response, chargePointManager);
      return Optional.of(response);
    }
    targetFirmwareVersion = config.getFirmware().getVersion();
    currentChargepoint.setState(true);
    currentChargepoint.setStatus(Chargepoint.StatusProcess.PENDING);
    logger.info(new BusinessLog(null,
            currentChargepoint,
            BusinessLogEntity.Category.LOGIN,
            "chargepoint ("
                    + currentChargepoint.getSerialNumberChargepoint()
                    + ") is authenticated"));
    chargepointRepository.save(currentChargepoint);
    // Dispatch information to users
    chargePointManager.notifyStatusUpdate();
    chargePointManager.notifyOnConnection();
    // Send BootNotification Response
    var response = new BootNotificationResponse16(
            LocalDateTime.now().toString(),
            5,
            RegistrationStatus.Accepted
    );
    sender.sendMessage(response, chargePointManager);
    return switch (currentChargepoint.getStep()) {
      case Chargepoint.Step.CONFIGURATION -> processConfigurationRequest();
      case Chargepoint.Step.FIRMWARE -> processFirmwareRequest();
    };
  }

  /**
   * Processes the configuration.<br>
   * It searches in database the configuration for the chargepoint,
   * loads the key-value and sends a {@link ChangeConfigurationRequest16} for the chargepoint.<br>
   */
  private Optional<OcppMessage> processConfigurationRequest() {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    if (queue.isEmpty() && !loaded) {
      loadKeyValue();
      loaded = true;
    }
    currentChargepoint.setStatus(Chargepoint.StatusProcess.PROCESSING);
    chargepointRepository.save(currentChargepoint);
    // Dispatch information to users
    chargePointManager.notifyStatusUpdate();
    var config = queue.poll();
    if (config == null && (loaded && !lastOrderModeOn)) {
      prepareLastOrder();
      lastOrderModeOn = true;
      return processConfigurationRequest();
    } else if (config == null && lastOrderModeOn) {
      logger.info(new BusinessLog(null,
              currentChargepoint,
              BusinessLogEntity.Category.CONFIG,
              "configuration for the chargepoint ("
                      + currentChargepoint.getSerialNumberChargepoint()
                      + ") is done ! "));
      currentChargepoint.setStatus(Chargepoint.StatusProcess.FINISHED);
      chargepointRepository.save(currentChargepoint);
      // Dispatch information to users
      chargePointManager.notifyStatusUpdate();
      chargePointManager.notifyProcess();
      return processResetRequest();
    } else {
      sender.sendMessage(config, chargePointManager);
      assert config != null;
      return Optional.of(config);
    }
  }

  /**
   * Processes the {@link ChangeConfigurationResponse16} sent by the chargepoint.<br>
   * If the change has been rejected, we stop the update.<br>
   * Otherwise, we continue.
   *
   * @param response {@link ChangeConfigurationResponse16}.
   */
  private Optional<OcppMessage> processConfigurationResponse(
          ChangeConfigurationResponse16 response
  ) {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    return switch (response.status()) {
      case Accepted, RebootRequired -> processConfigurationRequest();
      default -> {
        logger.warn(new BusinessLog(null,
                currentChargepoint,
                BusinessLogEntity.Category.CONFIG,
                "configuration for the chargepoint ("
                        + currentChargepoint.getSerialNumberChargepoint()
                        + ") has failed, see its status ! "));
        currentChargepoint.setStatus(Chargepoint.StatusProcess.FAILED);
        currentChargepoint.setError(response.status().name());
        chargepointRepository.save(currentChargepoint);
        // Dispatch information to users
        chargePointManager.notifyStatusUpdate();
        chargePointManager.notifyProcess();
        queue.clear();
        yield Optional.empty();
      }
    };
  }

  private void prepareLastOrder() {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    logger.info(new BusinessLog(null,
            currentChargepoint,
            BusinessLogEntity.Category.CONFIG,
            "configuration for the chargepoint ("
                    + currentChargepoint.getSerialNumberChargepoint()
                    + ") is almost done, sending last order ! "));
    var clientId = currentChargepoint.getClientId();
    var changeConfig = new ChangeConfigurationRequest16(
            ConfigurationTranscriptor.CHARGEPOINT_IDENTITY
                    .getOcpp16Key()
                    .getFirmwareKeyAccordingToVersion(firmwareVersion),
            clientId);
    queue.add(changeConfig);
    var finalServerAddress = System.getenv("FINAL_WS_SERVER_ADDRESS");
    if (finalServerAddress != null) {
      changeConfig = new ChangeConfigurationRequest16(
              ConfigurationTranscriptor.NETWORK_PROFILE
                      .getOcpp16Key()
                      .getFirmwareKeyAccordingToVersion(firmwareVersion),
              ConfigurationTranscriptor.NETWORK_PROFILE
                      .getOcpp16Key()
                      .getValueFormatAccordingToVersion(firmwareVersion)
                      .formatted(finalServerAddress));
      queue.add(changeConfig);
    }
  }

  /**
   * Processes the firmware.<br>
   * It searches in database the firmware target for the chargepoint.<br>
   * We upgrade the firmware step by step, meaning,
   * we choose the closest firmware to the current firmware.<br>
   * Example :<br>
   * The firmware target is 5.8.7, and the current target is 5.4.8.<br>
   * We upgrade to 5.5 > 5.6 > 5.7 > 5.8.
   */
  private Optional<OcppMessage> processFirmwareRequest() {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    if (currentChargepoint.getConfiguration() == null) {
      currentChargepoint.setStatus(Chargepoint.StatusProcess.PENDING);
      chargepointRepository.save(currentChargepoint);
      chargePointManager.notifyStatusUpdate();
      return Optional.empty();
    }
    var firmware = currentChargepoint.getConfiguration().getFirmware();
    logger.info(new BusinessLog(null,
            currentChargepoint,
            BusinessLogEntity.Category.FIRM,
            "firmware target ("
                    + firmware.getVersion()
                    + ") for chargepoint ( "
                    + currentChargepoint.getSerialNumberChargepoint()
                    + ")"));
    var typesAllowed = firmware.getTypesAllowed();
    var link = "";
    for (var typeAllowed : typesAllowed) {
      if (typeAllowed.getType().equals(currentChargepoint.getType())
              && typeAllowed.getConstructor().equals(currentChargepoint.getConstructor())) {
        link = fetchUrlFromFirstCompatibleVersion(typeAllowed);
        break;
      }
    }
    if (link.isEmpty()) {
      logger.info(new BusinessLog(null,
              currentChargepoint,
              BusinessLogEntity.Category.FIRM,
              "firmware update for the chargepoint ("
                      + currentChargepoint.getSerialNumberChargepoint()
                      + ") is done ! "));
      currentChargepoint.setStatus(Chargepoint.StatusProcess.PENDING);
      currentChargepoint.setStep(Chargepoint.Step.CONFIGURATION);
      chargepointRepository.save(currentChargepoint);
      // Dispatch information to users
      chargePointManager.notifyStatusUpdate();
      chargePointManager.notifyProcess();
      return processConfigurationRequest();
    }
    currentChargepoint.setStatus(Chargepoint.StatusProcess.PROCESSING);
    chargepointRepository.save(currentChargepoint);
    // Dispatch information to users
    chargePointManager.notifyStatusUpdate();
    var firmwareRequest = new UpdateFirmwareRequest16(
            link, LocalDateTime.now().toString());
    sender.sendMessage(firmwareRequest, chargePointManager);
    return Optional.of(firmwareRequest);
  }

  /**
   * Fetches the first compatible firmware version from the database.
   *
   * @param typeAllowed {@link TypeAllowed}.
   * @return The URL for downloading the firmware.
   */
  private String fetchUrlFromFirstCompatibleVersion(TypeAllowed typeAllowed) {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    var comparison = targetFirmwareVersion.compareTo(firmwareVersion);
    if (comparison > 0) {
      var firmwares = firmwareRepository
              .findAllByTypeAllowedAsc(typeAllowed);
      for (var firmware : firmwares) {
        if (firmware.getVersion().compareTo(firmwareVersion) > 0) {
          logger.info(new BusinessLog(null,
                  currentChargepoint,
                  BusinessLogEntity.Category.FIRM,
                  "updating chargepoint ("
                          + currentChargepoint.getSerialNumberChargepoint()
                          + ") with firmware "
                          + firmware.getVersion()));
          return firmware.getUrl();
        }
      }
      logger.info(new BusinessLog(null,
              currentChargepoint,
              BusinessLogEntity.Category.FIRM,
              "didn't find any compatible firmware for chargepoint ("
                      + currentChargepoint.getSerialNumberChargepoint()
                      + ") : skipping to CONFIGURATION"));
      return "";
    } else if (comparison < 0) {
      var firmwares = firmwareRepository
              .findAllByTypeAllowedDesc(typeAllowed);
      for (var firmware : firmwares) {
        if (firmware.getVersion().compareTo(firmwareVersion) < 0) {
          logger.info(new BusinessLog(null,
                  currentChargepoint,
                  BusinessLogEntity.Category.FIRM,
                  "tried to rollback chargepoint ("
                          + currentChargepoint.getSerialNumberChargepoint()
                          + ") with firmware : FORBIDDEN, skipping to CONFIGURATION"));
          return "";
        }
      }
    }
    return "";
  }

  /**
   * Processes the received {@link ResetResponse16}.
   */
  private Optional<OcppMessage> processResetResponse() {
    if (chargePointManager.getCurrentChargepoint() != null) {
      var currentChargepoint = chargePointManager.getCurrentChargepoint();
      currentChargepoint.setStatus(Chargepoint.StatusProcess.FINISHED);
      currentChargepoint.setState(false);
      chargepointRepository.save(currentChargepoint);
      // Dispatch information to users
      chargePointManager.notifyStatusUpdate();
    }
    return Optional.empty();
  }

  private Optional<OcppMessage> processResetRequest() {
    var reset = new ResetRequest16(ResetType.Hard);
    sender.sendMessage(reset, chargePointManager);
    return Optional.of(reset);
  }

  /**
   * Processes the current firmware installation status.<br>
   * If the download has failed or the installation has failed,
   * we stop the process.<br>
   * Otherwise, we continue as normal.
   *
   * @param f {@link FirmwareStatusNotificationRequest16}.
   */
  private Optional<OcppMessage> processFirmwareStatusResponse(
          FirmwareStatusNotificationRequest16 f
  ) {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    return switch (f.status()) {
      case Installed -> {
        logger.info(new BusinessLog(null,
                currentChargepoint,
                BusinessLogEntity.Category.FIRM,
                "chargepoint ("
                        + currentChargepoint.getSerialNumberChargepoint()
                        + ") has successfully installed the firmware"));
        currentChargepoint.setStatus(Chargepoint.StatusProcess.PENDING);
        chargepointRepository.save(currentChargepoint);
        var reset = new ResetRequest16(ResetType.Hard);
        sender.sendMessage(reset, chargePointManager);
        logger.info(new BusinessLog(null,
                currentChargepoint,
                BusinessLogEntity.Category.FIRM,
                "rebooting the chargepoint ("
                        + currentChargepoint.getSerialNumberChargepoint()
                        + ")"));
        yield Optional.of(reset);
      }
      case DownloadFailed,
              InstallationFailed -> {
        if (f.status() == FirmwareStatus.DownloadFailed) {
          logger.warn(new BusinessLog(null,
                  currentChargepoint,
                  BusinessLogEntity.Category.FIRM,
                  "firmware for the chargepoint ("
                          + currentChargepoint.getSerialNumberChargepoint()
                          + ") couldn't be downloaded, "
                          + "check the internet connection of the chargepoint"));
        } else {
          logger.warn(new BusinessLog(null,
                  currentChargepoint,
                  BusinessLogEntity.Category.FIRM,
                  "firmware for the chargepoint ("
                          + currentChargepoint.getSerialNumberChargepoint()
                          + ") couldn't be installed, check the given URL to the chargepoint"));
        }
        currentChargepoint.setStatus(Chargepoint.StatusProcess.FAILED);
        chargepointRepository.save(currentChargepoint);
        chargePointManager.notifyStatusUpdate();
        chargePointManager.notifyProcess();
        yield Optional.empty();
      }
      default -> Optional.empty();
    };
  }

  private void loadKeyValue() {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    // The change configuration list is empty, so we load the configuration
    var configuration = currentChargepoint.getConfiguration().getConfiguration();
    var mapper = new ObjectMapper();
    try {
      // Cast
      HashMap<String, String> configMap = mapper.readValue(configuration, HashMap.class);
      configMap.forEach((key, value) -> {
        var configMessage = new ChangeConfigurationRequest16(
                ConfigurationTranscriptor.idToEnum(Integer.parseInt(key))
                        .getOcpp16Key()
                        .getFirmwareKeyAccordingToVersion(firmwareVersion),
                value
        );
        queue.add(configMessage);
        logger.info(new BusinessLog(null,
                currentChargepoint,
                BusinessLogEntity.Category.CONFIG,
                "added configuration in the waiting list for the chargepoint ("
                        + currentChargepoint.getSerialNumberChargepoint()
                        + ") : "
                        + configMessage));
      });
    } catch (JsonProcessingException e) {
      logger.error(new BusinessLog(null,
              currentChargepoint,
              BusinessLogEntity.Category.CONFIG,
              "couldn't read configuration for the chargepoint ("
                      + currentChargepoint.getSerialNumberChargepoint()
                      + "): " + e.getMessage()));
    }
  }

  private Optional<OcppMessage> processDefaultMessage() {
    var currentChargepoint = chargePointManager.getCurrentChargepoint();
    if (currentChargepoint == null) {
      return processResetRequest();
    }
    chargePointManager.setCurrentChargepoint(
            chargepointRepository.findBySerialNumberChargepointAndConstructor(
                    currentChargepoint.getSerialNumberChargepoint(),
                    currentChargepoint.getConstructor()
            ));
    currentChargepoint = chargePointManager.getCurrentChargepoint();
    var step = currentChargepoint.getStep();
    var status = currentChargepoint.getStatus();
    if (step == Chargepoint.Step.CONFIGURATION && status == Chargepoint.StatusProcess.PENDING) {
      return processConfigurationRequest();
    } else if (status == Chargepoint.StatusProcess.FAILED
            || status == Chargepoint.StatusProcess.FINISHED) {
      loaded = false;
      lastOrderModeOn = false;
      return processFirmwareRequest();
    } else {
      return Optional.empty();
    }
  }
}
