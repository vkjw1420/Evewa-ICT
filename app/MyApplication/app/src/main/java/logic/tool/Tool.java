package logic.tool;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import logic.Communication.BlueTooth;
import logic.DroneControl.Control;
import logic.Symbolic.*;

public class Tool {
	private int packageNameLength;

	/**
	 * 1번째는 assignmnetID를 저장하며 2번째에는 해당 ID의 Value를 저장함.
	 */
	int assignmentValues[] = new int[1000];
	/*int assignmentID = 0;
	int targetAssignmentID = 0;*/
	private ArrayList<logic.Symbolic.Assignment> assignmentList;

	Control control = new Control();

	String command;
	
	/**
	 * algorithmAnalisis에서 switch에서 가독성 높은 코드 작성을 위해 클래스 명을 제외한 path를 포함하는 index 검출
	 */
	public Tool(){
		Symbol symbol = new Symbol();
		String symbolPath = symbol.getClass().getName();

		packageNameLength = symbolPath.lastIndexOf(".") + 1;
	}
	public Tool(ArrayList<logic.Symbolic.Assignment> assignmentList){
		Symbol symbol = new Symbol();
		String symbolPath = symbol.getClass().getName();

		this.assignmentList =assignmentList;
		
		packageNameLength = symbolPath.lastIndexOf(".") + 1;
	}
	
	/**
	 * 생성된 심볼릭 코딩 묶음을 해석하여 택스트 형태의 실행 순서를 반환함.
	 * 사용자 지정 함수를 만들때는 당시에 바로 실행해주고, 다른 것은 명령 전송 전에 실행 할 것.
	 * @param symbol 심볼릭 코딩 묶음의 시작 Symbol
	 */
	public String makeCommand(Symbol symbol) {
		if(symbol == null){
			return "";
		}
		String classType = symbol.getClass().getName();
		// Switch문의 가독성을 위해서 실행 
		classType = classType.substring(packageNameLength);
		
		String innerCommand = new String();

		//System.out.println("makeCommand 실행됨.");
		System.out.println(classType);
		if(symbol.getPostSymbol() != null) {
			System.out.println("null 아님.");

			switch(classType) {
			/*case "StartEnd":
					System.out.println("Start 이후가 null 아님.");
					innerCommand =
							innerCommand + makeCommand(symbol.getPostSymbol());
				break;*/
			case "Call":
				//System.out.println("Call 실행.");
				Call callSymbol = (Call) symbol;
				innerCommand = 
						innerCommand + callSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
				
			case "Input":
				Input inputSymbol = (Input) symbol;
				innerCommand = 
						innerCommand + inputSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
				
			case "Output":
				Output outputSymbol = (Output) symbol;
				innerCommand = 
						innerCommand + outputSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
				
			case "Selection":
				Selection selectionSymbol = (Selection) symbol;
				Symbol trueSymbol = selectionSymbol.getTureStartSymbol();
				Symbol falseSymbol = selectionSymbol.getFalseStartSymbol();
				
				selectionSymbol.setTrueCommand(makeCommand(trueSymbol));
				selectionSymbol.setFalseCommand(makeCommand(falseSymbol));
				
				innerCommand = 
						innerCommand + selectionSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
			case "Loop":
				Loop loopSymbol = (Loop) symbol;
				Symbol loopTrueSymbol = loopSymbol.getTureStartSymbol();
				Symbol loopFalseSymbol = loopSymbol.getFalseStartSymbol();
				
				loopSymbol.setTrueCommand(makeCommand(loopTrueSymbol));
				loopSymbol.setFalseCommand(makeCommand(loopFalseSymbol));
				
				innerCommand = 
						innerCommand + loopSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
				
			case "LoopPoint":
				LoopPoint loopPointSymbol = (LoopPoint) symbol;
				innerCommand = 
						innerCommand + loopPointSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
				
			case "Assignment":
				Assignment assignmentSymbol = (Assignment) symbol;
				innerCommand = 
						innerCommand + assignmentSymbol.getCommand() + makeCommand(symbol.getPostSymbol());
				break;
			}
		}
		/*if(classType.equals("StartEnd") && symbol.getPostSymbol() == null){
			innerCommand += "þ";
		}*/

		return innerCommand;
	}
	
	
	public boolean compare(int value1, int value2, char sign) {
		switch(sign) {
		case '=':	 
			if(value1 == value2) {
				return true;
			}
			return false;
		case '>':	 
			if(value1 > value2) {
				return true;
			}
			return false;
		case '<':	 
			if(value1 < value2) {
				return true;
			}
			return false;
			// &와 |는 논리 조건이기에 나중에 추가
		/*
		 * case '&': if(value1 && value2) { return true; } return false; case '|':
		 * if(value1 == value2) { return true; } return false;
		 */
		}
		return false;
	}

