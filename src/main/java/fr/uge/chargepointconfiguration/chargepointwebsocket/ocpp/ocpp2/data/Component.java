package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data;

/**
 * A physical or logical component of the machine.
 *
 * @param name Name of the component. Name should be taken
 *             from the list of standardized components names whenever possible.<br>
 *             Case Insensitive. Strongly advised to use Camel Case.
 */
public record Component(String name) {}
