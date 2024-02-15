package fr.uge.chargepointconfiguration.chargepoint.ocpp.ocpp2;

import fr.uge.chargepointconfiguration.chargepoint.WebSocketRequestMessage;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.OcppMessage;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.OcppMessageBuilder;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.RegistrationStatus;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.ocpp2.data.RegistrationStatusEnumType;
import fr.uge.chargepointconfiguration.tools.JsonParser;
import java.time.LocalDateTime;

/**
 * Builds an OCPP 2.0.1 message.
 */
public class OcppMessageBuilder2 implements OcppMessageBuilder {

  @Override
  public OcppMessage buildMessage(WebSocketRequestMessage webSocketRequestMessage) {
    return switch (webSocketRequestMessage.messageName()) {
      case "BootNotification" ->
              new BootNotificationResponse(LocalDateTime.now().toString(),
                      5,
                      new RegistrationStatusEnumType(RegistrationStatus.Accepted));
      default -> throw new IllegalArgumentException("Message not recognized: "
              + webSocketRequestMessage);
    };
  }

  @Override
  public String transform(OcppMessage message) {
    return JsonParser.objectToJsonString(message);
  }
}
