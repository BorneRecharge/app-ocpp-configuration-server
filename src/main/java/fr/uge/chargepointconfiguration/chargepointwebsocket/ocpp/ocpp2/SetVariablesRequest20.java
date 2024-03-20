package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.OcppMessageRequest;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp2.data.SetVariableData;
import java.util.List;

/**
 * Request sent by the server to change the charge point configuration.
 *
 * @param setVariableData {@link SetVariableData}.
 */
public record SetVariablesRequest20(List<SetVariableData> setVariableData)
    implements OcppMessageRequest {}
