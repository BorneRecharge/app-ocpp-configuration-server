package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data;

/**
 * Reference key to a component-variable.
 *
 * @param name Name of the variable. Name should be taken from the list
 *             of standardized variable names whenever possible.<br>
 *             Case Insensitive. Strongly advised to use Camel Case.
 */
public record VariableType(String name) {}
