package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import logic.Symbolic.Assignment;
import logic.Symbolic.Call;
import logic.Symbolic.Condition;
import logic.Symbolic.Input;
import logic.Symbolic.Output;
import logic.Symbolic.Selection;

public class MyDialog {
    /**
     * Call view에 대한 행동들
     */
    final String[] actions = {"시동","모터 정지","이륙", "착륙", "상승", "하강", "전진", "후진", "좌이동", "우이동", "좌회전", "우회전", "대기"};

    /**
     * input Dialog에서 사용되는 handMotion에 대한 선택지
     */
    final String[] finger = {"엄지", "검지", "중지", "약지", "소지"};
    /**
     * input Dialog에서 사용되는 armMotion에 대한 선택지
     */
    final String[] arm = {"Down", "Up"};

    /**
     * 드론에서 받아올 값들의 종류
     */
    //final String[] outputValue = {"메세지", "롤", "피치", "요", "쓰로틀", "고도", "LED", "스피커"};
    final String[] outputValue = {"LED", "스피커"};

    /**
     * Assignment 이름 보관
     */
    ArrayList<String> variableNames = new ArrayList<String>();
    ArrayList<String> onlyVariableNames = new ArrayList<String>();

    /**
     * Assignment 값 대치용 선택지
     */
    final String[] valueChange = {"+", "-", "*", "/", "="};

    public void reset(){
        variableNames = new ArrayList<String>();
        onlyVariableNames = new ArrayList<String>();

    }

