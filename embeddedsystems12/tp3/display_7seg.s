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
 * Abstract: 	APF27 FPGA 7-Segment Display Device Driver
 *
 * Purpose:	This module implements a method to display an hex-digit on
 *   		7-segments connected the FPGA ports of the APF27
 *
 * Author: 	<Samuel Mertenat>
 * Date: 	<08/11/2014>
 */

.include "fpga.s"

/* Export public symbols */
		.global display_fpga_init, display_fpga_7seg
		.global display_fpga_test

/* Implementation of assembler functions and methods */
	.text

	/* routine to initialize the 7-segment display
	 * used: <r0-1>
	 */
display_fpga_init:	
	nop
	ldrh r0, =FPGA_BASE				// load the fpga base's address
	ldrh r1, =0x3FF					// load 0b1111111111
	strh r1, [r0, #FPGA_SEG7_CTRL]	// set each segment as output
	ldrh r1, =0x0					// load 0b0
	strh r1, [r0, #FPGA_SEG7_RW]	// clear the display
	bx	lr

	/* routine to display an hex-digit on the 7-segment display
	 * in:   r0 = value to display (0x0 .. 0xf)
	 *       r1 = 7-segment number (0x2=left | 0x1=right)
	 * used: <r0-1, r3-4>
	 */
display_fpga_7seg:
	nop
	ldr r3, =seg_7					// load the segments' table
	ldr r3, [r3, r0, lsl #2]		// load the corresponding segments' value
	orr r3, r3, r1					// define the digit to switch on (not the two=
	ldrh r4, =FPGA_BASE				// load the fpga base's address
	strh r3, [r4, #FPGA_SEG7_RW]	// switch on the segments
	bx	lr

	/* routine to test the routine display_val
	 * used: <r5,r12>
	 */
display_fpga_test:
	cmp	r12, #255				// test if the counter is equal to 255
	beq buttons_test			// branch to the buttons test
	addne r12, #1				// if it's not the case, +1
	ldr	r5,	=50000				// load +/- 0.25 [s]
	bl delayDisplayTestMode		// launch the timer
	bne display_fpga_test

	.section .rodata
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
SEG_A = 0x008
SEG_B = 0x010
SEG_C = 0x020
SEG_D = 0x040
SEG_E = 0x080
SEG_F = 0x100
SEG_G = 0x200

seg_7:
	.long SEG_A + SEG_B + SEG_C + SEG_D + SEG_E + SEG_F			// 0
	.long SEG_B + SEG_C											// 1
	.long SEG_A + SEG_B + SEG_D + SEG_E + SEG_G					// 2
	.long SEG_A + SEG_B + SEG_C + SEG_D + SEG_G					// 3
	.long SEG_B + SEG_C + SEG_F + SEG_G							// 4
	.long SEG_A + SEG_C + SEG_D + SEG_F + SEG_G					// 5
	.long SEG_A + SEG_C + SEG_D + SEG_E + SEG_F + SEG_G			// 6
	.long SEG_A + SEG_B + SEG_C									// 7
	.long SEG_A + SEG_B + SEG_C + SEG_D + SEG_E + SEG_F + SEG_G	// 8
	.long SEG_A + SEG_B + SEG_C + SEG_D + SEG_F + SEG_G			// 9
	.long SEG_A + SEG_B + SEG_C + SEG_E + SEG_F + SEG_G			// A
	.long SEG_C + SEG_D + SEG_E + SEG_F + SEG_G					// b
	.long SEG_A + SEG_D + SEG_E + SEG_F							// C
	.long SEG_B + SEG_C + SEG_D + SEG_E + SEG_G					// D
	.long SEG_A + SEG_D + SEG_E + SEG_F + SEG_G					// E
	.long SEG_A + SEG_E + SEG_F + SEG_G							// F
