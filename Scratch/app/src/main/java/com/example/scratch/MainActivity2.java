package com.example.scratch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity2 extends AppCompatActivity {
    ImageView img;
    ViewGroup root;
    int xdelta;
    int ydelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        root=findViewById(R.id.gameboard);
        img=root.findViewById(R.id.img);

        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(150,150);
        img.setLayoutParams(layoutParams);
        img.setOnTouchListener(new ChoiceTouchListener());
    }

    private class ChoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
           final  int X= (int)motionEvent.getRawX();
           final int Y = (int) motionEvent.getRawY();
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams=(RelativeLayout.LayoutParams) view.getLayoutParams();
                    xdelta=X-lParams.leftMargin;
                    ydelta=Y-lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                 case MotionEvent.ACTION_MOVE:
                     RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams) view.getLayoutParams();
                     layoutParams.leftMargin =X-xdelta;
                     layoutParams.topMargin=Y-ydelta;
                     layoutParams.rightMargin=-250;
                     layoutParams.bottomMargin=-250;
                     view.setLayoutParams(layoutParams);
                     break;
            }
            root.invalidate();

            return true;
        }
    }
}