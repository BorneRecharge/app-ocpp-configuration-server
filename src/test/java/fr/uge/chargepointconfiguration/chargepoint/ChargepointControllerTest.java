package fr.uge.chargepointconfiguration.chargepoint;

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
class ChargepointControllerTest {
  @Autowired
  private MockMvc mvc;

  @Test
  @Disabled
  void getAllChargepoints() {}

  @Test
  @Disabled
  void getChargepointById() {}

  @Test
  @Disabled
  void searchChargepoints() {}

  @Test
  @Disabled
  void registerChargepoint() {}

  @Test
  @Disabled
  void updateChargepoint() {}

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void getPage() throws Exception {
    mvc.perform(get("/api/chargepoint/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "clientId")
            .queryParam("order", "desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(8)))
        .andExpect(jsonPath("$.totalElement", is(8)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].clientId", is("stéphane borne (l'historien)")))
        .andExpect(jsonPath("$.data[1].clientId", is("nom de la borne")));

    mvc.perform(get("/api/chargepoint/search")
            .queryParam("size", "2")
            .queryParam("page", "1")
            .queryParam("sortBy", "clientId")
            .queryParam("order", "desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(8)))
        .andExpect(jsonPath("$.totalElement", is(8)))
        .andExpect(jsonPath("$.page", is(1)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].clientId", is("les bornés")))
        .andExpect(jsonPath("$.data[1].clientId", is("elisabeth borne")));
  }

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void getPageWithFilterMultiple() throws Exception {
    mvc.perform(get("/api/chargepoint/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "clientId")
            .queryParam("order", "desc")
            .queryParam("request", "clientId:`les`,type:`Single`"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(1)))
        .andExpect(jsonPath("$.totalElement", is(8)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].clientId", is("les bornés")))
        .andExpect(jsonPath("$.data[0].type", is("Eve Single S-line")));
  }

  @Test
  @WithMockUser(roles = "VISUALIZER")
  void getPageWithFilter() throws Exception {
    mvc.perform(get("/api/chargepoint/search")
            .queryParam("size", "2")
            .queryParam("page", "0")
            .queryParam("sortBy", "clientId")
            .queryParam("order", "desc")
            .queryParam("request", "clientId:`borne`"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.total", is(6)))
        .andExpect(jsonPath("$.totalElement", is(8)))
        .andExpect(jsonPath("$.page", is(0)))
        .andExpect(jsonPath("$.size", is(2)))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[0].clientId", is("stéphane borne (l'historien)")))
        .andExpect(jsonPath("$.data[1].clientId", is("nom de la borne")));
  }
}
