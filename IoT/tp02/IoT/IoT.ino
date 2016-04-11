#include "Logging.h"
#include "DHT.h"
#include <avr/sleep.h>
#include <avr/power.h>
#include <avr/wdt.h>

// defines the logs displayd on the serial
// depends on the "TR_LOGLEVEL"
uint8_t g_logging_Level = 3;

// DHT11
#define DHTPIN 8
#define DHTTYPE DHT11

// Initialize DHT sensor for normal 16mhz Arduino
DHT dht(DHTPIN, DHTTYPE);

// ALL THE CODE RELATED TO WATCHDOG WAS TAKEN ON ADAFRUIT
// https://learn.adafruit.com/low-power-wifi-datalogging/power-down-sleep
int sleepIterations = 0;
#define READING_SENSORS_FREQ_SECONDS  60
#define MAX_SLEEP_ITERATIONS  READING_SENSORS_FREQ_SECONDS / 8
volatile bool watchdogActivated = false;

// measures...
volatile float h;  // humidity
volatile float t;  // temperature
volatile int l;    // light

// for calibration ...
int lowLight = 0;
int highLight = 1023;

// Define watchdog timer interrupt.
ISR(WDT_vect) {
  // sets to true the watchdog activated flag.
  watchdogActivated = true;
}

// puts the Arduino in sleep mode
void sleep() {
  // Set sleep to full power down. Only external interrupts or 
  // the watchdog timer can wake the CPU!
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  // Turn off the ADC while asleep.
  power_adc_disable();
  // Enable sleep and enter sleep mode.
  sleep_mode();
  // CPU is now asleep and program execution completely halts!
  // Once awake, execution will resume at this point.
  // When awake, disable sleep mode and turn on all devices.
  sleep_disable();
  power_all_enable();
}

// the setup function runs once when you press reset or power the board
void setup() {
  Serial.begin(9600);  // initiates the serial communication
  dht.begin();  // initiates the DHT11
  
  // calibration
  while (millis() < 5000) {
    l = analogRead(A0);
    if (l > highLight) {
      highLight = l;
    }
    if (l < lowLight) {
      lowLight = l;
    }
  }
  
  // This next section of code is timing critical, so interrupts are disabled.
  // See more details of how to change the watchdog in the ATmega328P datasheet
  // around page 50, Watchdog Timer.
  noInterrupts();
  // Set the watchdog reset bit in the MCU status register to 0.
  MCUSR &= ~(1<<WDRF);
  // Set WDCE and WDE bits in the watchdog control register.
  WDTCSR |= (1<<WDCE) | (1<<WDE);
  // Set watchdog clock prescaler bits to a value of 8 seconds.
  WDTCSR = (1<<WDP0) | (1<<WDP3);
  // Enable watchdog as interrupt only (no reset).
  WDTCSR |= (1<<WDIE);
  // Enable interrupts again.
  interrupts();
}

void readSensors() {
  // reads the sensors
  h = dht.readHumidity();
  t = dht.readTemperature();
  l = analogRead(A0);
  // adjusts the value (from current value, low and high value to a range of 0-100)
  l = map(l, lowLight, highLight, 0, 100);
  
   // Check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t) || isnan(l)) {
    TraceError("Failed to read from DHT sensor or from the photocell!");
    return;
  }
  
  // displays the values 
  Serial.print(F("Humidity: "));   
  Serial.print(h);
  Serial.print(F(" % Temperature: "));
  Serial.print(t);
  Serial.print(F(" *C Light level: "));
  Serial.print(l);
  Serial.println(F(" [0 - 100]"));
  delay(100);
}

// the loop function runs over and over again forever
void loop() {
  // Don't do anything unless the watchdog timer interrupt has fired.
  if (watchdogActivated) {
    watchdogActivated = false;
    // Increase the count of sleep iterations and take a sensor
    // reading once the max number of iterations has been hit.
    sleepIterations += 1;
    if (sleepIterations >= MAX_SLEEP_ITERATIONS) {
      // Reset the number of sleep iterations.
      sleepIterations = 0;
      readSensors();
    }
  }
  // Go to sleep!
  sleep();
}
