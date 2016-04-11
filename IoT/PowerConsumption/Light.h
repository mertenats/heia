#pragma once

#include "ElectricalAppliance.h"

#define LIGHT_POWER 100

class Light:public ElectricalAppliance {
public:
  Light();
  void SetCurrentLightLevel(uint16_t p_light_level);
  void SetMinLightLevel(uint16_t p_light_level);

private:
  void UpdateState(void);

  uint16_t m_current_light_level;
  uint16_t m_min_light_level;

};