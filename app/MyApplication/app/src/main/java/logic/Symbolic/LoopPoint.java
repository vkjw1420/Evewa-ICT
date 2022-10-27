package logic.Symbolic;

public class LoopPoint extends Symbol implements Command{
	private int loopPointID;
	
	public void setLoopPointID(int ID) {
		this.loopPointID = ID;
	}
	
	public char getLoopPointID() {
		return (char)this.loopPointID;
	}
	
	public String getCommand() {
		return "LP" + getLoopPointID();
	}
}
