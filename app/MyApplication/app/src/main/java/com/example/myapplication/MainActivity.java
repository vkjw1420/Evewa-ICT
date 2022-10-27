package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import logic.Communication.BlueTooth;
import logic.Symbolic.Assignment;
import logic.Symbolic.Call;
import logic.Symbolic.Condition;
import logic.Symbolic.Input;
import logic.Symbolic.Loop;
import logic.Symbolic.LoopPoint;
import logic.Symbolic.Selection;
import logic.Symbolic.StartEnd;
import logic.Symbolic.Symbol;
import logic.tool.Tool;

public class MainActivity extends AppCompatActivity {
    /**
     * 누르면 지정된 순서도가 나타나요
     */
    Button first;
    Button seconds;

    BlueTooth blueTooth;

    /********** 안드로이드 뷰 객체 **********/
    TextView test;
    TextView makeCommand;

    TextView commandExecutionOrder;
    Button executionCommand;

    Button btnWearbleMode;
    Button btnDroneMode;

    RelativeLayout view;

    Button Call;
    Button Input;
    Button Output;
    Button Selection;
    Button Loop;
    Button Assignment;
    Button Delete;
    ToggleButton ToF;
    Button TFend;
    TextView Start;

    TextView Code;
    Button CodeOn;
    Button CodeOff;

    Button btnReset;
    /**************************************/

    /************ 로직용 객체 **************/
    StartEnd startEnd;

    Symbol currentSymbol = new Symbol();
    StartEnd end = new StartEnd();

    Tool tool;
    /**************************************/

    /************ 블투용 객체 **************/
    Button mBtnSearch;
    Button mBtnConnect;
    /**************************************/

    /**
     * 생성될 Symbol view의 id 값. 순서대로 증가
     */
    int id = 0;
    int lineId = 100;

    /**
     * 마지막 Symbol인 End view의 id 값.
     */
    int endId = 10000;

    int ToFId = 11000;
    int TFendId = 12000;
    int endLineId = 13000;

    int outputId = 0;
    int loopPointId = 0;
    int assignmentId = 0;

    int selectionTopIndex = 0;

    /**
     * 마지막으로 생성된 심볼의 Bottom 갑을 보관하는 변수
     */
    static int lastViewBottom = 0;
    /**
     * 심볼 삭제시 줄어들 높이 구하는 변수
     */
    int lastHeight = 0;

    /**
     *  view가 생성될 중심 좌표
     */
    int creationCenterPoint = 0;

    int tureLastviewBottom = 0;
    int falseLastviewBottom = 0;
    int tempLastViewBottom = 0;

    boolean TFflag = true;

    boolean selectionFlag = false;

    boolean loopFlag = false;

    boolean deleteFlag = false;

    boolean loopPointFlag = false;

    int selectionTop;

    /**
     * 0은 드론통신 1은 장갑 통신
     */
    int commandTypeMode = 0;

    /**
     * false이면 명령 실행이 안된다.
     */
    boolean isConneted = false;

    /**
     * 생성된 명령어 확인용
     */
    String commandMessage = "command";
    String executionOrder = "";
    String pythonCode = "";

    BluetoothAdapter mBluetoothAdapter;
    Handler mBluetoothHandler;

    final static int BT_MESSAGE_READ = 2;

    /**
     * 사용자가 생성한 Assignmet의 목록 배열
     */
    ArrayList<logic.Symbolic.Assignment> assignmentList = new ArrayList<Assignment>();

