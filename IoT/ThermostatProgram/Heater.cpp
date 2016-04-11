#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "Heater.h"

Heater::Heater()
: m_isOn(false)
{
}

void Heater::TurnOn(void)
{
  m_isOn = true;
}

void Heater::TurnOff(void)
{
  m_isOn = false;
}

bool Heater::IsOn(void) const
{
  return m_isOn;
}

