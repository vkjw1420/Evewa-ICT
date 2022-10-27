#include <I2Cdev.h>
#include "Mode_Change.h"

void SendAT(String str) {
  Serial.println(str);
  Serial1.print(str);
}

void PrintReq(unsigned long t) {
  unsigned long last = millis();
  while (millis() - last < t) {
    while (Serial1.available() > 0) {
      Serial.write(Serial1.read());
    }
  }
  Serial.println(".");
}

void Mode(int cheak) {
  switch (cheak) {

    case 1://set Master
      Serial.println("set Master");
      SendAT("AT");
      PrintReq(1000);
      //SendAT("AT+NAMEtest");
      //PrintReq(1000);
      SendAT("AT+ROLE?");
      PrintReq(1000);
      SendAT("AT+ROLE1");
      PrintReq(1000);
      SendAT("AT+ROLE?");
      PrintReq(1000);
      SendAT("AT+IMME?");
      PrintReq(1000);
      SendAT("AT+IMME1");
      PrintReq(1000);
      SendAT("AT+IMME?");
      PrintReq(1000);
      SendAT("AT+RESET");
      PrintReq(1000);
      SendAT("AT+RESET");
      delay(1000);
      SendAT("AT");
      PrintReq(1000);
      SendAT("AT+CON10CEA9E9376C");
      PrintReq(1000);
      break;

    case 0://set Slave
      Serial.println("set Slave");
      SendAT("AT");
      PrintReq(1000);
      //        SendAT("AT+NAME?");
      //        PrintReq(1000);
      SendAT("AT+ROLE?");
      PrintReq(1000);
      SendAT("AT+ROLE0");
      PrintReq(1000);
      SendAT("AT+ROLE?");
      PrintReq(1000);
      SendAT("AT+IMME?");
      PrintReq(1000);
      SendAT("AT+IMME1");
      PrintReq(1000);
      SendAT("AT+IMME?");
      PrintReq(1000);
      SendAT("AT+RESET");
      PrintReq(1000);
      SendAT("AT+RESET");
      delay(1000);
      SendAT("AT");
      PrintReq(1000);
      SendAT("AT+ADDR?");
      PrintReq(1000);
      break;
  }
}
