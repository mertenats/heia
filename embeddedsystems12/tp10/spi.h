#pragma once
#ifndef SPI_H
#define SPI_H
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
 * Project:   HEIA-FR / Embedded Systems 1+2 Laboratory
 *
 * Abstract:  CSPI - Configurable Serial Peripheral Interface
 *
 * Purpose:   Module to deal with the Configurable Serial Peripheral Interface
 *            Controller (CSPI) of the i.MX27.
 *
 * Author:    Daniel Gachet
 * Date:      28.06.2014
 */

#include <stdint.h>

/**
 * List of available CSPI controllers 
 */
enum spi_controllers {
    IMX27_SPI1, // < SPI1 controller
    IMX27_SPI2, // < SPI2 controller
};

/**
 * List of chip select to be activated during data transfer
 */
enum spi_chipselects {
    IMX27_SS0,  // < SS0 will be activated during transfer
    IMX27_SS1,  // < SS1 will be activated during transfer
    IMX27_SS2,  // < SS2 will be activated during transfer
    IMX27_NO_CS // < no chip select will be activated during transfer
};

/**
 * CSPI Controller 
 */
struct spi_ctrl {
    enum spi_controllers ctrl; // < CSPI controller
    enum spi_chipselects cs;   // < chip select to be activated

    /**
     * Method to write a command/data pair over the SPI
     *@param ctrl CSPI controller object
     *@param command command word
     *@param data data word
     */
    void (*write)(struct spi_ctrl* ctrl, uint32_t command, uint32_t data);

    /**
     * Method to read data pair from the SPI
     *@param ctrl CSPI controller object
     *@param command command word
     *@param data data words (reference to 1st data word)
     *@param number number of data words to read
     */
    void (*read)(struct spi_ctrl* ctrl, uint32_t command, uint32_t* data,
            uint32_t number);

    /* private attributes */
    volatile void* regs;
    void* private_data;
};

/**
 * Method to initialize the resources of a CSPI
 *
 *@param ctrl CSPI controller
 *@param cs chipselect to be activated during transfer
 *@param datarate transmission data rate in kbit/s
 *@param wordsize size of data word in bits
 *@return controller object
 */
extern struct spi_ctrl spi_init(enum spi_controllers ctrl,
        enum spi_chipselects cs, uint32_t datarate, uint32_t wordsize);

#endif

