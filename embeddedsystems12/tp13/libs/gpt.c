/**
 * File: 		gpt.c
 *
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
 * Abstract: 	TP11 - GPT controller
 *
 * Purpose:		Main module to demonstrate and to test the GPT controller
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		12.06.2015
 */

#include <stdint.h>
#include "gpt.h"
#include "aitc.h"

// General Purpose Timer Register
// Documentation: 02_ARM....pdf - General Purpose Timer Register - 31.2 - p1263-4
struct gpt_regs {
	uint32_t TCTL;	// GPT Control Registers
	uint32_t TPRER;	// GPT Prescaler Register
	uint32_t TCMP;	// GPT Compare Register
	uint32_t TCR;	// GPT Capture Register (non used)
	uint32_t TCN;	// GPT Counter Register (non used)
	uint32_t TSTAT;	// GPT Status Register
};

// Registers addresses structure
// Documentation: 02_ARM....pdf - Programming Model - 31.3 - p1262-4
static volatile struct gpt_regs* gpt[] = {
		(struct gpt_regs*)0x10003000,
		(struct gpt_regs*)0x10004000,
		(struct gpt_regs*)0x10005000,
		(struct gpt_regs*)0x10019000,
		(struct gpt_regs*)0x1001a000,
		(struct gpt_regs*)0x1001f000,
};

// GPT Control Register
// Documentation: 02_ARM....pdf - GPT Control Register - 31.3 - p1265-6
#define TCTL_SWR 		(1<<15)	// software reset GPT
#define TCTL_COMP_EN	(1<<4)	// compare interrupt enable
#define TCTL_PERCLK1	(1<<0)	// PERCLK1 (TP13)
#define TCTL_CLK_SOURCE	(7<<1)	// clock source
#define TCTL_TEN		(1<<0)	// timer enable

// GPT Status Register
// Documentation: 02_ARM....pdf - GPT Status Register - 31.3.6 - p1271
#define TSTAT_COMP		(1<<0)	// compare event

// GPT ISR Handler Structure Definition
struct gpt_isr_handlers {
	gpt_isr_t routine;		// Application specific interrupt routine
	struct gpt_regs* gpt;	// GPT registers pointer
	void* param;			// Application specific parameter
	uint16_t period;		// The period of the clock
};
static struct gpt_isr_handlers handlers [GPT_NB_OF_TIMERS];

// Interrupt service routine
void gpt_isr(void* param) {
	struct gpt_isr_handlers* t = (struct gpt_isr_handlers*)param;
	t->gpt->TSTAT |= TSTAT_COMP;	// acknowledges the interrupt
	t->routine (t->param);
}

// Method to enable / start the specified timer
int gpt_enable(enum gpt_timers timer, uint16_t period, gpt_isr_t routine, void* param) {
	int8_t status = -1;
	struct gpt_isr_handlers* t = &(handlers[timer]);
	struct gpt_regs* gptr = gpt[timer];

	if (timer < GPT_NB_OF_TIMERS && t->routine == 0) {
		if (timer == 0)
			status = aitc_attach(AITC_GPT1, AITC_IRQ, gpt_isr, t);	// 0: GPT1
		else if (timer == 1)
			status = aitc_attach(AITC_GPT2, AITC_IRQ, gpt_isr, t);	// 1: GPT2
		else if (timer == 2)
			status = aitc_attach(AITC_GPT3, AITC_IRQ, gpt_isr, t);	// 2: GPT3
		else if (timer == 3)
			status = aitc_attach(AITC_GPT4, AITC_IRQ, gpt_isr, t);	// 3: GPT4
		else if (timer == 4)
			status = aitc_attach(AITC_GPT5, AITC_IRQ, gpt_isr, t);	// 4: GPT5
		else if (timer == 5)
			status = aitc_attach(AITC_GPT6, AITC_IRQ, gpt_isr, t);	// 5: GPT6
	}

	if (status == 0) {
		gptr->TCTL = TCTL_SWR; 			// resets the timer
		t->routine = routine;			// links the service routine
		t->param = param;				// sets the param
		t->period = period;				// sets the period (clock)
		t->gpt = gpt[timer];
		// waits on the software reset
		while ((gptr->TCTL & TCTL_SWR) != 0);
		gptr->TCTL |= TCTL_CLK_SOURCE;	// uses 32k clock
		//gptr->TCTL |= TCTL_PERCLK1;
		gptr->TCTL |= TCTL_COMP_EN;		// enables the compare interrupt mode
		gptr->TPRER = 31;				// 32k / 32 => 1/1000 [s]
		//gptr->TPRER = 15;				// 32k / 32 => 1/1000 [s]
		gptr->TCMP = period;
		gptr->TCTL |= TCTL_TEN;			// timer enable
	}
	return status;
}

// Method to disable / stop the specific timer
int gpt_disable(enum gpt_timers timer) {
	int8_t status = -1;
	if (timer < GPT_NB_OF_TIMERS) {
		struct gpt_regs* gptr = gpt[timer];
		gptr->TCTL |= TCTL_SWR;					// resets the timer
		if (timer == 0)
			status = aitc_detach(AITC_GPT1);	// GPT1
		else if (timer == 1)
			status = aitc_detach(AITC_GPT2);	// GPT2
		else if (timer == 2)
			status = aitc_detach(AITC_GPT3);	// GPT3
		else if (timer == 3)
			status = aitc_detach(AITC_GPT4);	// GPT4
		else if (timer == 4)
			status = aitc_detach(AITC_GPT5);	// GPT5
		else if (timer == 5)
			status = aitc_detach(AITC_GPT6);	// GPT6
		struct gpt_isr_handlers* t = &(handlers[timer]);
		// resets the ISR handler
		t->routine = 0;
		t->param = 0;
		t->period = 0;
		t->gpt = 0;
	}
	return status;
}

void gpt_init() {
	// Sets the first num bytes of the block of memory pointed by 'handlers' to the value '0'
	memset(handlers, '\0', sizeof(handlers));
}
