package logic.Symbolic;

public class Output extends Symbol implements Command{
	public enum OutputEvent {
		MESSAGE, ROLL, PITCH, YAW, THROTTLE, ALTITUDE, LED, SPEAKER
	}
	
	// 이후 드론에서 해당 output 심볼에 대한 리액션을 요구할 시 outputID값을 통해 심볼을 구분함.
	// 8bit의 한 문자로 표현 해야하기에 char형태로 선언.
	private char outputID;
	private String message = new String();
	private int roll, pitch, yaw, throttle, altitude = 0;
	
	private OutputEvent event = OutputEvent.ROLL;

	/* 각 set 메소드들은 이벤트를 통해 활성화 되며 활성화 되었다면 메세지 수신 시 작동함 */
	/* 메세지, 롤, 피치... 등등은 그냥 출력 화면 부분에서 구현해서 플래그를 통해 활성화 시켜도 될거 같다.*/
	public void setOutputID(int outputID) {
		this.outputID = (char)outputID;
	}
	/* 이벤트 요청 수신 시 출력될 메세지를 지정 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* 이하 Roll Pitch Yaw Throttle Altitude 메소드는 드론에서 값을 받아와 실행하는 메소드? */
	public void setRoll(int roll) {
		this.roll = roll;
	}
	public void setPitch(int pitch) {
		this.pitch = pitch;
	}
	public void setYaw(int yaw) {
		this.yaw = yaw;
	}
	public void setThrottle(int throttle) {
		this.throttle = throttle;
	}
	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}
	
	/* 어떠한 이벤트를 할당할지 지정함. 메소드 찾기에 용이하게 event를 메소드 명 앞 부분에 기술 */
	public void eventIsMessage() {
		this.event = OutputEvent.MESSAGE;
	}
	public void eventIsRoll() {
		this.event = OutputEvent.ROLL;
	}
	public void eventIsPitch() {
		this.event = OutputEvent.PITCH;
	}
	public void eventIsYaw() {
		this.event = OutputEvent.YAW;
	}
	public void eventIsThrottle() {
		this.event = OutputEvent.THROTTLE;
	}
	public void eventIsAltitude() {
		this.event = OutputEvent.ALTITUDE;
	}
	public void eventIsLED(){
		this.event = OutputEvent.LED;
	}
	public void eventIsSpeaker(){
		this.event = OutputEvent.SPEAKER;
	}
	
	/* getOutputID()는 ID 비교 단계에서 호출되며, 나머지는 화면 출력 시 호출될것 */
	public char getOutputID() {
		return this.outputID;
	}
	public String getMessage() {
		return this.message;
	}
	public int getRoll() {
		return this.roll;
	}
	public int getPitch() {
		return this.pitch;
	}
	public int getYaw() {
		return this.yaw;
	}
	public int getThrottle() {
		return this.throttle;
	}
	public int getAltitude() {
		return this.altitude;
	}
	/**
	 * 이벤트의 종류에 대한 enum 순서를 반환
	 * @return
	 */
	public int getEvent() {
		return this.event.ordinal();
	}
	
	public String getResult() {
		switch(event) {
			case MESSAGE:	return getMessage();
			case ROLL:		return Integer.toString(roll);
			case PITCH:		return Integer.toString(pitch);
			case YAW:		return Integer.toString(yaw);
			case THROTTLE:	return Integer.toString(throttle);
			case ALTITUDE:	return Integer.toString(altitude);
		}
		
		return "지정된 값이 없습니다.";
	}
	
	public String getCommand() {
		return "OP" + getOutputID() + Character.toString((char)getEvent());
	}
}
