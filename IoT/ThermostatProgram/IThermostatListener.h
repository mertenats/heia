#pragma once

class IThermostatListener
{
public:
  // Thermostat state
  enum State
  {
    AllOff = 0,
    CoolerOn = 1,
    HeaterOn = 2
  };
  
  virtual void OnStateChanged(unsigned int thermostatID, State oldState, State newState) = 0;
};
