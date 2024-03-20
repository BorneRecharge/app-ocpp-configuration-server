package fr.uge.chargepointconfiguration.chargepointwebsocket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.uge.chargepointconfiguration.chargepoint.Chargepoint;
import fr.uge.chargepointconfiguration.chargepoint.ChargepointRepository;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.MessageType;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessage;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppVersion;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.BootNotificationRequest16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.BootNotificationResponse16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.ChangeConfigurationRequest16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.ChangeConfigurationResponse16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.FirmwareStatusNotificationRequest16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.ResetRequest16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.UpdateFirmwareRequest16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.UpdateFirmwareResponse16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.ConfigurationStatus;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.FirmwareStatus;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.RegistrationStatus;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data.ResetType;
import fr.uge.chargepointconfiguration.configuration.ConfigurationTranscriptor;
import fr.uge.chargepointconfiguration.firmware.FirmwareRepository;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import fr.uge.chargepointconfiguration.tools.JsonParser;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * JUnit test class for the {@link ChargePointManager}.
 */
@SpringBootTest
public class ChargepointManagerTest {

  @Autowired
  private ChargepointRepository chargepointRepository;

  @Autowired
  private FirmwareRepository firmwareRepository;

  @Autowired
  private CustomLogger customLogger;

  private ChargePointManager instantiate() {
    return new ChargePointManager(
        OcppVersion.V1_6,
        (ocppMessage, chargePointManager) -> {
          if (Objects.requireNonNull(OcppMessage.ocppMessageToMessageType(ocppMessage))
              == MessageType.REQUEST) {
            var request = new WebSocketRequestMessage(
                MessageType.REQUEST.getCallType(),
                chargePointManager.getCurrentId(),
                WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(ocppMessage),
                JsonParser.objectToJsonString(ocppMessage));
            chargePointManager.setPendingRequest(request);
          }
        },
        chargepointRepository,
        firmwareRepository,
        customLogger);
  }

  /**
   * Should not throw an exception while calling the constructor.
   */
  @Test
  public void correctConstructorShouldNotThrowException() {
    assertDoesNotThrow(() -> {
      new ChargePointManager(
          OcppVersion.V1_6,
          (ocppMessage, chargePointManager) -> {},
          chargepointRepository,
          firmwareRepository,
          customLogger);
    });
  }

  /**
   * Should correctly change the current chargepoint entry in the database on error.
   */
  @Test
  public void onErrorShouldResultInAChangeInDatabase() {
    var chargepointManager = instantiate();
    var currentChargepoint = chargepointRepository.findAllByOrderByIdDesc().getFirst();
    chargepointManager.setCurrentChargepoint(currentChargepoint);
    var error = new Exception("An error for the test :)");
    chargepointManager.onError(error);
    var actualChargepoint = chargepointRepository.findAllByOrderByIdDesc().getFirst();
    assertEquals(currentChargepoint.getError(), actualChargepoint.getError());
    assertEquals(currentChargepoint.getStep(), actualChargepoint.getStep());
    assertEquals(currentChargepoint.isState(), actualChargepoint.isState());
    assertEquals(
        currentChargepoint.getSerialNumberChargepoint(),
        actualChargepoint.getSerialNumberChargepoint());
    assertEquals(currentChargepoint.getType(), actualChargepoint.getType());
    assertEquals(currentChargepoint.getClientId(), actualChargepoint.getClientId());
    assertEquals(currentChargepoint.getStatus(), actualChargepoint.getStatus());
  }

  /**
   * Should correctly change the current chargepoint entry in the database on disconnection.
   */
  @Test
  public void onDisconnectionShouldResultInAChangeInDatabase() {
    var chargepointManager = instantiate();
    var currentChargepoint = chargepointRepository.findAllByOrderByIdDesc().getFirst();
    chargepointManager.setCurrentChargepoint(currentChargepoint);
    chargepointManager.onDisconnection();
    var actualChargepoint = chargepointRepository.findAllByOrderByIdDesc().getFirst();
    assertEquals(currentChargepoint.getError(), actualChargepoint.getError());
    assertEquals(currentChargepoint.getStep(), actualChargepoint.getStep());
    assertEquals(currentChargepoint.isState(), actualChargepoint.isState());
    assertEquals(
        currentChargepoint.getSerialNumberChargepoint(),
        actualChargepoint.getSerialNumberChargepoint());
    assertEquals(currentChargepoint.getType(), actualChargepoint.getType());
    assertEquals(currentChargepoint.getClientId(), actualChargepoint.getClientId());
    assertEquals(currentChargepoint.getStatus(), actualChargepoint.getStatus());
  }

