package fr.uge.chargepointconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Defines the web socket configuration.
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final String websocketPath;

  public WebSocketConfig(@Value("${websocket.path}") String websocketPath) {
    this.websocketPath = websocketPath;
  }

  /**
   * Register a new websocket handler.
   *
   * @param registry websocket handler registry.
   */
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new WebSocketHandler(), websocketPath)
            .setAllowedOrigins("*"); //TODO: maybe check CORS
  }

  /**
   * Returns the web socket current handler.
   *
   * @return WebSocketHandler.
   */
  @Bean
  public WebSocketHandler myHandler() {
    return new WebSocketHandler();
  }

}