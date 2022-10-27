package logic.Symbolic;

public class Assignment extends Symbol implements Command{
	enum Sign{ // =, +, -, *, /
		SUBSITUTE, ADD, SUB, MUL, DIV
	}

	// 8bit의 한 문자로 표현 해야하기에 char형태로 선언.
	private char assignmentID;
	// 변수 이름
	private String variableName = new String();
	// 변수 값. int 형태의 값만 저장
	private int value = 0;
	// 계산될 값
	private int calcValue = 0;
	// 부호
	private Sign sign = Sign.SUBSITUTE;
	// 변수 변환 플래그
	private boolean change = false;
	// 변환 시키기고자하는 대상 Assignment Symbol
	private Assignment targetAssignment = null;

	public Assignment(){
		variableName = "";
		setValue(0);
	}
	/**
	 * 생성과 동시에 변수 명을 초기화한다.
	 * @param variableName
	 */
	public Assignment(String variableName){
		this.variableName = variableName;
	}
	
	public void setAssignmentID(int assignmentID) {
		this.assignmentID = (char)assignmentID;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public void setChange(boolean change){
		this.change = change;
	}
	public void setTargetAssignment(Assignment targetAssignment){
		this.targetAssignment = targetAssignment;
	}
	public void setCalcValue(int calcValue){
		this.calcValue = calcValue;
	}
	public void subsitute() {
		this.sign = Sign.SUBSITUTE;
	}
	public void add() {
		this.sign = Sign.ADD;
	}
	public void sub() {
		this.sign = Sign.SUB;
	}
	public void mul() {
		this.sign = Sign.MUL;
	}
	public void div() {
		this.sign = Sign.DIV;
	}

	/**
	 * value 값음 0~250사이의 int 형태임. 해당 범위를 벗어날 시 0 또는 250의 값으로 변경됨
	 * @param value 0~250 사이의 int 값을 넣을 것.
	 */
	public void setValue(int value) {
		if(value < 0) {
			value = 0;
		}
		else if(value > 250) {
			value = 250;
		}
		this.value = value;
	}
	/**
	 * value에 임의의 값을 더해줌.
	 * @param value 0~250 사이의 int 값을 넣을 것.
	 */
	public void addValue(int value) {
		value += this.value;

		if(value < 0) {
			value = 0;
		}
		else if(value > 250) {
			value = 250;
		}
		this.value = value;
	}
	/**
	 * value에 임의의 값을 빼줌.
	 * @param value 0~250 사이의 int 값을 넣을 것.
	 */
	public void subValue(int value) {
		value -= this.value;

		if(value < 0) {
			value = 0;
		}
		else if(value > 250) {
			value = 250;
		}
		this.value = value;
	}
	/**
	 * value에 임의의 값을 곱줌.
	 * @param value 0~250 사이의 int 값을 넣을 것.
	 */
	public void mulValue(int value) {
		value *= this.value;

		if(value < 0) {
			value = 0;
		}
		else if(value > 250) {
			value = 250;
		}
		this.value = value;
	}
	/**
	 * value에 임의의 값을 나눠줌.
	 * @param value 0~250 사이의 int 값을 넣을 것.
	 */
	public void divValue(int value) {
		value /= this.value;

		if(value < 0) {
			value = 0;
		}
		else if(value > 250) {
			value = 250;
		}
		this.value = value;
	}
	
	public char getAssignmentID() {
		return this.assignmentID;
	}
	public String getVariableName() {
		return this.variableName;
	}
	public int getValue() {
		return this.value;
	}
	public boolean getChange(){
		return this.change;
	}
	public int getCalcValue(){
		return this.calcValue;
	}
	public char getValueToChar() {
		return (char)this.value;
	}
	public char getAssignmentIDToChar(){
		return (char)this.assignmentID;
	}
	public Assignment getTargetAssignment(){
		return this.targetAssignment;
	}
	public char getSign() {
		switch(this.sign) {
			case SUBSITUTE:	 return '=';
			case 	   ADD:	 return '+';
			case       SUB:	 return '-';
			case       MUL:	 return '*';
			case       DIV:	 return '/';
		}
		return '!';
	}
	
	public String getCommand() {
		if(change == false){
			return "AS" + getAssignmentID() + getSign() + Character.toString((char)getValue());
		}
		else if(change == true){
			return "AS" + getAssignmentID() + "<" + Character.toString(getTargetAssignment().getAssignmentID())
					+ getSign() + Character.toString((char)getCalcValue());
		}
		return "AS" + "250" + "=" + "0";
	}
}
