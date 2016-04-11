#pragma once

#include "ElectricalAppliance.h"

#define HEATER_POWER 1000

class Heater:public ElectricalAppliance {
public:
  Heater();
  void SetCurrentTemperature(float p_temperature);
  void SetMinTemperature(float p_temperature);

private:
  void UpdateState(void);

  float m_min_temperature;
  float m_current_temperature;
};

