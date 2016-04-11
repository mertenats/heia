#pragma once
#ifndef GPT_H
#define GPT_H
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
 * Abstract: 	TP11 - GPT controller
 *
 * Purpose:		Main module to demonstrate and to test the GPT controller
 *
 * Authors:		Fabio Valverde & Samuel Mertenat
 * Date:		02.04.2015
 */

#include <stdint.h>

// Enum of the 6 timers
enum gpt_timers {
	GPT1,
	GPT2,
	GPT3,
	GPT4,
	GPT5,
	GPT6,
	GPT_NB_OF_TIMERS
};

// Interrupts routine
typedef void (*gpt_isr_t)(void*param);

// Method to initialize the GPT controller
extern void gpt_init();

#endif
