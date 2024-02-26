package fr.uge.chargepointconfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Component;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Package visibility, just for testing purpose.
 */
@Component
class FakeController {

  @PreAuthorize("isAuthenticated()") // TODO : should not be necessary
  String authenticated() {
    return "authenticated api";
  }

  @PreAuthorize("hasRole('Administrator')")
  String admin() {
    return "Administrator api";
  }

  @PreAuthorize("hasRole('Editor')")
  String editor() {
    return "Editor api";
  }

  @PreAuthorize("hasRole('Visualizer')")
  String visualizer() {
    return "Visualizer api";
  }
}

@SpringBootTest
//@RunWith(SpringRunner.class)
public class PermissionTest {

  @Autowired
  private FakeController fakeController;

  @Test
  @WithAnonymousUser
  void notAuthenticated() {
    assertThrows(AccessDeniedException.class, () -> fakeController.authenticated());
  }

  @Test
  @WithMockUser
  void authenticated() {
    assertDoesNotThrow(() -> fakeController.authenticated());
  }

  @Test
  @WithMockUser(roles = "Administrator")
  void authenticatedWithAdminRole() {
    assertDoesNotThrow(() -> fakeController.authenticated());
  }

  @Test
  @WithMockUser(roles = "RANDOM")
  void adminWithRandomRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.admin());
  }

  @Test
  @WithMockUser(roles = "Visualizer")
  void adminWithUserRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.admin());
  }

  @Test
  @WithMockUser(roles = "Administrator")
  void adminWithAdministratorRole() {
    assertDoesNotThrow(() -> fakeController.admin());
  }

  @Test
  @WithMockUser(roles = "RANDOM")
  void editorWithRandomRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.editor());
  }

  @Test
  @WithMockUser(roles = "Editor")
  void editorWithEditorRole() {
    assertDoesNotThrow(() -> fakeController.editor());
  }

  @Test
  @WithMockUser(roles = "Administrator")
  void editorWithAdministratorRole() {
    assertDoesNotThrow(() -> fakeController.editor());
  }

  @Test
  @WithMockUser(roles = "RANDOM")
  void visualizerWithRandomRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.visualizer());
  }

  @Test
  @WithMockUser(roles = "Editor")
  void visualizerWithEditorRole() {
    assertDoesNotThrow(() -> fakeController.visualizer());
  }

  @Test
  @WithMockUser(roles = "Visualizer")
  void visualizerWithVisualizerRole() {
    assertDoesNotThrow(() -> fakeController.visualizer());
  }

  @Test
  @WithMockUser(roles = "Administrator")
  void visualizerWithAdministratorRole() {
    assertDoesNotThrow(() -> fakeController.visualizer());
  }
}