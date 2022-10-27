#include <I2Cdev.h>
#include "mpu_sensor.h"
#include "Mode_Change.h"

//가변저항
//int pin= A0;

//플렉스센서
#define SENSOR1 26
#define SENSOR2 25 //검지
#define SENSOR3 24 //중지
#define SENSOR4 23 //약지
#define SENSOR5 22 //소지

//int flexpin_1 = A2;
//int flexpin_2 = A3;
//int flexpin_3 = A4;
//int flexpin_4 = A5;

unsigned char FlexSensor[5];
unsigned char AccelValue = 0;

uint8_t message[100];
char read_data = 0;
unsigned char command[100];
unsigned int Count = 0;

char throttle = NULL;
char roll_target_angle = NULL;
char pitch_target_angle = NULL;
char yaw_target_angle = NULL;

char Start_Flag = 0;
char Mode_Flag = 0;
char WT_Flag = 0;

int takeOff_Key = 0;

void setup() {
  Serial1.begin(9600);
  Serial1.println("Hello!");
  Serial.begin(9600);

  //  Mode(0);
  //Init_mpu6050();
}

//저항값 비교후 0/1로 변경하는 함수
//int value(int flex, int resist) {
//  if (flex > resist) return 0;
//  else return 1;
//}

void call(uint8_t t, uint8_t r, uint8_t p, uint8_t y) {
  message[0] = '$';
  message[1] = 0;
  message[2] = 0;
  message[3] = 0;
  message[4] = 0;
  message[5] = r;
  message[6] = p;
  message[7] = y;
  message[8] = t;
  message[9] = 0;
  message[10] = 0;

  for (int i = 0; i < 12; i++) {
    Serial.write(message[i]);
    //Serial.write((int)message[i]);

    Serial1.print(message[i]);
  }
  Serial1.println();
}

int takeOff() {  // Flag 사용해서 한번 실행 후 착륙 or 긴급정지 이전까지는 실핼 금지하기
  int current_seconds = 0;
  int previous_seconds = 0;
  Serial1.println("TO");

  takeOff_Key = 1;

  throttle = 249;
  roll_target_angle = 125;
  pitch_target_angle = 125;
  yaw_target_angle   = 125;

  current_seconds = millis();
  previous_seconds = millis();
  while (previous_seconds - current_seconds <= 5000) {
    previous_seconds = millis();
    call(throttle, roll_target_angle, pitch_target_angle, yaw_target_angle);
    myFlexRead();
    if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 0))
    {
      Serial1.println("긴급 정지");
      throttle = 0;
      roll_target_angle = 125;
      pitch_target_angle = 125;
      yaw_target_angle   = 125;
      takeOff_Key = 0;
      return -1;
    }
  }
  Serial1.println(throttle);
  while (throttle < 80) {
    throttle -= 30;
    current_seconds = millis();
    previous_seconds = millis();
    while (previous_seconds - current_seconds <= 1000) {
      previous_seconds = millis();
      call(throttle, roll_target_angle, pitch_target_angle, yaw_target_angle);
      myFlexRead();
      if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 0))
      {
        Serial1.println("긴급 정지");
        throttle = 0;
        roll_target_angle = 125;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;
        takeOff_Key = 0;
        return -1;
      }
    }
    Serial1.println(throttle);
  }
  throttle = 80;

  takeOff_Key = 0;
}

void myFlexRead() {
  //  Serial1.println(AccelValue);
  Read_Axis_From_Mpu6050();

  AccelValue = AccelMoni();

  if (digitalRead(SENSOR1))  FlexSensor[0] = 0;
  else                       FlexSensor[0] = 1;

  if (digitalRead(SENSOR2))  FlexSensor[1] = 0;
  else                       FlexSensor[1] = 1;

  if (digitalRead(SENSOR3))  FlexSensor[2] = 0;
  else                       FlexSensor[2] = 1;

  if (digitalRead(SENSOR4))  FlexSensor[3] = 0;
  else                       FlexSensor[3] = 1;

  if (digitalRead(SENSOR5))  FlexSensor[4] = 0;
  else                       FlexSensor[4] = 1;

  //    Serial1.println(FlexSensor[0]);
  //    Serial1.println(FlexSensor[1]);
  //    Serial1.println(FlexSensor[2]);
  //    Serial1.println(FlexSensor[3]);
  //    Serial1.println(FlexSensor[4]);
  //    Serial1.println("-------------");

  //  if (Serial.available()) {
  //
  //    Serial1.write(Serial.read());
  //
  //  }
  //
  //  if (Serial1.available()) {
  //
  //    Serial.write(Serial1.read());
  //
  //  }

}


