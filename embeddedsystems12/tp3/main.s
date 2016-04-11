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
 * Abstract: 	TP3 - An introduction to I/O handling
 *
 * Purpose:	Main program implementing a 8-bit gauge with press-buttons  
 *   		as input and 7-segments as display.
 *
 * Author: 	<Samuel Mertenat>
 * Date: 	<08/11/2014>
 */

/* Export public symbols */
		.global main, delayDisplayTestMode, delayButtonsTestMode, main_loop

/* some help/debug info to display on the shell */
	.section .rodata
hello:	.ascii "\n"
	.ascii "EIA-FR - Embedded Systems 1 Laboratory\n"
	.ascii "TP3: Introduction to I/O Handling\n"
	.ascii "     An 8-bit gauge implementation\n"
	.asciz "\n"

/* Implementation of assembler functions and methods */
	.text
main:	nop

	/* display hello message */
	ldr	r0,	=hello
	bl	imx27_serial_puts

	/* Hardware initialization
	* -----------------------------------------------------------------
	*/
	bl	display_fpga_init	// initialize the display
	bl	buttons_init		// initialize the leds / buttons

	ldr r11, =0b1			// set r11 t0 1 (first led, test mode)
	ldr r12, =0				// initialize the counter
	b	display_fpga_test	// launch the test mode (display and leds)

	/* Main Program - main loop (never exit...)
	 * -----------------------------------------------------------------
	 */
main_loop:
	bl	buttons_get_state	// return 0 or 1-2-4 (buttons decrement, reset, increment)
	cmp	r0,	#0b001			// test if the first button is pressed
	beq decrement
	cmp r0,	#0b010			// test if the second button is pressed
	beq reset
	cmp r0,	#0b100			// test if the third button is pressed
	beq increment

	bl	display_val
	b 	main_loop

decrement:
	ldr	r5,	=100000			// load +/- 0.5 [s]
	cmp r12, #0				// test if the counter is equal to 0
	subne r12, #1			// if it's not the case, -1
	bl delay

increment:
	ldr	r5,	=100000			// load +/- 0.5 [s]
	cmp	r12, #255			// test if the counter is equal to 255
	addne r12, #1			// if it's not the case, +1
	bl delay

reset:
	ldr	r5,	=100000			// load +/- 0.5 [s]
	mov r12, #0				// reset the counter to 0
	bl	delay

delay:
	sub	r5, #1				// decrement the timer by 1
	bl	display_val
	cmp r5,	#0				// test if the timer is equal to 0
	bne	delay				// if it's not the case, re-call the loop again
	bl	main_loop

delayDisplayTestMode:
	sub	r5, #1					// decrement the timer by 1
	bl	display_val
	cmp r5,	#0					// test if the timer is equal to 0
	bne	delayDisplayTestMode	// if it's not the case, re-call the loop again
	bl display_fpga_test

delayButtonsTestMode:
	sub	r5, #1					// decrement the timer by 1
	bl	display_val
	cmp r5,	#0					// test if the timer is equal to 0
	bne	delayButtonsTestMode	// if it's not the case, re-call the loop again
	bl buttons_test

