package logic.DroneControl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.charset.Charset;
import java.util.Base64;

import logic.Communication.BlueTooth;

public class Control {
	/**
	 * 특정 값을 일정시간 유지하기 위해 사용하는 변수
	 */
	int callCount = 0;
	int downCount = 0;
	int opticalFlag = 0;

	int roll, pitch, yaw, throttle, temp;
	
	BlueTooth bluetooth;

	public void setBluetooth(BlueTooth bluetooth){
		this.bluetooth = bluetooth;
	}

	public void motorOn(){
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 125;
			yaw		 = 125;
			throttle = 10;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("시동");

	}
	public void motorOff(){
		System.out.println("종료");
		roll	 = 125;
		pitch 	 = 125;
		yaw		 = 125;
		throttle = 0;
		opticalFlag = 0;
	}
	public void takeOff() {
		System.out.println("이륙");
		roll	 = 125;
		pitch 	 = 125;
		yaw		 = 125;
		throttle = 150;
		opticalFlag = 0;
//		callCount++;
//		System.out.println(callCount);
//		System.out.println(Integer.toString(((int)throttle)));
//		System.out.println(throttle);
//		opticalFlag = 1;
//		if(callCount < 50) {
//			roll = 125;
//			pitch = 125;
//			yaw = 125;
//			throttle = 249;
//			System.out.println("1번째 조건");
//		}
//		else if(callCount >= 50 && callCount <= 100 && throttle > 100){
//			roll = 125;
//			pitch = 125;
//			yaw = 125;
//			if(downCount++ == 9) {
//				throttle -= 30;
//				downCount = 0;
//			}
//			System.out.println("2번째 조건");
//			//throttle -= (byte) 30;
//		}
//		else if(callCount >= 190 || throttle < 100){
//			roll = 125;
//			pitch = 125;
//			yaw = 125;
//			throttle = 100;
//			System.out.println("3번째 조건");
//		}
		//bluetooth.sendData("TO");
	}
	public void landing() {
		System.out.println("착륙");
		roll	 = 125;
		pitch 	 = 125;
		yaw		 = 125;
		throttle = 0;
		callCount = 0;
		opticalFlag = 0;
		//bluetooth.sendData("LD");
	}
	public void up() {
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 125;
			yaw		 = 125;
			throttle += 3;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}