    public MainActivity() {
        tool = new Tool(assignmentList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.condition);
        setContentView(R.layout.symbolic_code);

        /**
         * 누르면 지정된 순서도가 나타나요
         */
        first = (Button)findViewById(R.id.first);
        seconds = (Button)findViewById(R.id.seconds);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        blueTooth = new BlueTooth(MainActivity.this, mBluetoothAdapter, mBluetoothHandler);

        test = (TextView)findViewById(R.id.test);
        makeCommand = (Button)findViewById(R.id.makeCommand);

        //commandExecutionOrder = (TextView)findViewById(R.id.commandExecutionOrder);
        executionCommand = (Button)findViewById(R.id.executionCommand);

        //btnWearbleMode = (Button)findViewById(R.id.btnWearable);
        //btnDroneMode = (Button)findViewById(R.id.btnDrone);

        view = (RelativeLayout) findViewById(R.id.draw);

        Call = (Button) findViewById(R.id.Call);
        Input = (Button) findViewById(R.id.Input);
        Output = (Button) findViewById(R.id.Output);
        Selection = (Button) findViewById(R.id.Selection);
        Loop = (Button) findViewById(R.id.Loop);
        Assignment = (Button) findViewById(R.id.Assignment);
        Delete = (Button) findViewById(R.id.delete);

        Start = (TextView) findViewById(R.id.Start);

        Code = (TextView) findViewById(R.id.code);
        Code.setVisibility(View.INVISIBLE);
        CodeOn = (Button) findViewById(R.id.codeOn);
        CodeOff = (Button) findViewById(R.id.codeOff);

        btnReset = (Button)findViewById(R.id.btnReset);

        //mBtnSearch = (Button) findViewById(R.id.btnWearable);
        mBtnConnect = (Button) findViewById(R.id.buttonConnect);

        StartEnd end = new StartEnd();
        end.setPostSymbol(null);

        startEnd = new StartEnd();
        startEnd.setPostSymbol(end);
        currentSymbol = startEnd;
        end.setPreSymbol(currentSymbol);

        Start.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                creationCenterPoint = Start.getLeft() + Start.getWidth()/2;
                lastViewBottom = Start.getBottom() + 50;

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                createEnd();
                View verticalLine1 = new View(getApplicationContext());
                RelativeLayout.LayoutParams textParam1 = new RelativeLayout.LayoutParams(2, 85);
                textParam1.setMargins((creationCenterPoint), Start.getBottom(), 0, 0);
                verticalLine1.setBackground(getResources().getDrawable(R.drawable.vertical_line));
                verticalLine1.setLayoutParams(textParam1);
                view.addView(verticalLine1);


            }
        });

        CodeOn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                commandMessage = tool.makeCommand(startEnd.getPostSymbol());
                try {
                    pythonCode = tool.codeGeneration(commandMessage, 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isFill() == false){
                    Toast.makeText(getApplicationContext(), "내용이 선택되지 않은 심볼이 있습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Code.setText(pythonCode);
                    Code.setVisibility(View.VISIBLE);
                    Code.bringToFront();
                    //Code.invalidate();

                    for(int i = 0; i < id;i++){
                        findViewById(i).setVisibility(View.INVISIBLE);
                    }
                    findViewById(endId).setVisibility(View.INVISIBLE);
                }
            }
        });

        CodeOff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Code.setVisibility(View.INVISIBLE);
                for(int i = 0; i < id;i++){
                    findViewById(i).setVisibility(View.VISIBLE);
                }
                findViewById(endId).setVisibility(View.VISIBLE);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                for(int i = 0; i < 100; i++){
                    deleteSymbol();
                }
                id = 0;
                lineId = 100;

                /**
                 * 마지막 Symbol인 End view의 id 값.
                 */
                endId = 10000;

                ToFId = 11000;
                TFendId = 12000;
                endLineId = 13000;

                outputId = 0;
                loopPointId = 0;
                assignmentId = 0;

                startEnd.setPostSymbol(end);
                currentSymbol = startEnd;
                end.setPreSymbol(currentSymbol);

                for(int i = 0; i < 100000; i += 100){
                    view.removeView(findViewById(i));
                    view.removeView(findViewById(i + 1));
                    view.removeView(findViewById(i + 2));
                    view.removeView(findViewById(i + 3));
                    view.removeView(findViewById(i + 4));
                    view.removeView(findViewById(i + 5));
                    view.removeView(findViewById(i + 6));
                    view.removeView(findViewById(i + 7));
                    view.removeView(findViewById(i + 8));
                    view.removeView(findViewById(i + 9));
                    view.removeView(findViewById(i + 10));
                }

                assignmentList = new ArrayList<Assignment>();
                assignmentId = 0;

                tool.setAssignmentList(assignmentList);
                createEnd();
            }
        });

        /*btnWearbleMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                commandTypeMode = 1;

                Call.setVisibility(View.INVISIBLE);
                Output.setVisibility(View.INVISIBLE);
                Selection.setVisibility(View.INVISIBLE);
                Loop.setVisibility(View.INVISIBLE);
            }
        });*/

        /*btnDroneMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                commandTypeMode = 0;

                Call.setVisibility(View.VISIBLE);
                Output.setVisibility(View.VISIBLE);
                Selection.setVisibility(View.VISIBLE);
                Loop.setVisibility(View.VISIBLE);
            }
        });*/

        /*mBtnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });*/

        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBluetoothAdapter == null){
                    Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    blueTooth.listPairedDevices();
                    isConneted = blueTooth.isConneted();

                    tool.setBluetoothObject(blueTooth);
                }
            }
        });



        makeCommand.setOnClickListener(new View.OnClickListener(){
            //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v){
                commandMessage = tool.makeCommand(startEnd.getPostSymbol());
                test.setText(commandMessage);
            }
        });

        executionCommand.setOnClickListener(new View.OnClickListener(){
            //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v){
                if(isConneted == false){
                    Toast.makeText(getApplicationContext(), "블루투스로 연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(isFill() == false){
                    Toast.makeText(getApplicationContext(), "내용이 선택되지 않은 심볼이 있습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        executionOrder = tool.commandExecution(commandMessage);
                        Toast.makeText(getApplicationContext(), "명령 실행!", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //commandExecutionOrder.setText(executionOrder);
                }


            }
        });

        Call.setOnClickListener(new View.OnClickListener(){
            //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "Call 버튼 클릭", Toast.LENGTH_SHORT).show();
                createCall();
                createEnd();

                commandMessage = tool.makeCommand(startEnd);
                //(getApplicationContext(), commandMessage, Toast.LENGTH_SHORT).show();
            }
        });
        Input.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "Input 버튼 클릭", Toast.LENGTH_SHORT).show();
                createInput();
                createEnd();

                commandMessage = tool.makeCommand(startEnd);
            }
        });
        Output.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "Output 버튼 클릭", Toast.LENGTH_SHORT).show();
                createOutput();
                createEnd();

                commandMessage = tool.makeCommand(startEnd);
            }
        });
        Selection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                logic.Symbolic.Selection selection;

                selection = createSelection();
                createToF(selection);
                createEnd();

                tempLastViewBottom = lastViewBottom;
                tureLastviewBottom = lastViewBottom;
                falseLastviewBottom = lastViewBottom;
                creationCenterPoint -= 210;

                commandMessage = tool.makeCommand(startEnd);
            }
        });
        Loop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "Loop 버튼 클릭", Toast.LENGTH_SHORT).show();
                Loop loop;

                loop = createLoop();
                createToF(loop);
                createEnd();

                tempLastViewBottom = lastViewBottom;
                tureLastviewBottom = lastViewBottom;
                falseLastviewBottom = lastViewBottom;
                creationCenterPoint -= 210;

                commandMessage = tool.makeCommand(startEnd);
            }
        });
        Assignment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(getApplicationContext(), "Assignment 버튼 클릭", Toast.LENGTH_SHORT).show();
                createAssignment();
                createEnd();

                commandMessage = tool.makeCommand(startEnd);
            }
        });
        Delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "최근 Symbol 삭제", Toast.LENGTH_SHORT).show();
                deleteSymbol();

                commandMessage = tool.makeCommand(startEnd);
            }
        });


        /**
         * 누르면 지정된 순서도가 나타나요
         */
        first.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Call.callOnClick();
                Call.callOnClick();
/*                ((Button)findViewById(id - 1)).setText("이륙");
                ((Call)currentSymbol).takeOff();
                connectSymbolToLast(currentSymbol);*/
                Assignment.callOnClick();
/*                ((TextView)((FrameLayout)findViewById(id - 1)).getChildAt(0)).setText("cnt = 0");
                ((Assignment)currentSymbol).setValue(0);
                ((Assignment)currentSymbol).setVariableName("cnt");
                ((Assignment)currentSymbol).setAssignmentID(0);*/
                Loop.callOnClick();
/*                ((TextView)((FrameLayout)findViewById(id - 1)).getChildAt(0)).setText("cnt < 3");
                Condition condition = new Condition();
                condition.setFrontValue((Assignment) currentSymbol);
                condition.equel();
                condition.getRearValue().setValue(2);
                connectSymbolToLast(currentSymbol);
                ((Loop)currentSymbol).setCondition(condition);
                //condition.setFrontValue((Loop)currentSymbol);*/
                ((ToggleButton)findViewById(ToFId - 1)).setChecked(false);
                Call.callOnClick();
/*                ((Button)findViewById(id - 1)).setText("상승");
                ((Call)currentSymbol).up();*/
                Call.callOnClick();
/*                ((Button)findViewById(id - 1)).setText("하강");
                ((Call)currentSymbol).down();*/
                Assignment.callOnClick();
                ((Button)findViewById(TFendId -1)).callOnClick();
                Call.callOnClick();
