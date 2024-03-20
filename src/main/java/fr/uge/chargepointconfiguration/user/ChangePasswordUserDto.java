package fr.uge.chargepointconfiguration.user;

/**
 * DTO to update password of the user in database.
 *
 * @param oldPassword old password of the user
 * @param newPassword new password of the user
 */
public record ChangePasswordUserDto(String oldPassword, String newPassword) {}
