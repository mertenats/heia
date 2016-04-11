#pragma once

class Heater
{
public:
  // constructor
  Heater();

  // methods
  void TurnOn(void);
  void TurnOff(void);
  bool IsOn(void) const;
 
private:
  // data members
  bool m_isOn;
};

