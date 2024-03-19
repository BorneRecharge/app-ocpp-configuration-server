package fr.uge.chargepointconfiguration.chargepointwebsocket;

import fr.uge.chargepointconfiguration.chargepoint.ChargepointRepository;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.MessageType;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessage;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppVersion;
import fr.uge.chargepointconfiguration.firmware.FirmwareRepository;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLog;
import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import fr.uge.chargepointconfiguration.tools.JsonParser;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * Configures the web socket server.
 */
public class ConfigurationServer extends WebSocketServer {
  private final HashMap<InetSocketAddress, ChargePointManager> chargePoints = new HashMap<>();
  private final ChargepointRepository chargepointRepository;
  private final FirmwareRepository firmwareRepository;
  private final CustomLogger logger;

  /**
   * {@link ConfigurationServer}'s constructor.
   *
   * @param address               The remote address to connect to.
   * @param chargepointRepository {@link ChargepointRepository}.
   * @param firmwareRepository    {@link FirmwareRepository}.
   * @param logger                {@link CustomLogger}.
   */
  public ConfigurationServer(InetSocketAddress address,
                             ChargepointRepository chargepointRepository,
                             FirmwareRepository firmwareRepository,
                             CustomLogger logger) {
    super(Objects.requireNonNull(address));
    this.chargepointRepository = Objects.requireNonNull(chargepointRepository);
    this.firmwareRepository = Objects.requireNonNull(firmwareRepository);
    this.logger = Objects.requireNonNull(logger);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    Objects.requireNonNull(conn);
    Objects.requireNonNull(handshake);
    logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
            "new connection to " + conn.getRemoteSocketAddress()));
    var ocppVersion = OcppVersion.parse(handshake.getFieldValue("Sec-Websocket-Protocol"));
    if (ocppVersion.isPresent()) {
      chargePoints.putIfAbsent(conn.getRemoteSocketAddress(),
              instantiate(ocppVersion.orElseThrow(), conn));
    } else {
      logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
              "Unknown OCPP version !"));
    }
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    Objects.requireNonNull(conn);
    logger.warn(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
            "closed "
                    + conn.getRemoteSocketAddress()
                    + " with exit code "
                    + code
                    + " additional info: "
                    + reason));
    var chargepoint = chargePoints.getOrDefault(conn.getRemoteSocketAddress(), null);
    if (chargepoint != null) {
      chargepoint.onDisconnection();
    }
    chargePoints.remove(conn.getRemoteSocketAddress());
    conn.close();
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    Objects.requireNonNull(conn);
    Objects.requireNonNull(message);
    var remote = conn.getRemoteSocketAddress();
    var webSocketMessage = WebSocketMessage.parse(message);
    if (webSocketMessage.isEmpty()) {
      logger.warn(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
              "failed to parse message from " + remote));
      return;
    }
    if (webSocketMessage.get().isRequest()) {
      logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
              "received request from "
                      + remote
                      + ": "
                      + message));
    } else {
      logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
              "received response from "
                      + remote
                      + ": "
                      + message));
    }
    chargePoints.get(conn.getRemoteSocketAddress())
            .processMessage(webSocketMessage.get());
  }

  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    Objects.requireNonNull(conn);
    Objects.requireNonNull(message);
    logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
            "received ByteBuffer from " + conn.getRemoteSocketAddress()));
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    // Keep the case if conn is null
    Objects.requireNonNull(ex);
    logger.error(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
            "an error occurred on connection "
                    + (conn == null ? "" : conn.getRemoteSocketAddress())
                    + " : "
                    + ex));
    if (conn != null) {
      var chargepoint = chargePoints.get(conn.getRemoteSocketAddress());
      if (chargepoint != null) {
        chargepoint.onError(ex);
      }
    }
  }

  @Override
  public void onStart() {
    logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
            "server started successfully: " + getAddress()));
  }

  /**
   * Instantiates the {@link ChargePointManager} according to the OCPP version.
   *
   * @param ocppVersion {@link OcppVersion}.
   * @param conn The web socket connection.
   * @return {@link ChargePointManager}.
   */
  private ChargePointManager instantiate(OcppVersion ocppVersion, WebSocket conn) {
    return new ChargePointManager(ocppVersion,
            (ocppMessage, chargePointManager) -> {
              switch (OcppMessage.ocppMessageToMessageType(ocppMessage)) {
                case REQUEST -> {
                  var request = new WebSocketRequestMessage(
                          MessageType.REQUEST.getCallType(),
                          chargePointManager.getCurrentId(),
                          WebSocketMessage
                                  .MessageTypeRequest
                                  .ocppMessageToEnum(ocppMessage),
                          JsonParser.objectToJsonString(ocppMessage)
                  );
                  chargePointManager.setPendingRequest(request);
                  conn.send(request.toString());
                  logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
                          "sent request to " + conn.getRemoteSocketAddress()
                                  + " : " + request));
                }
                case RESPONSE -> {
                  var response = new WebSocketResponseMessage(
                          MessageType.RESPONSE.getCallType(),
                          chargePointManager.getCurrentId(),
                          JsonParser.objectToJsonString(ocppMessage));
                  conn.send(
                          response.toString());
                  logger.info(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
                          "sent response to " + conn.getRemoteSocketAddress()
                                  + " : " + response));
                }
                default -> // ignore
                        logger.error(new TechnicalLog(TechnicalLogEntity.Component.BACKEND,
                                "tried to send an unknown packet"));
              }
            },
            chargepointRepository,
            firmwareRepository,
            logger);
  }
}