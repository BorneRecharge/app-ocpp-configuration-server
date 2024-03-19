package fr.uge.chargepointconfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point to the application.<br>
 * It implements CommandLineRunner.
 */
@SpringBootApplication
public class ChargePointConfigurationApplication {
  /**
   * Launches the server by instantiating the application and running it.
   *
   * @param args String[].
   */
  public static void main(String[] args) {
    SpringApplication.run(ChargePointConfigurationApplication.class, args);
  }
}
