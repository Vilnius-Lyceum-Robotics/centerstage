package org.firstinspires.ftc.teamcode.helpers;

public class BooleanState {
    private boolean state;
    public BooleanState(boolean state){
        this.state = state;
    }

    public void set(boolean newState){
        state = newState;
    }

    public boolean get(){
        return state;
    }
    public void toggleState(){
        this.state = !state;
    }


}
