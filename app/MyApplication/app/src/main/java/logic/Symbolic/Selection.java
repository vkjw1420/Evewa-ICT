package logic.Symbolic;

public class Selection extends Symbol implements Command{
	// 참 조건을 만족 시 실행되는 첫번째 심볼 위치
	private Symbol trueStartSymbol;
	// 거짓 조건을 만족 시 실행되는 첫번째 심볼 위치
	private Symbol falseStartSymbol;
	// 조건을 보유한 심볼
	protected Condition 	  condition = new Condition();
	
	private String trueCommand;
	private String falseCommand;

	public Selection(){
		trueStartSymbol = new Symbol();
		falseStartSymbol = new Symbol();
	}
	
	public void setTureStartSymbol(Symbol trueStartSymbol) {
		this.trueStartSymbol = trueStartSymbol;
	}
	public void setFalseStartSymbol(Symbol falseStartSymbol) {
		this.falseStartSymbol = falseStartSymbol;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public void setTrueCommand(String command) {
		this.trueCommand = command;
	}
	public void setFalseCommand(String command) {
		this.falseCommand = command;
	}
	
	public Symbol getTureStartSymbol() {
		return this.trueStartSymbol;
	}
	public Symbol getFalseStartSymbol() {
		return this.falseStartSymbol;
	}
	public Symbol getCondition() {
		return this.condition;
	}
	public String getTrueCommand() {
		return this.trueCommand;
	}
	public String getFalseCommand() {
		return this.falseCommand;
	}
	
	public String getCommand() {
		return "SE" + condition.getCommand() + "TR" + getTrueCommand() + "þ" + "FA" + getFalseCommand() + "þ";
	}
}
