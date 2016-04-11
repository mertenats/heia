/**
 * Copyright 2015 University of Applied Sciences Western Switzerland / Fribourg
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
 * Project:		EIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: 	TP11 - FPGA 7-Segment Display Device Driver
 *
 * Purpose:		Module to demonstrate and to test the FPGA 7segments
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		23.04.2015
 */

#include "fpga.h"

#include <stdbool.h>

/**
 * 7-segment display and LED interface
 */
struct fpga_regs {
	uint16_t reserved1[4];
	uint16_t seg7_rw;
	uint16_t seg7_ctrl;
	uint16_t seg7_id;
	uint16_t reserved2[1];
	uint16_t leds_rw;
	uint16_t leds_ctrl;
	uint16_t leds_id;
};
static volatile struct fpga_regs* fpga = (struct fpga_regs*)0xd6000000;

// Method to initialize the 7-segment display
void fpga_display_init() {
	static bool is_initialized=false;
	if (!is_initialized) {
		fpga->leds_rw = 0;
		fpga->leds_ctrl = 0xff;
		fpga->seg7_rw = 0;
		fpga->seg7_ctrl = 0x3ff;
		is_initialized = true;
	}
}

// Method to display a decimal number on the 7segments
void fpga_display_value(struct chrono_value *chrono_val) {
	// will contain 1 or 0, depending on the digit to display
	static uint8_t index = 0;
	fpga_display_init();

	if (index == 0) {
		fpga->seg7_rw = chrono_val->dg3;
		index = (index + 1) % 2;
	} else {
		fpga->seg7_rw = chrono_val->dg4;
		index = (index + 1) % 2;
	}
}
