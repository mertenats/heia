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
 * Abstract: 	7-segments Device Driver
 *
 * Purpose:	This module implements the services for the I2C module
 *          	of the i.MX27 processor.
 *
 * Autĥor:	<Samuel Mertenat>
 * Date:	<22.12.2014>
 */

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include "display_7seg.h"

/* Data source: fpga.s (TP3) */
/* FPGA Constants declaration */
struct fpga_ctrl {
	uint16_t NOTUSED1;			// 0x000 - 0x006 (not used)
	uint16_t NOTUSED2;
	uint16_t NOTUSED3;
	uint16_t NOTUSED4;
	uint16_t FPGA_SEG7_RW;		// 0x008
	uint16_t FPGA_SEG7_CTRL;	// 0x00a
	uint16_t FPGA_GPIO0_ID;		// 0x00c
	uint16_t NOTUSED5;			// 0x00e
	uint16_t FPGA_LED_SW_RW;	// 0x010
	uint16_t FPGA_LED_SW_CTRL;	// 0x012
	uint16_t FPGA_GPIO1_ID;		// 0x014
};
static volatile struct fpga_ctrl* fpga = (struct fpga_ctrl*)0xd6000000;	// FPGA Base address

/* Documentation: TP3 Intro aux I/O - p5-6 */
/* Data source: display_7seg.s (TP3) */
/* 7-segement: segment definition
           +-- seg A --+
           |           |
         seg F       seg B
           |           |
           +-- seg G --+
           |           |
         seg E       seg C
           |           |
           +-- seg D --+
*/

#define SEG_O 0x004
#define SEG_A 0x008
#define SEG_B 0x010
#define SEG_C 0x020
#define SEG_D 0x040
#define SEG_E 0x080
#define SEG_F 0x100
#define SEG_G 0x200

static const uint16_t seg_7 [] = {
    SEG_A + SEG_B + SEG_C + SEG_D + SEG_E + SEG_F,			// 0
	SEG_B + SEG_C,											// 1
	SEG_A + SEG_B + SEG_D + SEG_E + SEG_G,					// 2
	SEG_A + SEG_B + SEG_C + SEG_D + SEG_G,					// 3
	SEG_B + SEG_C + SEG_F + SEG_G,							// 4
	SEG_A + SEG_C + SEG_D + SEG_F + SEG_G,					// 5
	SEG_A + SEG_C + SEG_D + SEG_E + SEG_F + SEG_G,			// 6
	SEG_A + SEG_B + SEG_C,									// 7
	SEG_A + SEG_B + SEG_C + SEG_D + SEG_E + SEG_F + SEG_G,	// 8
	SEG_A + SEG_B + SEG_C + SEG_D + SEG_F + SEG_G,			// 0
};

/* Method to initialize the 7-segment display */
extern void display_fpga_init(){
    fpga->FPGA_SEG7_CTRL = 0x03ff;	// sets each segment as output
    fpga->FPGA_SEG7_RW = 0x0;		// clears the display
}

/* Method to display a value [-99 - 99] in decimal on the the 7-segment */
extern void display_val(int8_t value) {
	if (value < -99 || value > 99) {
		printf("ERROR: The current temperature %d can't be displayed on the display", value);
	} else {
		display_fpga_7seg(abs(value) % 10, 0, 1);			// right digit
		if (value < 0) {
			display_fpga_7seg(abs(value) / 10, SEG_O, 2); 	// left digit; switches on the dot if the value is negative
		} else {
			display_fpga_7seg(abs(value) / 10, 0, 2);		// left digit
		}
	}
}

/* Method to display a decimal-digit on the 7-segment display */
void display_fpga_7seg(uint8_t value, uint16_t sign, uint8_t digit) {
	fpga->FPGA_SEG7_RW = ((seg_7[value] + sign) | digit);
}
