package fr.uge.chargepointconfiguration.configuration;

import fr.uge.chargepointconfiguration.shared.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for the Configuration entity.
 */
@RestController
@RequestMapping("/api/configuration")
@Tag(name = "Configuration", description = "The configuration API")
public class ConfigurationController {

  private final ConfigurationService configurationService;

  /**
   * ConfigurationController's constructor.
   *
   * @param configurationService A ConfigurationService doing database manipulations.
   */
  @Autowired
  public ConfigurationController(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  /**
   * Returns a list of all configuration without the configuration.
   *
   * @return A list of all the configuration.
   */
  @Operation(summary = "Get all configuration")
  @ApiResponse(
      responseCode = "200",
      description = "Found all the configuration.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ConfigurationDto.class)))
  @GetMapping(value = "/all")
  @PreAuthorize("hasRole('VISUALIZER')")
  public List<ConfigurationDto> getAllConfiguration() {
    return configurationService.getAllConfigurations().stream()
        .map(Configuration::toDto)
        .toList();
  }

  /**
   * Return the configuration corresponding to the id parameter.
   *
   * @return The corresponding configuration.
   */
  @Operation(summary = "Get configuration by its id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the corresponding configuration",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ConfigurationDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "This configuration does not exist",
            content = @Content)
      })
  @GetMapping(value = "/{id}")
  @PreAuthorize("hasRole('VISUALIZER')") // For showing detailed infos
  public ConfigurationDto getConfigurationById(
      @Parameter(description = "Id of the configuration your are looking for.") @PathVariable
          int id) {
    return configurationService.getConfiguration(id).toDto();
  }

  /**
   * Create a configuration.
   *
   * @param createConfigurationDto All the necessary information for a configuration creation.
   * @return A configuration created with its information and its http result status.
   */
  @Operation(summary = "Create a configuration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Configuration created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ConfigurationDto.class)))
      })
  @PostMapping("/create")
  @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<ConfigurationDto> registerConfiguration(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The configuration to be sent to the controller.",
              required = true,
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              """
                        {
                          "name": "the name of the configuration",
                          "description": "A short description about the configuration",
                          "configuration": "{"1":"100"}",
                          "firmware": 1
                        }
                        """)))
          @RequestBody
          CreateConfigurationDto createConfigurationDto) {
    return new ResponseEntity<>(
        configurationService.save(createConfigurationDto), HttpStatus.CREATED);
  }

  /**
   * Update a configuration.
   *
   * @param createConfigurationDto All the necessary information for a configuration update.
   * @return A configuration created with its information and its http result status.
   */
  @Operation(summary = "Update a configuration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Configuration updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ConfigurationDto.class)))
      })
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<ConfigurationDto> updateConfiguration(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The configuration to be sent to the controller.",
              required = true,
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              """
                      {
                        "name": "the name of the configuration",
                        "description": "A short description about the configuration",
                        "configuration": "{"1":"100"}",
                        "firmware": 1
                      }
                      """)))
          @RequestBody
          CreateConfigurationDto createConfigurationDto,
      @PathVariable int id) {
    var configuration = configurationService.update(id, createConfigurationDto);
    return new ResponseEntity<>(configuration.toDto(), HttpStatus.OK);
  }

  /**
   * Returns a list of {@link ConfigurationTranscriptor}.
   *
   * @return list of {@link ConfigurationTranscriptor}.
   */
  @Operation(summary = "Get all fullName key configuration")
  @ApiResponse(
      responseCode = "200",
      description = "Found all fullName key configuration.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ConfigurationTranscriptorDto.class)))
  @GetMapping(value = "/transcriptor")
  @PreAuthorize("hasRole('VISUALIZER')")
  public List<ConfigurationTranscriptorDto> getAllConfigurationTranscriptor() {
    return Arrays.stream(ConfigurationTranscriptor.values())
        .filter(transcriptor -> transcriptor != ConfigurationTranscriptor.CHARGEPOINT_IDENTITY
            && transcriptor != ConfigurationTranscriptor.NETWORK_PROFILE)
        .map(ConfigurationTranscriptor::toDto)
        .toList();
  }

  /**
   * Search for {@link ConfigurationDto} with a pagination.
   *
   * @param size   Desired size of the requested page.
   * @param page   Requested page.
   * @param sortBy The column you want to sort by. Must be an attribute of
   *               the {@link ConfigurationDto}.
   * @param order  The order of the sort. Must be "asc" or "desc".
   * @return A page containing a list of {@link ConfigurationDto}
   */
  @Operation(summary = "Search for configurations")
  @ApiResponse(
      responseCode = "200",
      description = "Found configurations",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ConfigurationDto.class))
      })
  @GetMapping(value = "/search")
  @PreAuthorize("hasRole('VISUALIZER')")
  public PageDto<ConfigurationDto> searchWithPage(
      @Parameter(description = "Desired size of the requested page.")
          @RequestParam(required = false, defaultValue = "10")
          int size,
      @Parameter(description = "Requested page.")
          @RequestParam(required = false, defaultValue = "0")
          int page,
      @Parameter(
              description =
                  "The column you want to sort by. Must be an attribute of the configuration.")
          @RequestParam(required = false, defaultValue = "id")
          String sortBy,
      @Parameter(description = "The order of the sort. must be \"asc\" or \"desc\"")
          @RequestParam(required = false, defaultValue = "asc")
          String order,
      @Parameter(description = "The request used to search.")
          @RequestParam(required = false, defaultValue = "")
          String request) {
    var total = configurationService.countTotalWithFilter(request);
    var totalElement = configurationService.count();

    var data = configurationService
        .search(
            request, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
        .stream()
        .map(Configuration::toDto)
        .toList();

    return new PageDto<>(total, totalElement, page, size, data);
  }
}
