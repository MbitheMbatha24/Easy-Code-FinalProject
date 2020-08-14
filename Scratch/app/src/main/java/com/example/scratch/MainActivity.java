package com.example.scratch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef ;
    FirebaseUser user ;
    Map<String, Object> token;
    RecyclerView recyclerView;
    private List<ListData> listData;
    private MyAdapter adapter;
    TranslateAnimation ta1,ta2;
   // ImageView img;
   ImageView imgVw;
  // RelativeLayout gameboard;
   float width;
   float height;
   FirebaseUser currentUser;
    String text;
    TextToSpeech tts;
    Boolean sound=true;
    ImageButton mute;

    ViewGroup gameboard;
    int xdelta;
    int ydelta;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser == null){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Anonymously Signed in.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                               // Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Anonymous Authentication failed.", Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mAuth = FirebaseAuth.getInstance();
        gameboard=findViewById(R.id.gameboard);
         imgVw = (ImageView) findViewById(R.id.img);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(150,150);
        imgVw.setLayoutParams(layoutParams);
        imgVw.setOnTouchListener(new MainActivity.ChoiceTouchListener());
        imgVw.setTag("ANDROID ICON");

        //gameboard =findViewById(R.id.gameboard);

        width=gameboard.getWidth();
        height=gameboard.getHeight();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Actions");
        user= mAuth.getCurrentUser();
        token = new HashMap<>();
        recyclerView = findViewById(R.id.recyler);
        recyclerView.setHasFixedSize(true);
        mute=findViewById(R.id.mute);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        listData=new ArrayList<>();
        ta1=new TranslateAnimation(imgVw.getY()+20,imgVw.getX(),imgVw.getX(),imgVw.getY());
        ta1.setDuration(1000); //1 second
        currentUser = mAuth.getCurrentUser();
        //retrive actions from the database, first we check if there is a signed in user
     if (currentUser!=null){
         final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("Actions").child(user.getUid());
         nm.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     listData.clear();
                     for (DataSnapshot npsnapshot : snapshot.getChildren()){
                         ListData l=npsnapshot.getValue(ListData.class);
                         listData.add(l);

                     }
                     adapter=new MyAdapter(listData);
                     adapter.notifyDataSetChanged();
                     recyclerView.setAdapter(adapter);

                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
     }
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }

                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
    }
    int left;
    int top;

    public void help(View view) {
        Intent k = new Intent(MainActivity.this,  TutorialActivity.class);
        startActivity(k);
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
            gameboard.invalidate();

            return true;
        }
    }

    public void add_right(View view) {
         //imgVw.animate().translationX(0).translationY(0);
        if (sound){
            text="right";
            ConvertTextToSpeech();
        }
        token.put("action", "r");
        token.put("timestamp",  ServerValue.TIMESTAMP);
        myRef.child(currentUser.getUid()).push().setValue(token);
    }

    public void add_left(View view) {
        if (sound){
            text="left";
            ConvertTextToSpeech();
        }

        token.put("action", "l");
        token.put("timestamp",  ServerValue.TIMESTAMP);
        myRef.child(user.getUid()).push().setValue(token);
    }

    public void add_up(View view) {
        if (sound){
            text="up";
            ConvertTextToSpeech();
        }

        token.put("action", "u");
        token.put("timestamp",  ServerValue.TIMESTAMP);
        myRef.child(user.getUid()).push().setValue(token);
    }

    public void add_down(View view) {

        if (sound){
            text="down";
            ConvertTextToSpeech();
        }
        token.put("action", "d");
        token.put("timestamp",  ServerValue.TIMESTAMP);
        myRef.child(user.getUid()).push().setValue(token);
    }

    public void move(View view) {
        final Handler handler = new Handler();
        int diff= (int) (gameboard.getHeight()-(imgVw.getY()+14));
        List<ObjectAnimator>mylist=new ArrayList<>();
        //ObjectAnimator animU = ObjectAnimator.ofFloat(imgVw, "y", imgVw.getY() - 20);
       // ObjectAnimator animR = ObjectAnimator.ofFloat(imgVw, "x", imgVw.getX() + 50);
       // ObjectAnimator animR1 = ObjectAnimator.ofFloat(imgVw, "x", imgVw.getX() + 50);
       // ObjectAnimator animL = ObjectAnimator.ofFloat(imgVw, "x", imgVw.getX() - 20);
        //ObjectAnimator animD = ObjectAnimator.ofFloat(imgVw,"y",imgVw.getY()+20);
        //ObjectAnimator animD1 = ObjectAnimator.ofFloat(imgVw,"y",imgVw.getY()+20);

        for (int i=0; i<listData.size(); i++) {
            ListData ld=listData.get(i);
            String action=ld.getAction();
            //String act= String.valueOf(listData.get(i));
            //Toast.makeText(MainActivity.this,action, Toast.LENGTH_SHORT).show();
            switch (action) {
                case "u":

                    ObjectAnimator animv = ObjectAnimator.ofFloat(imgVw, "y", imgVw.getY() - 20);
                    mylist.add(animv);

                    break;
                case "r":
                    if(diff>5){
                    ObjectAnimator animR = ObjectAnimator.ofFloat(imgVw, "x", imgVw.getX() + 50);
                    //ObjectAnimator animY = ObjectAnimator.ofFloat(imgVw, "y", imgVw.getY()-134);
                    mylist.add(animR);
            }
                    break;
                case "l":
                        ObjectAnimator animL = ObjectAnimator.ofFloat(imgVw, "x", imgVw.getX() - 20);
                        //ObjectAnimator animY = ObjectAnimator.ofFloat(imgVw, "y", imgVw.getY()-134);
                    mylist.add(animL);

                    break;
                case "d":
                    ObjectAnimator animD = ObjectAnimator.ofFloat(imgVw,"y",imgVw.getY()+20);
                    //ObjectAnimator animY = ObjectAnimator.ofFloat(imgVw, "y", imgVw.getY()-134);
                  mylist.add(animD);
                    break;
            }

        }


        ObjectAnimator[] objectAnimators = mylist.toArray(new ObjectAnimator[mylist.size()]);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(500);
        animSetXY.playSequentially(objectAnimators);
        animSetXY.start();
        animSetXY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // ...
                text="Good Job";
                ConvertTextToSpeech();
            }
        });
    }
    public void chng_to_farm(View view) {
        gameboard.setBackgroundResource(R.drawable.farm);
    }

    public void chg_to_city(View view) {
        gameboard.setBackgroundResource(R.drawable.city);
    }

    public void chg_to_land(View view) {
        gameboard.setBackgroundResource(R.drawable.land);
    }

    public void chng_to_dogo(View view) {
        //imgVw.setBackgroundResource(R.drawable.dogo);
        if (sound){
            text="A dog";
            ConvertTextToSpeech();
        }
        imgVw.setImageResource(R.drawable.dogo);

    }

    public void chng_to_boy(View view) {
        if (sound){
            text="A boy";
            ConvertTextToSpeech();
        }
        imgVw.setImageResource(R.drawable.boy);
    }
    public void chng_to_peng(View view) {
        if (sound){
            text="A penguin";
            ConvertTextToSpeech();
        }
        imgVw.setImageResource(R.drawable.penguin);
    }
    public void chg_to_cat(View view) {
       // imgVw.setBackgroundResource(R.drawable.cartoon);
        if (sound){
            text="A girl";
            ConvertTextToSpeech();
        }
        imgVw.setImageResource(R.drawable.girl);
    }

    public void clear(View view) {
       // gameboard.invalidate();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference("Actions").child(user.getUid());
        nm.removeValue();
        listData.clear();
        adapter.notifyDataSetChanged();
        imgVw.animate().translationX(0).translationY(0);
    }

    public void add_text(View view) {
        token.put("action", "t");
        token.put("Timestamp",  ServerValue.TIMESTAMP);
        myRef.child(user.getUid()).push().setValue(token);
    }



    public void mute(View view) {
        if (sound){
            sound=false;
            mute.setImageResource(R.drawable.ic_baseline_music_off_24);
        }else {
            sound=true;
            mute.setImageResource(R.drawable.ic_baseline_music_note_24);
        }

        }
    private void ConvertTextToSpeech() {
        // TODO Auto-generated method stub
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }



}