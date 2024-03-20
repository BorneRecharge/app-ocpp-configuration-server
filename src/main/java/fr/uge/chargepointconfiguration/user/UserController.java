package fr.uge.chargepointconfiguration.user;

import fr.uge.chargepointconfiguration.shared.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for user management.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "The user API")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * retrieve info in database for all users.
   *
   * @return Details about all the users.
   */
  @Operation(summary = "Get all users.")
  @ApiResponse(
      responseCode = "200",
      description = "Found all users",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserDto.class)))
  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public List<UserDto> getAllUsers() {
    return userService.getAllUsers().stream().map(User::toDto).toList();
  }

  /**
   * retrieve info in database for the current user.
   *
   * @return Details about the current user.
   */
  @Operation(summary = "Get the current authenticated user.")
  @ApiResponse(
      responseCode = "200",
      description = "Found the authenticated user",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserDto.class)))
  @GetMapping("/me")
  @PreAuthorize("hasRole('VISUALIZER')")
  public UserDto getAuthenticatedUser() {
    return userService.getAuthenticatedUser().toDto();
  }

  /**
   * Update the password of the user.
   *
   * @param changePasswordUserDto a ChangePasswordUserDto.
   * @return a ResponseEntity of UserDto.
   */
  @Operation(summary = "Update password")
  @ApiResponse(
      responseCode = "200",
      description = "Update the password of the current user",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserDto.class)))
  @PatchMapping("/updatePassword/{id}")
  @PreAuthorize("hasRole('VISUALIZER')")
  public ResponseEntity<UserDto> updatePassword(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Old and new password.",
              required = true)
          @RequestBody
          ChangePasswordUserDto changePasswordUserDto,
      @PathVariable int id) {
    var user = userService.updatePassword(id, changePasswordUserDto);
    return new ResponseEntity<>(user.toDto(), HttpStatus.OK);
  }

  /**
   * Update the role of the user.
   *
   * @param id the id of the user to change.
   * @param role the new role to be applied.
   * @return a ResponseEntity of UserDto.
   */
  @Operation(summary = "Update role")
  @ApiResponse(
      responseCode = "200",
      description = "Update the role of the current user",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserDto.class)))
  @PatchMapping("/{id}/role/{role}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public ResponseEntity<UserDto> updateRole(@PathVariable int id, @PathVariable User.Role role) {
    var user = userService.updateRole(id, role).toDto();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Search for {@link UserDto} with a pagination.
   *
   * @param size    Desired size of the requested page.
   * @param page    Requested page.
   * @param sortBy  The column you want to sort by. Must be an attribute of
   *                the {@link UserDto}.
   * @param request the request used to search
   * @param order   The order of the sort. Must be "asc" or "desc".
   * @return A page containing a list of {@link UserDto}
   */
  @Operation(summary = "Search for users")
  @ApiResponse(
      responseCode = "200",
      description = "Found users",
      content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
      })
  @GetMapping(value = "/search")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public PageDto<UserDto> searchWithPage(
      @Parameter(description = "Desired size of the requested page.")
          @RequestParam(required = false, defaultValue = "10")
          int size,
      @Parameter(description = "Requested page.")
          @RequestParam(required = false, defaultValue = "0")
          int page,
      @Parameter(description = "The column you want to sort by. Must be an attribute of the users.")
          @RequestParam(required = false, defaultValue = "id")
          String sortBy,
      @Parameter(description = "The order of the sort. must be \"asc\" or \"desc\"")
          @RequestParam(required = false, defaultValue = "asc")
          String order,
      @Parameter(description = "The request used to search.")
          @RequestParam(required = false, defaultValue = "")
          String request) {
    var total = userService.countWithFilters(request);
    var totalElement = userService.count();

    var data = userService
        .search(
            request, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sortBy)))
        .stream()
        .map(User::toDto)
        .toList();
    return new PageDto<>(total, totalElement, page, size, data);
  }

  /**
   * Get all available roles for users.
   *
   * @return a list of roles.
   */
  @Operation(summary = "Get all availables roles")
  @ApiResponse(
      responseCode = "200",
      description = "Found roles",
      content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = User.Role.class))
      })
  @GetMapping(value = "/allRoles")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public List<User.Role> getAllRoles() {
    return Arrays.stream(User.Role.values()).toList();
  }

  /**
   * Create a new user in the database.
   *
   * @param createUserDto the user to create.
   * @return a ResponseEntity of the user.
   */
  @Operation(summary = "Create new user")
  @ApiResponse(
      responseCode = "201",
      description = "User created",
      content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
      })
  @PostMapping(value = "/create")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public ResponseEntity<UserDto> addUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "JSON with all parameters of the new user.",
              required = true)
          @RequestBody
          CreateUserDto createUserDto) {
    var user = userService.createUser(createUserDto);
    return new ResponseEntity<>(user.toDto(), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
