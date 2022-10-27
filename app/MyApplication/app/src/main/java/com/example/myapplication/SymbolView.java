package com.example.myapplication;

public class SymbolView {
    SymbolView(){

    }
    SymbolView(int id, int top, int center){
        this.id = id;
        this.top = top;
        this.center = center;
    }

    enum Symbol{
        CALL, INPUT, OUTPUT, SELECTION, LOOPPOINT, LOOP, ASSIGNMENT
    }

    Symbol type;


    int id;
    int top;
    int center;
    int selectionID;

    void call(){
        this.type = Symbol.CALL;
    }
    void input(){
        this.type = Symbol.INPUT;
    }
    void output(){
        this.type = Symbol.OUTPUT;
    }
    void selection(){
        this.type = Symbol.SELECTION;
    }
    void loopPoint(){
        this.type = Symbol.LOOPPOINT;
    }
    void loop(){
        this.type = Symbol.LOOP;
    }
    void assignment(){
        this.type = Symbol.ASSIGNMENT;
    }


}
