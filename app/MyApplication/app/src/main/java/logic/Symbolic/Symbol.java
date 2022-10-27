package logic.Symbolic;

public class Symbol {
	enum SymbolType{	//시작, 호출, 입력, 출력, 조건, 반복, 할당
		START,
		CALL,
		INPUT,
		OUTPUT,
		SELECTION,
		LOOP,
		ASSIGNMENT,
		CONDITION
	};

	// 심볼 내부에 값이 선택 되었는지 확인하는 플래그
	private boolean isFill = false;
	
	// 위에 붙은 심볼, 이전 심볼
	private Symbol preSymbol = null;
	// 아래에 붙은 심볼, 이후 심볼
	private Symbol postSymbol = null;
	
	/**
	 * 해당 Symbol이 조건문 내부에서 끝부분임을 알리는 flag. true 조건의 끝, false 조건의 끝
	 */
	private boolean tureEndFlag = false;
	private boolean falseEndFlag = false;
	private boolean loopFlag = false;

	public Symbol(){
		setPreSymbol(null);
		setPostSymbol(null);
	}

	public void setPreSymbol(Symbol preSymbol) {
		this.preSymbol = preSymbol; 
	}
	public void setPostSymbol(Symbol postSymbol) {
		if(postSymbol != null)
			postSymbol.setPreSymbol(this);
		this.postSymbol = postSymbol;
	}
	public void setIsFill(boolean isFill){
		this.isFill = isFill;
	}
	public boolean isFill(){
		return isFill;
	}
	
	
	public Symbol getPreSymbol() {
		return this.preSymbol;
	}
	public Symbol getPostSymbol() {
		return this.postSymbol;
	}
	public boolean getTrueEndFlag() {
		return this.tureEndFlag;
	}
	public boolean getFalseEndFlag() {
		return this.falseEndFlag;
	}
	public boolean getLoopFlag() {
		return this.loopFlag;
	}
	
	// 조건문의 참거짓 끝을 알리는 플래그 온 오프
	public void tureEndFlagOn() {
		this.tureEndFlag = true;
	}
	public void falseEndFlagOn() {
		this.falseEndFlag = true;
	}
	public void tureEndFlagOff() {
		this.tureEndFlag = false;
	}
	public void falseEndFlagOff() {
		this.falseEndFlag = false;
	}
	public void loopFlagOn() {
		this.loopFlag = true;
	}
	public void loopFlagOff() {
		this.loopFlag = false;
	}
}
