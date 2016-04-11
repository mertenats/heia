#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "Thermostat.h"

unsigned int Thermostat::g_numberOfThermostats = 0;

Thermostat::Thermostat(IThermostatListener& listener, float minTemperature, float maxTemperature)
: m_listener(listener),
  m_minTemperature(minTemperature),
  m_maxTemperature(maxTemperature),
  m_thermostatID(g_numberOfThermostats),
  m_state(IThermostatListener::AllOff),
  m_currentTemperature(-100)
{
  if (m_minTemperature >= m_maxTemperature)
  {
    m_minTemperature = m_maxTemperature - 1;
  }
  
  g_numberOfThermostats++;
  
}

IThermostatListener::State Thermostat::GetState(void) const
{
  return m_state;
}

unsigned int Thermostat::GetID(void) const
{
  return m_thermostatID;
}

float Thermostat::GetMinTemperature(void) const
{
  return m_minTemperature;
}

float Thermostat::GetMaxTemperature(void) const
{
  return m_maxTemperature;
}

void Thermostat::SetTemperatureRange(float minTemperature, float maxTemperature)
{
  if (minTemperature >= maxTemperature)
  {
    minTemperature = maxTemperature - 1;
  }
  
  if (m_currentTemperature != -100)
  {
    if (minTemperature > m_currentTemperature)
    {
      SetState(IThermostatListener::HeaterOn);
    }
    else if (maxTemperature < m_currentTemperature)
    {
      SetState(IThermostatListener::CoolerOn);
    }
    else
    {
      SetState(IThermostatListener::AllOff);
    }
  }
  
  // store new values
  if (Serial)
  {
    Serial.print("Temperature range is: ");
    Serial.print(minTemperature);
    Serial.print(" - ");
    Serial.print(maxTemperature);
    Serial.println(" degrees");
  }
  m_minTemperature = minTemperature;
  m_maxTemperature = maxTemperature;
}

void Thermostat::SetCurrentTemperature(float temperature)
{
  if (temperature < m_minTemperature)
  {
    SetState(IThermostatListener::HeaterOn);
  }
  else if (temperature > m_maxTemperature)
  {
    SetState(IThermostatListener::CoolerOn);
  }
  else
  {
    SetState(IThermostatListener::AllOff);
  }
  
  // store the temperature
  m_currentTemperature = temperature;
}

void Thermostat::SetState(IThermostatListener::State state)
{
  if (state == m_state)
  {
    // nothing to do
    return;
  }
  
  if (state == IThermostatListener::HeaterOn)
  {
    // turn cooler off
    m_cooler.TurnOff();
    // turn heater on
    m_heater.TurnOn();
  }
  else if (state == IThermostatListener::CoolerOn)
  {
    // turn cooler on
    m_cooler.TurnOn();
    // turn heater off
    m_heater.TurnOff();
  }
  else if (state == IThermostatListener::AllOff)
  {
    // turn cooler off
    m_cooler.TurnOff();
    // turn heater off
    m_heater.TurnOff();
  }
  
  // notify state change
  m_listener.OnStateChanged(m_thermostatID, m_state, state);
      
  // save state 
  m_state = state;
}
