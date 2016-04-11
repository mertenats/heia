#include <SPI.h>
#include <aci_cmds.h>
#include <aci_evts.h>
#include <hal_aci_tl.h>
#include <lib_aci.h>
#include <EEPROM.h>

#include "NRF8001Device.h"
//#include "_BLE_connect_TxPower.h"
//#include "_BLE_broadcast_TxPower.h"
#include "services.h"
#include "Logging.h"

// loggig purpose ...
int g_loggingLevel = 3;

// 1/0 : On/Off
#ifndef MEASURING
#define MEASURING 1
#endif

#if (MEASURING)
  #include "DHT.h"
  #define DHTPIN 8
  #define DHTTYPE DHT11
  DHT dht(DHTPIN, DHTTYPE);
  // variables for the measures...
  float h = 0;  // humidity
  float t = 0;  // temperature
  float l = 0;  // light
#endif

static const hal_aci_data_t setup_msgs[NB_SETUP_MESSAGES] PROGMEM = SETUP_MESSAGES_CONTENT;
// for storing the service pipe data created in nRFgo Studio
#ifdef SERVICES_PIPE_TYPE_MAPPING_CONTENT
  static services_pipe_type_mapping_t services_pipe_type_mapping[NUMBER_OF_PIPES] = SERVICES_PIPE_TYPE_MAPPING_CONTENT;
#else
  #define NUMBER_OF_PIPES 0
  static services_pipe_type_mapping_t* services_pipe_type_mapping = NULL;
#endif

#define REQ 10
#define RDY 2  
#define RST 9

// creates a instance of nRF8001Device
nRF8001Device nrf(
  setup_msgs,
  NB_SETUP_MESSAGES,
  services_pipe_type_mapping,
  NUMBER_OF_PIPES,
  nRF8001Device::CONNECT,
  REQ,
  RDY,
  RST);

// ACI event status on the nRF8001
aci_evt_opcode_t lastStatus = ACI_EVT_DISCONNECTED;

void setup() {
  Serial.begin(9600);
  // sets the device name
  nrf.setDeviceName("SAMeteo");
  // starts the nRF8001
  bool nrfIsStarted = nrf.begin(1, 0x0100, 0);
  if (nrfIsStarted == false)
    TraceError(F("nRF8001 device can't be started"));
  
  #if (MEASURING)
    dht.begin();  // initialization the DHT11
  #endif
}

void loop() {
  // gets all aci events at regular intervals
  nrf.pollACI();
  
  // gets the device state
  aci_evt_opcode_t status = nrf.getState();
  
  if (status != lastStatus) {
    if (status == ACI_EVT_DEVICE_STARTED) {
      TraceInfo(F("Advertising started"));
    }
    if (status == ACI_EVT_CONNECTED) {
      TraceInfo(F("Connected!"));
    }
    if (status == ACI_EVT_DISCONNECTED) {
      TraceInfo(F("Disconnected or advertising timed out"));
    }
    // updates the last status to this one
    lastStatus = status;
  }
  if (status == ACI_EVT_CONNECTED) {
    #if (MEASURING)
      // reads the sensors
      h = dht.readHumidity();
      t = dht.readTemperature();
      l = analogRead(A0);
    
      // checks if any reads failed and exit early (to try again).
      if (isnan(h) || isnan(t) || isnan(l)) {
        TraceInfo(F("Failed to read from DHT sensor or from the photocell!"));
        return;
      }
      // displays the measures
      Serial.print(F("Humidity: "));   
      Serial.print(h);
      Serial.print(F(" % Temperature: "));
      Serial.print(t);
      Serial.print(F(" *C Light level: "));
      Serial.print(l);
      Serial.println(F(" [0 - 1023]"));
      delay(5000);
    #endif
    // sends data
    // receives data
    // ...
  }
}
