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
 * Project:	EIA-FR / Embedded Systems 1 Laboratory
 *
 * Abstract: 	I2C Device Driver 
 *
 * Purpose:	This module implements the services for the I2C module
 *          	of the i.MX27 processor.
 *
 * Autĥor:	<Samuel Mertenat>
 * Date:	<22.12.2014>
 */

#include <imx27_clock.h>
#include <imx27_gpio.h>
#include "i2c.h"

/* -- Internal types and constant definition -------------------------------- */
/* I2C Clock rate conversion table */
struct ic_pair {uint16_t divider; uint16_t ic; };
static const struct ic_pair ic_divider[] = {
	{  22, 0x20}, {  24, 0x21}, {  26, 0x22}, {  28, 0x23},
	{  30, 0x00}, {  32, 0x01}, {  36, 0x02}, {  40, 0x26},
	{  42, 0x03}, {  44, 0x27}, {  48, 0x04}, {  52, 0x05},
	{  56, 0x29}, {  60, 0x06}, {  64, 0x2a}, {  72, 0x07},
	{  80, 0x08}, {  88, 0x09}, {  96, 0x2d}, { 104, 0x0a},
	{ 112, 0x2e}, { 128, 0x0b}, { 144, 0x0c}, { 160, 0x0d},
	{ 192, 0x0e}, { 224, 0x32}, { 240, 0x0f}, { 256, 0x33},
	{ 288, 0x10}, { 320, 0x11}, { 384, 0x12}, { 448, 0x36},
	{ 480, 0x13}, { 512, 0x37}, { 576, 0x14}, { 640, 0x15},
	{ 768, 0x16}, { 896, 0x3a}, { 960, 0x17}, {1024, 0x3b},
	{1152, 0x18}, {1280, 0x19}, {1536, 0x1a}, {1792, 0x3e},
	{1920, 0x1b}, {2048, 0x3f}, {2304, 0x1c}, {2560, 0x1d},
	{3072, 0x1e}, {3840, 0x1f}
};

/* I2C Control Register (I2CR) bit fields */
/* Documentation: 02_ARM_... - 24.3.3.3 - p850 */
#define I2CR_IEN 	(1<<7)	// I2C enable
#define I2CR_IIEN 	(1<<6)	// I2C interrupt enable
#define I2CR_MSTA	(1<<5)	// Master/slave mode select bit
#define I2CR_MTX	(1<<4)	// Transmit/receive mode select bit
#define I2CR_TXAK	(1<<3)	// Transmit acknowledge enable
#define I2CR_RSATA	(1<<2)	// Repeat start

/* I2C Status Register (I2SR) bit fields */
/* Documentation: 02_ARM_... - 24.3.3.4 - p851 */
#define I2SR_ICF	(1<<7)	// Data transferring bit; 1: transfer in progress / 0: complete
#define I2SR_IBB	(1<<5)	// I2C busy bit; 1: bus is busy
#define I2SR_IIF	(1<<1)	// I2C interrupt; 1: an interrupt is pending
#define I2SR_RXAK	(1<<0)	// Received ack; 0: ack signal was received / 1: no ack

/* definition of I2C operations within address word */
/* Documentation: TP4 Interface I2C - p10-11 */
#define I2C_WRITE_OPERATION 0
#define I2C_READ_OPERATION 1

/* I2C Controller Register Definition */
struct i2c_ctrl {
	uint32_t IADR;	// I2C Address Register
	uint32_t IFDR;	// I2C Frequency Register
	uint32_t I2CR;	// I2C Control Register
	uint32_t I2SR;	// I2C Status Register
	uint32_t I2DR;	// I2C Data Register
};
static volatile struct i2c_ctrl* i2c = (struct i2c_ctrl*)0x10012000; // I2C1