    /**
     * 드론의 여러 행동(Actions)를 선택할 수 있는 AlertDialog를 생성.
     * <p>.show()를 통해 표시 가능. </p>
     *
     * @param context Dialog가 생성되는 context
     * @param button  Dialog의 선택 결과가 반영될 Button
     * @param call    logic적 처리를 위한 call 객체
     * @return 생성된 Dialog 반환
     */
    public AlertDialog.Builder callActionDialog(Context context, Button button, Call call) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("행동 지정")
                .setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,
                                actions[which], Toast.LENGTH_SHORT).show();
                        if (actions[which] == "대기") {
                            final String[] selectNumber = new String[1];
                            final EditText editText = new EditText(context);

                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                            AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                            dlg.setTitle("몇 초간 대기 할까요?");
                            dlg.setView(editText);
                            dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectNumber[0] = editText.getText().toString();
                                    Toast.makeText(context, selectNumber[0], Toast.LENGTH_SHORT).show();

                                    button.setText(selectNumber[0] + "초 동안 대기");
                                    call.wait(Integer.parseInt(selectNumber[0]));
                                }
                            }).show();
                        } else {
                            switch (actions[which]) {
                                case "시동":
                                    call.turnOn();
                                    break;
                                case "모터 정지":
                                    call.turnOff();
                                    break;
                                case "이륙":
                                    System.out.println("이륙으로 설정");
                                    call.takeOff();
                                    break;
                                case "착륙":
                                    call.landing();
                                    break;
                                case "상승":
                                    call.up();
                                    break;
                                case "하강":
                                    call.down();
                                    break;
                                case "전진":
                                    call.front();
                                    break;
                                case "후진":
                                    call.rear();
                                    break;
                                case "좌이동":
                                    call.leftMove();
                                    break;
                                case "우이동":
                                    call.rightMove();
                                    break;
                                case "좌회전":
                                    call.leftTurn();
                                    break;
                                case "우회전":
                                    call.rightTurn();
                                    break;
                            }
                        }
                        button.setText(actions[which]);
                        call.setIsFill(true);
                    }
                })
                .setCancelable(false);

        return builder;
    }

    /**
     * 드론의 여러 행동(Actions)를 선택할 수 있는 AlertDialog를 생성.
     * <p>.show()를 통해 표시 가능. </p>
     *
     * @param context  Dialog가 생성되는 context
     * @param textView Dialog의 선택 결과가 반영될 TextView
     * @return 생성된 Dialog 반환
     */
    public AlertDialog.Builder callActionDialog(Context context, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("행동 지정")
                .setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,
                                actions[which], Toast.LENGTH_SHORT).show();
                        if (actions[which] == "대기") {
                            final String[] selectNumber = new String[1];
                            final EditText editText = new EditText(context);

                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                            AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                            dlg.setTitle("몇 초간 대기 할까요?");
                            dlg.setView(editText);
                            dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectNumber[0] = editText.getText().toString();
                                    Toast.makeText(context, selectNumber[0], Toast.LENGTH_SHORT).show();

                                    textView.setText(selectNumber[0] + "초 동안 대기");
                                }
                            })
                                    .show();
                        } else {
                            textView.setText(actions[which]);
                        }
                    }
                })
                .setCancelable(false);

        return builder;
    }

    /**
     * 커스텀 드론 조작을 위한 손동작을 지정할 수 있는 AlertDialog를 생성
     *
     * @param context    Dialog가 생성되는 context
     * @param inputFrame input Symbol
     * @param handFrame  Dialog의 선택 결과가 반영될 TextView
     * @param input      logic적 처리를 위한 input 객체
     * @return
     */
    public void callInputDialog(Context context, FrameLayout inputFrame, TextView handFrame, Input input) {
        inputFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                boolean selectedItems[] = {false, false, false, false, false};
                int fingerValue[] = {10000, 1000, 100, 10, 1};
                final String[] handState = {null};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("펼쳐진 손가락 선택    (V : 폄, ㅁ : 접음)");
                builder.setMultiChoiceItems(finger, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItems[which] = isChecked;
                            switch (which){
                                case 0:
                                    input.thumbUp();
                                    break;
                                case 1:
                                    input.indexUp();
                                    break;
                                case 2:
                                    input.middleUp();
                                    break;
                                case 3:
                                    input.ringUp();
                                    break;
                                case 4:
                                    input.littleUp();
                                    break;
                            }
                        } else {
                            selectedItems[which] = false;
                            switch (which) {
                                case 0:
                                    input.thumbDown();
                                    break;
                                case 1:
                                    input.indexDown();
                                    break;
                                case 2:
                                    input.middleDown();
                                    break;
                                case 3:
                                    input.ringDown();
                                    break;
                                case 4:
                                    input.littleDown();
                                    break;
                            }
                        }
                    }
                });
                builder.setNeutralButton("closed", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        int handMotion = 00000;
                        String str = "";
                        for (int i = 0; i < finger.length; i++) {
                            if (selectedItems[i]) {
                                handMotion
                                        += fingerValue[i];
                            }
                        }
                        handState[0] = String.format("%05d", handMotion);

                        builder.setTitle("행동 지정")
                                .setItems(actions, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context,
                                                actions[which], Toast.LENGTH_SHORT).show();
                                        if (actions[which] == "대기") {
                                            final String[] selectNumber = new String[1];
                                            final EditText editText = new EditText(context);

                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                                            AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                                            dlg.setTitle("몇 초간 대기 할까요?");
                                            dlg.setView(editText);
                                            dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    selectNumber[0] = editText.getText().toString();
                                                    Toast.makeText(context, selectNumber[0], Toast.LENGTH_SHORT).show();

                                                    handFrame.setText(handState[0] + "_" + selectNumber[0] + "초 동안 대기");
                                                    input.wait(Integer.parseInt(selectNumber[0]));
                                                }
                                            }).show();
                                        } else {
                                            switch (actions[which]) {
                                                case "시동":
                                                    input.turnOn();
                                                    break;
                                                case "모터 정지":
                                                    input.turnOff();
                                                    break;
                                                case "이륙":
                                                    System.out.println("이륙으로 설정");
                                                    input.takeOff();
                                                    break;
                                                case "착륙":
                                                    input.landing();
                                                    break;
                                                case "상승":
                                                    input.up();
                                                    break;
                                                case "하강":
                                                    input.down();
                                                    break;
                                                case "전진":
                                                    input.front();
                                                    break;
                                                case "후진":
                                                    input.rear();
                                                    break;
                                                case "좌이동":
                                                    input.leftMove();
                                                    break;
                                                case "우이동":
                                                    input.rightMove();
                                                    break;
                                                case "좌회전":
                                                    input.leftTurn();
                                                    break;
                                                case "우회전":
                                                    input.rightTurn();
                                                    break;
                                            }
                                            handFrame.setText(handState[0] + "_" +actions[which]);
                                        }
                                        input.setIsFill(true);
                                    }
                                })
                                .setCancelable(false)
                                .show();
