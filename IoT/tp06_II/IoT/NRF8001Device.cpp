/*********************************************************************
 * This is a library for our nRF8001 Bluetooth Low Energy Breakout
 * 
 * Pick one up today in the adafruit shop!
 * ------> http://www.adafruit.com/products/1697
 * 
 * These displays use SPI to communicate, 4 or 5 pins are required to  
 * interface
 * 
 * Adafruit invests time and resources providing this open source code, 
 * please support Adafruit and open-source hardware by purchasing 
 * products from Adafruit!
 * 
 * Written by Kevin Townsend/KTOWN  for Adafruit Industries.  
 * MIT license, check LICENSE for more information
 * All text above, and the splash screen below must be included in any redistribution
 *********************************************************************/

#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif
#include "nRF8001Device.h"

// for logging

// logging macros
#ifndef TR_LOGLEVEL
#define TR_LOGLEVEL 3
#endif

#include "Logging.h"

// ACI library
#include <lib_aci.h>
#include <aci_setup.h>

// Enabling services
#include "services.h"

uint8_t nRF8001Device::g_echo_data[] = { 0x00, 0xaa, 0x55, 0xff, 0x77, 0x55, 0x33, 0x22, 0x11, 0x44, 0x66, 0x88, 0x99, 0xbb, 0xdd, 0xcc, 0x00, 0xaa, 0x55, 0xff };

// Define how assert should function in the BLE library 
void __ble_assert(const char *szFileName, uint16_t lineNumber)
{
  TraceErrorFormat(F("%s: line %d\n"), szFileName, lineNumber);
  while(1);
}

/**************************************************************************/
/*!
 Implementation for the NRR8001 device service
 */
/**************************************************************************/

/**************************************************************************/
 nRF8001Device::nRF8001Device(const hal_aci_data_t* pSetupMessageContent, unsigned int nbrOfSetUpMessages, 
   services_pipe_type_mapping_t* pServicesPipeTypeMapping, unsigned int nbrOfPipes,
   SettingsType settingsType,
   int8_t reqPin, int8_t rdyPin, int8_t rstPin)
 : m_pSetupMessageContent(pSetupMessageContent),
 m_nbrOfSetupMessages(nbrOfSetUpMessages),
 m_pServicesPipeTypeMapping(pServicesPipeTypeMapping),
 m_nbrOfPipes(nbrOfPipes),
 m_settingsType(settingsType),
 m_reqPin(reqPin),
 m_rdyPin(rdyPin),
 m_rstPin(rstPin),
 m_nbrOfEchoCommandsBeforeSetup(0),
 m_nbrOfEchoCommandsIssued(0),
 m_nbrOfEchoEventsReceived(0),  
 m_currentState(ACI_EVT_DISCONNECTED)
 {
  // reset device name
  memset(m_szDeviceName, 0, sizeof(m_szDeviceName) / sizeof(m_szDeviceName[0]));
  
  // reset device address
  memset(m_szDeviceAddress, 0, sizeof(m_szDeviceAddress) / sizeof(m_szDeviceAddress[0]));
  
  // reset aci state & aci_data
  memset(&aci_state, 0, sizeof(aci_state));
  memset(&aci_data, 0, sizeof(aci_data));
}

/**************************************************************************/
aci_evt_opcode_t nRF8001Device::getState(void) 
{
  return m_currentState;
}

/**************************************************************************/
void nRF8001Device::setDeviceName(const char* szDeviceName)
{
  if (strlen(szDeviceName) > 16)
  {
    // String too long
    return;
  }
  else
  {
    memcpy(m_szDeviceName, szDeviceName, strlen(szDeviceName));
  }
}

