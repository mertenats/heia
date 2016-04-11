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
 *
 * Note: 	Part of the code is directly inspired by the code presented at the time of the TP
 */

#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include "interrupt.h"

/* Low level interrupt handling ----------------------------------------------*/
/* declare low level routines implemented in assembler file interrupt_asm.s */
extern void interrupt_init_sp ();
extern void interrupt_init_sp (uint32_t mode, void* sp);
extern void interrupt_undefined_handler();
extern void interrupt_swi_handler();
extern void interrupt_prefetch_abort_handler();
extern void interrupt_data_abort_handler();
extern void interrupt_irq_handler();
extern void interrupt_fiq_handler();

/* Interrupt vector table contained in vram */
struct arm_vram {
	void (*undefined)();
	void (*swi)();
	void (*prefetch_abort)();
	void (*data_abort)();
	void (*irq)();
	void (*fiq)();
};
static volatile struct arm_vram* vram = (struct arm_vram*) 0xfffffef0;

/* defines stack for each processor mode */
/* 16k / 4 (.long) = 4096 -> 0x1000 */
static uint32_t undefined_stack[0x1000];
static uint32_t abort_stack[0x1000];
static uint32_t irq_stack[0x1000];
static uint32_t fiq_stack[0x1000];

/* interrupt service routines */
struct int_vector_entry {
    interrupt_isr_t handler;
    void* param;
};
static struct int_vector_entry int_vector_table[INT_NB_VECTORS];

/* interrupt handler called from assembler code */
void interrupt_process(void* address, enum interrupt_vectors vector) {
	// makes a pointer with the address of the given vector
	struct int_vector_entry* node = &int_vector_table[vector];
    if (node->handler != 0) {
        node->handler (address, vector, node->param);
    } else {
        printf("No interrupt handler defined for vector %d.", vector);
        while(1);
    }
}

/* Public methods ------------------------------------------------------------*/
/* Documentation: Interruptions (cours) - Modes de fonctionnement du processeur - p15 */
/* Documentation: Interruptions (cours) - Etat du processeur - p17 */
void interrupt_init() {
	// defines the code & sp for each interruption vector
	// Ex: FIQ: 0xd1 = 0b 1 (I) 1 (F) 0 1 0 0 0 1 (17) = 209 
	interrupt_init_sp (0xd1, fiq_stack + sizeof(fiq_stack));
	interrupt_init_sp (0xd2, irq_stack + sizeof(irq_stack));
	interrupt_init_sp (0xd7, abort_stack + sizeof(abort_stack));
	interrupt_init_sp (0xdb, undefined_stack + sizeof(undefined_stack));

	memset (int_vector_table, 0, sizeof(int_vector_table));

	vram->undefined = interrupt_undefined_handler;
	vram->swi = interrupt_swi_handler;
	vram->prefetch_abort = interrupt_prefetch_abort_handler;
	vram->data_abort = interrupt_data_abort_handler;
	vram->irq = interrupt_irq_handler;
	vram->fiq = interrupt_fiq_handler;
}

int interrupt_attach(enum interrupt_vectors vector, interrupt_isr_t routine, void* param) {
	// makes a pointer with the address of the given vector
	struct int_vector_entry* node = &int_vector_table[vector];
	if ((vector >= INT_NB_VECTORS) && (node->handler != 0))
		return -1;	//	returns -1 if an error has occurred (not in the table)
	node->handler = routine;	//	attaches the vector to the interruption
	node->param = param;
	return 0;
}

void interrupt_detach(enum interrupt_vectors vector) {
	// if the vector is present into the table, sets the vector's handler to 0
	if (vector < INT_NB_VECTORS)
		int_vector_table[vector].handler = 0;
}
