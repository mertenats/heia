#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "ElectricalAppliance.h"

ElectricalAppliance::ElectricalAppliance(uint16_t p_power):
  m_power(p_power),
  m_isOn(false) {}


void ElectricalAppliance::TurnOn(void) {
  m_isOn = true;
}

void ElectricalAppliance::TurnOff(void) {
  m_isOn = false;
}

bool ElectricalAppliance::IsOn(void) {
  return m_isOn;
}

uint16_t ElectricalAppliance::GetPower(void) {
  if (m_isOn == false)
    return 0;
  return m_power;
}