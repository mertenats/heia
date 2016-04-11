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
 * Authors: 	Daniel Gachet - Jacques Supcik
 * Date: 	13.03.2015
 */

#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include "interrupt.h"


/* Macro ---------------------------------------------------------------------*/
/**
 * Macro to get the number of elements in array
 */
#define ARRAY_SIZE(x) (sizeof(x) / sizeof((x)[0]))


/* Low level interrupt handling ----------------------------------------------*/

/* declare low level routines implemented in assembler file interrupt_asm.s */
extern void interrupt_undef_handler();
extern void interrupt_swi_handler();
extern void interrupt_prefetch_handler();
extern void interrupt_data_handler();
extern void interrupt_irq_handler();
extern void interrupt_fiq_handler();
extern void interrupt_init_sp (uint32_t mode, void* sp);

/* ARM9/i.MX27 interrupt vector table contained in vram */
struct arm_vram {
	void (*undef)();    
	void (*swi)();      
	void (*prefetch)();     
	void (*data)();     
	void (*irq)();      
	void (*fiq)();     
};
static volatile struct arm_vram* vram = (struct arm_vram*) 0xfffffef0;

/* define stack for each processor mode */
static uint32_t undef_stack[0x1000];
static uint32_t abort_stack[0x1000];
static uint32_t irq_stack[0x1000];
static uint32_t fiq_stack[0x1000];


/* interrupt service routines */
struct int_vector_entry {
    interrupt_isr_t handler;
    void* param;
};
static struct int_vector_entry int_vector_table[INT_NB_VECTORS];


/* Low level interrupt handler written in C and called from assembler file */
void interrupt_process (void* address, enum interrupt_vectors vector) 
{
    struct int_vector_entry* node = &int_vector_table[vector];
    if (node->handler != 0) {
        node->handler (address, vector, node->param);
    } else {
        printf("No interrupt handler defined for vector %d. Freezing!", vector);
        while(1);
    }
}


/* Public methods ------------------------------------------------------------*/

void interrupt_init() 
{
	interrupt_init_sp (0xd1, &fiq_stack[ARRAY_SIZE(fiq_stack)]);
	interrupt_init_sp (0xd2, &irq_stack[ARRAY_SIZE(irq_stack)]);
	interrupt_init_sp (0xd7, &abort_stack[ARRAY_SIZE(abort_stack)]);
	interrupt_init_sp (0xdb, &undef_stack[ARRAY_SIZE(undef_stack)]);

	memset (int_vector_table, 0, sizeof(int_vector_table));

	vram->undef = interrupt_undef_handler;
	vram->swi = interrupt_swi_handler;
	vram->prefetch = interrupt_prefetch_handler;
	vram->data = interrupt_data_handler;
	vram->irq = interrupt_irq_handler;
	vram->fiq = interrupt_fiq_handler;
}

int interrupt_attach (
	enum interrupt_vectors vector, 
	interrupt_isr_t routine,
        void* param) 
{
	struct int_vector_entry* node = &int_vector_table[vector];
	if ((vector >= INT_NB_VECTORS) && (node->handler != 0)) return -1;
	node->handler = routine;
	node->param = param;
	return 0;
}

void interrupt_detach (enum interrupt_vectors vector) 
{
	if (vector < INT_NB_VECTORS) int_vector_table[vector].handler = 0;
}

