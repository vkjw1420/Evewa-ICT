package logic.Symbolic;

public class Action extends Symbol implements Command {
	public enum ActionType { // 시동, 종료, 이륙, 착륙, 상승, 하강, 전진, 후진, 좌이동, 우이동, 좌회전, 우회전
		ON, OFF, TAKEOFF, LANDING, UP, DOWN, FRONT, REAR, LEFTMOVE, RIGHTMOVE, LEFTTURN, RIGHTTURN, WAIT
	}

	ActionType myAction;

	/* 허용되는 각도의 집합. 해당 각도들만 적용됨. */
	/*------------------------ 반드시 드론 측과 값을 일치 시킬 것. ---------------*/
	int allowDegree[] = { 0, 45, 90, 135, 180, 225, 270, 315, 360 };

	private int degree = 0;
	private int distance = 0;
	private int seconds = 0;

	/**
	 * 드론의 행동을 지정. (해당 메소드를 이용하지말고 각 행동에 대한 메소드를 이용할 것. 가독성 차원에서의 결정)
	 * 
	 * @param actionType enum형인 ActionType의 값을 이용해 생성할 것. 가독성을 위해 ActionType 사용을
	 *                   권장함.
	 */
	public void setActionType(ActionType actionType) {
		this.myAction = actionType;
	}

	/**
	 * 드론의 이동 거리를 지정. 행동 지정 메소드 내부에서만 이용된다.
	 * 
	 * @param distance 프로토콜의 단위가 byte(256 bits)이기에 0~250 사이의 값으로 강제됨.
	 */
	private void setDistance(int distance) {
		if (distance < 0) {
			distance = 0;
		} else if (distance > 250) {
			distance = 250;
		}

		this.distance = distance;
	}

	/**
	 * 각도는 45도 단위로 지정 가능. 행동 지정 메소드 내부에서만 이용된다.
	 *
	 * 
	 *                    <pre>
	 * ex) a.setDegree(a.degreeValue._45);
	 *                    </pre>
	 */
	private void setDegree(int degree) {
		// 지금은 허용된 범위가 아니면 불가능하게 했지만 가능하면 범위 벗어 날 시 근삿값으로 변경해주는 코드 짜자.

		// 사용자가 지정한 각도가 허용되는 각도인지 파악하기위한 플래그
		boolean degreeFlag = false;

		for (int i = 0; i < allowDegree.length; i++) {
			if (allowDegree[i] == degree) {
				this.degree = i;
				degreeFlag = true;
			}
		}
		if (degreeFlag == false) {
			this.degree = 0;
			System.out.print("해당 되는 지정 각도가 없습니다.");
		}
	}

	/**
	 * 수행 시간을 결정하는 메소드. 0~250 사이의 값을 벗어날 시 강제로 0 또는 250으로 고정함.
	 * 
	 * @param seconds 초 단위로 0~250 사이의 값을 입력할 것.
	 */
	private void setSeconds(int seconds) {
		if (seconds < 0) {
			seconds = 0;
		} else if (seconds > 250) {
			seconds = 250;
		}

		this.seconds = seconds;
	}

	public void turnOn() {
		this.myAction = ActionType.ON;
	}
	public void turnOff() {
		this.myAction = ActionType.OFF;
	}
	public void takeOff() {
		this.myAction = ActionType.TAKEOFF;
	}
	public void landing() {
		this.myAction = ActionType.LANDING;
	}
	public void up() {
		this.myAction = ActionType.UP;
	}
	public void down() {
		this.myAction = ActionType.DOWN;
	}
	public void front() {
		this.myAction = ActionType.FRONT;
	}
	public void rear() {
		this.myAction = ActionType.REAR;
	}
	public void leftMove() {
		this.myAction = ActionType.LEFTMOVE;
	}
	public void rightMove() {
		this.myAction = ActionType.RIGHTMOVE;
	}
	public void leftTurn() {
		this.myAction = ActionType.LEFTTURN;
	}
	public void rightTurn() {
		this.myAction = ActionType.RIGHTTURN;
	}
	/**
	 * @param seconds 대기하고자하는 시간을 초 단위로 표기. 250초 이하로 넣을 것.
	 */
	public void wait(int seconds) {
		this.myAction = ActionType.WAIT;
		setSeconds(seconds);
	}