/**************************************************************************/
bool nRF8001Device::begin(uint16_t advTimeout, uint16_t advInterval, uint32_t nbrOfEchoCommandsBeforeSetup) 
{
  // initialize Serial Peripheral Interface
  SPI.setBitOrder(LSBFIRST);
  SPI.setClockDivider(SPI_CLOCK_DIV8);
  SPI.setDataMode(SPI_MODE0);

  // Store the advertising timeout and interval
  m_advTimeout = advTimeout;   // ToDo: Check range!
  m_advInterval = advInterval; // ToDo: Check range!

  // Setup the service data from nRFGo Studio (services.h)
  if (NULL != m_pServicesPipeTypeMapping)
  {
    TraceInfo(F("Service type mapping set"));
    aci_state.aci_setup_info.services_pipe_type_mapping = &m_pServicesPipeTypeMapping[0];
  }
  else
  {
    aci_state.aci_setup_info.services_pipe_type_mapping = NULL;
  }
  aci_state.aci_setup_info.number_of_pipes = m_nbrOfPipes;
  aci_state.aci_setup_info.setup_msgs = (hal_aci_data_t*) m_pSetupMessageContent;
  aci_state.aci_setup_info.num_setup_msgs = m_nbrOfSetupMessages;

  //  Tell the ACI library, the MCU to nRF8001 pin connections.
  //  The Active pin is optional and can be marked UNUSED
  aci_state.aci_pins.board_name = BOARD_DEFAULT; //See board.h for details
  aci_state.aci_pins.reqn_pin = m_reqPin;
  aci_state.aci_pins.rdyn_pin = m_rdyPin;
  aci_state.aci_pins.mosi_pin = MOSI;
  aci_state.aci_pins.miso_pin = MISO;
  aci_state.aci_pins.sck_pin = SCK;

#if defined(__SAM3X8E__)
  aci_state.aci_pins.spi_clock_divider = 84;
#else
  aci_state.aci_pins.spi_clock_divider = SPI_CLOCK_DIV8;
#endif

  aci_state.aci_pins.reset_pin = m_rstPin;
  aci_state.aci_pins.active_pin = UNUSED;
  aci_state.aci_pins.optional_chip_sel_pin = UNUSED;

  aci_state.aci_pins.interface_is_interrupt = false;
  aci_state.aci_pins.interrupt_number = 4;//1;

  // The second parameter is for turning debug printing on for the ACI Commands and Events so they be printed on the Serial
  lib_aci_init(&aci_state, false);

  // store the number of echo commands that must be issued before setup
  m_nbrOfEchoCommandsBeforeSetup = nbrOfEchoCommandsBeforeSetup;
  m_nbrOfEchoCommandsIssued = 0;
  
  return true;
}

