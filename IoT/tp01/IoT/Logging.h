#pragma once
#ifndef LOGGING_H
#define LOGGING_H

#include <stdio.h>
#include "Arduino.h"

extern uint8_t g_logging_Level;

//logging macros
#ifndef TR_LOGLEVEL 
#define TR_LOGLEVEL 3
// 0: no logs
// 1: Error logs
// 2: Error + Info logs
// 3: Error + Info + Debug logs
#endif

void PrintHeader(const char* szHeaderType);
void PrintSerial(const char* format, int arg1);

// ERROR
#if(TR_LOGLEVEL >= 1)
#define TraceError(format) if (g_logging_Level>=1) {PrintHeader("ERROR"); Serial.println(format);}
#define TraceErrorFormat(format, arg1) if (g_logging_Level >= 1) { PrintHeader("ERROR"); PrintSerial(format, arg1);}
#else
#define TraceError(format)
#define TraceErrorFormat(format, arg1)
#endif

// INFO
#if(TR_LOGLEVEL >= 2)
#define TraceInfo(format) if (g_logging_Level>=2) {PrintHeader("INFO"); Serial.println(format);}
#define TraceInfoFormat(format, arg1) if (g_logging_Level >= 2) { PrintHeader("INFO"); PrintSerial(format, arg1);}
#else
#define TraceInfo(format)
#define TraceInfoFormat(format, arg1)
#endif

// DEBUG
#if(TR_LOGLEVEL >= 3)
#define TraceDebug(format) if (g_logging_Level>=3) {PrintHeader("DEBUG"); Serial.println(format);}
#define TraceDebugFormat(format, arg1) if (g_logging_Level >= 3) { PrintHeader("DEBUFG"); PrintSerial(format, arg1);}
#else
#define TraceDebug(format)
#define TraceDebugFormat(format, arg1)
#endif

#endif
