package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data;

/**
 * Represents the tuple for changing the value of the component's variable.
 *
 * @param attributeValue Value to be assigned to attribute of variable.
 * @param component {@link Component}.
 * @param variable {@link VariableType}.
 */
public record SetVariableData(String attributeValue, Component component, VariableType variable) {}
