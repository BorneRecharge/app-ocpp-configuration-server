package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.ChangeConfigurationRequest16;
import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.ChangeConfigurationResponse16;

/**
 * Defines the status used in {@link ChangeConfigurationResponse16}.<br>
 * It is used to respond after a {@link ChangeConfigurationRequest16}
 * It can be :<br>
 * - Accepted, the change has been accepted ;<br>
 * - Rejected, the change has been rejected because the value is wrong ;<br>
 * - RebootRequired, the change has been accepted but the chargepoint needs to reboot ;<br>
 * - NotSupported, the given key is not correct.
 */
public enum ConfigurationStatus {
  Accepted,
  Rejected,
  RebootRequired,
  NotSupported
}
