package fr.uge.chargepointconfiguration.logs.technical;

import fr.uge.chargepointconfiguration.logs.sealed.TechnicalLogEntity;
import fr.uge.chargepointconfiguration.shared.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for technical log.
 */
@RequestMapping("/api/log/technical")
@RestController
@Tag(name = "Technical log", description = "The technical log API")
public class TechnicalLogController {

  private final TechnicalLogService technicalLogService;

  /**
   * TechnicalLogController's constructor.
   *
   * @param technicalLogService a TechnicalLogService.
   */
  @Autowired
  public TechnicalLogController(TechnicalLogService technicalLogService) {
    this.technicalLogService = technicalLogService;
  }

  /**
   * Returns a list of technical logs according to the given component and criticality.
   *
   * @param component   a type of component of the system.
   * @param level a {@link Level}.
   * @return a list of technical logs by component and criticality.
   */
  @Operation(summary = "Get a list of logs by its component and level")
  @ApiResponse(
      responseCode = "200",
      description = "Found the list of technical logs",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TechnicalLogDto.class))
      })
  @GetMapping(value = "/{component}/{level}")
  @PreAuthorize("hasRole('EDITOR')")
  public List<TechnicalLogDto> getTechnicalLogByComponentAndLevel(
      @Parameter @PathVariable TechnicalLogEntity.Component component,
      @Parameter @PathVariable Level level) {
    return technicalLogService.getTechnicalLogByComponentAndLevel(component, level).stream()
        .map(TechnicalLogEntity::toDto)
        .toList();
  }

  /**
   * Search for {@link TechnicalLogDto} with a pagination.
   *
   * @param size Desired size of the requested page.
   * @param page Requested page.
   * @param sortBy The column you want to sort by. Must be an attribute of
   *               the {@link TechnicalLogDto}.
   * @param order The order of the sort. Must be "asc" or "desc".
   * @return A page containing a list of {@link TechnicalLogDto}
   */
  @Operation(summary = "Search for technical logs")
  @ApiResponse(
      responseCode = "200",
      description = "Found technical logs",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TechnicalLogDto.class))
      })
  @GetMapping(value = "/search")
  @PreAuthorize("hasRole('EDITOR')")
  public PageDto<TechnicalLogDto> getPage(
      @Parameter(description = "Desired size of the requested page.")
          @RequestParam(required = false, defaultValue = "10")
          int size,
      @Parameter(description = "Requested page.")
          @RequestParam(required = false, defaultValue = "0")
          int page,
      @Parameter(
              description =
                  "The column you want to sort by. Must be an attribute of the technical log.")
          @RequestParam(required = false, defaultValue = "id")
          String sortBy,
      @Parameter(description = "The order of the sort. must be \"asc\" or \"desc\"")
          @RequestParam(required = false, defaultValue = "asc")
          String order,
      @Parameter(description = "The request used to search.")
          @RequestParam(required = false, defaultValue = "")
          String request) {
    var total = technicalLogService.countTotalWithFilter(request);
    var totalElement = technicalLogService.count();

    var data = technicalLogService
        .search(
            request, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
        .stream()
        .map(TechnicalLogEntity::toDto)
        .toList();

    return new PageDto<>(total, totalElement, page, size, data);
  }
}
