package logic.Symbolic;

/**
 * 드론의 행동 구현을 위해 Action 클래스를 상속받음.
 * Action 클래스는 Symbol 클래스를 상속하기에 Symbol로서의 기능도 구현 가능
 * @author vkjw0
 *
 */
public class Call extends Action {
	// 사용자 지정 함수를 불러올 시 사용되는 객체.
	private StartEnd customFunciton = new StartEnd();

	public Call(){}
	public Call(Symbol preSymbol){
		super.setPreSymbol(preSymbol);
	}
	
	/*-------------------드론 행동에 관련된 함수는 부모 클래스인 Action을 참고할 것.----------------------*/
	
	/**
	 * 사용자 지정 함수 호출 시 해당 함수의 시작 Symbol을 Call 객체 내부에 저장
	 * @param customFunciton 사용자 지정 함수의 시작 Symbol 입력
	 */
	public void setCustomFunction(StartEnd customFunciton) {
		this.customFunciton = customFunciton;
	}
	/**
	 * 사용자 지정 함수의 시작 Symbol 객체를 반환
	 */
	public StartEnd getCustomFunction() {
		return this.customFunciton;
	}
	/**
	 * 사용자 지정 함수의 명령어 집합을 반환
	 */
	public String getCustomFunctionCommand() {
		return (String) this.customFunciton.getCommand();
	}
	
	public String getCommand() {
		return "CA" + super.getCommand();
	}
}