	/**
	 * 지정되어있는 액션을 String 타입으로 반환
	 */
	public String getActionType() {
		try {
			return this.myAction.name();
		} catch (NullPointerException e) {
			String error = "지정된 액션이 없습니다.";
			return error;
		}
	}

	/**
	 * int 행터의 cm 단위 distance 값을 반환
	 */
	public int getDistance() {
		return this.distance;
	}

	/**
	 * int 형태의 각도 값을 반환 degree는 allowDegree의 인덱스 값을 가지기에 이를 처리하는 알고리즘.
	 * 
	 * @return 올바른 각도 값이 아닐 시에는 "해당 되는 지정 각도가 없습니다."라는 메시지와 함께 111111을 리턴.
	 */
	public int getDegree() {
		for (int i = 0; i < allowDegree.length; i++) {
			if (allowDegree[i] == allowDegree[degree]) {
				return allowDegree[i];
			}
		}
		System.out.print("해당 되는 지정 각도가 없습니다.");
		return 111111;
	}

	/**
	 * int 형태의 초 단위 seconds를 반환.
	 * 
	 * @return
	 */
	public int getSeconds() {
		return this.seconds;
	}

	public String getCommand() {
		if(this.myAction == null) {
			return "action not assigned";
		}
		switch (this.myAction) {
		case ON:
			return "ON";
		case OFF:
			return "OF";
		case TAKEOFF:
			return "TO";
		case LANDING:
			return "LD";
		case UP:
			return "UP";
		// + Character.toString((char)getDistance());
		case DOWN:
			return "DW";
		// + Character.toString((char)getDistance());
		case FRONT:
			return "FR";
		// + Character.toString((char)getDistance());
		case REAR:
			return "RE";
		// + Character.toString((char)getDistance());
		case LEFTMOVE:
			return "LM";
		// + Character.toString((char)getDistance());
		case RIGHTMOVE:
			return "RM";
		// + Character.toString((char)getDistance());
		case LEFTTURN:
			return "LT";
		// + Character.toString((char)getDegree());
		case RIGHTTURN:
			return "RT";
		// + Character.toString((char)getDegree());
		case WAIT:
			return "WT" + Character.toString((char)getSeconds());
		default:
			return "action not assigned";
		}
	}
}




// 심볼릭에서 사용자가 이동거리 및 각도를 지정못하게 막음. 따라서 밑의 메소드들은 이용하지 않는다.

/**
 * @param distance 이동하고자하는 거리를 cm 단위로 표기. 250이하로 이용할것.
 */
/*
 * public void up(int distance) { setDistance(distance); this.myAction =
 * ActionType.UP; }
 *//**
	 * @param distance 이동하고자하는 거리를 cm 단위로 표기. 250이하로 이용할것.
	 */
/*
 * public void down(int distance) { setDistance(distance); this.myAction =
 * ActionType.DOWN; }
 *//**
	 * @param distance 이동하고자하는 거리를 cm 단위로 표기. 250이하로 이용할것.
	 */
/*
 * public void front(int distance) { setDistance(distance); this.myAction =
 * ActionType.FRONT; }
 *//**
	 * @param distance 이동하고자하는 거리를 cm 단위로 표기. 250이하로 이용할것.
	 */
/*
 * public void rear(int distance) { setDistance(distance); this.myAction =
 * ActionType.REAR; }
 *//**
	 * @param distance 이동하고자하는 거리를 cm 단위로 표기. 250이하로 이용할것.
	 */
/*
 * public void leftMove(int distance) { setDistance(distance); this.myAction =
 * ActionType.LEFTMOVE; }
 *//**
	 * @param distance 이동하고자하는 거리를 cm 단위로 표기. 250이하로 이용할것.
	 */
/*
 * public void rightMove(int distance) { setDistance(distance); this.myAction =
 * ActionType.RIGHTMOVE; }
 *//**
	 * @param distance 이동하고자하는 각도를 표기. 허용된 각도값 내에서 넘겨줄 것.(해당 프로그램에서는 45도 단위)
	 */
/*
 * public void leftTurn(int degree) { setDegree(degree); this.myAction =
 * ActionType.LEFTTURN; }
 *//**
	 * @param distance 이동하고자하는 각도를 표기. 허용된 각도값 내에서 넘겨줄 것.(해당 프로그램에서는 45도 단위)
	 *//*
		 * public void rightTurn(int degree) { setDegree(degree); this.myAction =
		 * ActionType.RIGHTTURN; }
		 */
