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
 * Project:	HEIA-FR / Embedded Systems 2 Laboratory
 *
 * Abstract: 	ARM Interrupt Handling - Low Level Interface
 *
 * Author: 	Fabio Valverde & Samuel Mertenat
 * Date: 	06.03.15
 */

/*-- exports --------------------------------------------------------------- */
	.global	interrupt_init_sp
	.global interrupt_undefined_handler, interrupt_swi_handler
	.global interrupt_prefetch_abort_handler, interrupt_data_abort_handler
	.global interrupt_irq_handler, interrupt_fiq_handler
	.global	interrupt_enable, interrupt_disable

/*-- local variable -------------------------------------------------------- */
/* Documentation: Interruptions (cours) - Table des vecteurs d'interruptions - p13 */
	.bss
	INT_UNDEFINED		= 0	//	Undefined instruction
	INT_SWI				= 1	//	Software interrupt (SWI)
	INT_PREFETCH_ABORT	= 2	//	Prefetch abort (instruction prefetch)
	INT_DATA_ABORT		= 3	//	Data abort (data access)
	INT_IRQ				= 4	//	(interrupt)
	INT_FIQ				= 5	//	(fast interrupt)

/*-- public methods -------------------------------------------------------- */
	.text

/* MRS: Move to ARM register from system coprocessor register */
/* MSR: Move to system coprocessor register from ARM register */
interrupt_init_sp:
	nop
	mrs	r2, cpsr	// saves the current mode into r2 from cpsr
	msr	cpsr_c, r0 	// switches to the mode given by the first param (r0)
	mov	sp, r1		// initiates the stack pointer with the second given param (r1)
	msr	cpsr_c, r2 	// restores to the previous mode (affects only the 8 first bits)
	bx	lr

/* STMFD SP!, {<registers>, LR}: to save the registers */
/* LDMFD SP!, {<registers>, LR}: to restore the registers */
/* Documentation: Interruptions (cours) - Sortie de la routine d'interruption - p20 */
/* Offset of -4: Data A., FIQ, IRQ, Pre-fetch Abort. The others: 0 */
interrupt_undefined_handler:
	nop
	//sub	lr,	#0				// adjusts the returned value with the offset
	stmfd	sp!, {r0-r12,lr}	// saves the registers & the link register
	mov		r0, lr				// saves the return address (lr)
	mov		r1, #INT_UNDEFINED	// saves the vector number
	bl		interrupt_process	// calls interrupt_process(r0, r1) (C function)
	ldmfd   sp!,{r0-r12,pc}^	// restores back the registers, pc & cpsr
	//bx	lr

interrupt_swi_handler:
	nop
	//sub	lr,	#0				// adjusts the returned value with the offset
	stmfd	sp!, {r0-r12,lr}	// saves the registers & the link register
	mov		r0, lr				// saves the return address (lr)
	mov		r1, #INT_SWI		// saves the vector number
	bl		interrupt_process	// calls interrupt_process(r0, r1) (C function)
	ldmfd   sp!,{r0-r12,pc}^	// restores back the registers, pc & cpsr
	//bx	lr

interrupt_prefetch_abort_handler:
	nop
	sub		lr,	#4					// adjusts the returned value with the offset
	stmfd	sp!, {r0-r12,lr}		// saves the registers & the link register
	mov		r0, lr					// saves the return address (lr)
	mov		r1, #INT_PREFETCH_ABORT	// saves the vector number
	bl		interrupt_process		// calls interrupt_process(r0, r1) (C function)
	ldmfd   sp!,{r0-r12,pc}^		// restores back the registers, pc & cpsr
	//bx	lr

interrupt_data_abort_handler:
	nop
	sub		lr,	#4				// adjusts the returned value with the offset
	stmfd	sp!, {r0-r12,lr}	// saves the registers & the link register
	mov		r0, lr				// saves the return address (lr)
	mov		r1, #INT_DATA_ABORT	// saves the vector number
	bl		interrupt_process	// calls interrupt_process(r0, r1) (C function)
	ldmfd   sp!,{r0-r12,pc}^	// restores back the registers, pc & cpsr
	//bx	lr

interrupt_irq_handler:
	nop
	sub		lr,	#4				// adjusts the returned value with the offset
	stmfd	sp!, {r0-r12,lr}	// saves the registers & the link register
	mov		r0, lr				// saves the return address (lr)
	mov		r1, #INT_IRQ		// saves the vector number
	bl		interrupt_process	// calls interrupt_process(r0, r1) (C function)
	ldmfd   sp!,{r0-r12,pc}^	// restores back the registers, pc & cpsr
	//bx	lr

interrupt_fiq_handler:
	nop
	sub		lr,	#4				// adjusts the returned value with the offset
	stmfd	sp!, {r0-r12,lr}	// saves the registers & the link register
	mov		r0, lr				// saves the return address (lr)
	mov		r1, #INT_FIQ		// saves the vector number
	bl		interrupt_process	// calls interrupt_process(r0, r1) (C function)
	ldmfd   sp!,{r0-r12,pc}^	// restores back the registers, pc & cpsr
	//bx	lr

/* BIC: Bit clear (Rd AND NOT Rm) */
/* Source code: Ex1 - P4 - Série 1 */
/* Documentation: Interruptions (cours) - Acceptation des interruptions - p30 */
interrupt_enable:
	nop
	mrs	r0, cpsr	// saves the current mode into r0
	bic	r0, #0xc0	// sets the bits I 6 F to 0 (interruptions enabled)
	msr	cpsr_c, r0	// restores to the previous mode
	bx	lr

/* ORR: bitwise OR operation */
interrupt_disable:
	nop
	mrs	r0, cpsr	// saves the current mode into r0
	orr	r0, #0xc0	// sets the bits I 6 F to 1 (interruptions disabled)
	msr	cpsr_c, r0	// restores to the previous mode
	bx	lr
