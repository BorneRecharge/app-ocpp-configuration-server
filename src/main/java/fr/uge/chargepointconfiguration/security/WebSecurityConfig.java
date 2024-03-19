package fr.uge.chargepointconfiguration.security;

import fr.uge.chargepointconfiguration.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for endpoints of backend.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

  @Bean
  SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http
          .authorizeHttpRequests(authorize ->
                authorize.requestMatchers("/api/**").authenticated()
                      // allow React to access its files
                      .requestMatchers(
                        "/index.html",
                        "/static/**",
                        "/manifest.json",
                        "/assets/**")
                      .permitAll()
                      .requestMatchers("/", "/logout", "/about").permitAll()
                      .requestMatchers("/ocpp/**").permitAll()
                      .anyRequest().authenticated()
          )
          .formLogin(formLogin -> formLogin.loginPage("/").permitAll()
                .failureUrl("/?failed")
                .defaultSuccessUrl("/home", true)
                // see : https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configurers/AbstractAuthenticationFilterConfigurer.html#defaultSuccessUrl(java.lang.String,boolean)
                .loginProcessingUrl("/authentication/login/process"))
          .csrf(AbstractHttpConfigurer::disable) // TODO csrf propre
          .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  static GrantedAuthorityDefaults grantedAuthorityDefaults() {
    return new GrantedAuthorityDefaults(""); // none
  }

  @Bean
  static RoleHierarchy roleHierarchy() {
    var hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("""
          ROLE_ADMINISTRATOR > ROLE_EDITOR
          ROLE_EDITOR > ROLE_VISUALIZER
          """);
    return hierarchy;
  }

  // required because using method security
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler expressionHandler =
          new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(roleHierarchy);
    return expressionHandler;
  }

  @Bean
  UserDetailsService userDetailsService(UserRepository userRepository) {
    return new UserDetailsServiceImpl(userRepository);
  }

  /**
   * Provides the authentication service for the app to let users connects thanks to their username.
   *
   * @param userRepository The user's repository.
   * @return An authentication provider for the user.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserRepository userRepository) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService(userRepository));
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
