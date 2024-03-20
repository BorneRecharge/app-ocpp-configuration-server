package fr.uge.chargepointconfiguration.chargepointwebsocket;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessage;
import java.io.IOException;

/**
 * An interface to defines a message sender via a web socket.
 */
@FunctionalInterface
public interface OcppMessageSender {

  /**
   * Sends an {@link OcppMessage} through the web socket connection.
   *
   * @param ocppMessage {@link OcppMessage}.
   * @param chargePointManager {@link ChargePointManager}.
   */
  void sendMessage(OcppMessage ocppMessage, ChargePointManager chargePointManager)
      throws IOException;
}
