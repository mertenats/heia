#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

// DHT related include
#include "DHT.h"

// Thermostat related includes 
#include "Thermostat.h"
#include "ThermostatListener.h"

// DHT related declarations
// define to which pin of the Arduino board the pin 2 of the DHT11 chip is connected
#define DHTPIN 8     

// we are using DHT11
#define DHTTYPE DHT11

// the first argument is the connected pin, the second the dht chip model
DHT g_dht(DHTPIN, DHTTYPE);

// Thermostat related declarations
ThermostatListener g_thermostatListener;
Thermostat g_thermostat(g_thermostatListener, 15, 25);
Thermostat g_thermostat2(g_thermostatListener, 15, 25);

void setup()
{
  // configure the serial port
  Serial.begin(9600); 
  
  // initialize the dht library
  g_dht.begin();
  
  // set the temperature range
  g_thermostat.SetTemperatureRange(10, 28);
  
  if (Serial)
  {
    Serial.print("Thermostat id is ");
    Serial.println(g_thermostat.GetID());
    
    Serial.print("Second thermostat id is ");
    Serial.println(g_thermostat2.GetID());
  }
}

void loop()
{
  // put your main code here, to run repeatedly:

  // Wait 2 seconds between measurements
  delay(2000);

  // read temperature
  // Read temperature as Celsius
  float t = g_dht.readTemperature();
  
  g_thermostat.SetCurrentTemperature(t);
  
  if (Serial)
  {
    Serial.print("Size of reference is ");
    Serial.println(sizeof(IThermostatListener&));

    Serial.print("Temperature is ");
    Serial.print(t);
    Serial.print(" and thermostat state is ");
    Serial.println(g_thermostat.GetState());
    
    // check for input
    if (Serial.available() > 0)
    {
      int minTemperature = Serial.parseInt();
      int maxTemperature = g_thermostat.GetMaxTemperature();
      if (Serial.available() > 0)
      { 
        maxTemperature = Serial.parseInt();
      }
      g_thermostat.SetTemperatureRange(minTemperature, maxTemperature);
    }
  }
}
