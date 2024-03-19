package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp;

import fr.uge.chargepointconfiguration.chargepoint.ChargepointRepository;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ChargePointManager;
import fr.uge.chargepointconfiguration.chargepointwebsocket.OcppMessageSender;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.OcppConfigurationObserver16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.OcppConfigurationObserver20;
import fr.uge.chargepointconfiguration.firmware.FirmwareRepository;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import java.util.Objects;
import java.util.Optional;

/**
 * Interface to defines the message observer.<br>
 * It listens and processes the message sent by the chargepoint.
 */
public interface OcppObserver {

  /**
   * Instantiates the correct observer according to the {@link OcppVersion}.
   *
   * @param ocppVersion {@link OcppVersion}.
   * @param chargePointManager {@link ChargePointManager}.
   * @param ocppMessageSender {@link OcppMessageSender}.
   * @param chargepointRepository {@link ChargepointRepository}.
   * @param firmwareRepository {@link FirmwareRepository}.
   * @param logger {@link CustomLogger}.
   * @return {@link OcppObserver}.
   */
  static OcppObserver instantiateFromVersion(OcppVersion ocppVersion,
                                             ChargePointManager chargePointManager,
                                             OcppMessageSender ocppMessageSender,
                                             ChargepointRepository chargepointRepository,
                                             FirmwareRepository firmwareRepository,
                                             CustomLogger logger) {
    Objects.requireNonNull(ocppVersion);
    Objects.requireNonNull(chargePointManager);
    Objects.requireNonNull(ocppMessageSender);
    Objects.requireNonNull(chargepointRepository);
    Objects.requireNonNull(firmwareRepository);
    Objects.requireNonNull(logger);
    return switch (ocppVersion) {
      case V1_6 -> new OcppConfigurationObserver16(
              ocppMessageSender,
              chargePointManager,
              chargepointRepository,
              firmwareRepository,
              logger
      );
      case V2 -> new OcppConfigurationObserver20(
              ocppMessageSender,
              chargePointManager,
              chargepointRepository
      );
    };
  }

  /**
   * Does something when receiving a message.
   *
   * @param ocppMessage {@link OcppMessage}.
   * @return {@link OcppMessage}, the message sent in response.
   */
  Optional<OcppMessage> onMessage(OcppMessage ocppMessage);

  /**
   * Does something when a connection has been set.
   *
   * @param chargePointManager {@link ChargePointManager}.
   */
  void onConnection(ChargePointManager chargePointManager);

  /**
   * Does something when a disconnection has been done.
   *
   * @param chargePointManager {@link ChargePointManager}.
   */
  void onDisconnection(ChargePointManager chargePointManager);
}
