#pragma once

#ifndef _LOGGING_H_
#define _LOGGING_H_

extern int g_loggingLevel;

// logging macros
#ifndef TR_LOGLEVEL
#define TR_LOGLEVEL 3
#endif

#if (TR_LOGLEVEL >= 1)
void PrintHeader(const char* szHeaderType);
void PrintProgMem(Print &out, const __FlashStringHelper* format, ...);
#define SerialPrint(format, ...) PrintProgMem(Serial, format, ##__VA_ARGS__)
#define StreamPrint(stream, format, ...) PrintProgMem(stream, format, ##__VA_ARGS__) 
#endif

#if (TR_LOGLEVEL >= 1)
#define TraceError(format) if (g_loggingLevel >= 1) { PrintHeader("ERROR"); SerialPrint(format); }
#define TraceErrorFormat(format, ...) if (g_loggingLevel >= 1) {  PrintHeader("ERROR"); SerialPrint(format, __VA_ARGS__); }
#else
#define TraceError(format)
#define TraceErrorFormat(format, ...)
#endif

#if (TR_LOGLEVEL >= 2)
#define TraceInfo(format) if (g_loggingLevel >= 2) { PrintHeader("INFO"); SerialPrint(format); }
#define TraceInfoFormat(format, ...) if (g_loggingLevel >= 2) { PrintHeader("INFO"); SerialPrint(format, __VA_ARGS__); }
#else
#define TraceInfo(format)
#define TraceInfoFormat(format, ...)
#endif

#if (TR_LOGLEVEL >= 1)
#define TraceDebug(format) if (g_loggingLevel >= 3) { PrintHeader("DEBUG"); SerialPrint(format); }
#define TraceDebugFormat(format, ...) if (g_loggingLevel >= 3) { PrintHeader("DEBUG"); SerialPrint(format, __VA_ARGS__);  }
#else
#define TraceDebug(format)
#define TraceDebugFormat(format, ...)
#endif

#endif
