#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

// for temperature
#include "DHT.h"

// local includes
#include "Logging.h"
#include <limits.h>
#include <float.h>

#include "Heater.h"
#include "Light.h"

// logging level
unsigned int g_loggingLevel = 3;

// delay variable 
#define MEASURE_INTERVAL  5000  // 5 x 1000 [ms]

// define to which pin of the Arduino board the pin 2 of the DHT11 chip is connected
#define DHTPIN 8     

// we are using DHT11
#define DHTTYPE DHT11

DHT g_dht(DHTPIN, DHTTYPE);

// variables used for the measurements
uint16_t g_light_level = INT_MAX;
float g_temperature = FLT_MAX;

// declare one instance of each electrical appliance
Heater g_heater;
Light g_light;

// variables used for storing power and consumption values
uint32_t time = INT_MAX;
uint32_t sum_heater_power = INT_MAX;
uint32_t sum_light_power = INT_MAX;

//#define N 1 // returns 240 bytes

#define N (((2048 - 236)/2)/4)

typedef struct power_measure {
  uint16_t heater_power;
  uint16_t light_power;
}power_measure;

power_measure g_history[N];
uint32_t index = 0;

void setup() 
{
  // configure the serial port
  Serial.begin(9600);

  // message shown at startup
  TraceInfo(F("PowerConsumption started..............\n"));

  // initialize the dht library
  g_dht.begin();

  // sets the initial values for the heater / light
  // displays the minimal values
  g_light.SetMinLightLevel(950);
  g_heater.SetMinTemperature(18);

  // initializes the variables
  uint32_t time = 0;
  uint32_t sum_heater_power = 0;
  uint32_t sum_light_power = 0;
}

void loop() 
{
  delay(MEASURE_INTERVAL);
  time += MEASURE_INTERVAL;

  // gets the temperature / light level
  // sends the temperature / light to the designated instance
  g_light.SetCurrentLightLevel(analogRead(A0));
  g_heater.SetCurrentTemperature(g_dht.readTemperature());

  TraceInfoFormat(F("Heater power is %uW\n"), g_heater.GetPower());
  TraceInfoFormat(F("Light power is %uW\n"), g_light.GetPower());
  TraceInfoFormat(F("Total power is %uW\n"), g_heater.GetPower() + g_light.GetPower());

  g_history[index].heater_power = g_heater.GetPower();
  g_history[index].light_power = g_light.GetPower();
  index = (index + 1) % N;

  computeAVG();

  /*

  sum_heater_power += g_heater.GetPower();
  sum_light_power += g_light.GetPower();

  float heaterConsumptionInWh = (time / 1000 / 3600) * (sum_heater_power / 1000);
  float lightConsumptionInWh = (time / 1000/ 3600) * (sum_light_power / 1000);

  g_history[index].heater_conso = heaterConsumptionInWh;
  g_history[index].light_conso = lightConsumptionInWh;
  index = (index + 1) % N;

  unsigned int decimalHeaterConsumptionInWh = int(heaterConsumptionInWh * 10) - (int(heaterConsumptionInWh) * 10);
  TraceInfoFormat(F("Heater consumption is %ld.%dWh\n"), (long) heaterConsumptionInWh, decimalHeaterConsumptionInWh);

  decimalHeaterConsumptionInWh = int(heaterConsumptionInWh * 10) - (int(lightConsumptionInWh) * 10);
  TraceInfoFormat(F("Light consumption is %ld.%dWh\n"), (long) heaterConsumptionInWh, decimalHeaterConsumptionInWh);

  float totalConsumptionInWh = heaterConsumptionInWh + lightConsumptionInWh;
  decimalHeaterConsumptionInWh = int(heaterConsumptionInWh * 10) - (int(totalConsumptionInWh) * 10);
  TraceInfoFormat(F("Total consumption is %ld.%dWh\n"), (long) heaterConsumptionInWh, decimalHeaterConsumptionInWh);


  //TraceInfoFormat(F("Heater sum power: %u\n"), sum_heater_power);
  //TraceInfoFormat(F("Light sum power: %u\n"), sum_light_power);


  // help provided for displaying a floating point value with 1 decimal 
  */

  // check for input
  if (Serial.available() > 0) {
    int minTemperature = Serial.parseInt();
    int minLight = 0;
    if (Serial.available() > 0) { 
      minLight = Serial.parseInt();
    }
    g_light.SetMinLightLevel(minLight);
    g_heater.SetMinTemperature(minTemperature);
  }
}

void computeAVG(void) {
  uint32_t sum_light_power = 0;
  uint32_t sum_heater_power = 0;
  for (uint32_t i = 0; i < index; i++) {
    sum_light_power += g_history[i].light_power;
    sum_heater_power += g_history[i].heater_power;
  }
  TraceInfoFormat(F("Light power avg : %uW\n"), sum_light_power / index);
  TraceInfoFormat(F("Heater power avg : %uW\n"), sum_heater_power / index);

  float heaterConsumptionInWh = (time / 1000 / 3600) * (sum_heater_power);
  float lightConsumptionInWh = (time / 1000/ 3600) * (sum_light_power);

  unsigned int decimalHeaterConsumptionInWh = int(heaterConsumptionInWh * 10) - (int(heaterConsumptionInWh) * 10);
  TraceInfoFormat(F("Heater consumption is %ld.%dWh\n"), (long) heaterConsumptionInWh, decimalHeaterConsumptionInWh);

  decimalHeaterConsumptionInWh = int(heaterConsumptionInWh * 10) - (int(lightConsumptionInWh) * 10);
  TraceInfoFormat(F("Light consumption is %ld.%dWh\n"), (long) heaterConsumptionInWh, decimalHeaterConsumptionInWh);
}