void loop() {
  //  Serial1.println("Hello");
  pinMode(SENSOR1, INPUT);
  pinMode(SENSOR2, INPUT);
  pinMode(SENSOR3, INPUT);
  pinMode(SENSOR4, INPUT);
  pinMode(SENSOR5, INPUT);

  AccelValue = AccelMoni();
  Read_Axis_From_Mpu6050();

  if (digitalRead(SENSOR1))  FlexSensor[0] = 0;
  else                       FlexSensor[0] = 1;

  if (digitalRead(SENSOR2))  FlexSensor[1] = 0;
  else                       FlexSensor[1] = 1;

  if (digitalRead(SENSOR3))  FlexSensor[2] = 0;
  else                       FlexSensor[2] = 1;

  if (digitalRead(SENSOR4))  FlexSensor[3] = 0;
  else                       FlexSensor[3] = 1;

  if (digitalRead(SENSOR5))  FlexSensor[4] = 0;
  else                       FlexSensor[4] = 1;

  //  Serial1.println(FlexSensor[0]);
  //  Serial1.println(FlexSensor[1]);
  //  Serial1.println(FlexSensor[2]);
  //  Serial1.println(FlexSensor[3]);
  //  Serial1.println(FlexSensor[4]);
  //  Serial1.println("-------------");

  /////////////////////////////////////////////////////////

  if (Mode_Flag == 0) {
    //    Serial.println(Mode_Flag);
    while (Serial.available()) {
      read_data = (char)Serial.read();
      //      Serial.println(read_data);
      command[Count] = read_data;
      //      Serial.write(command[Count]);
      Count++;
    }
    //    read_data=0;

    //        Serial.println(command[0]);

    if ((FlexSensor[0] == 1) && (FlexSensor[1] == 0) && (FlexSensor[2] == 0) && (FlexSensor[3] == 1) && (FlexSensor[4] == 1)) {
      Mode_Flag = 1;
      Serial1.println("Mode changing...");
      delay(2000);
      Serial1.println("Mode changing success!");
      //      Mode(1);
    }
  }

  //Mode_Flag==1 일때
  else {
    //    Serial.println(Mode_Flag);
    if (Start_Flag == 0) {
      //      Serial.println(Start_Flag);
      if ((FlexSensor[0] == 1) && (FlexSensor[1] == 0) && (FlexSensor[2] == 0) && (FlexSensor[3] == 1) && (FlexSensor[4] == 1)) {
        Mode_Flag = 0;
        Serial1.println("Mode changing");
        delay(2000);
        Serial1.println("Mode changing success!");
        //        Mode(0);
      }
      //시동
      if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 0) && (FlexSensor[3] == 0) && (FlexSensor[4] == 0))
      {
        Start_Flag = 1;
        Serial1.println("ON");
        delay(3000);
        throttle = 10;
        roll_target_angle = 125;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;

        call(throttle, roll_target_angle, pitch_target_angle, yaw_target_angle);

        //        sprintf(message, "T%dR%dP%dY%d", throttle, roll_target_angle, pitch_target_angle, yaw_target_angle);
        //        Set_BT_Send_Data(message);
      }
    }
    //Start_Flag==1 일때
    else {
      //      Serial.println(Start_Flag);
      //이륙
      if ((FlexSensor[0] == 0) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 1))
      {
        Serial1.println("TO");       /***************************     테스트용으로 take off 코드 실행 중     ****************************************/
        if (takeOff_Key != 1) {
          takeOff();
        }
      }

      //상승
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 0) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 1))
      {
        Serial1.println("UP");
        throttle = throttle + 7;
        delay(1000);
      }

      //하강
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 0) && (FlexSensor[3] == 1) && (FlexSensor[4] == 1))
      {
        Serial1.println("DW");
        throttle = throttle - 7;
        delay(1000);
      }
      //좌이동
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 0) && (FlexSensor[4] == 1))
      {
        Serial1.println("LM");
        //throttle = 100;
        roll_target_angle = 135;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;
      }
      //우이동
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 0) && (FlexSensor[4] == 0))
      {
        Serial1.println("RM");
        //throttle = 65;
        roll_target_angle = 115;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;
      }/*
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 0))
      {
        Serial1.println("RM");
        throttle = 0;
        roll_target_angle = 125;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;
      }*/
      //착륙
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 0))
      {
        Serial1.println("LD");
        throttle = 0;
        roll_target_angle = 125;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;
        delay(500);
        Start_Flag = 0;
      }
      //대기
      else if ((FlexSensor[0] == 1) && (FlexSensor[1] == 1) && (FlexSensor[2] == 1) && (FlexSensor[3] == 1) && (FlexSensor[4] == 1))
      {
        Serial1.println("WT");
        if (WT_Flag == 0) {
          throttle = throttle + 1;
          WT_Flag = 1;
          Serial1.print("sucess");
        }
        else if (WT_Flag == 1) {
          throttle = throttle - 1;
          WT_Flag = 0;
          Serial1.print("yes");
        }

        roll_target_angle = 125;
        pitch_target_angle = 125;
        yaw_target_angle   = 125;
        delay(1000);
      }
      call(throttle, roll_target_angle, pitch_target_angle, yaw_target_angle);
    }
  }
}