/**************************************************************************/
void nRF8001Device::pollACI(void)
{
  if ((m_nbrOfEchoCommandsIssued < m_nbrOfEchoCommandsBeforeSetup) || (m_nbrOfEchoEventsReceived < m_nbrOfEchoCommandsIssued))
  {
    pollACIForEchoCommands();
    return;
  }
  
  // We enter the if statement only when there is a ACI event available to be processed
  if (lib_aci_event_get(&aci_state, &aci_data))
  {
    aci_evt_t* aci_evt = &aci_data.evt;
    
    switch (aci_evt->evt_opcode)
    {
      // As soon as you reset the nRF8001 you will get an ACI Device Started Event
      case ACI_EVT_DEVICE_STARTED:
      {          
        aci_state.data_credit_total = aci_evt->params.device_started.credit_available;
        switch(aci_evt->params.device_started.device_mode)
        {
          case ACI_DEVICE_SETUP:
          {
            // Device is in setup mode            
            TraceInfoFormat(F("Evt Device Started: Setup (nbr of credits available is %d)\n"), aci_state.data_credit_total);
            
            uint8_t rc = do_aci_setup(&aci_state);
            if (SETUP_SUCCESS != rc)
            {
              TraceErrorFormat(F("Error (in pollACI at line %d) in ACI Setup: %08x\n"), __LINE__, rc);
            }
            
            // get the device version
            if (! lib_aci_device_version())
            {
              TraceErrorFormat(F("Error (in pollACI at line %d) in lib_aci_device_version\n"), __LINE__);
            }
            
            // get the device address
            if (! lib_aci_get_address())
            {
              TraceErrorFormat(F("Error (in pollACI at line %d) in lib_aci_get_address\n"), __LINE__);
            }
          }
          break;

          case ACI_DEVICE_STANDBY:
          {
            TraceInfoFormat(F("Evt Device Started: Standby (nbr of credits available is %d)\n"), aci_state.data_credit_total);

            if (m_szDeviceName[0] != 0x00)
            {
              TraceInfoFormat(F("Setting device name to %s\n"), m_szDeviceName);

              // Update the device name 
              int length = min(strlen(m_szDeviceName), PIPE_GAP_DEVICE_NAME_SET_MAX_SIZE);
              lib_aci_set_local_data(&aci_state, /*1*/ PIPE_GAP_DEVICE_NAME_SET, (uint8_t *) &m_szDeviceName, length);
            }

            // Start advertising ... first value is advertising time in seconds, the
            // second value is the advertising interval in 0.625ms units   
            // See ACI Broadcast in the data sheet of the nRF8001
            
            // call  lib_aci_broadcast or lib_aci_connect depending on the requested mode
            if (m_settingsType == BROADCAST)
            {
              lib_aci_broadcast(m_advTimeout, m_advInterval);
              TraceInfo(F("Broadcasting started\n"));
            }
            else
            {
              lib_aci_connect(m_advTimeout, m_advInterval);
              TraceInfo(F("Connect started\n"));
            }
            
            TraceInfo(F("Broadcasting started\n"));

            // To stop the broadcasting before the timeout use the
            // lib_aci_radio_reset to soft reset the radio
            // See ACI RadioReset in the datasheet of the nRF8001

            setState(ACI_EVT_DEVICE_STARTED);
            onACIEvent(aci_evt);
          }          
          break;
        }
      }
      break;

      case ACI_EVT_CMD_RSP:
      {
        /* If an ACI command response event comes with an error -> stop */
        if (ACI_STATUS_SUCCESS != aci_evt->params.cmd_rsp.cmd_status)
        {
          // ACI ReadDynamicData and ACI WriteDynamicData will have status codes of
          // TRANSACTION_CONTINUE and TRANSACTION_COMPLETE
          // all other ACI commands will have status code of ACI_STATUS_SUCCESS for a successful command
          //TraceErrorFormat("ACI Command: 0x%08x Evt Cmd response: Error with status 0x%08x -> Arduino is in an while(1); loop\n", aci_evt->params.cmd_rsp.cmd_opcode, aci_evt->params.cmd_rsp.cmd_status);
          TraceErrorFormat(F("ACI Command: 0x%08x Evt Cmd response: Error (0x%08x). Arduino is in an while(1); loop\n"), aci_evt->params.cmd_rsp.cmd_opcode, aci_evt->params.cmd_rsp.cmd_status);

          while (1);
        }

        if (ACI_CMD_GET_DEVICE_VERSION == aci_evt->params.cmd_rsp.cmd_opcode)
        {
          // Store the version and configuration information of the nRF8001 in the Hardware Revision String Characteristic
          //lib_aci_set_local_data(&aci_state, 
          //                       PIPE_DEVICE_INFORMATION_HARDWARE_REVISION_STRING_SET, 
          //                       (uint8_t *)&(aci_evt->params.cmd_rsp.params.get_device_version), 
          //                       sizeof(aci_evt_cmd_rsp_params_get_device_version_t));
        }
        
        if (ACI_CMD_GET_DEVICE_ADDRESS == aci_evt->params.cmd_rsp.cmd_opcode)
        {
          uint8_t* bd_addr_own = aci_evt->params.cmd_rsp.params.get_device_address.bd_addr_own;
          snprintf(m_szDeviceAddress, sizeof(m_szDeviceAddress), "%02x:%02x:%02x:%02x:%02x:%02x",
           bd_addr_own[5], bd_addr_own[4], bd_addr_own[3], bd_addr_own[2], bd_addr_own[1], bd_addr_own[0]);

          TraceInfoFormat(F("Device address is %s (type %d)\n"), m_szDeviceAddress, aci_evt->params.cmd_rsp.params.get_device_address.bd_addr_type);
        }
      }        
      break;      
      
      case ACI_EVT_CONNECTED: 
      {
        TraceInfo(F("Evt Connected\n"));

        aci_state.data_credit_available = aci_state.data_credit_total;

        // Get the device version of the nRF8001 and store it in the Hardware Revision String
        lib_aci_device_version();

        setState(ACI_EVT_CONNECTED);
        onACIEvent(aci_evt);
      }
      break;
      
      case ACI_EVT_PIPE_STATUS:
      {
        TraceInfoFormat(F("Evt PipeStatus: pipes_open_bitmap[0] is 0x%08x\n"), aci_evt->params.pipe_status.pipes_open_bitmap[0]);
      }
      break;

      case ACI_EVT_TIMING:
      {
        // Link connection interval changed 
      }
      break;

      case ACI_EVT_DISCONNECTED:
      {
        TraceInfo(F("Evt Disconnected\n"));
        
        // Restart advertising ... first value is advertising time in seconds, the */
        // second value is the advertising interval in 0.625ms units */
        setState(ACI_EVT_DISCONNECTED);
        onACIEvent(aci_evt);

        // call  lib_aci_broadcast or lib_aci_connect depending on the requested mode
        if (m_settingsType == BROADCAST)
        {
          lib_aci_broadcast(m_advTimeout, m_advInterval);
          TraceInfo(F("Broadcasting restarted\n"));
        }
        else
        {
          lib_aci_connect(m_advTimeout, m_advInterval);
          TraceInfo(F("Connect restarted\n"));
        }

        TraceInfo(F("Broadcasting restarted\n"));

        setState(ACI_EVT_DEVICE_STARTED);
        onACIEvent(aci_evt);
      }      
      break;

      case ACI_EVT_DATA_RECEIVED:
      {
        TraceInfo(F("Evt Data Received\n"));
        onACIEvent(aci_evt);
      }
      break;
      
      case ACI_EVT_DATA_ACK:
      {
        //TraceInfo(F("Evt Data Acknowledged\n"));
        onACIEvent(aci_evt);
      }
      break;
      
      case ACI_EVT_DATA_CREDIT:      
      {
        aci_state.data_credit_available = aci_state.data_credit_available + aci_evt->params.data_credit.credit;
        TraceDebugFormat(F("Evt Data credit: available %d\n"), aci_state.data_credit_available);
      }
      break;

      case ACI_EVT_PIPE_ERROR:
      {
        // See the appendix in the nRF8001 Product Specication for details on the error codes */
        TraceErrorFormat(F("ACI Evt Pipe Error: Pipe #: %d,  Pipe Error Code: 0x%08x\n"), aci_evt->params.pipe_error.pipe_number, aci_evt->params.pipe_error.error_code);

        // Increment the credit available as the data packet was not sent */
        aci_state.data_credit_available++;
      }
      break;    
    }
  }
  else
  {
    // Serial.println(F("No ACI Events available"));
    // No event in the ACI Event queue and if there is no event in the ACI command queue the arduino can go to sleep
    // Arduino can go to sleep now
    // Wakeup from sleep from the RDYN line
  }
}