		//bluetooth.sendData("UP");
	}
	public void down() {
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 125;
			yaw		 = 125;
			throttle -= 3;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("하강");

		//bluetooth.sendData("DW");
	}
	public void front() {
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 135;
			yaw		 = 125;
			//throttle = (byte) 140;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("전진");

		//bluetooth.sendData("FR");
	}
	public void rear() {
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 115;
			yaw		 = 125;
			//throttle = (byte) 140;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("후진");

		//bluetooth.sendData("BK");
	}
	public void leftMove() {
		callCount++;
		if(callCount < 10) {
			roll	 = 115;
			pitch 	 = 125;
			yaw		 = 125;
			//throttle = (byte) 140;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("좌이동");

		//bluetooth.sendData("LM");
	}
	public void rightMove() {
		callCount++;
		if(callCount < 10) {
			roll	 = 135;
			pitch 	 = 125;
			yaw		 = 125;
			//throttle = (byte) 140;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("우이동");

		//bluetooth.sendData("RM");
	}
	public void leftTurn() {
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 125;
			yaw		 = 145;
			//throttle = (byte) 140;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("좌회전");

	}
	public void rightTurn() {
		callCount++;
		if(callCount < 10) {
			roll	 = 125;
			pitch 	 = 125;
			yaw		 = 105;
			//throttle = (byte) 140;
			opticalFlag = 0;
		}
		else{
			callCount = 0;
		}
		System.out.println("우회전");

	}
	public int wait(byte seconds) {
		System.out.println(Byte.toString(seconds) + "초 만큼 대기");
		roll	 = 125;
		pitch 	 = 125;
		yaw		 = 125;
		opticalFlag = 1;

		return (int) seconds * 10;
	}
	public void hovering(){
		System.out.println("호버링");
		roll	 = 125;
		pitch 	 = 125;
		yaw		 = 125;
	}

	/**
	 * output 명령 실행용, send가 여기 있어서 여기에 선언
	 */
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void customMotion(int handMotion, String motion){
		sendInput(handMotion, motion);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void send() {
		String command;
		char r, p, y, t;

		byte[] values = {'$', 0, 0, 0, getUunsignedByte(opticalFlag), getUunsignedByte(roll), getUunsignedByte(pitch), getUunsignedByte(yaw), getUunsignedByte(throttle), 0, '#'};

		String stringValues = String.valueOf(values);
		//command = '$' + stringValues + '#';
		bluetooth.sendData(values);
		System.out.println("전송 : " + throttle);
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void sendInput(int handMotion, String motion) {
		String command;

		byte[] values = {'$', getUunsignedByte(roll), getUunsignedByte(pitch), getUunsignedByte(yaw), getUunsignedByte(254)};

		String stringValues = String.valueOf(values);
		//command = '$' + stringValues + '#';
		bluetooth.sendData(values);
		System.out.println("커스텀 전송");
	}

	public byte getUunsignedByte(int value){
		if(value >= 128){
			return (byte)(value - 256);
		}
		return (byte)value;
	}

//	public static final char[] EXTENDED = { 0x00C7, 0x00FC, 0x00E9, 0x00E2,
//			0x00E4, 0x00E0, 0x00E5, 0x00E7, 0x00EA, 0x00EB, 0x00E8, 0x00EF,
//			0x00EE, 0x00EC, 0x00C4, 0x00C5, 0x00C9, 0x00E6, 0x00C6, 0x00F4,
//			0x00F6, 0x00F2, 0x00FB, 0x00F9, 0x00FF, 0x00D6, 0x00DC, 0x00A2,
//			0x00A3, 0x00A5, 0x20A7, 0x0192, 0x00E1, 0x00ED, 0x00F3, 0x00FA,
//			0x00F1, 0x00D1, 0x00AA, 0x00BA, 0x00BF, 0x2310, 0x00AC, 0x00BD,
//			0x00BC, 0x00A1, 0x00AB, 0x00BB, 0x2591, 0x2592, 0x2593, 0x2502,
//			0x2524, 0x2561, 0x2562, 0x2556, 0x2555, 0x2563, 0x2551, 0x2557,
//			0x255D, 0x255C, 0x255B, 0x2510, 0x2514, 0x2534, 0x252C, 0x251C,
//			0x2500, 0x253C, 0x255E, 0x255F, 0x255A, 0x2554, 0x2569, 0x2566,
//			0x2560, 0x2550, 0x256C, 0x2567, 0x2568, 0x2564, 0x2565, 0x2559,
//			0x2558, 0x2552, 0x2553, 0x256B, 0x256A, 0x2518, 0x250C, 0x2588,
//			0x2584, 0x258C, 0x2590, 0x2580, 0x03B1, 0x00DF, 0x0393, 0x03C0,
//			0x03A3, 0x03C3, 0x00B5, 0x03C4, 0x03A6, 0x0398, 0x03A9, 0x03B4,
//			0x221E, 0x03C6, 0x03B5, 0x2229, 0x2261, 0x00B1, 0x2265, 0x2264,
//			0x2320, 0x2321, 0x00F7, 0x2248, 0x00B0, 0x2219, 0x00B7, 0x221A,
//			0x207F, 0x00B2, 0x25A0, 0x00A0 };
//
//	public static final char getAscii(int code) {
//		if (code >= 0x80 && code <= 0xFF) {
//			return EXTENDED[code - 0x7F];
//		}
//		return (char) code;
//	}
}
