package fr.uge.chargepointconfiguration.firmware;

import fr.uge.chargepointconfiguration.shared.PageDto;
import fr.uge.chargepointconfiguration.typeallowed.TypeAllowed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
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
 * A controller for the firmware entity.
 */
@RequestMapping("/api/firmware")
@RestController
@Tag(name = "Firmware", description = "The firmware API")
public class FirmwareController {

  private final FirmwareService firmwareService;

  /**
   * FirmwareController's constructor.
   *
   * @param firmwareService A FirmwareRepository.
   */
  @Autowired
  public FirmwareController(FirmwareService firmwareService) {
    this.firmwareService = firmwareService;
  }

  /**
   * Returns all the firmwares.
   *
   * @return A list of all the firmwares.
   */
  @Operation(summary = "Get all the firmwares")
  @ApiResponse(
      responseCode = "200",
      description = "Found all the firmwares",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = FirmwareDto.class)))
  @GetMapping(value = "/all")
  @PreAuthorize("hasRole('EDITOR')")
  public List<FirmwareDto> getAllFirmwares() {
    return firmwareService.getAllFirmwares().stream().map(Firmware::toDto).toList();
  }

  /**
   * Returns a firmware according to the given id.<br>
   * The optional will be empty if a firmware could not be found.
   *
   * @param id An int.
   * @return An optional of firmware.
   */
  @Operation(summary = "Get a firmware by its id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the firmware",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FirmwareDto.class))),
        @ApiResponse(responseCode = "404", description = "This firmware does not exist")
      })
  @GetMapping(value = "/{id}")
  @PreAuthorize("hasRole('EDITOR')")
  public FirmwareDto getFirmwareById(
      @Parameter(description = "id of firmware to be searched") @PathVariable int id) {
    return firmwareService.getFirmwareById(id).toDto();
  }

  /**
   * Search for {@link FirmwareDto} with a pagination.
   *
   * @param size   Desired size of the requested page.
   * @param page   Requested page.
   * @param sortBy The column you want to sort by. Must be an attribute of
   *               the {@link FirmwareDto}.
   * @param order  The order of the sort. Must be "asc" or "desc".
   * @return A page containing a list of {@link FirmwareDto}
   */
  @Operation(summary = "Search for configurations")
  @ApiResponse(
      responseCode = "200",
      description = "Found configurations",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = FirmwareDto.class))
      })
  @GetMapping(value = "/search")
  @PreAuthorize("hasRole('EDITOR')")
  public PageDto<FirmwareDto> getPage(
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
    var total = firmwareService.countTotalWithFilter(request);
    var totalElement = firmwareService.count();

    var data = firmwareService
        .search(
            request, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
        .stream()
        .map(entity -> new FirmwareDto(
            entity.getId(),
            entity.getUrl(),
            entity.getVersion(),
            entity.getConstructor(),
            entity.getTypesAllowed().stream().map(TypeAllowed::toDto).collect(Collectors.toSet())))
        .toList();

    return new PageDto<>(total, totalElement, page, size, data);
  }

  /**
   * Create a firmware.
   *
   * @param createFirmwareDto All the necessary information for a firmware creation.
   * @return A firmware created with its information and its http result status.
   */
  @Operation(summary = "Create a firmware")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Firmware created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateFirmwareDto.class)))
      })
  @PostMapping("/create")
  @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<FirmwareDto> registerConfiguration(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The firmware to be sent to the controller.",
              required = true,
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              """
                                  {
                                    "url": "http://exemple.com",
                                    "version": "6.0.54",
                                    "constructor": "Alfen BV",
                                    "typesAllowed": [{
                                      id: 1,
                                      constructor: "Alfen BV"
                                      type: "NGX9"
                                    },{
                                      id: 2,
                                      constructor: "Alfen BV"
                                      type: "NGX99"
                                    }]
                                  }
                                  """)))
          @RequestBody
          CreateFirmwareDto createFirmwareDto) {
    return new ResponseEntity<>(firmwareService.save(createFirmwareDto), HttpStatus.CREATED);
  }

  /**
   * Update a firmware.
   *
   * @param createFirmwareDto All the necessary information for a firmware update.
   * @return A firmware updated by the given information and its http result status.
   */
  @Operation(summary = "Update a firmware")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Firmware updated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateFirmwareDto.class)))
      })
  @PatchMapping("/update/{id}")
  @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<FirmwareDto> updateConfiguration(
      @Parameter(description = "Id of the firmware your are looking for.") @PathVariable int id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The firmware to be sent to the controller.",
              required = true,
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              """
                                  {
                                    "url": "http://exemple.com",
                                    "version": "6.0.54",
                                    "constructor": "Alfen BV",
                                    "typesAllowed": [
                                      {
                                        id: 1,
                                        constructor: "Alfen BV"
                                        type: "NGX9"
                                      },
                                      {
                                        id: 2,
                                        constructor: "Alfen BV"
                                        type: "NGX99"
                                      }
                                    ]
                                  }
                                  """)))
          @RequestBody
          CreateFirmwareDto createFirmwareDto) {
    var firmware = firmwareService.update(id, createFirmwareDto);
    return new ResponseEntity<>(firmware.toDto(), HttpStatus.OK);
  }
}