	public void setAssignmentList(ArrayList<logic.Symbolic.Assignment> assignmentList){
		this.assignmentList = assignmentList;
	}
	
	
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public String commandExecution(String command) throws InterruptedException {
		int i = 0;
		int j = 0;
		int tempIndex = 0;
		int value1 = 0, value2 = 0;
		int[][] loopPointIndex = new int[100][2];	// LoopPointID를 저장, 문자열 인덱스(i)
		int handMotion;
		int waitTime = 10, tempTime = 40;
		int count = 0;
		int assignmentID = 0;
		int targetAssignmentID = 0;
		
		boolean waitFlag = false;		// wait 동작 실행 시 인덱스 i 값이 계속 증가하지 않도록 이용하는 플래그
		boolean run;
		
		String temp;
		String interpretedCommand = new String();
		
		while(true) {
			if(command.length() - 1 <= i)
				break;
			
			temp = command.substring(i, i + 2);
			
			i += 2;
			
			//System.out.println(i);
			System.out.println(temp);
			
			switch(temp) {
			case "CA":	// Call
				temp = command.substring(i, i + 2);
				
				i += 2;
				
			//	System.out.println("CA 내부의 temp " + temp);
				
				for(int t = 0; t < waitTime; t++) {
					switch(temp) {
						case "ON":
//							waitTime = control.wait((byte)4);
							control.motorOn();
							break;
						case "OF":
							control.motorOff();
							break;
						case "TO":
							waitTime = control.wait((byte)5);
							control.takeOff();
							break;
						case "LD":
							control.landing();
							break;
						case "UP":
//							waitTime = control.wait((byte)4);
							control.up();		// 0은 지정된 거리, 시간 이동. 추후 변경 할 시 바꿀것
							break;
						case "DW":
//							waitTime = control.wait((byte)4);
							control.down();
							break;
						case "FR":
//							waitTime = control.wait((byte)4);
							control.front();
							break;
						case "RE":
//							waitTime = control.wait((byte)4);
							control.rear();
							break;
						case "LM":
//							waitTime = control.wait((byte)4);
							control.leftMove();
							break;
						case "RM":
//							waitTime = control.wait((byte)4);
							control.rightMove();
							break;
						case "LT":
//							waitTime = control.wait((byte)4);
							control.leftTurn();
							break;
						case "RT":
//							waitTime = control.wait((byte)4);
							control.rightTurn();
							break;
						case "WT":
							waitTime = control.wait((byte)command.substring(i).toCharArray()[0]);
							waitFlag = true;
							break;
					}
					try {
						Thread.sleep(100); // 0.1초 대기, 0.1초 간격으로 이동 명령을 전송
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					control.send();
				}
				if(waitFlag == true) {
					i++;
					waitFlag = false;
					waitTime = 10;
				}
				waitTime = 10;
				break;
				
			case "IP":	// Input		모션 코딩용 부분. 개발 후순위
				handMotion = (int)command.charAt(++i);	// 작동 테스트를 위한 임시 코드. 의미 없음
				temp = command.substring(i, i + 2);
				control.customMotion(handMotion, temp);
				break;
			case "OP":	// Output		드론 리액션 부분. 개발 후순위
				handMotion = (int)command.charAt(++i);	// 작동 테스트를 위한 임시 코드. 의미 없음
				handMotion = (int)command.charAt(++i);	// 작동 테스트를 위한 임시 코드. 의미 없음
				break;
			case "SE":	// Selection
				temp = command.substring(i, i + 2);	// condition 부호 받기
				i += 2; // 사용 문자 인덱스 넘김

				// 각 명령어 형태에 맞는 변수를 저장. value1 value2는 각 명령어 마다 (CO 상수 상수, AL 변수 상수, AR 상수 변수, AA 변수 변수)를 의미함.
				switch(temp){
					case "CO":
						value1 = (int)command.charAt(i);
						value2 = (int)command.charAt(++i);
						break;
					case "AL":
						value1 = assignmentValues[(int)command.charAt(i)];
						value2 = (int)command.charAt(++i);
						break;
					case "AR":
						value1 = (int)command.charAt(i);
						value2 = assignmentValues[(int)command.charAt(++i)];
						break;
					case "AA":
						value1 = assignmentValues[(int)command.charAt(i)];
						value2 = assignmentValues[(int)command.charAt(++i)];
						break;
				}
				
				run = compare(value1, value2, command.charAt(++i));
				System.out.println(command.charAt(i));
				
				temp = command.substring(++i, i + 2);
				i += 2;
				
				if(run == true) {
					if(command.charAt(i) != 'þ') {
						commandExecution(command.substring(i));
					}
					
					while(true) {								// 참조건 끝 부분 þ2개, 거짓조건 끝 부분 þ2개 총 4개 넘김
						if(command.charAt(i++) == 'þ') {
							count++;
							if(count == 2) {
								count = 0;
								break;
							}
						}
					}
				}else if(run == false) {
					//System.out.println("거짓이야");
					while(true) {								// 참조건 끝 부분 þ1개 넘김
						if(command.charAt(i++) == 'þ') {
							count++;
							if(count == 1) {
								count = 0;
								break;
							}
						}
					}
					
					i += 2;
					
					commandExecution(command.substring(i));
					
					while(true) {
						if (command.charAt(i++) == 'þ')
							break;
					}
				}
				break;
				
			case "LP":
				loopPointIndex[j][0] = (int)command.charAt(i);
				loopPointIndex[j][1] = ++i; 
				//System.out.println(loopPointIndex[j][0]);
				//System.out.println(loopPointIndex[j][1]);
				
				j++;
				break;
			case "LO":	// Loop
				temp = command.substring(i, i + 2);	// condition 부호 받기
				i += 2; // 사용 문자 인덱스 넘김

				// 각 명령어 형태에 맞는 변수를 저장. value1 value2는 각 명령어 마다 (CO 상수 상수, AL 변수 상수, AR 상수 변수, AA 변수 변수)를 의미함.
				switch(temp){
					case "CO":
						value1 = (int)command.charAt(i);
						value2 = (int)command.charAt(++i);
						break;
					case "AL":
						value1 = assignmentValues[(int)command.charAt(i)];
						value2 = (int)command.charAt(++i);
						break;
					case "AR":
						value1 = (int)command.charAt(i);
						value2 = assignmentValues[(int)command.charAt(++i)];
						break;
					case "AA":
						value1 = assignmentValues[(int)command.charAt(i)];
						System.out.println((int)command.charAt(i));
						value2 = assignmentValues[(int)command.charAt(++i)];
						System.out.println((int)command.charAt(i));
						break;
				}
				
				run = compare(value1, value2, command.charAt(++i));
				System.out.println(Integer.toString(value1) + "       " + Integer.toString(value2));
				
				temp = command.substring(++i, i + 2);
				i += 2;
				
				if(run == true) {
					//if(command.charAt(i) != 'þ') {
					//	commandExecution(command.substring(i));
					//}
					tempIndex = i;

					System.out.println("참이야!");

					while(true) {
						if(command.charAt(i++) == 'þ')
							break;
					}

					commandExecution(command.substring(tempIndex, i));
					while(true) {								// 참조건 끝 부분 þ2개, 거짓조건 끝 부분 þ2개 총 4개 넘김
						if(command.charAt(i++) == 'þ') {
							break;
						}
					}
					i += 1;	// 거짓 조건 실행문 마지막은 þ + LoopPointID 값이기에 2를 넘김
				}else if(run == false) {
					System.out.println("거짓이야");
					while(true) {								// 참조건 끝 부분 þ 넘김
						if(command.charAt(i++) == 'þ') {
							count++;
							if(count == 1) {
								count = 0;
								break;
							}
						}
					}
					i += 2;	// þFA 생략. 즉 참조건 끝과 거짓조건 시작 메시지 넘김.

					tempIndex = i;
					
					//System.out.println("거짓이야 종료");
					
					while(true) {
						if(command.charAt(i++) == 'þ')
							break;
					}

					commandExecution(command.substring(tempIndex, i));

					//i++;
					
					int ID = (int)command.charAt(i);
					
					for(int k = 0; k < j; k++) {
						if(ID == loopPointIndex[k][0]) {
							i = loopPointIndex[k][1];
							break;
						}
					}
				}
				break;
				
			case "AS":	// Assignment 실행 시 변수가 의미를 가지는 것은 Condition 내부이니 구현 하지 않아도 됨.
				temp = command.substring(++i, i + 1);

				if(temp.equals("=") == true){
					assignmentValues[assignmentID++] = (int)command.charAt(++i);
					System.out.println("변수 할당 성공    " + (assignmentID-1) + assignmentValues[assignmentID - 1]);
					i++;

				}
				else if(temp.equals("<") == true){
					targetAssignmentID = (int)command.charAt(++i);
					temp = command.substring(++i, i + 1);

					switch(temp){
						case "+":
							assignmentValues[targetAssignmentID] += (int)command.charAt(++i);
							i++;
							break;
						case "-":
							assignmentValues[targetAssignmentID] -= (int)command.charAt(++i);
							i++;
							break;
						case "*":
							assignmentValues[targetAssignmentID] *= (int)command.charAt(++i);
							i++;
							break;
						case "/":
							assignmentValues[targetAssignmentID] /= (int)command.charAt(++i);
							i++;
							break;
						case "=":
							assignmentValues[targetAssignmentID] = assignmentValues[(int)command.charAt(++i)];
							i++;
							break;
					}
				}
			}
		}
		return interpretedCommand;
	}

	public String codeGeneration(String command, int tap) throws InterruptedException {
		int i = 0;
		int j = 0;
		int value1 = 0, value2 = 0;
		int[][] loopPointIndex = new int[100][2];	// LoopPointID를 저장, 문자열 인덱스(i)
		int handMotion;
		int waitTime = 40, tempTime = 40;
		int count = 0;
		int characterCount = 0;
		int targetCharacterCount = 1;
		int assignmentID = 0;
		int targetAssignmentID = 0;

		boolean waitFlag = false;		// wait 동작 실행 시 인덱스 i 값이 계속 증가하지 않도록 이용하는 플래그
		boolean run;

		String temp;
		String tempCode;
		String originCommand = command;
		String pythonCode = new String();
		String preprocessing = new String();

		preprocessing = "import symCoDrone\n\ndef when_start():\n";

		while(true) {
			if(command.length() - 1 <= i)
				break;

			temp = command.substring(i, i + 2);

			i += 2;

			//System.out.println(i);
			System.out.println(temp);

			switch(temp) {
				case "CA":	// Call
					for(int t = 0; t < tap; t++) {
						pythonCode = pythonCode + "    ";
					}
					temp = command.substring(i, i + 2);
					i += 2;

					switch(temp) {
						case "ON":
							pythonCode = pythonCode + "sysCoDrone.motorOn()\n";
							break;
						case "OF":
							pythonCode = pythonCode + "sysCoDrone.motorOff()\n";
							break;
						case "TO":
							pythonCode = pythonCode + "symCoDrone.takeOff()\n";
							break;
						case "LD":
							pythonCode = pythonCode + "symCoDrone.landing()\n";
							break;
						case "UP":
							pythonCode = pythonCode + "symCoDrone.up()\n";
							break;
						case "DW":
							pythonCode = pythonCode + "symCoDrone.down()\n";
							break;
						case "FR":
							pythonCode = pythonCode + "symCoDrone.front()\n";
							break;
						case "RE":
							pythonCode = pythonCode + "symCoDrone.rear()\n";
							break;
						case "LM":
							pythonCode = pythonCode + "symCoDrone.leftMove()\n";
							break;
						case "RM":
							pythonCode = pythonCode + "symCoDrone.rightMove()\n";
							break;
						case "LT":
							pythonCode = pythonCode + "symCoDrone.leftTurn()\n";
							break;
						case "RT":
							pythonCode = pythonCode + "symCoDrone.rightTurn()\n";
							break;
						case "WT":
							pythonCode = pythonCode + "symCoDrone.wait(" + Byte.toString((byte)command.substring(i).toCharArray()[0]) + ")\n";
							i++;
							break;
					}
					break;

				case "IP":	// Input		모션 코딩용 부분. 개발 후순위
					for(int t = 0; t < tap; t++) {
						pythonCode = pythonCode + "    ";
					}
					pythonCode = pythonCode + "symCoDrone.input(0b00101, symCoDrone.rear())\n";
					handMotion = (int)command.charAt(i++);	// 작동 테스트를 위한 임시 코드. 의미 없음
					handMotion = (int)command.charAt(i++);	// 작동 테스트를 위한 임시 코드. 의미 없음
					handMotion = (int)command.charAt(i++);	// 작동 테스트를 위한 임시 코드. 의미 없음

					break;
				case "OP":	// Output		드론 리액션 부분. 개발 후순위
					for(int t = 0; t < tap; t++) {
						pythonCode = pythonCode + "    ";
					}
					pythonCode = pythonCode + "symCoDrone.output(symCoDrone.";
					handMotion = (int)command.charAt(i++);	// Output ID 부분 생략
					switch((int)command.charAt(i++)){
						case 6:
							pythonCode = pythonCode + "LED";
							break;
						case 7:
							pythonCode = pythonCode + "Speaker";
							break;
					}
					pythonCode = pythonCode + ")\n";
					break;
				case "SE":	// Selection
					for(int t = 0; t < tap; t++) {
						pythonCode = pythonCode + "    ";
					}
					pythonCode = pythonCode + "if ";

					temp = command.substring(i, i + 2);	// condition 부호 받기
					i += 2; // 사용 문자 인덱스 넘김

					// 각 명령어 형태에 맞는 변수를 저장. value1 value2는 각 명령어 마다 (CO 상수 상수, AL 변수 상수, AR 상수 변수, AA 변수 변수)를 의미함.
					switch(temp){
						case "CO":
							value1 = (int)command.charAt(i);
							value2 = (int)command.charAt(++i);
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i + 1) + "= " + Integer.toString(value2) + ":\n";
							}else{
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i + 1) + " " + Integer.toString(value2) + ":\n";
							}
							break;
						case "AL":
							value1 = assignmentValues[(int)command.charAt(i)];
							value2 = (int)command.charAt(++i);
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + "= " + Integer.toString(value2) + ":\n";
							}else{
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + " " + Integer.toString(value2) + ":\n";
							}
							break;
						case "AR":
							value1 = (int)command.charAt(i);
							value2 = assignmentValues[(int)command.charAt(++i)];
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i) + "= "
										+ assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}else{
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i) + " "
										+ assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}
							break;
						case "AA":
							value1 = assignmentValues[(int)command.charAt(i)];
							value2 = assignmentValues[(int)command.charAt(++i)];
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + "= " + assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}else{
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + " " + assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}
							break;
					}

					run = compare(value1, value2, command.charAt(++i));

					temp = command.substring(++i, i + 2);
					i += 2;

					// 참 조건 해석
					if(command.charAt(i) != 'þ') {
						for(int k = i; k < command.length(); k++){
							if(command.charAt(k) == 'þ'){
								characterCount++;
								if(characterCount >= targetCharacterCount) {
									break;
								}
							}else if(command.charAt(k) == 'T' && command.charAt(k + 1) == 'R'){
								targetCharacterCount += 2;
							}
							count++;
						}

						/*for(int t = 0; t < tap; t++) {
							pythonCode = pythonCode + "    ";
						}
*/
						pythonCode = pythonCode + codeGeneration(command.substring(i, i + count), tap + 1);
						i += count;
						count = 0;
					}


					//FA 생략
					i += 3;

					// 거짓 조건 해석
					if(command.charAt(i) != 'þ') {
						for(int k = i; k < command.length(); k++){
							if(command.charAt(k) == 'þ'){
								characterCount++;
								if(characterCount >= targetCharacterCount) {
									break;
								}
							}else if(command.charAt(k) == 'T' && command.charAt(k + 1) == 'R'){
								targetCharacterCount += 2;
							}
							count++;
						}

						for(int t = 0; t < tap; t++) {
							pythonCode = pythonCode + "    ";
						}

						pythonCode = pythonCode + "else:\n";

						pythonCode = pythonCode + codeGeneration(command.substring(i, i + count), tap + 1) + '\n';
						i += count;
						count = 0;
					}
					i++;
					break;

				case "LP":
					loopPointIndex[j][0] = (int)command.charAt(i);
					loopPointIndex[j][1] = ++i;
					//System.out.println(loopPointIndex[j][0]);
					//System.out.println(loopPointIndex[j][1]);

					j++;
					break;
				case "LO":	// Loop
					for(int t = 0; t < tap; t++) {
						pythonCode = pythonCode + "    ";
					}
					pythonCode = pythonCode + "while ";

					temp = command.substring(i, i + 2);	// condition 부호 받기
					i += 2; // 사용 문자 인덱스 넘김

					// 각 명령어 형태에 맞는 변수를 저장. value1 value2는 각 명령어 마다 (CO 상수 상수, AL 변수 상수, AR 상수 변수, AA 변수 변수)를 의미함.
					switch(temp){
						case "CO":
							value1 = (int)command.charAt(i);
							value2 = (int)command.charAt(++i);
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i + 1) + "= " + Integer.toString(value2) + ":\n";
							}else{
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i + 1) + " " + Integer.toString(value2) + ":\n";
							}
							break;
						case "AL":
							value1 = assignmentValues[(int)command.charAt(i)];
							value2 = (int)command.charAt(++i);
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + "= " + Integer.toString(value2) + ":\n";
							}else{
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + " " + Integer.toString(value2) + ":\n";
							}
							break;
						case "AR":
							value1 = (int)command.charAt(i);
							value2 = assignmentValues[(int)command.charAt(++i)];
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i) + "= "
										+ assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}else{
								pythonCode = pythonCode + Integer.toString(value1) + " " + command.charAt(i) + " "
										+ assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}
							break;
						case "AA":
							value1 = assignmentValues[(int)command.charAt(i)];
							value2 = assignmentValues[(int)command.charAt(++i)];
							if(command.charAt(i + 1) == '=') {
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + "= " + assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}else{
								pythonCode = pythonCode + assignmentList.get((int) command.charAt(i - 1)).getVariableName() + " "
										+ command.charAt(i + 1) + " " + assignmentList.get((int) command.charAt(i)).getVariableName() + ":\n";
							}
							break;
					}

					run = compare(value1, value2, command.charAt(++i));

					temp = command.substring(++i, i + 2);
					i += 2;

					// 참 조건 해석
					if(command.charAt(i) != 'þ') {
						for(int k = i; k < command.length(); k++){
							if(command.charAt(k) == 'þ'){
								characterCount++;
								if(characterCount >= targetCharacterCount) {
									break;
								}
							}else if(command.charAt(k) == 'T' && command.charAt(k + 1) == 'R'){
								targetCharacterCount += 2;
							}
							count++;
						}

						/*for(int t = 0; t < tap; t++) {
							pythonCode = pythonCode + "    ";
						}
*/
						pythonCode = pythonCode + codeGeneration(command.substring(i, i + count), tap + 1);
						i += count;
						count = 0;
					}


					//FA 생략
					i += 3;

					// 거짓 조건 해석
					if(command.charAt(i) != 'þ') {
						for(int k = i; k < command.length(); k++){
							if(command.charAt(k) == 'þ'){
								characterCount++;
								if(characterCount >= targetCharacterCount) {
									break;
								}
							}else if(command.charAt(k) == 'T' && command.charAt(k + 1) == 'R'){
								targetCharacterCount += 2;
							}
							count++;
						}

						for(int t = 0; t < tap; t++) {
							pythonCode = pythonCode + "    ";
						}

						pythonCode = pythonCode + "else:\n";

						pythonCode = pythonCode + codeGeneration(command.substring(i, i + count), tap + 1) + '\n';
						i += count;
						count = 0;
					}
					//LoopPointID 생략
					i++;
					i++;
					break;

				case "AS":	// Assignment 실행 시 변수가 의미를 가지는 것은 Condition 내부이니 구현 하지 않아도 됨.
					for(int t = 0; t < tap; t++) {
						pythonCode = pythonCode + "    ";
					}
					temp = command.substring(++i, i + 1);

					if(temp.equals("=") == true){
						assignmentValues[assignmentID++] = (int)command.charAt(++i);
						pythonCode = pythonCode + assignmentList.get(assignmentID - 1).getVariableName() + " = "
								+ Integer.toString(assignmentList.get(assignmentID - 1).getValue()) + "\n";
						i++;
					}
					else if(temp.equals("<") == true){
						targetAssignmentID = (int)command.charAt(++i);
						temp = command.substring(++i, i + 1);

						switch(temp){
							case "+":
								assignmentValues[targetAssignmentID] += (int)command.charAt(++i);
								i++;
								pythonCode = pythonCode + assignmentList.get(targetAssignmentID).getVariableName() + " = "
										+ assignmentList.get(targetAssignmentID).getVariableName() + " + "
										+ (int)command.charAt(i - 1) + "\n";
								break;
							case "-":
								assignmentValues[targetAssignmentID] -= (int)command.charAt(++i);
								i++;
								pythonCode = pythonCode + assignmentList.get(targetAssignmentID).getVariableName() + " = "
										+ assignmentList.get(targetAssignmentID).getVariableName() + " - "
										+ (int)command.charAt(i - 1) + "\n";
								break;
							case "*":
								assignmentValues[targetAssignmentID] *= (int)command.charAt(++i);
								i++;
								pythonCode = pythonCode + assignmentList.get(targetAssignmentID).getVariableName() + " = "
										+ assignmentList.get(targetAssignmentID).getVariableName() + " * "
										+ (int)command.charAt(i - 1) + "\n";
								break;
							case "/":
								assignmentValues[targetAssignmentID] /= (int)command.charAt(++i);
								i++;
								pythonCode = pythonCode + assignmentList.get(targetAssignmentID).getVariableName() + " = "
										+ assignmentList.get(targetAssignmentID).getVariableName() + " / "
										+ (int)command.charAt(i - 1) + "\n";
								break;
							case "=":
								assignmentValues[targetAssignmentID] = assignmentValues[(int)command.charAt(++i)];
								i++;
								pythonCode = pythonCode + assignmentList.get(targetAssignmentID).getVariableName() + " = "
										+ assignmentList.get((int)command.charAt(i - 1)).getVariableName() + "\n";
								break;
						}
					}
			}
		}
		if(tap == 1){
			pythonCode = preprocessing + pythonCode;
			//assignmentID = 0;
			//targetAssignmentID = 0;
		}
		return pythonCode;
	}

	public void setBluetoothObject(BlueTooth blueTooth){
		control.setBluetooth(blueTooth);
	}
}
