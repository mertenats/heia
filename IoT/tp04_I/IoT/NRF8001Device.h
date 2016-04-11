/*********************************************************************
This is a library inspired both from Nordics SDK and from Adafruit nRF8001 Bluetooth Low Energy Breakout

These displays use SPI to communicate, 4 or 5 pins are required to  
interface

*********************************************************************/
#pragma once

#ifndef _NRF8001_DEVICE_H_
#define _NRF8001_DEVICE_H_

// ACI library
#include <aci_cmds.h>
#include <aci_evts.h>
#include <hal_aci_tl.h>
#include <lib_aci.h>

// SPI library
#include <SPI.h>

extern "C" 
{
  /* Callback prototypes */
  typedef void (*aci_callback)(aci_evt_opcode_t event);
}

class nRF8001Device
{
public:
  enum SettingsType
  {
    BROADCAST = 0,
    CONNECT = 1
  };
  
  // constructor
  // pSetupMessageContent: pointer to setup message content generated in services.h from nRFgo Studio
  // nbrOfSetUpMessage: number of setup messages generated in services.h from nRFgo Studio
  // pServicesPipeTypeMapping: pointer to services pipe mapping generated in services.h from nRFgo Studio
  // nbrOfPipes: number of service pipes generated in services.h from nRFgo Studio
  // settingsType: type of settings
  // reqPin: pin used for connecting REQ
  // rdyPin: pin used for connecting RDY
  // rstPin: pin used for connecting RST
  nRF8001Device (const hal_aci_data_t* pSetupMessageContent, unsigned int nbrOfSetUpMessage, 
                 services_pipe_type_mapping_t* pServicesPipeTypeMapping, unsigned int nbrOfPipes,
                 SettingsType settingsType,
                 int8_t reqPin, int8_t rdyPin, int8_t rstPin);
  
  // public methods
  
  // configures the nRF8001 and starts advertising the configured services
  // advTimeout is the duration during which advertising packets will be sent (in secs - 0 means infinite advertising)
  // valid timeout range in second is from 1 to 16383
  // advInterval is the time interval between advertising packets (in 0.625ms units) 
  // when broadcasting the valid range is from 0x0100 to 0x4000
  // nbrOfEchoCommandsBeforeSetup is the number of echo commands issued before setup is done, useful for testing the SPI connectivity
  bool begin(uint16_t advTimeout, uint16_t advInterval, uint32_t nbrOfEchoCommandsBeforeSetup);
  
  // called for polling all aci events at regular intervals
  void pollACI(void);
  
  // called for setting callbacks to be called upon receiving events
  void setACIcallback(aci_callback aciEvent = NULL);

  // called for setting device name
  void setDeviceName(const char* szDeviceName);

  // called for getting the device state
  aci_evt_opcode_t getState(void);

  // called for getting the device address
  
 private:  
  // private methods
  void setState(aci_evt_opcode_t event);
  void pollACIForEchoCommands(void);
  
  // private data members
  
  // callbacks you can set with setCallback function for user extension
  aci_callback m_pACIEvent;
  
  // values set in begin()
  uint16_t m_advTimeout;
  uint16_t m_advInterval;
  
  // device name set in setDeviceName()
  char m_szDeviceName[16];

  // device address obtained at setup time
  char m_szDeviceAddress[16];
  
  // current state
  aci_evt_opcode_t m_currentState;
  
  // pins used for SPI connection
  int8_t m_reqPin;
  int8_t m_rdyPin;
  int8_t m_rstPin;
  
  // hal_aci_data_t setup at construction time
  // nbr of setup messages
  int m_nbrOfSetupMessages;
  // pointer to setup messages (owned by class owner - only storing a pointer here)
  const hal_aci_data_t* m_pSetupMessageContent;
  // pointer to the service pipe data (owned by class owner - only storing a pointer here)
  int m_nbrOfPipes;
  services_pipe_type_mapping_t* m_pServicesPipeTypeMapping;
  // settings type
  SettingsType m_settingsType;
  
  // data members used for checking 
  uint32_t m_nbrOfEchoCommandsBeforeSetup;
  uint32_t m_nbrOfEchoCommandsIssued;
  uint32_t m_nbrOfEchoEventsReceived;
  static uint8_t g_echo_data[];
};

#endif

