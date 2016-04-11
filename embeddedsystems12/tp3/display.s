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
 * Abstract: 	7-Segement Display Driver
 *
 * Purpose:	This module implements a method to diplay an hex-value
 *   		in range 0x00..0xff on a two 7-segments display
 *
 * Author: 	<Samuel Mertenat>
 * Date: 	<08/11/2014>
 */

/* Export public symbols */
		.global display_val

/* Implementation of assembler functions and methods */
	.text
	/* routine to display a value [0..255] in hex on the the 7-segments
	 * in:   r0 = value to display
	 *
	 * used: <r0-2>
	 */
display_val:
	nop
	stmfd	sp!, {r12,lr}
	mov r2, r12				// make a backup of the value to display
	ldr r1, =0x1			// load 1 : right digit
	and r0, r2, #0xF		// determine the value of the right digit
	bl display_fpga_7seg	// diplay the right digit (r0)

	ldr r1, =0x2			// load 2 : left digit
	mov r0, r2, lsr #4		// determine the value of the left digit
	and r0, r0, #0xF
	bl display_fpga_7seg	// display the left digit (r0)

	ldmfd	sp!, {r12,pc}
