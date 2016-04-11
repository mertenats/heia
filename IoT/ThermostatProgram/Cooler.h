#pragma once

class Cooler
{
public:
  // constructor
  Cooler();

  // methods
  void TurnOn(void);
  void TurnOff(void);
  bool IsOn(void) const;
 
private:
  // data members
  bool m_isOn;
};

