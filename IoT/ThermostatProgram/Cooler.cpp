#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "Cooler.h"

Cooler::Cooler()
: m_isOn(false)
{
}

void Cooler::TurnOn(void)
{
  m_isOn = true;
}

void Cooler::TurnOff(void)
{
  m_isOn = false;
}

bool Cooler::IsOn(void) const
{
  return m_isOn;
}

