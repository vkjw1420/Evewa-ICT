#include <Wire.h>
#include "Adafruit_VL53L0X.h" 
#include "vl53l0x_api_core.h"
#include "Bitcraze_PMW3901.h" 

// MPU6050
const int mpu6050_addr=0x68; 
int16_t GyX,GyY,GyZ;
double throttle = 0;

// VL53L0X : 높이(레이저방식)
Adafruit_VL53L0X lox = Adafruit_VL53L0X();  // I2C통신
VL53L0X_RangingMeasurementData_t measure;
double tHeight = 0;//target height
double cHeight = 0;//current height 


// PMW3901 : 전후좌우 움직임(표면 흐름 검출 <- 저사양의 카메라 이용)
Bitcraze_PMW3901 flow(7); // SPI 통신 7 : 
int16_t deltaX,deltaY;
double FrcH ;

void setup(){  
  Serial.begin(9600); 
  Serial1.begin(9600); 
  Wire.begin();  
  lox.begin(); 
// Gets the fraction enable parameter indicating the resolution of range measurements.
//  Gets the fraction enable state, which translates to the resolution of range measurements as follows :Enabled:=0.25mm resolution, (Not Enabled:=1mm resolution)
  VL53L0X_SetRangeFractionEnable(lox.getMyDevice(), true);
// Set the VCSEL (vertical cavity surface emitting laser) pulse period for the  given period type (pre-range or final range) to the given value in PCLKs./
// Longer periods seem to increase the potential range of the sensor.
// Valid values are (even numbers only):
// pre:  12 to 18 (initialized default: 14)
//  final: 8 to 14 (initialized default: 10)
// increase laser pulse periods (defaults are 14 and 10 PCLKs) 
  VL53L0X_set_vcsel_pulse_period(lox.getMyDevice(),VL53L0X_VCSEL_PERIOD_PRE_RANGE, 18);
  VL53L0X_set_vcsel_pulse_period(lox.getMyDevice(),VL53L0X_VCSEL_PERIOD_FINAL_RANGE, 14);

  flow.begin();

  Wire.beginTransmission(mpu6050_addr); 
  Wire.write(0x6b);  
  Wire.write(0);     
  Wire.endTransmission(true); 
 } 
 
