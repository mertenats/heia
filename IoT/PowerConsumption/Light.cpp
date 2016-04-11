#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include <limits.h>

#include "Light.h"
#include "Logging.h"

Light::Light():
  ElectricalAppliance(LIGHT_POWER),
  m_current_light_level(INT_MAX),
  m_min_light_level(INT_MAX) {}


void Light::UpdateState(void) {
    if (m_current_light_level < m_min_light_level)
    TurnOn();   // switches on the light
  else
    TurnOff();  // switches off the light
}

void Light::SetMinLightLevel(uint16_t p_light_level) {
  m_min_light_level = p_light_level;
  UpdateState();
  //Serial.print("Sets minimal light level to : ");
  //Serial.println(m_min_light_level);
  TraceInfoFormat(F("Sets minimal light level to : %u\n"), m_min_light_level);
}

void Light::SetCurrentLightLevel(uint16_t p_light_level) {
  m_current_light_level = p_light_level;
  UpdateState();
  //Serial.print("Current light level : ");
  //Serial.println(m_current_light_level);
  TraceInfoFormat(F("Current light level : %u\n"), m_current_light_level);
}