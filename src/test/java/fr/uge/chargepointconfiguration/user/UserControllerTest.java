package fr.uge.chargepointconfiguration.user;

import static fr.uge.chargepointconfiguration.tools.JsonParser.objectToJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
  @Autowired
  private MockMvc mvc;

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getAllUsers() throws Exception {
    mvc.perform(get("/api/user/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(4)));
  }

  @Test
  @WithUserDetails("admin@email")
  void getAuthenticatedUser() throws Exception {
    mvc.perform(get("/api/user/me"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.email", is("admin@email")))
        .andExpect(jsonPath("$.lastName", is("adminLastName")))
        .andExpect(jsonPath("$.firstName", is("adminFirstName")))
        .andExpect(jsonPath("$.role", is("ADMINISTRATOR")))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  @WithUserDetails("admin@email")
  void updatePassword() throws Exception {
    mvc.perform(patch("/api/user/updatePassword/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJsonString(new ChangePasswordUserDto("password", "_Azerty123_"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.email", is("admin@email")))
        .andExpect(jsonPath("$.lastName", is("adminLastName")))
        .andExpect(jsonPath("$.firstName", is("adminFirstName")))
        .andExpect(jsonPath("$.role", is("ADMINISTRATOR")))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  @WithUserDetails("admin@email")
  void updatePasswordBadFormatPassword() throws Exception {
    mvc.perform(patch("/api/user/updatePassword/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJsonString(new ChangePasswordUserDto("password", "azerty"))))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails("admin@email")
  void updatePasswordBadPassword() throws Exception {
    mvc.perform(patch("/api/user/updatePassword/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectToJsonString(new ChangePasswordUserDto("WRONG_PASSWORD_HERE", "azerty"))))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails("admin@email")
  void updateRoleOwner() throws Exception {
    mvc.perform(patch("/api/user/1/role/VISUALIZER")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails("admin@email")
  void updateRole() throws Exception {
    mvc.perform(patch("/api/user/4/role/ADMINISTRATOR"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.role", is("ADMINISTRATOR")));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getPage() throws Exception {
    mvc.perform(get("/api/user/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "email")
            .queryParam("order", "desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(4)))
        .andExpect(jsonPath("$.totalElement", is(4)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].email", is("visualizer@email")))
        .andExpect(jsonPath("$.data[1].email", is("random@notEmail")));

    mvc.perform(get("/api/user/search")
            .queryParam("size", "2")
            .queryParam("page", "1")
            .queryParam("sortBy", "email")
            .queryParam("order", "desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(4)))
        .andExpect(jsonPath("$.totalElement", is(4)))
        .andExpect(jsonPath("$.page", is(1)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].email", is("editor@email")))
        .andExpect(jsonPath("$.data[1].email", is("admin@email")));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getPageWithFilter() throws Exception {
    mvc.perform(get("/api/user/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "email")
            .queryParam("order", "desc")
            .queryParam("request", "email:`notEmail`"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(1)))
        .andExpect(jsonPath("$.totalElement", is(4)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].email", is("random@notEmail")));

    mvc.perform(get("/api/user/search")
            .queryParam("size", "2")
            .queryParam("page", "1")
            .queryParam("sortBy", "email")
            .queryParam("order", "desc")
            .queryParam("request", "email:`notEmail`"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(1)))
        .andExpect(jsonPath("$.totalElement", is(4)))
        .andExpect(jsonPath("$.page", is(1)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(0)));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void getAllRoles() throws Exception {
    mvc.perform(get("/api/user/allRoles"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]", is("VISUALIZER")))
        .andExpect(jsonPath("$[1]", is("EDITOR")))
        .andExpect(jsonPath("$[2]", is("ADMINISTRATOR")));
  }

  @Test
  @WithUserDetails("admin@email")
  void delete() throws Exception {
    mvc.perform(MockMvcRequestBuilders.delete("/api/user/2"))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.lastName").doesNotExist())
        .andExpect(jsonPath("$.firstName").doesNotExist())
        .andExpect(jsonPath("$.role").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist());

    mvc.perform(get("/api/user/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void addUser() throws Exception {
    mvc.perform(post("/api/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJsonString(new CreateUserDto(
                "newFirstName", "newLastName", "newEmail", "password", User.Role.EDITOR))))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(6)))
        .andExpect(jsonPath("$.email", is("newEmail")))
        .andExpect(jsonPath("$.lastName", is("newLastName")))
        .andExpect(jsonPath("$.firstName", is("newFirstName")))
        .andExpect(jsonPath("$.role", is("EDITOR")))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void addUserEmailAlreadyExist() throws Exception {
    mvc.perform(post("/api/user/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJsonString(new CreateUserDto(
                "newFirstName", "newLastName", "admin@email", "password", User.Role.EDITOR))))
        .andExpect(status().isConflict());
  }
}