void loop(){  
  Wire.beginTransmission(mpu6050_addr); 
  Wire.write(0x43);  
  Wire.endTransmission(false); 
  Wire.requestFrom(mpu6050_addr, 6, true);  
  GyX=Wire.read()<<8|Wire.read();  
  GyY=Wire.read()<<8|Wire.read();  
  GyZ=Wire.read()<<8|Wire.read();

  static int st_bitcraze = 0;
  static int st_vl53L0X = 0;
  static bool vl53L0X_ready = false;
  static bool pwm3901_ready = true;

//      VL53L0X_PerformSingleMeasurement(lox.getMyDevice());//3ms  
// Perform Reference Calibration.
// Perform a reference calibration of the Device.
// * This function should be run from time to time before doing
// * a ranging measurement.
// * This function will launch a special ranging measurement, so if interrupt are enable an interrupt will be done.
// * This function will clear the interrupt generated automatically.   

//      VL53L0X_GetRangingMeasurementData(lox.getMyDevice(), &measure);//3ms
// Retrieve the measurements from device for a given setup
// * Get data from last successful Histogram measurement
// * USER should take care about  @a VL53L0X_GetNumberOfROIZones() before get data.
// * PAL will fill a NumberOfROIZones times the corresponding data structure  used in the measurement function.   

 
  
  /*
  1. 오프셋 값 구하기
  1000번 읽어서 더해서 1000으로 나눈값 - 오프셋 값
  2. 센서 값 보정
     GyX -= OffX
  3. 각속도 구하기
     GyX /= 131 => 각속도(w)
  4. 센서 입력 주기 => dt(1/1000)
  5. 변화각도 구하기
     dQ = w*dt
  6. 현재각도 구하기
     Qnow = Qprev + dQ
   */
  static int16_t GyXOff,GyYOff,GyZOff;
  static int32_t GyXSum,GyYSum,GyZSum;
  #define NUM_SAMPLE 1000
  static int cnt_sample = NUM_SAMPLE;
  if(cnt_sample>0) {
    GyXSum += GyX;
    GyYSum += GyY;
    GyZSum += GyZ;
    cnt_sample --;
    
    if(cnt_sample==0) {
      GyXOff = GyXSum/NUM_SAMPLE;
      GyYOff = GyYSum/NUM_SAMPLE;
      GyZOff = GyZSum/NUM_SAMPLE;
    }
//    delayMicroseconds(500);
    delay(1);    
    return;
  }

  // 2. 센서 값 보정
  GyX -= GyXOff;
  GyY -= GyYOff;
  GyZ -= GyZOff;
    
  // 3. 각속도 w 구하기 
  double GyXR, GyYR, GyZR;
  GyXR = GyX/131.0;// w
  GyYR = GyY/131.0;
  GyZR = GyZ/131.0;

  // 4. dt 구하기
  static long t_prev = 0;
  long t_now = micros();
  double dt = 0.0;
  if(t_prev!=0) dt = (t_now - t_prev)/1000000.0;
  t_prev = t_now;


  // 변화각도 구하기 d@ = w*dt
  static double AngleX, AngleY, AngleZ;
  AngleX = AngleX + GyXR*dt;
  AngleY = AngleY + GyYR*dt;
  AngleZ = AngleZ + GyZR*dt;  
  if(throttle<10) {
    AngleX = 0;
    AngleY = 0;
    AngleZ = 0;
  }
  
  // 각도오차 = 목표각도 - 현재각도
  static double tAngleX=0, tAngleY=0, tAngleZ=0;
  static double t_tAngleX=0, t_tAngleY=0;
  double eAngleX, eAngleY, eAngleZ;
  
  eAngleX = tAngleX - AngleX;
  eAngleY = tAngleY - AngleY;
  eAngleZ = tAngleZ - AngleZ;
  if(throttle<10) {
    tAngleX = 0;
    tAngleY = 0;
    tAngleZ = 0;
  }
  
  // 균형힘 구하기
  double FrcX, FrcY, FrcZ; 
  double KpX = 2.13125, KpY = 2.13125, KpZ = 1.0;//0.5, 2.0
//  double Kp = 1.0, Kd = 0.3, Ki = 0.4;//0.5, 2.0  
  FrcX = KpX * eAngleX;
  FrcY = KpY * eAngleY;
  FrcZ = KpZ * eAngleZ;

  double KdX = 1.0, KdY = 1.0, KdZ = 1.0;
  // 빠른회전 상쇄하기
  FrcX = FrcX + KdX * -GyXR;
  FrcY = FrcY + KdY * -GyYR;
  FrcZ = FrcZ + KdZ * -GyZR;


  double KiX = 0.2, KiY = 0.2, KiZ = 0.07;  
  // 느린 각도 접근 상쇄하기
  static double SFrcX, SFrcY, SFrcZ;
  SFrcX = SFrcX + KiX* eAngleX*dt; 
  SFrcY = SFrcY + KiY* eAngleY*dt;
  SFrcZ = SFrcZ + KiZ* eAngleZ*dt;  
//  SFrcZ = SFrcZ + 0* eAngleZ*dt;  // Why Ki=0 ?
  if(throttle<10) {
    SFrcX = 0;
    SFrcY = 0;
    SFrcZ = 0;
  }
  FrcX = FrcX + SFrcX;
  FrcY = FrcY + SFrcY;
  FrcZ = FrcZ + SFrcZ; 



  // 사용자 입력 받기
  static uint8_t cnt_msg;
  if(Serial1.available()>0) {
    while(Serial1.available()>0) {
      uint8_t msp_data = Serial1.read();
      if(msp_data=='$') cnt_msg = 0; 
      else cnt_msg++;
      if(cnt_msg==8) {//4) {//
        throttle = msp_data;//0~250cm        
        tHeight = throttle;
        if(tHeight>170) tHeight=170;
//        constrain(tHeight, 0, 170);
      }
      else if(cnt_msg==5) t_tAngleY = msp_data-125;
      else if(cnt_msg==6) t_tAngleX = -(msp_data-125);
      else if(cnt_msg==7) tAngleZ = -(msp_data-125);
    }

      #define LOW1_ANGLE_RANGE 5  // throttle, YAW 하한 <- 조정기 아날로그 입력 드리프트
      if(throttle<LOW1_ANGLE_RANGE) throttle = 0;
      if(tAngleZ<LOW1_ANGLE_RANGE && tAngleZ>-LOW1_ANGLE_RANGE) tAngleZ = 0;

      #define LOW2_ANGLE_RANGE 2  // ROLL, PITCH 하한 <- 조정기 아날로그 입력 드리프트
      if(t_tAngleY<LOW2_ANGLE_RANGE && t_tAngleY>-LOW2_ANGLE_RANGE) {
//        tAngleY = 0;
        pwm3901_ready = true;     
      }
      if(t_tAngleX<LOW2_ANGLE_RANGE && t_tAngleX>-LOW2_ANGLE_RANGE) {
//        tAngleX = 0;
        pwm3901_ready = true;       
      }

      if( !(t_tAngleX < 2 && t_tAngleX > -2) || !(t_tAngleY < 2 && t_tAngleY > -2)) {
        tAngleX = t_tAngleX;    
        tAngleY = t_tAngleY;          
        pwm3901_ready = false;
      }
            
      #define ANGLE_RANGE 45
      if(t_tAngleY<-ANGLE_RANGE) tAngleY = -ANGLE_RANGE;
      else if(t_tAngleY>ANGLE_RANGE) tAngleY = ANGLE_RANGE;
      if(t_tAngleX<-ANGLE_RANGE) tAngleX = -ANGLE_RANGE;
      else if(t_tAngleX>ANGLE_RANGE) tAngleX = ANGLE_RANGE;
  }

  static int flow_cnt = 0;
  flow_cnt++;
  #define F_CNT 5.0

  static double PixelX=0.0, PixelY=0.0;  
  static double tPixelX=0.0, tPixelY=0.0;
  double ePixelX, ePixelY;
  static double SPixelX=0, SPixelY=0; 

  static long t_prev_pwm3901 = 0;
  long t_now_pwm3901;
  double dt_pwm3901 = 0.0;

  if(pwm3901_ready) { 
    pwm3901_ready = true;   

    if(flow_cnt == F_CNT) {
      flow_cnt = 0;       
      flow.readMotionCount(&deltaX, &deltaY);//가급적 최대 100Hz 이하, 즉 10ms 이상에서 작동 

      t_now_pwm3901 = micros();
      if(t_prev_pwm3901!=0) dt_pwm3901 = (t_now_pwm3901 - t_prev_pwm3901)/1000000.0;
      t_prev_pwm3901 = t_now_pwm3901;
    
//      PixelX = PixelX + (deltaX/F_CNT)*dt_pwm3901;
//      PixelY = PixelY + (deltaY/F_CNT)*dt_pwm3901;
      PixelX = PixelX + deltaX*dt_pwm3901;
      PixelY = PixelY + deltaY*dt_pwm3901;
      
      ePixelX = tPixelX - PixelX;
      ePixelY = tPixelY - PixelY;

      const double KP = 1; //0.875;  // 1
      const double KD = 0.5;  // 0.5
      const double KI = 0.3; // 0.3
  
      static double xKp = KP, yKp = KP;  
      tAngleY = -(xKp*ePixelX); //왼쪽(deltaX=> +값)으로 가면 PixelX=>+, (tPixelX==0 - PixelX)=>-, 따라서 오른쪽으로 가기 위해(FrcFlowY(ROLL) 양수)되도록 -값 취함
      tAngleX = -(yKp*ePixelY); //앞으로(deltaY=> +값) 가면 PixelY=>+, (tPixelY==0 - PixelX)=>-, 따라서 뒤로 가기 위해(FrcFlowX(PITCH) 양수)되도록 -값 취함

       
      static double xKd = KD, yKd = KD;
//      tAngleY += -(xKd * -(deltaX/F_CNT)); // 위와 동일한 개념
//      tAngleX += -(yKd * -(deltaY/F_CNT)); // 위와 동일한 개념
      tAngleY += -(xKd * -deltaX); // 위와 동일한 개념
      tAngleX += -(yKd * -deltaY); // 위와 동일한 개념



//    Serial.print(" '-(xKd*-deltaX) ");     Serial.print(-(xKd*-deltaX)); 
//    Serial.print(" Kd_tAngleY ");     Serial.print(tAngleY,6); 
//    Serial.print(" Pre_SPixelX ");     Serial.print(SPixelX,6); 
        
      double xKi = KI, yKi = KI;
  
      SPixelX += - xKi* ePixelX*dt_pwm3901; //위와 동일한 개념
      SPixelY += - yKi* ePixelY*dt_pwm3901; //위와 동일한 개념

      tAngleY += SPixelX;
      tAngleX += SPixelY;
      
      if(throttle<10) {
        SPixelX = 0;
        SPixelY = 0;  //10-13일 변경
        
        PixelX = 0;
        PixelY = 0;
        tAngleX = 0;
        tAngleY = 0;
        tAngleZ = 0;      
      }
      
      SFrcX = 0;
      SFrcY = 0;
    }
  }
    else {
    flow_cnt = 0;     
    PixelX = 0;
    PixelY = 0;
    SPixelX = 0;
    SPixelY = 0;
    pwm3901_ready = false;    
    t_prev_pwm3901 = micros();   
  }


    
  static int vl53_cnt = 0;
  vl53_cnt++;
  #define vl53_cnt_MAX 10

  if(vl53_cnt == vl53_cnt_MAX) {
    vl53_cnt =0;
    
    if(st_vl53L0X==0) {  
      VL53L0X_PerformSingleMeasurement(lox.getMyDevice());//3ms 
      st_vl53L0X=1;
    }            
    else {
      st_vl53L0X=0; 
      VL53L0X_GetRangingMeasurementData(lox.getMyDevice(), &measure);//3ms

      cHeight = measure.RangeMilliMeter/10.0;//출력값 mm, 10으로 나누면 cm   


      double hKp = 1;//1.0
      double hKd = 0.3;//0.3
      double hKi = 0.3;//0.4
      double eHeight;
      double dHeight;
      static double pHeight;       
      static double SHeight;

      static long t_prev_vl53L0X = 0;
      double dt_vl53L0X = 0.0;    
      long t_now_vl53L0X = micros();

      if(t_prev_vl53L0X!=0) dt_vl53L0X = (t_now_vl53L0X - t_prev_vl53L0X)/1000000.0;  
      t_prev_vl53L0X = t_now_vl53L0X;

      eHeight = (tHeight<=5.0)?0:(tHeight-cHeight);
    
      FrcH = hKp * eHeight;

      dHeight = cHeight - pHeight;
      pHeight = cHeight;   
      FrcH += hKd * -dHeight / dt_vl53L0X;
    
      SHeight += hKi * eHeight * dt_vl53L0X;
      FrcH += SHeight;

      if(throttle<10) SHeight = 0;
      if(throttle<10) FrcH = 0;
    if(FrcH<0) FrcH=0;
    else if(FrcH>250) FrcH=250;
    } 
  }


  // 모터속도 분배하기
  static double SpeedA, SpeedB, SpeedC, SpeedD;

  SpeedA = FrcH  + FrcZ + FrcY + FrcX;
  SpeedB = FrcH  - FrcZ - FrcY + FrcX;
  SpeedC = FrcH  + FrcZ - FrcY - FrcX;
  SpeedD = FrcH  - FrcZ + FrcY - FrcX;

//  SpeedA = FrcH + FrcY + FrcX;
//  SpeedB = FrcH - FrcY + FrcX;
//  SpeedC = FrcH - FrcY - FrcX;
//  SpeedD = FrcH + FrcY - FrcX;

  if(throttle<10) {
    SpeedA = 0;
    SpeedB = 0;
    SpeedC = 0;
    SpeedD = 0;
  }
  if(SpeedA<0) SpeedA = 0; 
  else if(SpeedA>250) SpeedA = 250;
  if(SpeedB<0) SpeedB = 0;
  else if(SpeedB>250) SpeedB = 250;
  if(SpeedC<0) SpeedC = 0;
  else if(SpeedC>250) SpeedC = 250;
  if(SpeedD<0) SpeedD = 0;
  else if(SpeedD>250) SpeedD = 250;



  // 모터 구동
  const int motorA=6;
  const int motorB=10;
  const int motorC=9; 
  const int motorD=5;
  analogWrite(motorA, (int)SpeedA);  
  analogWrite(motorB, (int)SpeedB);
  analogWrite(motorC, (int)SpeedC);
  analogWrite(motorD, (int)SpeedD);



/*
  static unsigned int cnt_loop; 
  cnt_loop++; 
  if(cnt_loop%20!=0) return; 

  static float ttAngleX, ttAngleY; 
  static float PPixelX, PPixelY; 
  static byte *tAngleXPointer = (byte*)&ttAngleX;
  static byte *tAngleYPointer = (byte*)&ttAngleY;
  static byte *PixelXPointer = (byte*)&PPixelX;
  static byte *PixelYPointer = (byte*)&PPixelY;
  static byte byteValue;


//    PPixelX=(float)PixelX;
//    ttAngleY=(float)tAngleY;
//    PPixelY=(float)PixelY;
//    ttAngleX=(float)tAngleX;

    PPixelX=(float)PixelX;
    ttAngleY=(float)tAngleY;
    PPixelY=(float)AngleY;
    ttAngleX=(float)eAngleY;

  int i;
// Transmitt PixelX  
  for( i= 0; i < 4; i++){
    byteValue = *(PixelXPointer + i);      
    Serial1.write(byteValue);    
  }

// Transmitt tAngleY 
  for( i= 0; i < 4; i++){
    byteValue = *(tAngleYPointer + i);
    Serial1.write(byteValue);    
  }

// Transmitt PixelY 
  for( i= 0; i < 4; i++){
    byteValue = *(PixelYPointer + i);
    Serial1.write(byteValue);    
  }

// Transmitt tAngleX
  for( i= 0; i < 4; i++){
    byteValue = *(tAngleXPointer + i);
    Serial1.write(byteValue);    
  }
*/
}
