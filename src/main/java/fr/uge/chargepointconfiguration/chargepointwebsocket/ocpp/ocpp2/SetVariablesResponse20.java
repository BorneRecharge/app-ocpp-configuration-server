package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageResponse;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data.SetVariableResult;
import java.util.List;

/**
 * Request sent by the server to change the charge point configuration.
 *
 * @param setVariableResult {@link SetVariableResult}.
 */
public record SetVariablesResponse20(List<SetVariableResult> setVariableResult)
    implements OcppMessageResponse {}
