#pragma once
#ifndef INTERRUPT_H
#define INTERRUPT_H
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
 * Purpose:	Module to deal with the low level ARM9/i.MX27 
 *		microprocessor interrupt logic
 *
 * Author: 	Fabio Valverde & Samuel Mertenat
 * Date: 	07.03.15
 */

/* interrupt vectors enumeration */
enum interrupt_vectors {
	INT_UNDEFINED,		//	Undefined instruction
	INT_SWI,			//	Software interrupt (SWI)
	INT_PREFETCH_ABORT,	//	Prefetch abort (instruction prefetch)
	INT_DATA_ABORT,		//	Data abort (data access)
	INT_IRQ,			//	(interrupt)
	INT_FIQ,			//	(fast interrupt)
	INT_NB_VECTORS		//	number of vectors, 6
};

/**
 * Prototype of the interrupt service routines
 *
 * @param addr return address
 * @param vector i.MX27 interrupt vector
 * @param param parameter specified while attaching the interrupt
 *              service routine
 */
typedef void (*interrupt_isr_t) (void* addr,
				 enum interrupt_vectors vector,
				 void* param);

/**
 * Method to initialize low level resources of the ARM9 microprocessor
 * A 16KB of memory will be allocated for each interrupt vector
 */
extern void interrupt_init();

extern int interrupt_attach (enum interrupt_vectors vector, interrupt_isr_t routine, void* param);
extern void interrupt_detach (enum interrupt_vectors vector);
extern void interrupt_enable();
extern void interrupt_disable();

#endif
