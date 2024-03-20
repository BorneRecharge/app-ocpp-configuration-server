package fr.uge.chargepointconfiguration.user;

/**
 * Dto to update the role of the user.
 *
 * @param id of the user
 * @param role the new role of the user
 */
public record ChangeRoleUserDto(int id, User.Role role) {}
