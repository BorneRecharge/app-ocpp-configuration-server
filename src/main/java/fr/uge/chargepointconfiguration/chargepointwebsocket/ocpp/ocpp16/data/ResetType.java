package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.ResetRequest16;

/**
 * Defines the status used in {@link ResetRequest16}.<br>
 * It can be :<br>
 * - Soft, the reboot will be done after all transactions are done ;<br>
 * - Hard, the reboot will be done as soon as possible.
 */
public enum ResetType {
  Hard,
  Soft
}
