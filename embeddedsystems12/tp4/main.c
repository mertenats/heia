/**
 * Copyright 2014 University of Applied Sciences Western Switzerland / Fribourg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Project:	EIA-FR / Embedded Systems 1 Laboratory
 *
 * Abstract: 	TP4 - Introduction to C programming and I2C bus interface
 *
 * Purpose:	This module is simple program to test and show the functionality
 *		of the I2C bus device driver and the I2C thermometer LM75.
 *
 * Autĥor:	<Samuel Mertenat>
 * Date:	<25.12.2014>
 */

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <imx27_serial.h>

#include "i2c.h"
#include "display_7seg.h"

/* LM75 Internal register structure */
/* Documentation: 01_LM75.pdf - 1.10-1 - p10-1 */
#define TEMPERATURE_REGISTER 		0		// Temperature register (read)
#define CONFIGURATION_REGISTER 		1		// Configuration register (read-write, sets operating modes)
#define T_HYST_SET_POINT_REGISTER 	2		// T Hyst set point register (read-write)
#define T_OS_SET_POINT_REGISTER 	3		// T Os set point register (read-write)

/* ------------------------------------------------------------------------- */

static inline const char* status2str(int status) {
	return status ? "nok" : "ok";
}

/* Method to read the temperature from the Temperature register */
static void get_temperature(int* status, uint8_t LM75_chip_id, uint8_t* temperature, uint8_t* comma) {
	// reads the Temperature register and stores the value
	// bits 7-15: temperature data; bit 15: sign; bit 7: comma -> 0 or 5
	*status = i2c_read(LM75_chip_id, TEMPERATURE_REGISTER, &temperature[0], 2);
	if (temperature[1] == 0) {
		*comma = 0;
	} else {
		*comma = 5;
	}
}
/* ------------------------------------------------------------------------- */

int main() {
	printf("\n");
	printf("EIA-FR - Embedded Systems 1 Laboratory\n");
	printf("TP4: I2C Device Driver\n");
	printf("     Interface to I2C Thermometer\n");
	printf("\n");

	printf("FPGA initialization\n");
	display_fpga_init();

	printf("I2C initialization\n");
	i2c_init();	// initialization of the I2C bus

	printf("Discover chip id of all the devices connected to the i2c bus\n");
	uint8_t LM75_chip_id = 0;
	for (uint8_t chip_id = 0; chip_id < 128; chip_id++) {
		if (i2c_probe(chip_id)) {
			/* Documentation: 01_LM75.pdf - 1.3 - p8 */
			/* The LM75 has a 7-bit slave address. The 4 mst bits of the slave address are hard wired inside the LM75 and are “1001”. */
			/* 0b1001000 < chip id > 0b1001111 (0x48 according to the documentation */
			if (0x48 <= chip_id && chip_id <= 0x4F) {
				LM75_chip_id = chip_id;
				printf("  chip id=0x%02x found (LM75)\n", chip_id);
			} else
				printf("  chip id=0x%02x found\n", chip_id);
		}
	}

	int status = 0;
	uint8_t config = 0;
	uint8_t temperature[2];		// temperature register is based on 16 bits
	uint8_t comma = 0;			// will contains 0 or 5 (depends on the bit 9 from the left)
	uint8_t thys[2];
	uint8_t tos [2];

	status = i2c_read(LM75_chip_id, CONFIGURATION_REGISTER, &config, 1);
	printf("\nLM75 thermometer device\n");
	printf("  configuration: read: status=%s, data=%d\n", status2str(status), config);

	//status = i2c_read(LM75_chip_id, TEMPERATURE_REGISTER, &temperature[0], 2);
	get_temperature(&status, LM75_chip_id, &temperature[0], &comma);
	printf("  temperature:   read: status=%s, data=%d,%d\n", status2str(status), temperature[0], comma);

	status = i2c_read(LM75_chip_id, T_HYST_SET_POINT_REGISTER, &thys[0], 2);
	printf("  Thys:    configured: status=%s, data=%d,%d\n", status2str(status), thys[0], 0);
	thys[0] = 18;	// limit for the lowest temperature
	status = i2c_write(LM75_chip_id,T_HYST_SET_POINT_REGISTER,&thys[0],2);
	printf("              written: status=%s, data=%d,%d\n", status2str(status), thys[0], 0);
	status = i2c_read(LM75_chip_id, T_HYST_SET_POINT_REGISTER, &thys[0], 2);
	printf("            read back: status=%s, data=%d,%d\n", status2str(status), thys[0], 0);

	status = i2c_read(LM75_chip_id, T_OS_SET_POINT_REGISTER, &tos[0], 2);
	printf("  Tos:     configured: status=%s, data=%d,%d\n", status2str(status), tos[0], 0);
	tos[0] = 25;	// limit for the highest temperature
	status = i2c_write(LM75_chip_id,T_OS_SET_POINT_REGISTER,&tos[0],2);
	printf("              written: status=%s, data=%d,%d\n", status2str(status), tos[0], 0);
	status = i2c_read(LM75_chip_id, T_OS_SET_POINT_REGISTER, &tos[0], 2);
	printf("            read back: status=%s, data=%d,%d\n", status2str(status), tos[0], 0);

	printf("\nTemperature will be read every 30 [s] and display on the 7-segment\n");
	printf("press any key to print it on the console\n");

	int timer = 2250000;	// ~30 [s]
	while (1) {
		display_val(temperature[0]);
		if (imx27_serial_tstc()) {		// if a keyboard key is pressed ...
			getchar();
			get_temperature(&status, LM75_chip_id, &temperature[0], &comma);
			printf("  LM75 temperature=%d,%d (%s)\n", temperature[0], comma, status2str(status));
		}
		if (--timer == 0) {		// decremential timer
			get_temperature(&status, LM75_chip_id, &temperature[0], &comma);
			printf("  LM75 temperature=%d,%d (%s) (automatically updated)\n", temperature[0], comma, status2str(status));
			timer = 2250000;
		}
	}
	return 0;
}
