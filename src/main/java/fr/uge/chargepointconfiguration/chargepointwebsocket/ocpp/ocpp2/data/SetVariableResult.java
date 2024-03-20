package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * For every changed configuration, the machine sends this.<br>
 * This class represents the status of a variable change.
 *
 * @param attributeStatus Result status of setting the variable.
 * @param component {@link Component}.
 * @param variable {@link VariableType}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SetVariableResult(
    String attributeStatus, Component component, VariableType variable) {}
