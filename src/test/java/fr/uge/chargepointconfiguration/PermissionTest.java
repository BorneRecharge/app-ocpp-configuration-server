package fr.uge.chargepointconfiguration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Component;

/**
 * Package visibility, just for testing purpose.
 */
@Component
class FakeController {

  @PreAuthorize("isAuthenticated()") // TODO : should not be necessary
  String authenticated() {
    return "authenticated api";
  }

  @PreAuthorize("hasRole('ADMINISTRATOR')")
  String admin() {
    return "Administrator api";
  }

  @PreAuthorize("hasRole('EDITOR')")
  String editor() {
    return "Editor api";
  }

  @PreAuthorize("hasRole('VISUALIZER')")
  String visualizer() {
    return "Visualizer api";
  }
}

@SpringBootTest
// @RunWith(SpringRunner.class)
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
  @WithMockUser(roles = "ADMINISTRATOR")
  void authenticatedWithAdminRole() {
    assertDoesNotThrow(() -> fakeController.authenticated());
  }

  @Test
  @WithMockUser(roles = "RANDOM")
  void adminWithRandomRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.admin());
  }

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void adminWithUserRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.admin());
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void adminWithAdministratorRole() {
    assertDoesNotThrow(() -> fakeController.admin());
  }

  @Test
  @WithMockUser(roles = "RANDOM")
  void editorWithRandomRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.editor());
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  void editorWithEditorRole() {
    assertDoesNotThrow(() -> fakeController.editor());
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void editorWithAdministratorRole() {
    assertDoesNotThrow(() -> fakeController.editor());
  }

  @Test
  @WithMockUser(roles = "RANDOM")
  void visualizerWithRandomRole() {
    assertThrows(AccessDeniedException.class, () -> fakeController.visualizer());
  }

  @Test
  @WithMockUser(roles = "EDITOR")
  void visualizerWithEditorRole() {
    assertDoesNotThrow(() -> fakeController.visualizer());
  }

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void visualizerWithVisualizerRole() {
    assertDoesNotThrow(() -> fakeController.visualizer());
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void visualizerWithAdministratorRole() {
    assertDoesNotThrow(() -> fakeController.visualizer());
  }
}