/**************************************************************************/
// PRIVATE METHODS
/**************************************************************************/
void nRF8001Device::pollACIForEchoCommands(void)
{
  // We enter the if statement only when there is a ACI event available to be processed
  if (lib_aci_event_get(&aci_state, &aci_data))
  {
    aci_evt_t* aci_evt = &aci_data.evt;
    
    switch (aci_evt->evt_opcode)
    {
      // As soon as you reset the nRF8001 you will get an ACI Device Started Event
      case ACI_EVT_DEVICE_STARTED:
      {          
        aci_state.data_credit_total = aci_evt->params.device_started.credit_available;
        switch(aci_evt->params.device_started.device_mode)
        {
          case ACI_DEVICE_SETUP:
          {
            lib_aci_test(ACI_TEST_MODE_DTM_ACI);
            TraceInfoFormat(F("Device test launched: %d echo commands will be issued\n"), m_nbrOfEchoCommandsBeforeSetup);
          }
          break;
          
          case ACI_DEVICE_TEST:
          {
            TraceInfo(F("Evt Device Started: Test\n"));
            if (m_nbrOfEchoCommandsIssued < m_nbrOfEchoCommandsBeforeSetup)
            {
              lib_aci_echo_msg(sizeof(g_echo_data), &g_echo_data[0]);
              m_nbrOfEchoCommandsIssued++;
            }
            TraceInfoFormat(F("Test: %d echo commands have been issued\n"), m_nbrOfEchoCommandsIssued);
          }
          break;
        }
      }
      break;

      case ACI_EVT_ECHO:
      {
        if (0 != memcmp(&g_echo_data[0], &(aci_evt->params.echo.echo_data[0]), sizeof(g_echo_data)))
        {
          TraceError(F("Error: Echo loop test failed. Verify the SPI connectivity on the PCB. \n"));
        }
        else
        {
          m_nbrOfEchoEventsReceived++;
          TraceInfoFormat(F("Echo OK: event #%d received\n"), m_nbrOfEchoEventsReceived);
          
          if (m_nbrOfEchoCommandsIssued < m_nbrOfEchoCommandsBeforeSetup)
          {
            lib_aci_echo_msg(sizeof(g_echo_data), &g_echo_data[0]);
            m_nbrOfEchoCommandsIssued++;
            TraceInfoFormat(F("Test: %d echo commands have been issued\n"), m_nbrOfEchoCommandsIssued);
          }
          else
          {
            TraceInfoFormat(F("Evt Device Started: Setup (nbr of credits available is %d)\n"), aci_state.data_credit_total);
            lib_aci_test(ACI_TEST_MODE_EXIT);
            
            uint8_t rc = do_aci_setup(&aci_state);
            if (SETUP_SUCCESS != rc)
            {
              TraceErrorFormat(F("Error (in pollACIForEchoCommands at line %d) in ACI Setup: %08x\n"), __LINE__, rc);
            }
          }
        }
      }
      break;
      
      case ACI_EVT_CMD_RSP:
      {
        // If an ACI command response event comes with an error -> stop 
        if (ACI_STATUS_SUCCESS != aci_evt->params.cmd_rsp.cmd_status)
        {
          // ACI ReadDynamicData and ACI WriteDynamicData will have status codes of
          // TRANSACTION_CONTINUE and TRANSACTION_COMPLETE
          // all other ACI commands will have status code of ACI_STATUS_SUCCESS for a successful command
          TraceErrorFormat(F("ACI Command: 0x%08x Evt Cmd response: Error (0x%08x). Arduino is in an while(1); loop\n"), aci_evt->params.cmd_rsp.cmd_opcode, aci_evt->params.cmd_rsp.cmd_status);

          while (1);
        }
      }
      break;
    }
  }
}

/**************************************************************************/
void nRF8001Device::setState(aci_evt_opcode_t event)
{
  m_currentState = event;
}
