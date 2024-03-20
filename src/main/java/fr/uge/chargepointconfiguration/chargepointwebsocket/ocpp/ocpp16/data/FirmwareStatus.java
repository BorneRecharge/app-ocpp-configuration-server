package fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.data;

import fr.uge.chargepointconfiguration.chargepointwebsocket.ocpp.ocpp16.FirmwareStatusNotificationRequest16;

/**
 * Defines the status used in {@link FirmwareStatusNotificationRequest16}.<br>
 * It can be :<br>
 * - Downloaded, the firmware has been downloaded ;<br>
 * - DownloadFailed, the firmware couldn't be downloaded ;<br>
 * - Downloading, the chargepoint currently downloads the firmware ;<br>
 * - Idle, the chargepoint is waiting for a process to finish before installing the firmware ;<br>
 * - InstallationFailed, the firmware couldn't be installed on the chargepoint ;<br>
 * - Installing, the chargepoint currently installs the firmware ;<br>
 * - Installed, the firmware has been installed on the chargepoint.
 */
public enum FirmwareStatus {
  Downloaded,
  DownloadFailed,
  Downloading,
  Idle,
  InstallationFailed,
  Installing,
  Installed
}