/* I2C Initialization Sequence */
/* Documentation: 02_ARM_... - 24.5.1 - p856 */
void i2c_init () {
	imx27_clock_enable_line (IMX27_CLOCK_I2C1);
	imx27_gpio_configure_controller (IMX27_GPIO_I2C1);
	i2c->I2CR = 0;						// initialization of the Control Register
	i2c_set_bus_speed(100000);			// sets the bus speed; 100 kHz
	i2c->IADR = 0x70;					// defines the slave address; range from 0 t0 0x7f
	i2c->I2CR = (i2c->I2CR | I2CR_IEN);	// enables the I2C interface system
}

/* ------------------------------------------------------------------------- */

void i2c_set_bus_speed(uint32_t speed) {
	uint32_t freq1 = imx27_clock_get_ipgclk();
	uint32_t refdiv = freq1 / speed;

	// searches for the corresponding divider from refdiv
	uint8_t i = 0;
	while (ic_divider[i].divider < refdiv && i < sizeof(ic_divider)) {
		i++;
	}
	i2c->IFDR = ic_divider[i].ic; // sets the Frequency Register
}

/* I2C Generation of START */
/* Documentation: 02_ARM_... - 24.3.3.3 & 24.5.2 - p850 & p857 */
static void i2c_send_start() {
	// looks at the I2C busy bit and waits until it's idle
	while ((i2c->I2SR & I2SR_IBB) != 0) {
	}
	// changing MSTA from 0 to 1 signals a START & selects master mode
	i2c->I2CR = i2c->I2CR | I2CR_MSTA;
}

/* I2C Generation of REPEATED START */
/* Documentation: 02_ARM_... - 24.3.3.3 & 24.5.5 - p851 & p858 */
static void i2c_send_repeated_start() {
	// looks at the I2C Data transferring bit and waits until it's cleared (=1)
	while((i2c->I2SR & I2SR_ICF) == 0) {
	}
	// changing RSATA from 0 to 1 signals the generation of a repeated start
	i2c->I2CR = i2c->I2CR | I2CR_RSATA;
}

/* I2C Reading a byte */
/* Documentation: 02_ARM_... - 24.5.3 - p857 */
static uint8_t i2c_read_byte(bool ack) {
	uint8_t byte = 0;
	// waits until the operation is finished; finished = 1 (I2SR_ICF)
	while((i2c->I2SR & I2SR_ICF) == 0) {
	}
	// waits until the interrupt is pending; I2SR_IIF = 1
	while((i2c->I2SR & I2SR_IIF) == 0) {
	}
	i2c->I2SR = i2c->I2SR & ~I2SR_IIF;	// clears the interrupt (sets the interrupt bit to 0)
	// to terminate a data transfer, it must not acknowledging the last data byte
	if (ack == true) {
		i2c->I2CR = i2c->I2CR | I2CR_TXAK;	// ack; I2CR_TXAK sets to 1
	} else {
		i2c->I2CR = i2c->I2CR & ~I2CR_TXAK;	// not ack; I2CR_TXAK sets to 0
	}
	byte = i2c->I2DR;	// reads a byte from the I2C Data Register
	return byte;
}

/* I2C Writing a byte */
/* Documentation: 02_ARM_... - 24.5.3 - p857 */
static int i2c_write_byte(uint8_t byte) {
	i2c->I2CR = i2c->I2CR | I2CR_MTX;		// enables the transmit mode
	i2c->I2DR = byte;						// loads the byte to write into the Data Register
	// waits until the operation is finished; finished = 1 (I2SR_ICF)
	while((i2c->I2SR & I2SR_ICF) == 0) {
	}
	// upon completion, the interrupt status is set & an external interrupt is generated
	// waits until the interrupt is pending; I2SR_IIF = 1
	while((i2c->I2SR & I2SR_IIF) == 0) {
	}
	i2c->I2SR = i2c->I2SR & ~I2SR_IIF;		// clears the interrupt (sets the interrupt bit to 0)
	// checks the received ack during the bus cycle;
	if ((i2c->I2SR & I2SR_RXAK) == 0) {
		return 0;	// 0: an ack was received after the data transmission
	} else {
		return 1;	// 1: a not ack was detected at the ninth clock
	}
}

