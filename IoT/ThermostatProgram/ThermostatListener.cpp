#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "ThermostatListener.h"

ThermostatListener::ThermostatListener()
{
}

void ThermostatListener::OnStateChanged(unsigned int thermostatID, State oldState, State newState)
{
  if (Serial)
  {
    Serial.print("Thermostat with ID ");
    Serial.print(thermostatID);
    Serial.print(" changed its state from ");
    Serial.print(oldState);
    Serial.print(" to ");
    Serial.println(newState);
  } 
}
