package fr.uge.chargepointconfiguration.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityConfigTest {

  @Autowired
  private MockMvc mvc;

  @Test
  @WithAnonymousUser
  void loginAllowed() throws Exception {
    mvc.perform(get("/about")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  void unauthorizedApi() throws Exception {
    mvc.perform(get("/api")).andDo(print()).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void authorizedApi() throws Exception {
    mvc.perform(get("/api/user/all")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void notFound() throws Exception {
    mvc.perform(get("/notfound")).andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  @WithAnonymousUser
  void logoutPost() throws Exception {
    mvc.perform(post("/logout")).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithAnonymousUser
  void logoutGet() throws Exception {
    mvc.perform(get("/logout")).andDo(print()).andExpect(status().isNoContent());
  }

  @Test
  @WithAnonymousUser
  void loginGet() throws Exception {
    mvc.perform(get("/")).andDo(print()).andExpect(status().isOk());
  }
}
