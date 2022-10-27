package logic.Symbolic;

/**
 * 드론의 행동 구현을 위해 Action 클래스를 상속받음.
 * Action 클래스는 Symbol 클래스를 상속하기에 Symbol로서의 기능도 구현 가능
 * 반드시 HandMotion과 Action 둘 다 모두 생성한 후 command를 반환할 것.
 * @author vkjw0
 *
 */
public class Input extends Action implements Command{
	/*-------------------드론 행동에 관련된 함수는 부모 클래스인 Action을 참고할 것.----------------------*/
	
	public enum Finger {
		THUMB(32), INDEX(16), MIDDLE(8), RING(4), LITTLE(2), UP(1);

		final private int value;
		
		private Finger(int value) {
			this.value = value;
		}
		public int getValue() {
			return this.value;
		}
	}
	
	/**
	 * 어떠한 손가락이 펴져있는지 나타내는 변수. 5자리 2진수로 값을 표현. ex) 10000 = 16
	 */
	private int handMotion = 0;
	
	/**
	 * 각 손가락이 어떠한 상태인지를 표시하는 플래그. true는 펴짐 false는 접힘
	 */
	private boolean thumbFlag, indxeFlag, middleFlag, ringFlag, littleFlag, upFlag = false;

	// 버튼 구현 시 사용
	String buttonID = null;
	
	/**
	 * 손가락에 대한 이벤트가 들어오면 실행할 것.
	 * 한 동작에 2번 실행해서는 안된다.
	 * 버튼의 이벤트와 연계하여 잘 설계해아함.
	 */
	public void thumbUp() {
		if(thumbFlag == false) {
			handMotion += Finger.THUMB.getValue();
			thumbFlag	= true;
		}
	}
	public void indexUp() {
		if(indxeFlag == false) {
			handMotion += Finger.INDEX.getValue();
			indxeFlag 	= true;
		}
	}
	public void middleUp() {
		if(middleFlag == false) {
			handMotion += Finger.MIDDLE.getValue();
			middleFlag	= true;
		}
	}
	public void ringUp() {
		if(ringFlag == false) {
			handMotion += Finger.RING.getValue();
			ringFlag	= true;
		}
	}
	public void littleUp() {
		if(littleFlag == false) {
			handMotion += Finger.LITTLE.getValue();
			littleFlag	= true;
		}
	}
	public void handUp(){
		if(upFlag == false) {
			handMotion += Finger.UP.getValue();
			upFlag 		= true;
		}
	}
	
	public void thumbDown() {
		if(thumbFlag == true) {
			handMotion -= Finger.THUMB.getValue();
			thumbFlag	= false;
		}
	}
	public void indexDown() {
		if(indxeFlag == true) {
			handMotion -= Finger.INDEX.getValue();
			indxeFlag 	= false;
		}
	}
	public void middleDown() {
		if(middleFlag == true) {
			handMotion -= Finger.MIDDLE.getValue();
			middleFlag	= false;
		}
	}
	public void ringDown() {
		if(ringFlag == true) {
			handMotion -= Finger.RING.getValue();
			ringFlag	= false;
		}
	}
	public void littleDown() {
		if(littleFlag == true) {
			handMotion -= Finger.LITTLE.getValue();
			littleFlag	= false;
		}
	}
	public void handDown(){
		if(upFlag == true) {
			handMotion -= Finger.UP.getValue();
			upFlag 		= false;
		}
	}
	
	/**
	 * 완성된 손의 모양을 반환한다.
	 * @return
	 */
	public int getHandMotion() {
		return this.handMotion;
	}
	
	/**
	 * 핸드 모션과 액션을 합친 command를 반환.
	 * @return
	 */
	public String getCommand() {
		return "IP" + Character.toString((char)getHandMotion()) + super.getCommand(); //+ super.getCommand();
	}
}
