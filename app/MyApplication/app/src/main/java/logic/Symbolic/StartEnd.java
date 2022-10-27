package logic.Symbolic;

enum Mode{	// 일반 심볼릭 코드, 사용자 지정 함수, 커스텀 모션 모드, 모션 코딩 모드
	SYMBOLIC_CODING, CUSTOM_FUNCION, CUSTOM_MOTION, MOTION_CODING
}

public class StartEnd extends Symbol implements Command{
	// 해당 심볼 집합의 명령을 저장
	private String command;
	//사용자 지정 함수로서 이용 시 이름을 지정
	private String customFunctionName = null;
	
	// 어떠한 역할을 하는지 저장.
	private Mode mode = Mode.SYMBOLIC_CODING;

	public StartEnd(){
		super.setPreSymbol(null);
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	public void setCustomFunctionName(String customFunctionName) {
		this.customFunctionName = customFunctionName;
	}
	public void setSymbolicCodingMode() {
		this.mode = Mode.SYMBOLIC_CODING;
	}
	public void setCustomFuncionMode() {
		this.mode = Mode.CUSTOM_FUNCION;
	}
	public void setCustomMotionMode() {
		this.mode = Mode.CUSTOM_MOTION;
	}
	public void setMotionCodingMode() {
		this.mode = Mode.MOTION_CODING;
	}
	
	public String getCommand() {
		return this.command;
	}
	public String getCustomFunctionName(String customFunctionName) {
		return this.customFunctionName;
	}
	/**
	 * 현재 지정된 모션을 String 객체로 알려줌.
	 */
	public String getMode() {
		switch(mode) {
		case SYMBOLIC_CODING:
			return "Symbolic_Coding";
		case CUSTOM_FUNCION:
			return "Custom_Funcion";
		case CUSTOM_MOTION:
			return "Custom_Motion";
		case MOTION_CODING:
			return "Motion_Coding";
		}
		return "지정된 모드가 없습니다.";
	}
}
