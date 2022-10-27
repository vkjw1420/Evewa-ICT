package logic.Symbolic;

import android.view.View;

/**
 * 좌우에 비교 가능한 변수를 담고 있으며 중앙에는 부등호 및 조건이 들어가는 Symbol.
 * Symbol class를 상속함.
 * @author vkjw0
 *
 */

public class Condition extends Symbol implements Command{
	enum Sign{ // =, <, >, &, |
		EQUEL, GREATER, LESS, AND, OR
	}

	public Condition(){
		equel();
	}
	
	private Assignment frontValue = new Assignment("frontValue");
	private Assignment rearValue  = new Assignment("rearValue");
	
	private Sign sign = Sign.EQUEL;

	private boolean useLeftVariableFlag = false;
	private boolean useRightVariableFlag = false;
	
	public void setFrontValue(Assignment assignment) {
		this.frontValue = assignment;
	}
	public void setRearValue(Assignment assignment) {
		this.rearValue = assignment;
	}

	public void setUseLeftVariableFlag(boolean useVariableFlag){
		this.useLeftVariableFlag = useVariableFlag;
	}
	public void setUseRightVariableFlag(boolean useVariableFlag){
		this.useRightVariableFlag = useVariableFlag;
	}
	
	public Assignment getFrontValue() {
		return this.frontValue;
	}
	public Assignment getRearValue() {
		return this.rearValue;
	}

	public boolean getLeftUseVariableFlag(){
		return this.useLeftVariableFlag;
	}
	public boolean getRightUseVariableFlag(){
		return this.useRightVariableFlag;
	}
	
	public void equel() {
		this.sign = Sign.EQUEL;
	}
	public void avobe() {
		this.sign = Sign.GREATER;
	}
	public void below() {
		this.sign = Sign.LESS;
	}
	public void and() {
		this.sign = Sign.AND;
	}
	public void or() {
		this.sign = Sign.OR;
	}
	
	/**
	 * 해당 Condition 심볼이 보유한 sign 값을 String 객체로 반환
	 */
	public char getSign() {
		switch(this.sign) {
		case   EQUEL:	 return '=';
		case GREATER:	 return '>';
		case    LESS:	 return '<';
		case     AND:	 return '&';
		case      OR:	 return '|';
		}
		return '!';
	}
	
	public String getCommand() {
		if(getLeftUseVariableFlag() == false && getRightUseVariableFlag() == false) {
			return "CO" + frontValue.getValueToChar() + rearValue.getValueToChar() + getSign();
		}
		else if(getLeftUseVariableFlag() == true && getRightUseVariableFlag() == false){
			return "AL" + frontValue.getAssignmentIDToChar() + rearValue.getValueToChar() + getSign();
		}
		else if(getLeftUseVariableFlag() == false && getRightUseVariableFlag() == true){
			return "AR" + frontValue.getValueToChar() + rearValue.getAssignmentIDToChar() + getSign();
		}
		else if(getLeftUseVariableFlag() == true && getRightUseVariableFlag() == true){
			return "AA" + frontValue.getAssignmentIDToChar() + rearValue.getAssignmentIDToChar() + getSign();
		}
		return "CO00=";
	}
}
