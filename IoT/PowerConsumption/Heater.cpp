#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include <limits.h>
#include <float.h>

#include "Heater.h"
#include "Logging.h"

Heater::Heater():
  ElectricalAppliance(HEATER_POWER),
  m_current_temperature(FLT_MIN),
  m_min_temperature(FLT_MAX) {}

void Heater::UpdateState(void) {
  if (m_current_temperature < m_min_temperature)
    TurnOn();   // switches on the heater
  else
    TurnOff();  // switches off the heater
}

void Heater::SetMinTemperature(float p_temperature) {
  m_min_temperature = p_temperature;
  UpdateState();
  TraceInfoFormat(F("Sets minimal temperature to : %u\n"), (uint16_t)m_min_temperature);
  //Serial.print("Sets minimal temperature to : ");
  //Serial.println(m_min_temperature);
}

void Heater::SetCurrentTemperature(float p_temperature) {
  m_current_temperature = p_temperature;
  UpdateState();
  //Serial.print("Current temperature : ");
  //Serial.println(m_current_temperature);
  TraceInfoFormat(F("Current temperature : %u\n"), (uint16_t)m_current_temperature);
}