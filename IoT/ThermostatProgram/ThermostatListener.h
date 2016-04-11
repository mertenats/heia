#pragma once

#include "IThermostatListener.h"

class ThermostatListener :
  public IThermostatListener
{
public:
  // constructor
  ThermostatListener();

  // methods
  virtual void OnStateChanged(unsigned int thermostatID, State oldState, State newState);

private:
  // data members
  
};