/*                ((Button)findViewById(id - 1)).setText("착륙");
                ((Call)currentSymbol).landing();*/
            }
        });

        seconds.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Call.callOnClick();
                Call.callOnClick();
                Assignment.callOnClick();
                Call.callOnClick();
                Call.callOnClick();
                Loop.callOnClick();
                ((ToggleButton)findViewById(ToFId - 1)).setChecked(false);
                Call.callOnClick();
                Call.callOnClick();
                Assignment.callOnClick();
                ((Button)findViewById(TFendId -1)).callOnClick();
                Call.callOnClick();
            }
        });
    }

    /**
     * Call view 생성
     *
     */
    private void createCall(){
        int width = 200;
        int height = 60;

        Button callButton;
        logic.Symbolic.Call call;

        // Symbol logic용 코드
        call = new Call();
        connectSymbolToLast(call);

        callButton = new Button(getApplicationContext());

        callButton.setTextSize(10);               // text 크기
        callButton.setId(id);                     // id 값. 위에서 부터 순서대로 증가함
        callButton.setGravity(Gravity.CENTER);    // text를 중앙에 배치함

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            //lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
            makeLine(Start.getBottom());
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
            makeLine(lastViewBottom - 50);
        }


        callButton.setLayoutParams(textParam);
        callButton.setBackgroundResource(R.drawable.action_block);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MyDialog dialog = new MyDialog();
                dialog.callActionDialog(MainActivity.this, callButton, call).show();
            }
        });


        view.addView(callButton);

        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        id++;
    }

    /**
     * Input view 생성
     *
     */
    private void createInput(){
        int width = 170;
        int height = 60;

        // Symbol logic용 코드
        logic.Symbolic.Input input;
        input = new Input();

        connectSymbolToLast(input);

        /**
         * input view의 틀 이미지를 담기 위한 frame
         */
        FrameLayout inputFrame = new FrameLayout(getApplicationContext());
        /**
         * 손동작 모션을 표현하기 위한 추가 프레임.(inputFrame 내부 좌측에 위치함.)
         */
        TextView handFrame  = new TextView(getApplicationContext());
        /**
         * 손동작 모션에 따른 액션을 지정하기 위한 textView.(inputFrame 내부 우측에 위치함.)
         */
        TextView actionText = new TextView(getApplicationContext());

        inputFrame.setId(id);                     // id 값. 위에서 부터 순서대로 증가함

        handFrame.setTextSize(10);
        handFrame.setTextColor(0xAA000000);       // white color
        handFrame.setGravity(Gravity.CENTER);
        actionText.setTextSize(10);
        actionText.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2) - 15, lastViewBottom, 0, 0);
            makeLine(Start.getBottom());
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2) - 15, lastViewBottom, 0, 0);
            makeLine(lastViewBottom - 50);
        }

        inputFrame.setLayoutParams(textParam);

        inputFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MyDialog dialog = new MyDialog();
                dialog.callInputDialog(MainActivity.this, inputFrame, handFrame, input);
            }
        });
        // width/2 - leftParams.width/2

        FrameLayout.LayoutParams leftParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        leftParams.setMargins(45,12,0,0);
        leftParams.gravity = Gravity.LEFT;

        FrameLayout.LayoutParams rightParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        rightParams.setMargins(0,12,15,0);
        rightParams.gravity = Gravity.RIGHT;

        inputFrame.addView(handFrame, leftParams);
        inputFrame.addView(actionText, rightParams);

        inputFrame.setBackground(getResources().getDrawable(R.drawable.input));

        //makeLine(height);
        view.addView(inputFrame);

        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        id++;
    }

    /**
     * Output view 생성
     *
     */
    private void createOutput(){
        int width = 170;
        int height = 60;

        // Symbol logic용 코드
        logic.Symbolic.Output output;
        output = new logic.Symbolic.Output();

        output.setOutputID(outputId++);

        connectSymbolToLast(output);

        /**
         * Output view의 틀 이미지를 담기 위한 frame
         */
        FrameLayout outputFrame = new FrameLayout(getApplicationContext());
        /**
         * 어떠한 이벤트를 발생시키는지에 대한 textView.(outputFrame 내부에 위치함.)
         */
        TextView    event = new TextView(getApplicationContext());

        outputFrame.setBackground(getResources().getDrawable(R.drawable.output));

        outputFrame.setId(id);                     // id 값. 위에서 부터 순서대로 증가함

        event.setTextSize(10);               // text 크기
        event.setGravity(Gravity.CENTER);    // text를 중앙에 배치함

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2) + 15, lastViewBottom, 0, 0);
            makeLine(Start.getBottom());
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2) + 15, lastViewBottom, 0, 0);
            makeLine(lastViewBottom - 50);
        }

        outputFrame.setLayoutParams(textParam);

        outputFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MyDialog dialog = new MyDialog();
                dialog.callOutputDialog(MainActivity.this, event, output).show();
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,15,0);
        params.gravity = Gravity.CENTER;

        outputFrame.addView(event, params);

        //makeLine(height);
        view.addView(outputFrame);

        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        id++;
    }

    /**
     * Selection view 생성
     *
     */
    private logic.Symbolic.Selection createSelection(){
        int width = 170;
        int height = 80;

        // Symbol logic용 코드
        logic.Symbolic.Selection selection;
        selection = new logic.Symbolic.Selection();

        connectSymbolToLast(selection);

        /**
         * Selection view의 틀 이미지를 담기 위한 frame
         */
        FrameLayout selectionFrame = new FrameLayout(getApplicationContext());
        /**
         * Selection Symbol의 조건을 담기위한 view
         */
        TextView    condition = new TextView(getApplicationContext());
        TextView    trueText = new TextView(getApplicationContext());
        TextView    falseText = new TextView(getApplicationContext());

        selectionFrame.setBackground(getResources().getDrawable(R.drawable.selection_loop));

        selectionFrame.setId(id);                     // id 값. 위에서 부터 순서대로 증가함

        condition.setTextSize(10);               // text 크기
        condition.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        trueText.setTextSize(10);               // text 크기
        trueText.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        trueText.setText("T");
        falseText.setTextSize(10);               // text 크기
        falseText.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        falseText.setText("F");
        RelativeLayout.LayoutParams trueTextParam = new RelativeLayout.LayoutParams(30, 30);
        RelativeLayout.LayoutParams falseTextParam = new RelativeLayout.LayoutParams(30, 30);
        trueTextParam.setMargins(0,selectionFrame.getTop(),0,0);
        falseTextParam.setMargins(140,selectionFrame.getTop(),1,0);
        trueText.setLayoutParams(trueTextParam);
        falseText.setLayoutParams(falseTextParam);

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
        }

        selectionFrame.setLayoutParams(textParam);

        selectionFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MyDialog cDialog = new MyDialog();
                cDialog.callConditionDialog(MainActivity.this, condition, selection, assignmentList);
                TFflag = true;
            }
        });

        selectionFrame.addView(condition);
        selectionFrame.addView(trueText);
        selectionFrame.addView(falseText);

        view.addView(selectionFrame);


        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        selectionTop = lastViewBottom + 50 - lastHeight;
        makeSelectionLine(lastViewBottom - lastHeight, height, width);

        id++;

        return selection;
    }

    /**
     * LoopPoint view 생성
     *
     */
    private LoopPoint createLoopPoint(){
        int width = 120;
        int height = 120;

        view.removeView((Button)findViewById(endId));

        Button loopPointButton = new Button(getApplicationContext());

        loopPointButton.setTextSize(10);               // text 크기
        loopPointButton.setId(id);                  // id 값. 위에서 부터 순서대로 증가함
        loopPointButton.setGravity(Gravity.CENTER);    // text를 중앙에 배치함

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성

        loopPointFlag = true;

        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
            makeLine(Start.getBottom());
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
            makeLine(lastViewBottom - 50);
        }
        loopPointButton.setText("Loop");


        loopPointButton.setLayoutParams(textParam);
        loopPointButton.setBackgroundResource(R.drawable.loop_point);


        loopPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stu
                //Toast.makeText(getApplicationContext(), findViewById(id - 1).toString(), Toast.LENGTH_SHORT).show();
                //((TextView)findViewById(R.id.test)).setText(Integer.toString(lastViewBottom));
            }
        });

        //makeLine(height);
        view.addView(loopPointButton);

        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        id++;

        // logic 처리 부분
        LoopPoint loopPoint = new LoopPoint();
        loopPoint.setLoopPointID(loopPointId++);
        loopPoint.setIsFill(true);

        connectSymbolToLast(loopPoint);

        return loopPoint;
    }

    /**
     * Loop view 생성
     *
     */
    private logic.Symbolic.Loop createLoop(){
        int width = 170;
        int height = 80;

        // Symbol logic용 코드
        logic.Symbolic.Loop loop;
        loop = new logic.Symbolic.Loop();

        loop.setLoopPoint(createLoopPoint());

        connectSymbolToLast(loop);

        /**
         * Selection view의 틀 이미지를 담기 위한 frame
         */
        FrameLayout selectionFrame = new FrameLayout(getApplicationContext());
        /**
         * Selection Symbol의 조건을 담기위한 view
         */
        TextView    condition = new TextView(getApplicationContext());
        TextView    trueText = new TextView(getApplicationContext());
        TextView    falseText = new TextView(getApplicationContext());

        selectionFrame.setBackground(getResources().getDrawable(R.drawable.selection_loop));

        selectionFrame.setId(id);                     // id 값. 위에서 부터 순서대로 증가함

        condition.setTextSize(10);               // text 크기
        condition.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        trueText.setTextSize(10);               // text 크기
        trueText.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        trueText.setText("T");
        falseText.setTextSize(10);               // text 크기
        falseText.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        falseText.setText("F");
        RelativeLayout.LayoutParams trueTextParam = new RelativeLayout.LayoutParams(30, 30);
        RelativeLayout.LayoutParams falseTextParam = new RelativeLayout.LayoutParams(30, 30);
        trueTextParam.setMargins(0,selectionFrame.getTop(),0,0);
        falseTextParam.setMargins(140,selectionFrame.getTop(),1,0);
        trueText.setLayoutParams(trueTextParam);
        falseText.setLayoutParams(falseTextParam);

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
        }

        selectionFrame.setLayoutParams(textParam);
        selectionFrame.addView(trueText);
        selectionFrame.addView(falseText);

        selectionFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MyDialog cDialog = new MyDialog();
                cDialog.callConditionDialog(MainActivity.this, condition, loop, assignmentList);
                TFflag = true;
            }
        });

        selectionFrame.addView(condition);

        //makeSelectionLine(height);
        view.addView(selectionFrame);

        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        selectionTop = lastViewBottom + 50 - lastHeight;
        makeSelectionLine(lastViewBottom - lastHeight, height, width);

        id++;

        return loop;
    }

    /**
     * Assignment view 생성
     *
     */
    private void createAssignment(){
        int width = 170;
        int height = 60;

        logic.Symbolic.Assignment assignment;


        // Symbol logic용 코드
        assignment = new logic.Symbolic.Assignment();

        assignment.setAssignmentID(assignmentId++);
        connectSymbolToLast(assignment);


        /**
         * Assignment view의 틀 이미지를 담기 위한 frame
         */
        FrameLayout assignmentFrame = new FrameLayout(getApplicationContext());
        /**
         * 어떠한 값을 가지는지에 대한 textView.
         */
        TextView    value = new TextView(getApplicationContext());

        assignmentFrame.setBackground(getResources().getDrawable(R.drawable.assignment));

        assignmentFrame.setId(id);                     // id 값. 위에서 부터 순서대로 증가함

        value.setTextSize(10);               // text 크기
        value.setGravity(Gravity.CENTER);    // text를 중앙에 배치함

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        if(this.id == 0) {  // 처음 생성이라면 Start 객체 밑에 생성
            lastViewBottom = Start.getBottom() + 50;                    // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
            makeLine(Start.getBottom());
        }
        else{   // 아니라면 가장 최근 생성된 객체 하단에 생성
            lastViewBottom += 50; // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
            textParam.setMargins((creationCenterPoint - width / 2), lastViewBottom, 0, 0);
            makeLine(lastViewBottom - 50);
        }

        assignmentFrame.setLayoutParams(textParam);

        assignmentFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MyDialog dialog = new MyDialog();
                dialog.callAssignmentDialog(MainActivity.this, value, assignment, assignmentList);
            }
        });
        assignmentFrame.addView(value);

        //makeLine(height);
        view.addView(assignmentFrame);

        lastViewBottom += height;   // 마지막 view의 botton이 특정 부분에서만 구할 수 있기 때문에 여기서 구해둠.
        lastHeight = height;

        id++;
    }

    /**
     * ToF view 생성 (selection용)
     *
     */
    private void createToF(Selection selection){
        int width = 240;
        int height = 100;

        //makeSelectionLine(80);

        selectionFlag = true;

        Symbol tempTrueSymbol = new Symbol();
        Symbol tempFalseSymbol = new Symbol();

        currentSymbol = findLastSymbol(tempTrueSymbol);

        final boolean[] toggleButtonFlag = new boolean[1];
        toggleButtonFlag[0] = true;

        ToggleButton ToF = new ToggleButton(getApplicationContext());

        Button TFend = new Button(getApplicationContext());

        ToF.setId(ToFId++);
        TFend.setId(TFendId++);

        ToF.setTextSize(10);               // text 크기
        ToF.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        ToF.setTextOn("참");
        ToF.setTextOff("거짓");
        ToF.setChecked(true);

        TFend.setTextSize(10);               // text 크기
        TFend.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        TFend.setText("참 거짓 종료");

        RelativeLayout.LayoutParams ToFParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        RelativeLayout.LayoutParams TFParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성


        ToFParam.setMargins(0,15,40,0);
        TFParam.setMargins(0,15 + height,40,0);


        ToF.setLayoutParams(ToFParam);
        TFend.setLayoutParams(TFParam);


        ToF.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    // 첫번째 인자는 ToggleButton, 두번째 인자는 on/off에 대한 boolean값
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //toggle 버튼이 on된 경우
                        if(isChecked){
                            toggleButtonFlag[0] = true;
                            falseLastviewBottom = lastViewBottom;
                            lastViewBottom = tureLastviewBottom;
                            creationCenterPoint -= 420;
                            currentSymbol = findLastSymbol(tempTrueSymbol);
                        }else{
                            toggleButtonFlag[0] = false;
                            tureLastviewBottom = lastViewBottom;
                            lastViewBottom = falseLastviewBottom;
                            creationCenterPoint += 420;
                            currentSymbol = findLastSymbol(tempFalseSymbol);
                        }
                        //Toast.makeText(MainActivity.this,toastMessage,Toast.LENGTH_SHORT).show();
                    }
                }
        );

        TFend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButtonFlag[0] == true){
                    creationCenterPoint += 210;
                    tureLastviewBottom = lastViewBottom;
                }else if(toggleButtonFlag[0] == false){
                    creationCenterPoint -= 210;
                    falseLastviewBottom = lastViewBottom;
                }

                lastViewBottom = Math.max(tureLastviewBottom, falseLastviewBottom);
                view.removeView((ToggleButton)findViewById(--ToFId));
                view.removeView((Button)findViewById(--TFendId));

                selection.setTureStartSymbol(tempTrueSymbol.getPostSymbol());
                selection.setFalseStartSymbol(tempFalseSymbol.getPostSymbol());

                currentSymbol = selection;
                selectionFlag = false;
            }
        });

        view.addView(ToF);
        view.addView(TFend);
    }

    /**
     * ToF view 생성 (loop용)
     *
     */
    private void createToF(logic.Symbolic.Loop loop){
        int width = 240;
        int height = 100;

        loopFlag = true;

        Symbol tempTrueSymbol = new Symbol();
        Symbol tempFalseSymbol = new Symbol();

        currentSymbol = findLastSymbol(tempTrueSymbol);

        final boolean[] toggleButtonFlag = new boolean[1];
        toggleButtonFlag[0] = true;

        ToggleButton ToF = new ToggleButton(getApplicationContext());

        Button TFend = new Button(getApplicationContext());

        ToF.setId(ToFId++);
        TFend.setId(TFendId++);

        ToF.setTextSize(10);               // text 크기
        ToF.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        ToF.setTextOn("참");
        ToF.setTextOff("거짓");
        ToF.setChecked(true);

        TFend.setTextSize(10);               // text 크기
        TFend.setGravity(Gravity.CENTER);    // text를 중앙에 배치함
        TFend.setText("참 거짓 종료");

        RelativeLayout.LayoutParams ToFParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성
        RelativeLayout.LayoutParams TFParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성


        ToFParam.setMargins(0,15,40,0);
        TFParam.setMargins(0,15 + height,40,0);


        ToF.setLayoutParams(ToFParam);
        TFend.setLayoutParams(TFParam);

        ToF.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            // 첫번째 인자는 ToggleButton, 두번째 인자는 on/off에 대한 boolean값
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String toastMessage;
                //toggle 버튼이 on된 경우
                if(isChecked){
                    toggleButtonFlag[0] = true;
                    falseLastviewBottom = lastViewBottom;
                    lastViewBottom = tureLastviewBottom;
                    creationCenterPoint -= 420;

                    currentSymbol = findLastSymbol(tempTrueSymbol);
                }else{
                    toggleButtonFlag[0] = false;
                    tureLastviewBottom = lastViewBottom;
                    lastViewBottom = falseLastviewBottom;
                    toastMessage = Integer.toString(lastViewBottom);
                    //lastViewBottom = 0;
                    creationCenterPoint += 420;

                    currentSymbol = findLastSymbol(tempFalseSymbol);
                }
            }
        }
        );

        TFend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButtonFlag[0] == true){
                    creationCenterPoint += 210;
                    tureLastviewBottom = lastViewBottom;
                }else if(toggleButtonFlag[0] == false){
                    creationCenterPoint -= 210;
                    falseLastviewBottom = lastViewBottom;
                }
                view.removeView((ToggleButton)findViewById(--ToFId));
                view.removeView((Button)findViewById(--TFendId));
                lastViewBottom = Math.max(tureLastviewBottom, falseLastviewBottom);
                loop.setTureStartSymbol(tempTrueSymbol.getPostSymbol());
                loop.setFalseStartSymbol(tempFalseSymbol.getPostSymbol());

                currentSymbol = loop;
                loopFlag = false;
            }
        });

        view.addView(ToF);
        view.addView(TFend);
    }

    /**
     * End view 생성
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createEnd(){
        int width = 120;
        int height = 120;
        int top = 0;

        view.removeView((Button)findViewById(endId));

        Button endButton = new Button(getApplicationContext());

        endButton.setTextSize(10);               // text 크기
        endButton.setId(endId);                  // id 값. 위에서 부터 순서대로 증가함
        endButton.setGravity(Gravity.CENTER);    // text를 중앙에 배치함

        RelativeLayout.LayoutParams textParam = new RelativeLayout.LayoutParams(width, height); //textView 속성 정의, 크기를 대입하여 생성


        if(selectionFlag == true || loopFlag == true) {
            if (lastViewBottom <= falseLastviewBottom) {
                top = falseLastviewBottom;
            } else if (lastViewBottom <= tureLastviewBottom) {
                top = tureLastviewBottom;
            }else{
                top = lastViewBottom;
            }
        }
        else{
            top = lastViewBottom;
        }

        if(id != 0){
            top += 50;
        }

        textParam.setMargins(Start.getLeft(), top, 0, 50);

        endButton.setText("End");

        endButton.setLayoutParams(textParam);
        endButton.setBackgroundResource(R.drawable.oval);


        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), findViewById(id - 1).toString(), Toast.LENGTH_SHORT).show();
                //((TextView)findViewById(R.id.test)).setText(Integer.toString(lastViewBottom));
            }
        });

        view.addView(endButton);

        if(selectionFlag == true) {
            makeSelectionBottomLine(top);
        }else if(loopFlag == true){
            makeLoopBottomLine(top);
        }

        if(findViewById(endLineId) != null){
            view.removeView(findViewById(endLineId));
        }
        View verticalLine1 = new View(getApplicationContext());
        RelativeLayout.LayoutParams textParam1 = new RelativeLayout.LayoutParams(2, 28);
        textParam1.setMargins(Start.getLeft() + Start.getWidth()/2, top - 26, 0, 0);
        verticalLine1.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        verticalLine1.setLayoutParams(textParam1);
        verticalLine1.setId(endLineId);
        view.addView(verticalLine1);

        //Toast.makeText(this, Integer.toString(lineId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 가장 최근 입력한 view 삭제
     */
    private void deleteSymbol(){
        try {
            view.removeView(findViewById(--id));

            if(id >= 0)
                view.removeView(findViewById(endId));

            if(id == 0)
                lastViewBottom = Start.getBottom() + 50;

            lineId -= 100;
            if(lineId == 0){
                lineId = 100;
            }else {
                view.removeView(findViewById(lineId));
                view.removeView(findViewById(lineId + 1));
                view.removeView(findViewById(lineId + 2));
                view.removeView(findViewById(lineId + 3));
                view.removeView(findViewById(lineId + 4));
                view.removeView(findViewById(lineId + 5));
                view.removeView(findViewById(lineId + 6));
                view.removeView(findViewById(lineId + 7));
                view.removeView(findViewById(lineId + 8));
                view.removeView(findViewById(lineId + 9));
                view.removeView(findViewById(lineId + 10));
                //Toast.makeText(this, Integer.toString(lineId), Toast.LENGTH_SHORT).show();
            }

            // view가 새로 그려지는 시점에서 lastHeight를 구한다. lastHeight를 이용하여 end Symbol을 정확한 위치에 생성한다.
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    View lastView = findViewById(id - 1);
                    if(lastView != null) {
                        lastHeight = lastView.getHeight();
                        //Toast.makeText(getApplicationContext(), "lastHeight = " + Integer.toString(lastHeight), Toast.LENGTH_SHORT).show();
                    }
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            if(id < 0){
                id = 0;
            }
            else if(id > 0) {
                lastViewBottom -= lastHeight + 50;
                deleteFlag = true;
                createEnd();
                deleteFlag = false;
            }else{
                lastViewBottom = Start.getBottom() + 50;
                deleteFlag = true;
                createEnd();
                deleteFlag = false;
            }


            if(currentSymbol != startEnd) {
                if (currentSymbol.getPreSymbol() == startEnd) {
                    startEnd.setPostSymbol(end);
                    end.setPreSymbol(startEnd);
                }

                currentSymbol.getPreSymbol().setPostSymbol(end);
                end.setPreSymbol(currentSymbol.getPreSymbol());
                currentSymbol = currentSymbol.getPreSymbol();
            }
        }catch(NullPointerException e) {
            ((Button)findViewById(TFendId -1)).callOnClick();
            view.removeView(findViewById(id));

            if(id >= 0)
                view.removeView(findViewById(endId));

            if(id == 0)
                lastViewBottom = Start.getBottom() + 50;

            if(lineId == 0){
                lineId = 100;
            }else {
                view.removeView(findViewById(lineId));
                view.removeView(findViewById(lineId + 1));
                view.removeView(findViewById(lineId + 2));
                view.removeView(findViewById(lineId + 3));
                view.removeView(findViewById(lineId + 4));
                view.removeView(findViewById(lineId + 5));
                view.removeView(findViewById(lineId + 6));
                view.removeView(findViewById(lineId + 7));
                view.removeView(findViewById(lineId + 8));
                view.removeView(findViewById(lineId + 9));
                view.removeView(findViewById(lineId + 10));
                //Toast.makeText(this, Integer.toString(lineId), Toast.LENGTH_SHORT).show();
            }

            // view가 새로 그려지는 시점에서 lastHeight를 구한다. lastHeight를 이용하여 end Symbol을 정확한 위치에 생성한다.
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    View lastView = findViewById(id - 1);
                    if(lastView != null) {
                        lastHeight = lastView.getHeight();
                        //Toast.makeText(getApplicationContext(), "lastHeight = " + Integer.toString(lastHeight), Toast.LENGTH_SHORT).show();
                    }
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            if(id < 0){
                id = 0;
            }
            else if(id > 0) {
                lastViewBottom -= lastHeight + 50;
                deleteFlag = true;
                createEnd();
                deleteFlag = false;
            }else{
                lastViewBottom = Start.getBottom() + 50;
                deleteFlag = true;
                createEnd();
                deleteFlag = false;
            }


            if(currentSymbol != startEnd) {
                if (currentSymbol.getPreSymbol() == startEnd) {
                    startEnd.setPostSymbol(end);
                    end.setPreSymbol(startEnd);
                }

                currentSymbol.getPreSymbol().setPostSymbol(end);
                end.setPreSymbol(currentSymbol.getPreSymbol());
                currentSymbol = currentSymbol.getPreSymbol();
            }
        }
    }

    /**
     * 순서도 제일 하단에 심볼을 추가함.
     * @param symbol 연결하고자하는 심볼 객체를 전달.
     */
    private void connectSymbolToLast(Symbol symbol){
        currentSymbol.setPostSymbol(symbol);
        symbol.setPreSymbol(currentSymbol);
        currentSymbol = symbol;
        currentSymbol.setPostSymbol(end);
        end.setPreSymbol(currentSymbol);
    }

    private Symbol findLastSymbol(Symbol symbol){
        while(symbol.getPostSymbol() != null) {
            //end symbol 바로 위에 붙은 symbol을 return함.
            if (symbol.getPostSymbol().getPostSymbol() == null) {
                return symbol;
            }

            symbol = symbol.getPostSymbol();
        }
        return symbol;
    }

    /****************************선긋기 망해버린 부분들. 개발 후순위로 미루자****************************/
    /**
     * 지금 알고리즘 자체를 잘 못 설계했다. 생성되는 뷰들을 리스트로 담아서 보관하고, 이전 뷰, 이후 뷰의 절대 좌표를 기준으로 현재 뷰와 선을 그어주면 될 일이다.
     *
     */
    private void makeLine(int originHeight){
        int height = 50;

        View verticalLine1 = new View(getApplicationContext());
        View verticalLine2 = new View(getApplicationContext());

        RelativeLayout.LayoutParams textParam1 = new RelativeLayout.LayoutParams(2, height/2);
        RelativeLayout.LayoutParams textParam2 = new RelativeLayout.LayoutParams(2, height/2+10);

        textParam1.setMargins((creationCenterPoint), originHeight + height/2, 0, 0);
        textParam2.setMargins((creationCenterPoint), originHeight + height*2, 0, 0);

        if(loopPointFlag == true){
            textParam2.setMargins((creationCenterPoint), originHeight + height*2 + 58, 0, 0);
            loopPointFlag = false;
        }

        verticalLine1.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        verticalLine1.setLayoutParams(textParam1);
        verticalLine2.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        verticalLine2.setLayoutParams(textParam2);

        verticalLine1.setId(lineId);
        verticalLine2.setId(lineId + 1);
        //Toast.makeText(this, Integer.toString(lineId), Toast.LENGTH_SHORT).show();
        /**
         * 100의 자리는 symbol ID, 1의 자리는 line ID
         */

        if(selectionFlag == false && loopFlag == false){
            lineId += 100;
        }

        view.addView(verticalLine1);
        view.addView(verticalLine2);
    }

    private void makeSelectionLine(int originHeight, int sysmbolHeight, int symbolWidth){
        View topLeftHorizontalLine = new View(getApplicationContext());
        View topRightHorizontalLine = new View(getApplicationContext());
        View tipVerticalLine = new View(getApplicationContext());

        RelativeLayout.LayoutParams tlhParam = new RelativeLayout.LayoutParams(210 - symbolWidth/2 + 4, 2);
        RelativeLayout.LayoutParams trhParam = new RelativeLayout.LayoutParams(210 - symbolWidth/2 + 6, 2);
        RelativeLayout.LayoutParams tvParam = new RelativeLayout.LayoutParams(2, 26);

        tlhParam.setMargins((creationCenterPoint) - 210, originHeight + sysmbolHeight/2, 0, 0);
        trhParam.setMargins((creationCenterPoint) + symbolWidth/2 - 2 , originHeight + sysmbolHeight/2, 0, 0);
        tvParam.setMargins((creationCenterPoint), originHeight - 26, 0, 0);

        topLeftHorizontalLine.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        topLeftHorizontalLine.setLayoutParams(tlhParam);
        topRightHorizontalLine.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        topRightHorizontalLine.setLayoutParams(trhParam);
        tipVerticalLine.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        tipVerticalLine.setLayoutParams(tvParam);

        topLeftHorizontalLine.setId(lineId + 2);
        topRightHorizontalLine.setId(lineId + 3);
        tipVerticalLine.setId(lineId + 4);

        view.addView(topLeftHorizontalLine);
        view.addView(topRightHorizontalLine);
        view.addView(tipVerticalLine);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeSelectionBottomLine(int originHeight) {
        int vTop = selectionTop;
        int vbuttom = lastViewBottom;

        View leftVerticalLine = new View(getApplicationContext());
        View rightVerticalLine = new View(getApplicationContext());
        View bottomHorizontalLine = new View(getApplicationContext());

        if(selectionFlag == true) {
            if (lastViewBottom <= falseLastviewBottom) {
                vbuttom = falseLastviewBottom;
            } else if (lastViewBottom <= tureLastviewBottom) {
                vbuttom = tureLastviewBottom;
            } else {
                vbuttom = lastViewBottom;
            }
        }else{
            vbuttom = lastViewBottom;
        }
        RelativeLayout.LayoutParams lvParam = new RelativeLayout.LayoutParams(2, vbuttom - selectionTop + 80/2 - 7);
        RelativeLayout.LayoutParams rvParam = new RelativeLayout.LayoutParams(2, vbuttom - selectionTop + 80/2 - 7);
        RelativeLayout.LayoutParams bhParam = new RelativeLayout.LayoutParams(422, 2);

        lvParam.setMargins((Start.getLeft() + Start.getWidth()/2) - 210, vTop - 5, 0, 0);
        rvParam.setMargins((Start.getLeft() + Start.getWidth()/2) + 210, vTop - 5, 0, 0);
        bhParam.setMargins((Start.getLeft() + Start.getWidth()/2) - 210, originHeight - 24, 0, 0);

        leftVerticalLine.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        leftVerticalLine.setLayoutParams(lvParam);
        rightVerticalLine.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        rightVerticalLine.setLayoutParams(rvParam);
        bottomHorizontalLine.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        bottomHorizontalLine.setLayoutParams(bhParam);

        view.removeView(findViewById(lineId + 5 - 100));
        view.addView(leftVerticalLine);
        leftVerticalLine.setId(lineId + 5);

        view.removeView(findViewById(lineId + 6 - 100));
        view.addView(rightVerticalLine);
        rightVerticalLine.setId(lineId + 6);

        view.removeView(findViewById(lineId + 7 - 100));
        view.addView(bottomHorizontalLine);
        bottomHorizontalLine.setId(lineId + 7);



        for(int i = 0; i < id; i++)
            findViewById(i).bringToFront();

        Code.bringToFront();

        if(deleteFlag == false) {
            lineId += 100;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeLoopBottomLine(int originHeight) {
        int vTop = selectionTop;
        int vbuttom = lastViewBottom;

        View leftVerticalLine = new View(getApplicationContext());
        View rightVerticalLine = new View(getApplicationContext());
        View bottomHorizontalLine = new View(getApplicationContext());
        View returnVerticalLine = new View(getApplicationContext());
        View bottomRightHorizontalLine = new View(getApplicationContext());
        View topHorizontalLine = new View(getApplicationContext());

        if(loopFlag == true) {
            if (lastViewBottom <= falseLastviewBottom) {
                vbuttom = falseLastviewBottom;
            } else if (lastViewBottom <= tureLastviewBottom) {
                vbuttom = tureLastviewBottom;
            } else {
                vbuttom = lastViewBottom;
            }
        }else{
            vbuttom = lastViewBottom;
        }
        RelativeLayout.LayoutParams lvParam = new RelativeLayout.LayoutParams(2, vbuttom - selectionTop + 80/2 - 7);
        RelativeLayout.LayoutParams rvParam = new RelativeLayout.LayoutParams(2, vbuttom - selectionTop + 80/2 - 7);
        RelativeLayout.LayoutParams bhParam = new RelativeLayout.LayoutParams(422/2, 2);
        RelativeLayout.LayoutParams rtvParam = new RelativeLayout.LayoutParams(2, vbuttom - selectionTop + 80/2 - 7 + 160);
        RelativeLayout.LayoutParams brhParam = new RelativeLayout.LayoutParams(120, 2);
        RelativeLayout.LayoutParams thParam = new RelativeLayout.LayoutParams(280, 2);

        lvParam.setMargins((Start.getLeft() + Start.getWidth()/2) - 210, vTop - 5, 0, 0);
        rvParam.setMargins((Start.getLeft() + Start.getWidth()/2) + 210, vTop - 5, 0, 0);
        bhParam.setMargins((Start.getLeft() + Start.getWidth()/2) - 210, originHeight - 24, 0, 0);
        rtvParam.setMargins((Start.getLeft() + Start.getWidth()/2) + 210 + 120, vTop - 5 - 160, 0, 0);
        brhParam.setMargins((Start.getLeft() + Start.getWidth()/2) + 210, originHeight - 24, 0, 0);
        thParam.setMargins((Start.getLeft() + Start.getWidth()/2) + 55, vTop - 5 - 160, 0, 0);

        leftVerticalLine.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        leftVerticalLine.setLayoutParams(lvParam);
        rightVerticalLine.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        rightVerticalLine.setLayoutParams(rvParam);
        bottomHorizontalLine.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        bottomHorizontalLine.setLayoutParams(bhParam);
        returnVerticalLine.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        returnVerticalLine.setLayoutParams(rtvParam);
        bottomRightHorizontalLine.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        bottomRightHorizontalLine.setLayoutParams(brhParam);
        topHorizontalLine.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        topHorizontalLine.setLayoutParams(thParam);

        view.removeView(findViewById(lineId + 5 - 100));
        view.addView(leftVerticalLine);
        leftVerticalLine.setId(lineId + 5);

        view.removeView(findViewById(lineId + 6 - 100));
        view.addView(rightVerticalLine);
        rightVerticalLine.setId(lineId + 6);

        view.removeView(findViewById(lineId + 7 - 100));
        view.addView(bottomHorizontalLine);
        bottomHorizontalLine.setId(lineId + 7);

        view.removeView(findViewById(lineId + 8 - 100));
        view.addView(returnVerticalLine);
        returnVerticalLine.setId(lineId + 8);

        view.removeView(findViewById(lineId + 9 - 100));
        view.addView(bottomRightHorizontalLine);
        bottomRightHorizontalLine.setId(lineId + 9);

        view.removeView(findViewById(lineId + 10 - 100));
        view.addView(topHorizontalLine);
        topHorizontalLine.setId(lineId + 10);



        for(int i = 0; i < id; i++)
            findViewById(i).bringToFront();

        if(deleteFlag == false) {
            lineId += 100;
        }
    }

    /*private void makeSelectionLine(int originHeight){
        int height = 56;

        // 루프문 하단에 오는 심볼들의 총 길이. 나중에 인자로 빼야할듯
        int heightSum = 80;

        View verticalLine_down = new View(getApplicationContext());

        View verticalLine_left = new View(getApplicationContext());
        View verticalLine_right = new View(getApplicationContext());

        View horizontalLine_up = new View(getApplicationContext());
        View horizontalLine_down = new View(getApplicationContext());

        RelativeLayout.LayoutParams vParam_down = new RelativeLayout.LayoutParams(2, height);

        RelativeLayout.LayoutParams vParam_left = new RelativeLayout.LayoutParams(2, height * 2 + heightSum + originHeight/2);
        RelativeLayout.LayoutParams vParam_right = new RelativeLayout.LayoutParams(2, height * 2 + heightSum + originHeight/2);

        RelativeLayout.LayoutParams hParam_up = new RelativeLayout.LayoutParams(420, 2);
        RelativeLayout.LayoutParams hParam_down = new RelativeLayout.LayoutParams(420, 2);

        vParam_down.setMargins((creationCenterPoint), lastViewBottom + height, 0, 0);

        vParam_left.setMargins((creationCenterPoint) - 210, lastViewBottom - originHeight/2, 0, 0);
        vParam_right.setMargins((creationCenterPoint) + 210, lastViewBottom - originHeight/2, 0, 0);

        hParam_up.setMargins((creationCenterPoint) - 210, lastViewBottom - originHeight/2, 0, 0);
        hParam_down.setMargins((creationCenterPoint) - 210, lastViewBottom - originHeight/2 + (height * 2 + heightSum + originHeight/2), 0, 0);

        verticalLine_down.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        verticalLine_down.setLayoutParams(vParam_down);

        verticalLine_left.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        verticalLine_left.setLayoutParams(vParam_left);
        verticalLine_right.setBackground(getResources().getDrawable(R.drawable.vertical_line));
        verticalLine_right.setLayoutParams(vParam_right);

        horizontalLine_up.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        horizontalLine_up.setLayoutParams(hParam_up);
        horizontalLine_down.setBackground(getResources().getDrawable(R.drawable.horizontal_line));
        horizontalLine_down.setLayoutParams(hParam_down);

        view.addView(verticalLine_down);

        view.addView(verticalLine_left);
        view.addView(verticalLine_right);

        view.addView(horizontalLine_up);
        view.addView(horizontalLine_down);
    }*/

    void makeRectangle(int originHeight){
        int height = 56;

        // 루프문 하단에 오는 심볼들의 총 길이. 나중에 인자로 빼야할듯
        int heightSum = 80;

        View rectangle = new View(getApplicationContext());

        RelativeLayout.LayoutParams rectPram = new RelativeLayout.LayoutParams(420, originHeight/2 + height*2 + heightSum);
        rectPram.setMargins((creationCenterPoint) - 210, lastViewBottom + originHeight/2, 0, 0);

        rectangle.setBackground(getResources().getDrawable(R.drawable.assignment));
        rectangle.setLayoutParams(rectPram);

        view.addView(rectangle);
    }

    boolean isFill(){
        Symbol currentSymbol = startEnd.getPostSymbol();

        while(currentSymbol.getPostSymbol() != null){
            if(!(currentSymbol.isFill())){
                return false;
            }
            currentSymbol = currentSymbol.getPostSymbol();
        }
        return true;
    }
}



