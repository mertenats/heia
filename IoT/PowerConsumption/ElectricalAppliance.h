#pragma once

class ElectricalAppliance {
public:
  ElectricalAppliance(uint16_t p_power);
  uint16_t GetPower(void);

protected:
  void TurnOn(void);
  void TurnOff(void);  

  bool IsOn(void);


private:
  uint16_t m_power;
  bool m_isOn;
};

