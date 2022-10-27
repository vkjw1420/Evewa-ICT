package logic.Symbolic;

/**
 * Loop는 Selection과 형태가 동일하지만 거짓 조건 만족 시 반복을 위해 되돌아가는 위치가 필요함.
 * 따라서 Selection 클래스를 상속함
 * @author vkjw0
 *
 */

public class Loop extends Selection implements Command{
	// 되돌아갈 위치를 저장

	private LoopPoint loopPoint = new LoopPoint();
	
	public void setLoopPoint(LoopPoint loopPoint) {
		this.loopPoint = loopPoint;
	}
	
	public LoopPoint getLoopPoint() {
		return this.loopPoint;
	}
	public char getLoopPointID() {
		return this.loopPoint.getLoopPointID();
	}
	
	public String getCommand() {
		return "LO" + condition.getCommand() + "TR" + getTrueCommand() + "þ" + "FA" + getFalseCommand() + "þ"
				+ getLoopPointID();
	}
}
