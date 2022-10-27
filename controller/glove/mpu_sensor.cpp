#include <I2Cdev.h>
#include "MPU6050_6Axis_MotionApps20.h"
#include "Wire.h"
#include "mpu_sensor.h"

#define OUTPUT_READABLE_YAWPITCHROLL
#define INTERRUPT_PIN 6

MPU6050 mpu;

bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer

Quaternion q;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
float euler[3];         // [psi, theta, phi]    Euler angle container
float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector
volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high

float X_temp[5];
float Y_temp[5];
float Z_temp[5];
float Total_temp[2];
float Temp_buf[5];
float X_Data;
float Y_Data;
float Z_Data;

float euler_X_Data;
float euler_Y_Data;
float euler_Z_Data;



void dmpDataReady()
{
  mpuInterrupt = true;
}

void Init_mpu6050(void)
{
  // I2C 설정
  Wire.begin();
  Wire.setClock(400000);

  // initialize device
  mpu.initialize();

  pinMode(INTERRUPT_PIN, INPUT);

  // verify connection
  Serial.println(mpu.testConnection() ? "MPU6050 connection successful" : "MPU6050 connection failed");
  delay(1000);

  devStatus = mpu.dmpInitialize();

  // supply your own gyro offsets here, scaled for min sensitivity

  mpu.setXAccelOffset(-2775);
  mpu.setYAccelOffset(-3848);
  mpu.setZAccelOffset(1350);
  mpu.setXGyroOffset(64);
  mpu.setYGyroOffset(-56);
  mpu.setZGyroOffset(44);


  // make sure it worked (returns 0 if so)
  if (devStatus == 0)
  {
    // turn on the DMP, now that it's ready
    mpu.setDMPEnabled(true);

    // enable Arduino interrupt detection
    attachInterrupt(0, dmpDataReady, RISING);
    mpuIntStatus = mpu.getIntStatus();

    // set our DMP Ready flag so the main loop() function knows it's okay to use it
    dmpReady = true;

    // get expected DMP packet size for later comparison
    packetSize = mpu.dmpGetFIFOPacketSize();
  }
}
float Read_DMP_MPU6050(uint8_t sel)
{
  if (!dmpReady)
  {
    //Serial.println("dmpReady return");
    return;
  }
  while (!mpuInterrupt && fifoCount < packetSize)
  {
    if (mpuInterrupt && fifoCount < packetSize)
    {
      fifoCount = mpu.getFIFOCount();
    }
  }
  // reset interrupt flag and get INT_STATUS byte
  mpuInterrupt = false;
  mpuIntStatus = mpu.getIntStatus();

  // get current FIFO count
  fifoCount = mpu.getFIFOCount();

  // check for overflow (this should never happen unless our code is too inefficient)
  if ((mpuIntStatus & _BV(MPU6050_INTERRUPT_FIFO_OFLOW_BIT)) || fifoCount >= 1024)
  {
    // reset so we can continue cleanly
    mpu.resetFIFO();
    fifoCount = mpu.getFIFOCount();
    //Serial.println(F("FIFO overflow!"));
  }
  else if (mpuIntStatus & _BV(MPU6050_INTERRUPT_DMP_INT_BIT))
  {
    // wait for correct available data length, should be a VERY short wait
    while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

    // read a packet from FIFO
    mpu.getFIFOBytes(fifoBuffer, packetSize);

    // track FIFO count here in case there is > 1 packet available
    // (this lets us immediately read more without waiting for an interrupt)
    fifoCount -= packetSize;

    #ifdef OUTPUT_READABLE_YAWPITCHROLL
        // display Euler angles in degrees
        mpu.dmpGetQuaternion(&q, fifoBuffer);
        mpu.dmpGetGravity(&gravity, &q);
        mpu.dmpGetYawPitchRoll(ypr, &q, &gravity);
    
        ypr[0] = ypr[0] * 180 / M_PI;
        ypr[1] = ypr[1] * 180 / M_PI;
        ypr[2] = ypr[2] * 180 / M_PI;


//        Serial1.print("ypr\t");
//        Serial1.print(ypr[0]);
//        Serial1.print("\t");
//        Serial1.print(ypr[1]);
//        Serial1.print("\t");
//        Serial1.println(ypr[2]);
     #endif

     #ifdef OUTPUT_READABLE_EULER
        // display Euler angles in degrees
        mpu.dmpGetQuaternion(&q, fifoBuffer);
        mpu.dmpGetEuler(euler, &q);

        euler_X_Data = euler[0] * 180/M_PI;//-이면 뒤, +이면 앞
        euler_Y_Data = euler[1] * 180/M_PI;
        euler_Z_Data = euler[2] * 180/M_PI;*/

        Serial.print("euler\t");
        Serial.print(euler_X_Data);
        Serial.print("\t");
        Serial.print(euler_Y_Data);
        Serial.print("\t");
        Serial.print(euler_Z_Data);
        Serial.println("\t");
     #endif

  }
  //if(sel == 1) return ypr[1];
  //if(sel == 2) return ypr[2];
}

unsigned char AccelMoni(void)
{

  if (ypr[1]>50)      return 1;
  else                return 0;
  
//  if (euler_X_Data > 20)         return  1;
//  else  if (euler_X_Data < -20)  return  2;
//
//  else  if (euler_Y_Data > 20)   return  3;
//  else  if (euler_Y_Data < -20)  return  4;
//
//  else  if (euler_Z_Data > 20)   return  5;
//  else  if (euler_Z_Data < -20)  return  6;
//
//  else return 0;
//  //return 0;

}

float Remove_Min_Max(void)
{
  float temp;
  for (uint8_t loop1 = 0; loop1 < 5; loop1++)
  {
    for (uint8_t loop2 = loop1 + 1; loop2 < 5; loop2++)
    {
      if (Temp_buf[loop1] > Temp_buf[loop2])
      {
        temp = Temp_buf[loop1];
        Temp_buf[loop1] = Temp_buf[loop2];
        Temp_buf[loop2] = temp;
      }
    }
  }
  return Temp_buf[2];
}

//사용 안하는듯
void Read_Axis_From_Mpu6050(void)
{
  //Read_DMP_MPU6050(1);

  for (uint8_t i = 0; i < 5; i++)
  {
    Read_DMP_MPU6050(1);
    X_temp[i] = ypr[1];
    Y_temp[i] = ypr[2];
  }

  memcpy(Temp_buf, X_temp, sizeof(X_temp));
  X_Data = Remove_Min_Max();//ypr[1];
  memcpy(Temp_buf, Y_temp, sizeof(Y_temp));
  Y_Data = Remove_Min_Max();//ypr[2];

}
