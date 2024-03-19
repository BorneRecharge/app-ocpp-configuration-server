package fr.uge.chargepointconfiguration;

import fr.uge.chargepointconfiguration.chargepoint.ChargepointRepository;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ConfigurationServer;
import fr.uge.chargepointconfiguration.firmware.FirmwareRepository;
import fr.uge.chargepointconfiguration.logs.CustomLogger;
import java.net.InetSocketAddress;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point to the application.<br>
 * It implements CommandLineRunner.
 */
@SpringBootApplication
public class ChargepointconfigurationApplication implements CommandLineRunner {

  private final ChargepointRepository chargepointRepository;
  private final FirmwareRepository firmwareRepository;
  private final CustomLogger logger;
  private final String websocketUrl;
  private final int websocketPort;

  @Autowired
  public ChargepointconfigurationApplication(ChargepointRepository chargepointRepository,
                                             FirmwareRepository firmwareRepository,
                                             CustomLogger customLogger,
                                             @Value("${websocket.url}")
                                             String websocketUrl,
                                             @Value("${websocket.port}")
                                             int websocketPort) {
    this.chargepointRepository = chargepointRepository;
    this.firmwareRepository = Objects.requireNonNull(firmwareRepository);
    this.logger = customLogger;
    this.websocketUrl = websocketUrl;
    this.websocketPort = websocketPort;
  }


  /**
   * Launches the server by instantiating the application and running it.
   *
   * @param args String[].
   */
  public static void main(String[] args) {
    SpringApplication.run(ChargepointconfigurationApplication.class, args);
  }

  /**
   * Call on spring server start.
   *
   * @param args program arguments
   * @throws Exception invalid parameter
   */
  @Override
  public void run(String... args) throws Exception {
    Thread.ofPlatform().start(() -> {
      var server = new ConfigurationServer(
              new InetSocketAddress(websocketUrl, websocketPort),
              chargepointRepository,
              firmwareRepository,
              logger
      );
      server.setReuseAddr(true);
      server.run();
    });
  }
}
