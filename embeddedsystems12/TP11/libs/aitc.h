#pragma once
#ifndef AITC_H
#define AITC_H

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
 * Autĥors:		Fabio Valverde & Samuel Mertenat
 * Date:		19.03.2015
 */

#include <stdbool.h>
#include <stdint.h>

// List of the used AITC vectors (64 available)
// Documentation: 02_ARM....pdf - Interrupt Assignments Low - 10.2.12.2 - p415
enum aitc_interrupt_vectors {
	AITC_Reserved,
	AITC_I2C2,
	AITC_GPT6,
	AITC_GPT5,
	AITC_GPT4,
	AITC_RTIC,
	AITC_CSPI3,
	AITC_MSHC,
	AITC_GPIO,
	AITC_SDHC3,
	AITC_SDHC2,
	AITC_SDHC1,
	AITC_I2C1,
	AITC_SSI2,
	AITC_SSI1,
	AITC_CPSI2,
	AITC_CPSI1,
	AITC_UART4,
	AITC_UART3,
	AITC_UART2,
	AITC_UART1,
	AITC_KPP,
	AITC_RTC,
	AITC_PWM,
	AITC_GPT3,
	AITC_GPT2,
	AITC_GPT1,
	AITC_WDOG,
	AITC_PCMCIA,
	AITC_NFC,
	AITC_ATA,
	AITC_CSI,
	AITC_DMACH0,
	AITC_DMACH1,
	AITC_DMACH2,
	AITC_DMACH3,
	AITC_DMACH4,
	AITC_DMACH5,
	AITC_DMACH6,
	AITC_DMACH7,
	AITC_DMACH8,
	AITC_DMACH9,
	AITC_DMACH10,
	AITC_DMACH11,
	AITC_DMACH12,
	AITC_DMACH13,
	AITC_DMACH14,
	AITC_DMACH15,
	AITC_UART6,
	AITC_UART5,
	AITC_FEC,
	AITC_EMMAPRP,
	AITC_EMMAPP,
	AITC_H264,
	AITC_USBHS1,
	AITC_USBHS2,
	AITC_USBOTG,
	AITC_SMN,
	AITC_SCM,
	AITC_SAHARA,
	AITC_SLCDC,
	AITC_LCDC,
	AITC_IMM,
	AITC_DPTC,
	AITC_NB_OF_VECTORS
};

// Type of AITC interruptions
enum aitc_interrupt_types {
	AITC_IRQ,
	AITC_FIQ
};

// Interrupts routine
typedef void(*aitc_isr_t)(void*param);

// Method to initialize AITC controller
extern void aitc_init();
// Method to attach an interrupt routine to the AITC
extern int aitc_attach(enum aitc_interrupt_vectors vector, enum aitc_interrupt_types type, aitc_isr_t routine, void* param);
// Method to detach an interrupt routine from the AITC
extern int aitc_detach(enum aitc_interrupt_vectors vector);
// Method to force an interrupt
extern void aitc_force(enum aitc_interrupt_vectors vector, bool state);

#endif