//                        new AlertDialog.Builder(context)
//                                .setTitle("선택")
//                                .setSingleChoiceItems(arm, -1, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        handFrame.setText(handState[0] + "_" + Integer.toString(which));
//                                        if(which == 0){
//                                            input.handDown();
//                                        }
//                                    }
//                                }).setNeutralButton("closed", null)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        // 액션 지정 부분. input은 handMotion만 지정하고 밑에 블럭이 행동을 뜻한다 해서 지움.
//                                       /* MyDialog actionDialog = new MyDialog();
//                                        actionDialog.actionSelection(MainActivity.this, actionText).show();*/
//                                    }
//                                })
//                                .setNegativeButton("cancel", null)
//                                .show();
                    }
                });
                builder.setNegativeButton("cancel", null);
                builder.show();
            }
        });
    }

    /**
     * 드론의 리액션 종류를 선택할 수 있는 AlertDialog를 생성.
     * <p>.show()를 통해 표시 가능. </p>
     *
     * @param context Dialog가 생성되는 context
     * @param textView Dialog의 선택 결과가 반영될 TextView
     * @return 생성된 Dialog 반환
     */
    public AlertDialog.Builder callOutputDialog(Context context, TextView textView, Output output) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("행동 지정")
                .setItems(outputValue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,
                                outputValue[which], Toast.LENGTH_SHORT).show();
                        if (outputValue[which] == "메세지") {
                            output.eventIsMessage();

                            final String[] message = new String[1];
                            final EditText editText = new EditText(context);

                            AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                            dlg.setTitle("어떤 메세지를 출력할까요?");
                            dlg.setView(editText);
                            dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    message[0] = editText.getText().toString();
                                    Toast.makeText(context, message[0], Toast.LENGTH_SHORT).show();

                                    textView.setText(message[0]);
                                    output.setMessage(message[0]);
                                }
                            })
                                    .show();
                        } else {
                            textView.setText(outputValue[which]);

                            switch (outputValue[which]){
                                case "롤":
                                    output.eventIsRoll();
                                    break;
                                case "피치":
                                    output.eventIsPitch();
                                    break;
                                case "요":
                                    output.eventIsYaw();
                                    break;
                                case "쓰로틀":
                                    output.eventIsThrottle();
                                    break;
                                case "고도":
                                    output.eventIsAltitude();
                                    break;
                                case "LED":
                                    output.eventIsLED();
                                    break;
                                case "스피커":
                                    output.eventIsSpeaker();
                                    break;
                            }
                        }
                        output.setIsFill(true);
                    }
                })
                .setCancelable(false);

        return builder;
    }

    /**
     * condition dialog에서 좌우에 변수로 대체되었음을 판단하는 Flag
     */
    boolean leftVariableFlag = false;
    boolean rightVariableFlag = false;
    /**
     * Selection과 Loop의 조건을 지정할 수 있는 Dialog를 생성.
     * @param context Dialog가 생성되는 context
     * @param textView Dialog의 선택 결과가 반영될 TextView
     */
    public void callConditionDialog(Context context, TextView textView, Selection selection, ArrayList<Assignment> assignmentList){
        //&, /는 아직 기능 구현 안됐기에 제외
        String[] signs = {"=", ">", "<"};//, "&", "|"};

        Dialog testDialog = new Dialog(context);

        Condition condition = new Condition();

        testDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        testDialog.setContentView(R.layout.condition);

        Spinner spinner = testDialog.findViewById(R.id.mySpinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, signs);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        variableNames.add("직접 입력");

        for(int i = 0; i < assignmentList.size(); i++){
            variableNames.add(assignmentList.get(i).getVariableName());
        }

        Spinner leftVariable = testDialog.findViewById(R.id.leftVariable);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, variableNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leftVariable.setAdapter(arrayAdapter2);

        Spinner rightVariable = testDialog.findViewById(R.id.rightVariable);
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, variableNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rightVariable.setAdapter(arrayAdapter3);


        Button yes = (Button) testDialog.findViewById(R.id.yesBtn);
        Button cancle = (Button) testDialog.findViewById((R.id.cancle));

        EditText frontValue = (EditText) testDialog.findViewById(R.id.frontValue);
        EditText rearValue = (EditText) testDialog.findViewById(R.id.rearValue);
        frontValue.setText("0");
        rearValue.setText("0");

        frontValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        rearValue.setInputType(InputType.TYPE_CLASS_NUMBER);

        frontValue.setFocusable(true);
        frontValue.setFocusableInTouchMode(true);
        rearValue.setFocusable(true);
        rearValue.setFocusableInTouchMode(true);

        leftVariable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAssignment = leftVariable.getSelectedItem().toString();
                if(selectedAssignment.equals("직접 입력")){
                    leftVariableFlag = false;
                    condition.setUseLeftVariableFlag(false);
                    frontValue.setFocusable(true);
                    frontValue.setFocusableInTouchMode(true);
                    frontValue.setVisibility(View.VISIBLE);
                }else {
                    leftVariableFlag = true;
                    condition.setUseLeftVariableFlag(true);
                    frontValue.setFocusable(false);
                    frontValue.setFocusableInTouchMode(false);
                    for (int i = 0; i < assignmentList.size(); i++) {
                        if (assignmentList.get(i).getVariableName().equals(selectedAssignment) == true) {
                            if(Integer.toString(assignmentList.get(i).getAssignmentID()) == "0"){
                                frontValue.setText("0");
                            }else {
                                frontValue.setText(Integer.toString(assignmentList.get(i).getAssignmentID()));
                            }
                            frontValue.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        rightVariable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAssignment = rightVariable.getSelectedItem().toString();

                if(selectedAssignment.equals("직접 입력")){
                    rightVariableFlag = false;
                    condition.setUseRightVariableFlag(false);
                    rearValue.setFocusable(true);
                    rearValue.setFocusableInTouchMode(true);
                    rearValue.setVisibility(View.VISIBLE);
                }else {
                    rightVariableFlag = true;
                    rearValue.setFocusable(false);
                    condition.setUseRightVariableFlag(true);
                    rearValue.setFocusableInTouchMode(false);
                    for (int i = 0; i < assignmentList.size(); i++) {
                        if (assignmentList.get(i).getVariableName().equals(selectedAssignment) == true) {
                            if(Integer.toString(assignmentList.get(i).getAssignmentID()) == "0"){
                                rearValue.setText("0");
                            }
                            else {
                                rearValue.setText(Integer.toString(assignmentList.get(i).getAssignmentID()));
                            }
                            rearValue.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String front;
                String  rear;
                String  sign = spinner.getSelectedItem().toString();

                System.out.println(frontValue.getText().toString());

                if(frontValue.getText().toString() == ""){
                    front = "0";
                }else{
                    front = frontValue.getText().toString();
                }

                if(rearValue.getText().toString() == ""){
                    rear = "0";
                }else{
                    rear = rearValue.getText().toString();
                }

                Assignment frontAs = new Assignment();
                Assignment rearAs = new Assignment();

                try {
                    frontAs.setValue(Integer.parseInt(front));
                } catch(NumberFormatException e){
                    frontAs.setValue(0);
                }
                try {
                    rearAs.setValue(Integer.parseInt(rear));
                } catch(NumberFormatException e){
                    frontAs.setValue(0);
                }

                condition.setFrontValue(frontAs);
                condition.setRearValue(rearAs);

                if(leftVariableFlag == true){
                    front = leftVariable.getSelectedItem().toString();

                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).equals(frontValue.getText().toString()) == true){
                            frontAs = assignmentList.get(i);
                            break;
                        }
                    }
                    rearAs = assignmentList.get(Integer.parseInt(frontValue.getText().toString()));
                    condition.setFrontValue(frontAs);
                }
                if(rightVariableFlag == true){
                    rear = rightVariable.getSelectedItem().toString();
                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).equals(rearValue.getText().toString()) == true){
                            rearAs = assignmentList.get(i);
                            break;
                        }
                    }
                    rearAs = assignmentList.get(Integer.parseInt(rearValue.getText().toString()));
                    condition.setRearValue(rearAs);
                }

                switch (sign){
                    case "=":
                        condition.equel();
                        sign += "=";
                        break;
                    case ">":
                        condition.avobe();
                        break;
                    case "<":
                        condition.below();
                        break;
                    case "&":
                        condition.and();
                        break;
                    case "|":
                        condition.or();
                        break;
                }

                textView.setText(front + " " + sign + " " + rear);

                selection.setCondition(condition);
                testDialog.dismiss();
                selection.setIsFill(true);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                testDialog.dismiss();
            }
        });
        testDialog.show();
    }

    /**
     * Assignment 변경용 플래그
     */
    boolean editValueFlag = false;
    /**
     * view visibility 관리용 플래그
     */
    boolean visibilityFlag = false;
    /**
     * 기존 assignment 값 변경 요구 관리용 플래그
     */
    boolean editToAssignmentValueFlag = false;
    /**
     * 기존 assignment 값 대치 요구 관리용 플래그
     */
    boolean changeToAssignmentValueFlag = false;
    /**
     * changeType spinner의 선택값을 보관하는 변수
     */
    String selectedValue;
    /**
     * changeTarget spinner의 선택값을 보관하는 변수
     */
    String selectedAssignment;

    /**
     * Assignment에 변수명과 변수값을 지정할 수 있는 Dialog를 생성
     * @param context Dialog가 생성되는 context
     * @param textView Dialog의 선택 결과가 반영될 TextView
     */
    public void callAssignmentDialog(Context context, TextView textView, Assignment assignment, ArrayList<Assignment> assignmentList){
        int nameIndex = 0;
        selectedValue = "+";

        Dialog testDialog = new Dialog(context);
        testDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        testDialog.setContentView(R.layout.assignment);

        variableNames.add("직접 입력");

        final Assignment[] modifyAssignment = new Assignment[1];


        Spinner changeType = testDialog.findViewById(R.id.variableChangeList);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, valueChange);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeType.setAdapter(arrayAdapter1);

        Spinner changeTarget = testDialog.findViewById(R.id.changeToThis);


        EditText editValue = testDialog.findViewById(R.id.variableValue2);

        for(int i = 0; i < assignmentList.size(); i++){
            if(!assignmentList.get(i).getVariableName().equals(""))
                variableNames.add(assignmentList.get(i).getVariableName());
        }

        Spinner spinner = testDialog.findViewById(R.id.variableList);

        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, variableNames);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter3);

        // variableNames의 0번째 요소("직접 입력")을 제외한 arrayList 생성
        for(int i = 1; i < variableNames.size() ; i++){
            onlyVariableNames.add(variableNames.get(i));
        }
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item, onlyVariableNames);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        changeTarget.setAdapter(arrayAdapter2);

        Button yes = (Button) testDialog.findViewById(R.id.yesBtn);
        Button cancle = (Button) testDialog.findViewById((R.id.cancle));

        EditText name = (EditText) testDialog.findViewById(R.id.variableName);
        EditText value = (EditText) testDialog.findViewById(R.id.variableValue);

        changeType.setVisibility(View.INVISIBLE);
        changeTarget.setVisibility(View.INVISIBLE);
        editValue.setVisibility(View.INVISIBLE);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(variableNames.get(position) != "직접 입력"){
                    editValueFlag = true;
                    name.setText(variableNames.get(position));

                    name.setFocusable(false);
                    value.setFocusable(false);

                    value.setText(Integer.toString(assignmentList.get(position - 1).getValue()));

                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).getVariableName() == variableNames.get(position).toString()){
                            modifyAssignment[0] = assignmentList.get(i);
                            break;
                        }
                    }

                    changeType.setVisibility(View.VISIBLE);
                    editValue.setVisibility(View.VISIBLE);
                    editToAssignmentValueFlag = true;
                    visibilityFlag = true;
                }
                else{
                    editValueFlag = false;
                    visibilityFlag = false;
                    editToAssignmentValueFlag = false;
                    changeToAssignmentValueFlag = false;

                    name.setText("");
                    name.setFocusable(true);
                    name.setFocusableInTouchMode(true);

                    value.setText("");
                    value.setFocusable(true);
                    value.setFocusableInTouchMode(true);

                    changeType.setVisibility(View.INVISIBLE);
                    changeTarget.setVisibility(View.INVISIBLE);
                    editValue.setVisibility(View.INVISIBLE);
                    name.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView.setText("선택 : ");
            }
        });

        changeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = changeType.getSelectedItem().toString();
                if(selectedValue.equals("=") == false){
                    if(visibilityFlag == true) {
                        editValue.setVisibility(View.VISIBLE);
                        changeTarget.setVisibility(View.INVISIBLE);

                        editToAssignmentValueFlag = true;
                        changeToAssignmentValueFlag = false;

                        Toast.makeText(context, changeType.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else if(selectedValue.equals("=") == true){
                    if(visibilityFlag == true){
                        changeTarget.setVisibility(View.VISIBLE);
                        editValue.setVisibility(View.INVISIBLE);

                        editToAssignmentValueFlag = false;
                        changeToAssignmentValueFlag = true;

                        Toast.makeText(context, changeType.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        changeTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAssignment = changeTarget.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean sameNameFlag = false;

                String  names = name.getText().toString();
                String values = value.getText().toString();

                for(int i = 0; i < variableNames.size(); i++){
                    if(variableNames.get(i).equals(names)){
                        sameNameFlag = true;
                    }
                }


                if (sameNameFlag == true && editValueFlag == false && editToAssignmentValueFlag == false && changeToAssignmentValueFlag == false){
                    Toast.makeText(context, names + "는 이미 존재하는 변수명입니다.", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    value.setText("");
                }
                else if(editToAssignmentValueFlag == true){     // 변수 내부 값에 연산을 실시한다.
                    textView.setText(names + " = " + names + " " + selectedValue + " " + Integer.parseInt(editValue.getText().toString()));
                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).getVariableName().equals(names)){
                            assignment.setChange(true);
                            assignment.setTargetAssignment(assignmentList.get(i));
                            switch (selectedValue){
                                case "+":
                                    assignment.add();
                                    assignment.setCalcValue(Integer.parseInt(editValue.getText().toString()));
                                    break;
                                case "-":
                                    assignment.sub();
                                    assignment.setCalcValue(Integer.parseInt(editValue.getText().toString()));
                                    break;
                                case "*":
                                    assignment.mul();
                                    assignment.setCalcValue(Integer.parseInt(editValue.getText().toString()));
                                    break;
                                case "/":
                                    assignment.div();
                                    assignment.setCalcValue(Integer.parseInt(editValue.getText().toString()));
                                    break;
                            }
                            break;
                        }
                    }
                    assignmentList.add(assignment);
                    testDialog.dismiss();
                }
                else if(changeToAssignmentValueFlag == true){   // 변수를 다른 변수로 대치한다.
                    Assignment targetAssignment= new Assignment();

                    textView.setText(names + " = " + selectedAssignment);

                    assignment.setChange(true);
                    assignment.subsitute();

                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).getVariableName().equals(selectedAssignment)){
                            targetAssignment = assignmentList.get(i);
                            assignment.setTargetAssignment(targetAssignment);
                            break;
                        }
                    }

                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).getVariableName().equals(name.getText().toString())){
                            targetAssignment = assignmentList.get(i);
                            assignment.setCalcValue(targetAssignment.getAssignmentID());
                            break;
                        }
                    }

                    testDialog.dismiss();
                }
                else if(sameNameFlag == false){                 // 변수를 생성한다
                    textView.setText(names + " = " + values);

                    testDialog.dismiss();

                    assignment.setVariableName(names);
                    assignment.setValue(Integer.parseInt(values));
                    assignment.subsitute();

                    assignmentList.add(assignment);
                }
                else if(sameNameFlag == true && editValueFlag == true){ // 변수 내부 값을 변경한다.
                    assignmentList.get(variableNames.indexOf(names) - 1).setValue(Integer.parseInt(values));
                    textView.setText(names + " = " + values);

                    for(int i = 0; i < assignmentList.size(); i++){
                        if(assignmentList.get(i).getVariableName().equals(names)){
                            assignmentList.get(i).setValue(Integer.parseInt(values));
                            assignmentList.get(i).subsitute();
                            break;
                        }
                    }

                    testDialog.dismiss();
                    Toast.makeText(context, values, Toast.LENGTH_SHORT).show();
                }
                assignment.setIsFill(true);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                testDialog.dismiss();
            }
        });

        testDialog.show();
    }
}
