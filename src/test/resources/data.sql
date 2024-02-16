INSERT INTO type_allowed  (id_type_allowed, constructor, type) VALUES (1, 'Alfen', 'Eve Single S-line');
INSERT INTO firmware (id_firmware, url, major_version, minor_version, constructor) VALUES (1, 'www.alfen.com', 3, 2, 'Alfen');
INSERT INTO compatibility (id_firmware, id_type_allowed) VALUES (1, 1);
INSERT INTO status (id_status, error) VALUES (1, '');
INSERT INTO chargepoint (id_chargepoint, serial_number_chargepoint, type, constructor, client_id, server_address, configuration, id_status, id_firmware) VALUES (1, 'alfen_serial_number', 'mega-type', 'alfen', 'COMMUNIAU_1', 'localhost', '{json}', 1, 1);