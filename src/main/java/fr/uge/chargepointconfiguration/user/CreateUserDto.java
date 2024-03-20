package fr.uge.chargepointconfiguration.user;

/**
 * DTO to create an user in the database.
 *
 * @param firstName first name of the user.
 * @param lastName last name of the user.
 * @param email email of the user.
 * @param password password encrypted of the user.
 * @param role role of the user.
 */
public record CreateUserDto(
    String firstName, String lastName, String email, String password, User.Role role) {}
