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
 * Abstract: 	APF27 FPGA Buttons & LED Display Device Driver
 *
 * Purpose:	This module implements a method to get the state of the 
 *		push-buttons and to show their state on the FPGA LED display
 *
 * Author: 	<Samuel Mertenat>
 * Date: 	<08/11/2014>
 */

.include "fpga.s"

/* Export public symbols */
		.global buttons_init, buttons_test, buttons_get_state

/* Implementation of assembler functions and methods */
	.text

	/* routine to initialize the buttons module
	 * used: <r0-1>
	 */
buttons_init:	
	nop
	ldrh r0, =FPGA_BASE					// load the fpga base's address
	ldrh r1, =0x0FF 					// 0b00011111111
	strh r1, [r0, #FPGA_LED_SW_CTRL]	// set the leds as output [0-7] and buttons as input [8-10]
	ldrh r1, =0x0						// load 0b0
	strh r1, [r0, #FPGA_LED_SW_RW]		// switch off all the leds
	bx	lr

buttons_test:
	ldrh	r0, =FPGA_BASE					// load the fpga base's address
	strh	r11, [r0, #FPGA_LED_SW_RW]		// switch off all the leds
	cmp		r11, #0b100000000				// while r11 isn't equal to 128
	moveq	r12, #0							// if r12 is equal to 128: set to 0
	beq		main_loop						// branch to the main loop (counter initialized)
	movne	r11, r11, lsl #1				// multiply r11 by 2: the next led
	ldr		r5, =250000						// load +/- 0.25 [s]
	bl		delayButtonsTestMode			// launch the timer
	bne		buttons_test

	/* routine to get the state of the buttons and to display it on the LED
	 * out:   r0 = buttons state (1=>button pressed, 0=>button open)
	 *
	 * used: <r0-2>
	 */
buttons_get_state:
	nop
	ldr		r1, =FPGA_BASE + FPGA_LED_SW_RW + 0x1	// load the fpga buttons' address
	ldrb	r0, [r1]								// load the current state of the buttons / leds
	mov 	r2, #7									// create a mask to filter the buttons from the rest
	mvn 	r0, r0									// inverse the loaded byte
	and 	r0, r2, r0								// get the pressed button [1-2-4] (0: not pressed)
	cmp 	r0, #0b010
	moveq	r0, #0b11111111							// switch on all the leds when the reset button is pressed
	ldrh 	r1, =FPGA_BASE							// load the fpga base's address
	strh 	r0, [r1, #FPGA_LED_SW_RW]				// switch on the corresponded led
	moveq	r0,	#0b010								// restore the originale value (0b010)
	bx 		lr
