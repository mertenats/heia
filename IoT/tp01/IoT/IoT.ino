#include "Logging.h"

// defines the logs displayd on the serial
// depends on the "TR_LOGLEVEL"
uint8_t g_logging_Level = 3;
uint8_t LED_PIN = 13;
uint16_t counter = 1;

// returns the number of free bytes (RAM)
int displayFreeRam() {
  extern int __heap_start; 
  uint8_t free_memory;
  return ((int)&free_memory) - ((int)&__heap_start);
} 

// the setup function runs once when you press reset or power the board
void setup() {
  Serial.begin(9600);  // initiates the serial communication
  // displays free Ram
  TraceInfoFormat("Free memory : " , displayFreeRam());
  pinMode(LED_PIN, OUTPUT);  // sets the pin as output
}

// the loop function runs over and over again forever
void loop() {
  digitalWrite(LED_PIN, HIGH);  // switches on the led
  TraceInfoFormat("The led is switched on; #", counter++);
  delay(1000);                  // waits for 1000ms -> 1s
  digitalWrite(LED_PIN, LOW);   // switches off the led
  delay(1000);
  
  // prints debug & error logs ...
  TraceDebug("Print a debug message ...");
  TraceError("Print an error message ...");
}
