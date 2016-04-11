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
 * Project:     HEIA-FR / Embedded Systems 1+2 Laboratory
 *
 * Abstract:    SPI - Configurable Serial Peripheral Interface
 *
 * Author:      <Samuel Mertenat>
 * Credit:      Daniel Gachet (template for the driver)
 *              Florian Nicoulaud (template for the driver)
 * Date:        <25.02.15>
 */

#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#include "spi.h"
#include "imx27_clock.h"
#include "imx27_gpio.h"

/* Bit definition and macro CONREG */
/* Documentation: 02_ARM_i_MX27_Reference_Manual.pdf - 23.4.3.3 - p831 */
#define CSPI_CONREG_BURST			(1 << 23)
#define CSPI_CONREG_XCH				(1 << 9)			// Data Exchange (1: busy / 0: idle)
#define CSPI_CONREG_CS(x)			(((x) & 0x3) << 19)	// Chip selected
#define CSPI_CONREG_DATA_RATE(x)	(((x) & 0x1F) << 14)	// Data Rate (divider)
#define CSPI_CONREG_MODE			(1 << 11)			// CSPI Mode Select (1: master / 0: slave)
#define CSPI_CONREG_SPIEN			(1 << 10)			// CSPI Mode Enable (1: enable)
#define CSPI_CONREG_PHA				(1 << 6)			// Phase (1: Phase 1 operation)
#define CSPI_CONREG_BIT_CCOUNT(x)	((x-1) & 0x1F)		// Bit Count

/* Bit definition and macro INTREG */
/* Documentation: 02_ARM_i_MX27_Reference_Manual.pdf - 23.4.3.4 - p834 */
#define CSPI_INTREG_BOEN			(1 << 17)
#define CSPI_INTREG_RR				(1 << 4)	// RxFIFO Full Status (1: 8 data words / 0: Less than 8)

/* Bit definition and macro RESETREG */
/* Documentation: 02_ARM_i_MX27_Reference_Manual.pdf - 23.4.3.8 - p841 */
#define CSPI_RESETREG_START			(1 << 1)	// Soft Reset bit (1: reset / 0: not reset)

/* SPI2 REGISTER */
/* Documentation: TP10: Serial Peripheral Interface Bus - p10 */
struct cspi_regs {
	uint32_t RXDATA;		// Receive Data Register, R
	uint32_t TXDATA;		// Transmit Data Register, W
	uint32_t CONREG;		// Control Register, R/W
	uint32_t INTREG;		// Interrupt Control and Status Register, R/W
	uint32_t TESTREG;		// Test Control Register, R/W
	uint32_t PERIODREG;		// Sample Period Control Register, R/W
	uint32_t DMAREG;		// DMA Control Register, R/W
	uint32_t RESETREG;		// Soft Reset Register, R/W
};

/* Documentation: TP10: Serial Peripheral Interface Bus - p10 */
static volatile struct cspi_regs* cspi[] = {
    (struct cspi_regs*) 0x1000E000,
    (struct cspi_regs*) 0x1000F000,
    (struct cspi_regs*) 0x10017000
};

static inline uint32_t cspi_bit2rate(uint32_t divider) {
    if (divider <= 3)   return 1;
    if (divider <= 4)   return 2;
    if (divider <= 6)   return 3;
    if (divider <= 8)   return 4;
    if (divider <= 12)  return 5;
    if (divider <= 16)  return 6;
    if (divider <= 24)  return 7;
    if (divider <= 32)  return 8;
    if (divider <= 48)  return 9;
    if (divider <= 64)  return 10;
    if (divider <= 96)  return 11;
    if (divider <= 128) return 12;
    if (divider <= 192) return 13;
    if (divider <= 256) return 14;
    if (divider <= 384) return 15;
    return 16;
}

static void cspi_write(struct spi_ctrl* ctrl, uint32_t command, uint32_t data) {
	volatile struct cspi_regs* spi = ctrl->regs;
	// waits until the bus is idle (1: busy)
	while ((spi->CONREG & CSPI_CONREG_XCH) != 0);
	spi->TXDATA = command;	// writes the command into TXDATA
	spi->TXDATA = data;	// write the data into TXDATA
	// initiates the transfer, sets CSPI_CONREG_XCH to 1
	spi->CONREG = spi->CONREG | CSPI_CONREG_XCH;
}

static void cspi_read(struct spi_ctrl* ctrl, uint32_t command, uint32_t* data, uint32_t number) {
	if (number > 7)
		return;	// the number must be smaller than 8
	volatile struct cspi_regs* spi = ctrl->regs;
	// waits until the bus is idle (1: busy)
	while ((spi->CONREG & CSPI_CONREG_XCH) != 0);
	spi->TXDATA = command;	// writes the command into TXDATA
	for (uint8_t n = number; n > 0; n--)
		spi->TXDATA = -1; // writes -1 into TXDATA n times (clocking words)
	// initiates the transfer, sets CSPI_CONREG_XCH to 1
	spi->CONREG = spi->CONREG | CSPI_CONREG_XCH;
	// waits until the transfer is finished
	while ((spi->CONREG & CSPI_CONREG_XCH) != 0);
	// reads & saves data if 8 data words are available
	uint8_t i = 0;
	//while ((spi->INTREG & CSPI_INTREG_RR)  == 0);
	while ((spi->INTREG & CSPI_INTREG_RR)  != 0) {
		uint32_t rx = spi->RXDATA;
		if ((i > 0) && (i <= number))
			*data++ = rx;
		i++;
	}
}

struct spi_ctrl spi_init(enum spi_controllers ctrl, enum spi_chipselects cs,
        uint32_t datarate, uint32_t wordsize) {

    /* enable CPSI clock */
    imx27_clock_config_cpsi();

    /* configure gpio port D for SPI1 */
    if (ctrl == IMX27_SPI1) {
        imx27_clock_enable_line(IMX27_CLOCK_CSPI1);
        imx27_gpio_configure_controller(IMX27_GPIO_CSPI1);
    }

    /* configure gpio port D for SPI2 */
    if (ctrl == IMX27_SPI2) {
        imx27_clock_enable_line(IMX27_CLOCK_CSPI2);
        imx27_gpio_configure_controller(IMX27_GPIO_CSPI2);
    }

    struct spi_ctrl ctl = {
    		.ctrl = ctrl,
			.cs = cs,
			.write = cspi_write,
			.read = cspi_read,
			.regs = cspi[ctrl],
			.private_data = 0,
    };
    volatile struct cspi_regs* spi = ctl.regs;

    /* reset the CPSI controller */
    spi->RESETREG = CSPI_RESETREG_START;
    while ((spi->RESETREG & CSPI_RESETREG_START) != 0);

    /* compute data rate */
    uint32_t divider = imx27_clock_get_perclk2() / datarate / 1000;
    uint32_t bitrate = cspi_bit2rate(divider);

    spi->CONREG = CSPI_CONREG_BURST
            | CSPI_CONREG_CS(cs) | CSPI_CONREG_DATA_RATE(bitrate)
            | (cs != IMX27_NO_CS ? CSPI_CONREG_MODE : 0)
            | CSPI_CONREG_SPIEN
            | CSPI_CONREG_PHA
            | CSPI_CONREG_BIT_CCOUNT(wordsize);

    spi->INTREG = 0;
    spi->TESTREG = 0;
    spi->PERIODREG = 0;
    spi->DMAREG = 0;

    return ctl;
}