/* I2C Generation of STOP */
/* Documentation: 02_ARM_... - 24.3.3.3-4 & 24.5.4 - p850-1 & p858 */
static void i2c_send_stop() {
	// looks at the I2C Data transferring bit and waits until the transfer is complete
	while ((i2c->I2SR & I2SR_ICF) == 0) {
	}
	// changing MSTA from 1 to 0 generates a STOP and selects slave mode
	i2c->I2CR = i2c->I2CR & ~I2CR_MSTA;
}

/* method to see if a chip is present on the I2C bus */
extern bool i2c_probe(uint8_t chip_id) {
	i2c_send_start();	// generates a START
	uint8_t ack = i2c_write_byte(chip_id << 1);
	i2c_send_stop();	// generates a STOP
	if (ack == 0) {
		return true;
	} else {
		return false;
	}
}

/* Method to read data bytes from the internal register files of the specified chip */
/* Documentation: TP4 Interface I2C - p11 */
/* Documentation: 03_le_bus_I2C.pdf - 2.7-8 - p7-13 */
extern int i2c_read  (uint8_t chip_id, uint8_t reg_addr, uint8_t* buffer, uint16_t buffer_len) {
	uint8_t status = 0;	// 0: success, 1: error
	i2c_send_start();		// generates a start
	// writes the slave address & sets operation mode to write (=0)
	status = i2c_write_byte((chip_id << 1) | I2C_WRITE_OPERATION);
	if (status == 0) {
		// if an ack was received, it writes the address of the Temperature register
		i2c_write_byte(reg_addr);
	}
	i2c_send_repeated_start();	// generates a repeated start
	if (status == 0) {
		// writes the slave address & sets operation mode to read (=1)
		i2c_write_byte((chip_id << 1) | I2C_READ_OPERATION);
	}
	// sets the Transmit/receive mode select bit to 0; 0: received mode
	i2c->I2CR = i2c->I2CR & ~I2CR_MTX;
	if (buffer_len < 1) {
		// if the buffer is smaller than 1 byte, it generates an ack
		i2c->I2CR = i2c->I2CR | I2CR_TXAK;
	} else {
		// if the buffer is bigger than 1 byte, it generates a not ack
		i2c->I2CR = i2c->I2CR & ~I2CR_TXAK;
	}
	uint8_t byte = i2c->I2DR; // reads a byte (unused)
	for (uint8_t i = buffer_len; i > 2; i--) {
		// reads byte after byte; the last acknowledge is a not ack
		*buffer++ = i2c_read_byte(true);
	}
	*buffer++ = i2c_read_byte(false);
	i2c_send_stop();	// generates a stop
	// reads the last byte
	*buffer = i2c_read_byte(true);
	return status;
}

/* Method to write data byte into the internal register files of the specified chip */
/* Documentation: TP4 Interface I2C - p11 */
/* Documentation: 03_le_bus_I2C.pdf - 2.7-8 - p7-13 */
extern int i2c_write (uint8_t chip_id, uint8_t reg_addr, const uint8_t* buffer, uint16_t buffer_len) {
	int status = 0;
	i2c_send_start();		// generates a start
	// writes the slave address & sets operation mode to write (=0)
	status = i2c_write_byte((chip_id << 1) | I2C_WRITE_OPERATION);
	if (status == 0) {
		// if an ack was received, it writes the address of the Temperature register
		i2c_write_byte(reg_addr);
	}
	uint8_t i = buffer_len;
	while (status == 0 && i > 0) {
		status = i2c_write_byte(*buffer++);		// writes byte after byte
		i--;
	}
	i2c_send_stop();	// generates a stop
	return status;
}
