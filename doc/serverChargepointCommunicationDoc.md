# How does the chargepoints communicate with the server ?

## Overview

This document is about the process and the description of the server to chargepoints and chargepoints to server.
The processes will be explained with examples and according to the OCPP protocol (1.6).

This application is ready to accept other OCPP version such as 2.0.1.

## Warning

> 1 : If a field is required for our application, it will be marked like that *.
>
> 2 : The communication between the two machines is a request-response, meaning, for every request sent a response
> is expected.
>
> 3 : The OCPP 2.0.1 protocol is not yet implemented, see [Not implemented](#ocpp-201) for more explanation.
>
> 4 : Every time a status is mentionned, it is an enum defined by the OCPP protocol
> and will be highlighted like this : <ins>Test</ins>.

## Processes

### First boot

On startup, the charge point sends a **BootNotificationRequest** to the server containing these information :
- The chargepoint's constructor* ;
- The chargepoint's model* ;
- The chargepoint's serial number* ;
- The chargebox serial number ;
- The chargepoint's firmware version*.

After receiving this message, we search in the database a chargepoint with the same serial number and vendor.
Then, the server responds with a **BootNotificationResponse** to the charge point containing these information :
- The status (<ins>Accepted</ins> or <ins>Rejected</ins>)* ;
- The current time* ;
- The interval which the charge point should respect before sending a message*.

Then, the server would check the current state of the charge point before doing anything.

### Firmware process

If the chargepoint is in FIRMWARE mode in the database, the server will compare the firmware target
in the linked configuration of the chargepoint to the current chargepoint's firmware.

If the target firmware is the same as the current firmware,
we just skip this process and put the chargepoint into [CONFIGURATION mode](#configuration-process).

If the target firmware is not the same, we downgrade or upgrade the firmware, step by step.

> Note : Downgrading process is in progress ! It is not fully stable right now.

During the firmware process, the server will choose intermediary firmwares before installing the target firmware,
so the process is technically a loop until the target firmware has been installed.

The process is started by the server sending an **UpdateFirmwareRequest** containing :
- The URL (FTP or HTTP/S) for the firmware file (.fwi)* ;
- The retrieve date when the charge point should download the firmware*.

The chargepoint should respond with an **UpdateFirmwareResponse** which is an empty message.
It is like an ACK packet in a IP/TCP communication.

The chargepoint will sometimes send a **FirmwareStatusNotificationRequest** containing the status of the download.
The status can be :
- <ins>Downloaded</ins>, the firmware has been downloaded ;
- <ins>DownloadFailed</ins>, the firmware couldn't be downloaded ;
- <ins>Downloading</ins>, the chargepoint currently downloads the firmware ;
- <ins>Idle</ins>, the chargepoint is waiting for a process to finish before installing the firmware ;
- <ins>InstallationFailed</ins>, the firmware couldn't be installed on the charge point ;
- <ins>Installing</ins>, the chargepoint currently installs the firmware ;
- <ins>Installed</ins>, the firmware has been installed on the chargepoint.

If the status is <ins>DownloadFailed</ins>/<ins>InstallationFailed</ins>, all the processes are stopped,
and we consider the process as FAILED.

If the status is <ins>Installed</ins>,
we continue by rebooting the machine and continue by switching the chargepoint into the CONFIGURATION mode.

If the status is something else, we just wait for the confirmation of the installation of the firmware.

### Configuration process

If the chargepoint is in CONFIGURATION mode in the database, the server will compare the last update date in
the linked configuration and the last update date in the chargepoint table.

If the configuration date is more recent than the chargepoint date, we skip this process.
We consider the process to be FINISHED.

Else, we search for the key/value in the configuration, and we send n packets for n key/value.

The server sends a **ChangeConfigurationRequest** containing :
- The key* ;
- The value* ;

The charge point will respond with a **ChangeConfigurationResponse** containing a status.
This status can be :
- <ins>Accepted</ins>, the change has been accepted ;
- <ins>Rejected</ins>, the change has been rejected because the value is wrong ;
- <ins>RebootRequired</ins>, the change has been accepted but the chargepoint needs to reboot ;
- <ins>NotSupported</ins>, the given key is not correct.

If all the messages sent by the server are <ins>Accepted</ins> or <ins>RebootRequired</ins>,
the configuration is finished, and we send the last order containing the new server address,
and we reboot the chargepoint.

If not, we stop the process, and we switch the status of the chargepoint as FAILED.

## Other

### Bugs

### Not implemented

#### OCPP 2.0.1

The OCPP 2.0.1 protocol is not implemented because this application has been tested with Alfen BV's charge points
(Eve Single S-Line), and the implementation of the 2.0.1 protocol is not fully done by Alfen BV.

We could not find the correct *Component* object for the **SetVariablesRequest**, except a Component called "none"
for this chargepoint.

However, the component "none" does not seem to work when giving new value to a key, it will always respond by
a <ins>Rejected</ins>.

These components are the only components recognized by ALfen BV's chargepoints (Eve Single S-Line) with
a firmware newer than 6.1.

> Note : When marked by a *, it means the *Component* is an OCPP standard, see the
> [official documentation](https://openchargealliance.org/protocols/open-charge-point-protocol/#OCPP2.0.1).

- AlignedDataCtrlr* ;
- AuthCtrlr* ;
- AuthCacheCtrlr* ;
- ChargingStation* ;
- ClockCtrlr* ;
- DeviceDataCtrlr* ;
- DisplayMessageCtrlr* ;
- LocalAuthListCtrlr* ;
- MonitoringCtrlr* ;
- none ;
- OCPPCommCtrlr* ;
- ReservationCtrlr* ;
- SampledDataCtrlr* ;
- SecurityCtrlr* ;
- SmartChargingCtrlr* ;
- TariffCostCtrlr* ;
- TxCtrlr*.

