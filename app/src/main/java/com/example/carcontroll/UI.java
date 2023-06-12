package com.example.carcontroll;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

public class UI {
    SharedPreferences pref;
    public UI(SharedPreferences _pref){
        pref = _pref;
    }

    public void update(ImageView btn,String element){
        String state = pref.getString(element,null);
        Log.w("WARNING",String.format("Test -> %s of element %s", state, element));
        switch(element){
            case "defDrlState":
            case "defAmpState":
            case "defDvrState":
            case "defInterState":
            case "drlState":
            case "ampState":
            case "dvrState":
            case "interState":
                // reverce images !!!
                if(state.equals("ON")){
                    btn.setBackgroundResource(R.drawable.btn_on);
                }else{
                    btn.setBackgroundResource(R.drawable.btn_off);
                }
                break;
        }
    }
}

