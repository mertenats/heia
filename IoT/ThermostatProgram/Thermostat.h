#pragma once

#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "IThermostatListener.h"
#include "Cooler.h"
#include "Heater.h"

class Thermostat
{
public:
  // constructor
  Thermostat(IThermostatListener& listener, float minTemperature, float maxTemperature);

  // methods
  IThermostatListener::State GetState(void) const;
  unsigned int GetID(void) const;
  float GetMinTemperature(void) const;
  float GetMaxTemperature(void) const;
  void SetTemperatureRange(float minTemperature, float maxTemperature);
  void SetCurrentTemperature(float temperature);
  
private:
  // private method
  void SetState(IThermostatListener::State state);
  
  // data members
  IThermostatListener& m_listener;
  float m_minTemperature;
  float m_maxTemperature;
  unsigned int m_thermostatID;
  static unsigned int g_numberOfThermostats;
  float m_currentTemperature;
  IThermostatListener::State m_state;
  Cooler m_cooler;
  Heater m_heater;
};