  /**
   * Should send a {@link BootNotificationResponse16} with a rejected status.
   */
  @Test
  public void onMessageShouldRejectUnknownChargepoint() throws IOException {
    var chargepointManager = instantiate();
    var bootNotifMessage = new BootNotificationRequest16(
        "Testor Corp", "A big chargepoint", "ABCDE524154", "Leroy Jenkins", "5.5.5-5555");
    var request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(bootNotifMessage),
        JsonParser.objectToJsonString(bootNotifMessage));
    var sentMessage = chargepointManager.processMessage(request);
    var actualResponse = (BootNotificationResponse16) sentMessage.orElseThrow();
    assertEquals(BootNotificationResponse16.class, sentMessage.orElseThrow().getClass());
    assertEquals(RegistrationStatus.Rejected, actualResponse.status());
  }

  /**
   * Should accept a known chargepoint even if it hasn't a configuration.
   */
  @Test
  public void onMessageShouldAcceptChargepointWithoutConfiguration() throws IOException {
    var chargepointManager = instantiate();
    var bootNotifMessage = new BootNotificationRequest16(
        "Alfen BV", "Borne to be alive", "ACE0000001", "Leroy Jenkins", "5.5.5-5555");
    var request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(bootNotifMessage),
        JsonParser.objectToJsonString(bootNotifMessage));
    var sentMessage = chargepointManager.processMessage(request);
    var actualResponse = (BootNotificationResponse16) sentMessage.orElseThrow();
    assertEquals(BootNotificationResponse16.class, sentMessage.orElseThrow().getClass());
    assertEquals(RegistrationStatus.Accepted, actualResponse.status());
  }

  /**
   * In this test, the chargepoint should have {@link UpdateFirmwareRequest16} sent
   * until it is done.
   */
  @Test
  public void onMessageShouldSendAUpdateFirmwareRequestUntilIsDone() throws IOException {
    var chargepointManager = instantiate();
    var bootNotifMessage = new BootNotificationRequest16(
        "Alfen BV", "Borne to be alive", "ACE0000005", "Leroy Jenkins", "5.5.5-5555");
    var request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(bootNotifMessage),
        JsonParser.objectToJsonString(bootNotifMessage));
    var sentMessage = chargepointManager.processMessage(request);
    var actualResponse = (UpdateFirmwareRequest16) sentMessage.orElseThrow();
    assertEquals(UpdateFirmwareRequest16.class, actualResponse.getClass());
    assertEquals("https://lienFirmware2", actualResponse.location());
    var responseFromTheChargepoint = new UpdateFirmwareResponse16();
    var response = new WebSocketResponseMessage(
        MessageType.RESPONSE.getCallType(),
        chargepointManager.getCurrentId(),
        JsonParser.objectToJsonString(responseFromTheChargepoint));
    sentMessage = chargepointManager.processMessage(response);
    assertThrows(NoSuchElementException.class, sentMessage::orElseThrow);
    var statusFromTheChargepoint =
        new FirmwareStatusNotificationRequest16(FirmwareStatus.Installed);
    request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(statusFromTheChargepoint),
        JsonParser.objectToJsonString(statusFromTheChargepoint));
    sentMessage = chargepointManager.processMessage(request);
    var reset = (ResetRequest16) sentMessage.orElseThrow();
    assertEquals(ResetRequest16.class, sentMessage.orElseThrow().getClass());
    assertEquals(ResetType.Hard, reset.type());
    bootNotifMessage = new BootNotificationRequest16(
        "Alfen BV", "Borne to be alive", "ACE0000005", "Leroy Jenkins", "5.8.1-4123");
    request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(bootNotifMessage),
        JsonParser.objectToJsonString(bootNotifMessage));
    sentMessage = chargepointManager.processMessage(request);
    actualResponse = (UpdateFirmwareRequest16) sentMessage.orElseThrow();
    assertEquals(UpdateFirmwareRequest16.class, actualResponse.getClass());
    assertEquals("https://lienFirmware1", actualResponse.location());
    responseFromTheChargepoint = new UpdateFirmwareResponse16();
    response = new WebSocketResponseMessage(
        MessageType.RESPONSE.getCallType(),
        chargepointManager.getCurrentId(),
        JsonParser.objectToJsonString(responseFromTheChargepoint));
    sentMessage = chargepointManager.processMessage(response);
    assertThrows(NoSuchElementException.class, sentMessage::orElseThrow);
    statusFromTheChargepoint = new FirmwareStatusNotificationRequest16(FirmwareStatus.Installed);
    request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(statusFromTheChargepoint),
        JsonParser.objectToJsonString(statusFromTheChargepoint));
    sentMessage = chargepointManager.processMessage(request);
    reset = (ResetRequest16) sentMessage.orElseThrow();
    assertEquals(ResetRequest16.class, sentMessage.orElseThrow().getClass());
    assertEquals(ResetType.Hard, reset.type());
    bootNotifMessage = new BootNotificationRequest16(
        "Alfen BV", "Borne to be alive", "ACE0000005", "Leroy Jenkins", "6.1.1-4160");
    request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(bootNotifMessage),
        JsonParser.objectToJsonString(bootNotifMessage));
    sentMessage = chargepointManager.processMessage(request);
    assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
    assertEquals(
        Chargepoint.Step.CONFIGURATION,
        chargepointManager.getCurrentChargepoint().getStep());
    assertEquals(
        Chargepoint.StatusProcess.PROCESSING,
        chargepointManager.getCurrentChargepoint().getStatus());
  }

  /**
   * Should send {@link ChangeConfigurationRequest16}
   * until the configuration is done.
   */
  @Test
  public void onMessageShouldSendAChangeConfigurationRequestUntilIsDone() throws IOException {
    var chargepointManager = instantiate();
    var bootNotifMessage = new BootNotificationRequest16(
        "Alfen BV", "Borne to be alive", "ACE0000002", "Leroy Jenkins", "5.5.5-5555");
    var request = new WebSocketRequestMessage(
        MessageType.REQUEST.getCallType(),
        chargepointManager.getCurrentId(),
        WebSocketMessage.MessageTypeRequest.ocppMessageToEnum(bootNotifMessage),
        JsonParser.objectToJsonString(bootNotifMessage));
    var sentMessage = chargepointManager.processMessage(request);
    assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
    var actualResponse = (ChangeConfigurationRequest16) sentMessage.orElseThrow();
    assertEquals(
        ConfigurationTranscriptor.LIGHT_INTENSITY
            .getOcpp16Key()
            .getFirmwareKeyAccordingToVersion(bootNotifMessage.firmwareVersion()),
        actualResponse.key());
    assertEquals("100", actualResponse.value());
    var responseFromTheChargepoint =
        new ChangeConfigurationResponse16(ConfigurationStatus.Accepted);
    var response = new WebSocketResponseMessage(
        MessageType.RESPONSE.getCallType(),
        chargepointManager.getCurrentId(),
        JsonParser.objectToJsonString(responseFromTheChargepoint));
    sentMessage = chargepointManager.processMessage(response);
    assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
    actualResponse = (ChangeConfigurationRequest16) sentMessage.orElseThrow();
    assertEquals(
        ConfigurationTranscriptor.CHARGEPOINT_IDENTITY
            .getOcpp16Key()
            .getFirmwareKeyAccordingToVersion(bootNotifMessage.firmwareVersion()),
        actualResponse.key());
    assertEquals("Borne-Test", actualResponse.value());
    sentMessage = chargepointManager.processMessage(response);
    assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
    actualResponse = (ChangeConfigurationRequest16) sentMessage.orElseThrow();
    assertEquals(
        ConfigurationTranscriptor.LOCAL_AUTH_LIST
            .getOcpp16Key()
            .getFirmwareKeyAccordingToVersion(bootNotifMessage.firmwareVersion()),
        actualResponse.key());
    assertEquals("true", actualResponse.value());
    sentMessage = chargepointManager.processMessage(response);
    assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
    actualResponse = (ChangeConfigurationRequest16) sentMessage.orElseThrow();
    assertEquals(
        ConfigurationTranscriptor.STATION_MAX_CURRENT
            .getOcpp16Key()
            .getFirmwareKeyAccordingToVersion(bootNotifMessage.firmwareVersion()),
        actualResponse.key());
    assertEquals("20", actualResponse.value());
    sentMessage = chargepointManager.processMessage(response);
    assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
    actualResponse = (ChangeConfigurationRequest16) sentMessage.orElseThrow();
    assertEquals(
        ConfigurationTranscriptor.CHARGEPOINT_IDENTITY
            .getOcpp16Key()
            .getFirmwareKeyAccordingToVersion(bootNotifMessage.firmwareVersion()),
        actualResponse.key());
    assertEquals("dépasse les bornes", actualResponse.value());
    if (System.getenv("FINAL_WS_SERVER_ADDRESS") != null) {
      sentMessage = chargepointManager.processMessage(response);
      assertEquals(ChangeConfigurationRequest16.class, sentMessage.orElseThrow().getClass());
      actualResponse = (ChangeConfigurationRequest16) sentMessage.orElseThrow();
      assertEquals(
          ConfigurationTranscriptor.NETWORK_PROFILE
              .getOcpp16Key()
              .getFirmwareKeyAccordingToVersion(bootNotifMessage.firmwareVersion()),
          actualResponse.key());
      assertEquals(System.getenv("FINAL_WS_SERVER_ADDRESS"), actualResponse.value());
    }
    sentMessage = chargepointManager.processMessage(response);
    assertEquals(ResetRequest16.class, sentMessage.orElseThrow().getClass());
    var resetRequest = (ResetRequest16) sentMessage.orElseThrow();
    assertEquals(ResetType.Hard, resetRequest.type());
    sentMessage = chargepointManager.processMessage(response);
    var finalSentMessage = sentMessage;
    assertThrows(NoSuchElementException.class, finalSentMessage::orElseThrow);
    assertEquals(
        Chargepoint.StatusProcess.FINISHED,
        chargepointManager.getCurrentChargepoint().getStatus());
  }
}
