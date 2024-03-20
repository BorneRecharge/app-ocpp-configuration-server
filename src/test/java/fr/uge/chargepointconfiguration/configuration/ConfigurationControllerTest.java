package fr.uge.chargepointconfiguration.configuration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConfigurationControllerTest {
  @Autowired
  private MockMvc mvc;

  @Test
  @Disabled
  void getAllConfiguration() {}

  @Test
  @Disabled
  void getConfigurationById() {}

  @Test
  @Disabled
  void registerConfiguration() {}

  @Test
  @Disabled
  void updateConfiguration() {}

  @Test
  @Disabled
  void getAllConfigurationTranscriptor() {}

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void getPage() throws Exception {
    mvc.perform(get("/api/configuration/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "lastEdit")
            .queryParam("order", "asc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(5)))
        .andExpect(jsonPath("$.totalElement", is(5)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].id", is(5)))
        .andExpect(jsonPath("$.data[1].id", is(2)));

    mvc.perform(get("/api/configuration/search")
            .queryParam("size", "2")
            .queryParam("page", "1")
            .queryParam("sortBy", "lastEdit")
            .queryParam("order", "asc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(5)))
        .andExpect(jsonPath("$.totalElement", is(5)))
        .andExpect(jsonPath("$.page", is(1)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].id", is(3)))
        .andExpect(jsonPath("$.data[1].id", is(4)));
  }

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void getPageWithFilterMultiple() throws Exception {
    mvc.perform(get("/api/configuration/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "lastEdit")
            .queryParam("order", "asc")
            .queryParam("request", "configuration:`{}`,name:`vide`"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(1)))
        .andExpect(jsonPath("$.totalElement", is(5)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].id", is(3)));
  }

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void getPageWithFilter() throws Exception {
    mvc.perform(get("/api/configuration/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "lastEdit")
            .queryParam("order", "asc")
            .queryParam("request", "id:`1`"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(1)))
        .andExpect(jsonPath("$.totalElement", is(5)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].id", is(1)));
  }
}
