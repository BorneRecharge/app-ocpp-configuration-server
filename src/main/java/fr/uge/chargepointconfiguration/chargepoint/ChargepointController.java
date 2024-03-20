package fr.uge.chargepointconfiguration.chargepoint;

import fr.uge.chargepointconfiguration.shared.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * A controller for the Chargepoint entity.
 */
@RequestMapping("/api/chargepoint")
@RestController
@Tag(name = "Chargepoint", description = "The chargepoint API")
public class ChargepointController {

  private final ChargepointService chargepointService;

  /**
   * ChargepointController's constructor.
   *
   * @param chargepointService   A ChargePointService.
   */
  @Autowired
  public ChargepointController(ChargepointService chargepointService) {
    this.chargepointService = chargepointService;
  }

  /**
   * Returns a list of all the chargepoint.
   *
   * @return A list of all the chargepoint.
   */
  @Operation(summary = "Get all the chargepoints")
  @ApiResponse(
      responseCode = "200",
      description = "Found all the chargepoints",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChargepointDto.class))
      })
  @GetMapping(value = "/all")
  @PreAuthorize("hasRole('VISUALIZER')")
  public List<ChargepointDto> getAllChargepoints() {
    return chargepointService.getAllChargepoints().stream()
        .map(Chargepoint::toDto)
        .toList();
  }

  /**
   * Returns a chargepoint according to the given id.<br>
   * It is empty if the repository could not find a chargepoint.
   *
   * @param id An int.
   * @return A chargepoint.
   */
  @Operation(summary = "Get a chargepoint by its id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the corresponding chargepoint",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChargepointDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "This chargepoint does not exist",
            content = @Content)
      })
  @GetMapping(value = "/{id}")
  @PreAuthorize("hasRole('VISUALIZER')")
  public ChargepointDto getChargepointById(
      @Parameter(description = "Id of the chargepoint your are looking for.") @PathVariable
          int id) {
    return chargepointService.getChargepointById(id).toDto();
  }

  /**
   * Returns a list of all the chargepoints.
   *
   * @return A list corresponding chargepoints.
   */
  @Operation(summary = "Search for chargepoints")
  @ApiResponse(
      responseCode = "200",
      description = "Found chargepoints",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ChargepointDto.class))
      })
  @GetMapping(value = "/search")
  @PreAuthorize("hasRole('VISUALIZER')")
  public PageDto<ChargepointDto> searchWithPage(
      @Parameter(description = "Desired size of the requested page.")
          @RequestParam(required = false, defaultValue = "10")
          int size,
      @Parameter(description = "Requested page.")
          @RequestParam(required = false, defaultValue = "0")
          int page,
      @Parameter(
              description =
                  "The column you want to sort by. Must be an attribute of the chargepoint.")
          @RequestParam(required = false, defaultValue = "id")
          String sortBy,
      @Parameter(description = "The order of the sort. must be \"asc\" or \"desc\"")
          @RequestParam(required = false, defaultValue = "asc")
          String order,
      @Parameter(description = "The request used to search.")
          @RequestParam(required = false, defaultValue = "")
          String request) {
    var total = chargepointService.countTotalWithFilter(request);
    var totalElement = chargepointService.count();

    var data = chargepointService
        .search(
            request, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
        .stream()
        .map(Chargepoint::toDto)
        .toList();

    return new PageDto<>(total, totalElement, page, size, data);
  }

  /**
   * Create a chargepoint.
   *
   * @param createChargepointDto All the necessary information for a chargepoint creation.
   * @return A chargepoint created with its information and its http result status.
   */
  @Operation(summary = "Create a chargepoint")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Chargepoint created",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChargepointDto.class)))
      })
  @PostMapping("/create")
  @PreAuthorize("hasRole('EDITOR')")
  public ResponseEntity<ChargepointDto> registerChargepoint(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The chargepoint to be sent to the controller.",
              required = true,
              content =
                  @Content(
                      examples =
                          @ExampleObject(
                              """
                  {
                    "serialNumber": "string",
                    "type": "string",
                    "constructor": "string",
                    "clientId": "string",
                    "configuration": -1
                  }
                  """)))
          @RequestBody
          CreateChargepointDto createChargepointDto) {
    var chargepointDto = chargepointService.save(createChargepointDto).toDto();
    return new ResponseEntity<>(chargepointDto, HttpStatus.CREATED);
  }

  /**
   * Returns a chargepoint according to the given id.<br>
   * It is empty if the repository could not find a chargepoint.
   *
   * @param id An int.
   * @return A chargepoint.
   */
  @Operation(summary = "Update a chargepoint by its id")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Updated the corresponding chargepoint",
        content = {
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ChargepointDto.class))
        }),
    @ApiResponse(
        responseCode = "404",
        description = "This chargepoint does not exist",
        content = @Content)
  })
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('EDITOR')")
  public ChargepointDto updateChargepoint(
      @Parameter(description = "Id of the chargepoint your are looking for.") @PathVariable int id,
      @RequestBody CreateChargepointDto createChargepointDto) {
    return chargepointService.update(id, createChargepointDto).toDto();
  }
}
