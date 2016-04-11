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
 * Abstract: 	APF27 FPGA Register Memory Layout Defintion
 *
 * Purpose:	This module defines the FPGA GPIO registers
 *
 * Author: 	Daniel Gachet
 * Date: 	28.06.2014
 */

/* Constants declaration */
FPGA_BASE	= 0xd6000000
FPGA_SEG7_RW 	= 0x008
FPGA_SEG7_CTRL 	= 0x00a
FPGA_GPIO0_ID 	= 0x00c
FPGA_LED_SW_RW 	= 0x010
FPGA_LED_SW_CTRL= 0x012
FPGA_GPIO1_ID	= 0x014
