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
 * Project:	EIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: 	ARM Interrupt Handling - Low Level Interface
 *
 * Author: 	Daniel Gachet
 * Date: 	28.06.2014
 */

/*-- exports --------------------------------------------------------------- */
		.global	interrupt_init_sp
		.global	interrupt_enable
		.global interrupt_disable
		.global interrupt_undef_handler
		.global interrupt_swi_handler
		.global interrupt_prefetch_handler
		.global interrupt_data_handler
		.global interrupt_irq_handler
		.global interrupt_fiq_handler

/*-- local symbols --------------------------------------------------------- */
INT_UNDEF    = 0	// undefined instruction
INT_SWI      = 1 	// software interrupt
INT_PREFETCH = 2 	// prefetch abort (instruction prefetch)
INT_DATA     = 3 	// data abort (data access)
INT_IRQ      = 4	// hardware interrupt request
INT_FIQ      = 5	// hardware fast interrupt request

/*-- public methods -------------------------------------------------------- */

		.text
interrupt_init_sp:
	nop
	mrs	r2, cpsr	// save mode
	msr     cpsr_c, r0 	// switch to mode
	mov	sp, r1		// init stack pointer
	msr     cpsr_c, r2 	// restore mode
	bx	lr

interrupt_enable:
	nop
	mrs	r0, cpsr
	bic	r0, #0xc0
	msr	cpsr_c, r0
	bx	lr

interrupt_disable:
	nop
	mrs	r0, cpsr
	orr	r0, #0xc0
	msr	cpsr_c, r0
	bx	lr

/*-- local methods  -------------------------------------------------------- */

/**
 * macro implementing low level isr entry operations
 */
.macro lowlevel_isr offset, vector
	nop	
	sub	lr, #\offset		// adjust return address
	stmfd   sp!, {r0-r12,lr}	// save context and return address
	mov	r0, lr			// indicate return address
	mov	r1, #\vector		// indicate vector number
	bl	interrupt_process	// process interrupt
	ldmfd   sp!,{r0-r12,pc}^	// restore the context (pc & cpsr)
.endm

/**
 * The methods have to be attached to the interrupt vectors 
 */
interrupt_undef_handler:	lowlevel_isr 0, INT_UNDEF
interrupt_swi_handler:		lowlevel_isr 0, INT_SWI
interrupt_prefetch_handler:	lowlevel_isr 4, INT_PREFETCH
interrupt_data_handler:		lowlevel_isr 4, INT_DATA
interrupt_irq_handler:		lowlevel_isr 4, INT_IRQ
interrupt_fiq_handler:		lowlevel_isr 4, INT_FIQ

