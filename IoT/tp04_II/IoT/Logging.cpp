#if ARDUINO >= 100
 #include "Arduino.h"
#else
 #include "WProgram.h"
#endif

#include "Logging.h"

#include <stdio.h>
#include <stdarg.h>

#include <avr/pgmspace.h>

#define MAX_MSG 256

#if (TR_LOGLEVEL >= 1)
void PrintProgMem(Print &out, const __FlashStringHelper* format, ...)
{
  // program memory version of printf - copy of format string and result share a buffer
  // so as to avoid too much memory use
  char formatString[MAX_MSG] = {0};
  
  // copy in from program memory
  strncpy_P(formatString, (const char*) format, sizeof(formatString));

  // null terminate - leave last char since we might need it in worst case for result's \0
  //formatString[sizeof(formatString) - 2] = '\0';
  
  // our result buffer...
  int bufferLength = strlen(formatString) + 1;
  char* ptr = &formatString[bufferLength]; 
  // formatting
  va_list args;
  va_start(args, format);
  vsnprintf(ptr, sizeof(formatString) - bufferLength, formatString, args);
  va_end(args);
  
  formatString[sizeof(formatString) - 1] = '\0';
  
  out.print(ptr);
}
 
void PrintHeader(const char* szHeaderType)
{
  unsigned long time = millis();
  int h = int(time / 3600000);
  unsigned long over = time % 3600000;
  int m = int(over / 60000);
  over = over % 60000;
  int s = int(over / 1000);
  int ms = over % 1000;
  SerialPrint(F("%s at time %d h %d m %d s %d ms: "), szHeaderType, h, m, s, ms);
}
#endif



