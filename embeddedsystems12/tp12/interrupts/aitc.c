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
 * Abstract:	AITC Driver
 *
 * Purpose:		Module designed to manage the AITC controller
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		19.03.2015
 */

#include <stdbool.h>
#include "interrupt.h"
#include "aitc.h"
#include <stdint.h>

// AITC registers
// Documentation: 02_ARM....pdf - Register Summary - 10.2.2 - p392-6
struct aitc_regs {
	uint32_t intcntl;
	uint32_t nimask;
	uint32_t intennum;		// enables a line
	uint32_t intdisnum;     // disables a line
	uint32_t intenable[2];
	uint32_t inttype[2];    // interrupt type (IRQ or FIQ)
	uint32_t nipriority[8];
	uint32_t nivecsr;
	uint32_t fivecsr;
	uint32_t intsrc[2];
	uint32_t intfrc[2];		// register to force interruptions
	uint32_t nipnd[2];
	uint32_t fipnd[2];
};
static volatile struct aitc_regs* aitc = (struct aitc_regs*)0x10040000;

// AITC routine parameters
struct aitc_routine_parameters {
	aitc_isr_t routine;
	void * param;
};
// cf. enum aitc_interrupt_vectors; aitc.h
struct aitc_routine_parameters handlers[64];

// Method to handle interrupts
// Documentation: 02_ARM....pdf - NIVECSR + FIVECSR - 10.2.10-1 - p411-2
static void aitc_handler(void* addr, enum aitc_interrupt_vectors vector, void* param) {
	uint32_t vect = 0;
	if (vector == INT_IRQ)
		// if it's an IRQ, reads the register, shifted by 16 bits
		vect = aitc -> nivecsr >> 16;
	else
		// if it's a FIQ, reads the register
		vect = aitc -> fivecsr;
	handlers[vect].routine(handlers[vect].param);
}

void aitc_init() {
	aitc->intcntl = 0;		// initializes the Interrupt Control Register
	aitc->nimask = -1;		// does not disable any normal interrupts priority levels (-1)
	aitc->intenable[0] = 0;	// resets all pending interrupt requests
	aitc->intenable[1] = 0;
	// sets the priority level to 0 (default; highest: 1 - 7 : lowest)
	// Documentation: Interruptions (cours) - Priorité des interruptions - p36
	for (uint8_t i = 0; i < 8; i++) {
		aitc->nipriority[i] = 0;
	}
	aitc->intfrc[0] = 0;	// initializes the Interrupt Control Register
	aitc->intfrc[1] = 0;
	// attaches the interrupt's vectors for IRQ & FIQ to the handler
	interrupt_attach(INT_IRQ, aitc_handler, 0);
	interrupt_attach(INT_FIQ, aitc_handler, 0);
}

int aitc_attach(enum aitc_interrupt_vectors vector, enum aitc_interrupt_types type, aitc_isr_t routine, void* param) {
	int8_t status = -1;
	if((vector < AITC_NB_OF_VECTORS) && (handlers[vector].routine == 0)) {
		handlers[vector].routine = routine;
		handlers[vector].param = param;
		// retrieve the type of the interrupt
		if(type == AITC_FIQ) {
			aitc->inttype[1-(vector/32)] |= (1<<(vector % 32));		// FIQ : bit = 1
		} else {
			aitc->inttype[1-(vector/32)] &= ~(1<<(vector % 32));	// IRQ : bit = 0
		}
		aitc->intennum = vector;	// enables interruptions for this vector
		status = 0;
	}
	return status;
}

int aitc_detach(enum aitc_interrupt_vectors vector) {
	int8_t status = -1;
	if(vector < AITC_NB_OF_VECTORS) {
		aitc->intdisnum = vector;		// disables the interrupts for this vector
		handlers[vector].routine = 0;	// resets the handler for this vector
		handlers[vector].param = 0;
		status = 0;
	}
	return status;
}

void aitc_force(enum aitc_interrupt_vectors vector, bool force) {
	if (vector < AITC_NB_OF_VECTORS) {
		if (force)
			aitc->intfrc[1 - (vector / 32)] |= (1 << (vector % 32));
		else
			aitc->intfrc[1 - (vector / 32)] &= ~(1 << (vector % 32));
	}
}
