package com.example.applanta;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InteractionActivity extends AppCompatActivity {
    private Plant plant;
    private User currentUser;
    private SoundPool soundPool;
    private int sampleExtrovert;
    private int sampleCalm;
    private int sampleHiper;
    private int samplePlayfull;
    private int sampleTimid;
    private int sampleUser;
    private int a;
    private boolean firstTouch = true;
    private ArrayList<Float> pitchOrder = new ArrayList<Float>();
    private ArrayList<Long> rhythmInput = new ArrayList<Long>();
    private long startElapse;
    private long endElapse;
    private long timeElapsed;
    private Random rand = new Random();
    private final float fofi = (float) 0.08; //a semitone, baptized as a "fofi" because using cent (an actual unit to measure sound frequencies) would be too complex for this project
    private Timer timer;
    private Timer dinoTimer; //We have to use another timer so the extroverts talk by themselves but still respond
    private Timer recorderTimer;
    private Timer recordProgressTimer;
    private boolean isRecording = false;
    private ArrayList<Note> recording = new ArrayList<Note>();
    private long SERecording;
    private long EERecording;
    private ImageView redRecordBtn;
    private ImageView note1;
    private ImageView note2;
    private ImageView note3;
    private ImageView note4;
    private ImageView note5;
    private int nextNote = 0;
    private ImageView noteToShow;
    private ProgressBar recordProgressBar;
    private int currentProgress = 0;
    public Context interactionContext;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);
        hideNavBar();
        interactionContext = getApplicationContext();
        redRecordBtn = findViewById(R.id.record_interaction_tier_red);
        recordProgressBar = findViewById(R.id.record_progress_bar);
        note1 = findViewById(R.id.note_1);
        note2 = findViewById(R.id.note_2);
        note3 = findViewById(R.id.note_3);
        note4 = findViewById(R.id.note_4);
        note5 = findViewById(R.id.note_5);
        Intent intent = getIntent();
        onHandleIntent(intent);
        updateOrCreateFriendship();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);}

        sampleUser = soundPool.load(this, R.raw.note_user, 1 );
        sampleExtrovert = soundPool.load(this, R.raw.sax, 1 );
        sampleCalm = soundPool.load(this, R.raw.note_calm, 1 );
        sampleHiper = soundPool.load(this, R.raw.note_hiper, 1 );
        samplePlayfull = soundPool.load(this, R.raw.note_playfull, 1 );
        sampleTimid = soundPool.load(this, R.raw.note_timid, 1 );
        a = soundPool.load(this, R.raw.a, 1 );

        ImageView interactiveZone = findViewById(R.id.interactive_zone);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                endElapse = System.currentTimeMillis();
                timeElapsed = endElapse - startElapse;
                if (timeElapsed > 2000 ){
                    try {
                        plantResponse(pitchOrder, rhythmInput);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        timer.schedule(task,0, 1000);

        if(plant.getNature().equals("Extrovertida")){
            dinoTimer = new Timer();
            TimerTask extrovertTask = new TimerTask() {
                @Override
                public void run() {
                    //the extrovert will reach out to you, or speak while you interact, on a 60% chance every 5 seconds (since the timer is called every 5 seconds)
                    if( (rand.nextInt(100) + 1) < 51 && firstTouch == true){
                        ArrayList<Float> greetingPitches = new ArrayList<Float>();
                        greetingPitches.add(rand.nextFloat()); greetingPitches.add(rand.nextFloat()); greetingPitches.add(rand.nextFloat()); greetingPitches.add(rand.nextFloat());
                        ArrayList<Long> greetingRythem = new ArrayList<Long>();
                        greetingRythem.add((long) 250); greetingRythem.add((long) 250); greetingRythem.add((long) 250); greetingRythem.add((long) 550);
                        try {
                            plantResponse(greetingPitches, greetingRythem);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            dinoTimer.schedule(extrovertTask,5000, 5000);
        }

        interactiveZone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                double y = event.getY();
                double x = event.getX();
                switch(nextNote) {
                    case 0:
                        noteToShow = note1;
                        break;
                    case 1:
                        noteToShow = note2;
                        break;
                    case 2:
                        noteToShow = note3;
                        break;
                    case 3:
                        noteToShow = note4;
                        break;
                    case 4:
                        noteToShow = note5;
                        nextNote = -1;
                        break;
                    default:
                        // code block
                }
                nextNote++;
                //Little note animation
                ObjectAnimator tapAnimationShow = ObjectAnimator.ofFloat(noteToShow, "alpha", 1);
                tapAnimationShow.setDuration(1);
                ObjectAnimator tapAnimationX = ObjectAnimator.ofFloat(noteToShow, "x", (float) x);
                tapAnimationX.setDuration(1);
                ObjectAnimator tapAnimationY = ObjectAnimator.ofFloat(noteToShow, "y", (float) y + 370);
                tapAnimationY.setDuration(1);
                ObjectAnimator tapSlide = ObjectAnimator.ofFloat(noteToShow, "y", (float) y + 240);
                tapSlide.setDuration(650);
                ObjectAnimator tapAnimationHide = ObjectAnimator.ofFloat(noteToShow, "alpha", 0);
                tapAnimationHide.setDuration(220);
                AnimatorSet animSetTap = new AnimatorSet();
                animSetTap.playTogether(tapAnimationShow, tapAnimationX, tapAnimationY);
                animSetTap.play(tapSlide).after(tapAnimationY);
                animSetTap.play(tapAnimationHide).after(tapSlide);
                animSetTap.start();

                if(y>0 && y<1260){
                    float pitch = (float) (1.5 - (y / 2000));
                    soundPool.play(sampleUser, 1, 1, 1, 0, pitch);
                    if(isRecording){addNoteToRecording(sampleUser, pitch);}

                    if (firstTouch == true){
                       firstTouch = false;
                       startElapse = System.currentTimeMillis();
                       pitchOrder.add(pitch);
                    } else {
                        endElapse = System.currentTimeMillis();
                        timeElapsed = endElapse - startElapse;
                        rhythmInput.add(timeElapsed);
                        startElapse = System.currentTimeMillis();
                        pitchOrder.add(pitch);
                    }
                }
                return false;
            }
        });

    }

    public void plantResponse(ArrayList<Float> pitches, ArrayList<Long> tengoku) throws InterruptedException {
        float plantPitch = (float) (0.1 * plant.getPitch());
        if (plantPitch <= 0.5){
            plantPitch *= -1;
        }
        plantPitch /= 1.25; //Lower the pitch a tiny bit so it's not as pronounced.
        String nature = plant.getNature();
        int voice;
        int loops = 0;
        float initiative = 0;
        float volume = 1;
        // to add a 3rd, 5th, 7th, 8th, 6th or nothing, we created this array with fofis to facilitate implementing randomness
        ArrayList<Float> harmonies = new ArrayList<Float>();
        harmonies.add(4*fofi); harmonies.add(7*fofi); harmonies.add(11*fofi); harmonies.add(12*fofi); harmonies.add(9*fofi); harmonies.add(0*fofi);

        switch(nature) {
            case "Calma":
                voice = sampleCalm;
                for (int i = 0; i < tengoku.size(); i++) {
                    if ((rand.nextInt(10) + 1) > 3){ //30% chance of extending a note's delay
                        tengoku.set(i, (long) (tengoku.get(i) + (tengoku.get(i)*0.3)) ); //adds 30% more delay
                    }
                }
                initiative = harmonies.get(rand.nextInt(2)); //uses a 5th or 3rd
                volume = (float) 0.9;
                break;
            case "Hiperativa":
                voice = sampleHiper;
                for (int i = 0; i < tengoku.size(); i++) {
                    if ((rand.nextInt(10) + 1) > 3){ //30% chance of reducing a note's delay
                        tengoku.set(i, (long) (tengoku.get(i) - (tengoku.get(i)*0.3)) ); //takes 30% from delay
                    }
                }
                if ((rand.nextInt(10) + 1) > 1){ //10% chance of choosing a random number of loops per note
                    loops = rand.nextInt(3);
                }
                break;
            case "Tímida":
                voice = sampleTimid;
                volume = (float) (0.7);
                break;
            case "Extrovertida":
                voice = sampleExtrovert;
                if(pitches.size() > 4 && pitches.size() < 12){
                    int originalSize = tengoku.size();
                    //add extra random delay to transition
                    tengoku.add((long) (rand.nextInt(1250) + 250));
                    for (int i = 0; i < originalSize; i++) {
                        pitches.add((float) (pitches.get(i) + harmonies.get(rand.nextInt(6))) );
                        tengoku.add(tengoku.get(i));
                    }
                }
                break;
            case "Brincalhona":
                voice = samplePlayfull;
                int harmony = 0;
                for (int i = 0; i < pitches.size(); i++) {
                    if ((rand.nextInt(10) + 1) < 4){ //30% chance of changing a note
                        pitches.set(i, (float) (pitches.get(i) + harmonies.get(rand.nextInt(6))) );
                    }
                }
                if ((rand.nextInt(10) + 1) == 1){ //10% chance of shuffling note order
                    Collections.shuffle(pitches);
                }
                break;
            default:
                voice = a;
               Log.i("response", "what nature is this?");
               break;
        }
        for (int i = 0; i < tengoku.size(); i++) {
            if ((rand.nextInt(10) + 1) > 2 || !(nature.equals("Tímida"))){ //20% chance of not singing a note if timid
                if(pitches.get(i) + plantPitch + initiative < 2){
                    soundPool.play(voice, volume, volume, 1, loops, pitches.get(i) + plantPitch + initiative);
                    if(isRecording){addNoteToRecording(voice,pitches.get(i) + plantPitch + initiative );}
                }else {
                    soundPool.play(voice, volume, volume, 1, loops, pitches.get(i) + plantPitch - initiative);
                    if(isRecording){addNoteToRecording(voice,pitches.get(i) + plantPitch - initiative);}
                }
                TimeUnit.MILLISECONDS.sleep(tengoku.get(i));
            }
        }
        //The number of delays is not the same as pitches so we do this to get the final pitch on the list
        //this also guarantees that the shy plant will always do the last note at least
        if(pitches.size() != 0){
            if(pitches.get(pitches.size()-1) + plantPitch + initiative < 2){
                soundPool.play(voice, volume, volume, 1, loops, pitches.get(pitches.size()-1) + plantPitch + initiative);
            }else {
                soundPool.play(voice, volume, volume, 1, loops, pitches.get(pitches.size()-1) + plantPitch - initiative);
            }
            if(isRecording){addNoteToRecording(voice, pitches.get(pitches.size()-1));}
        }
        pitchOrder.clear();
        rhythmInput.clear();
        firstTouch = true;
    }

    protected void onHandleIntent(Intent i) {
        if (i != null) {
            plant = (Plant) i.getSerializableExtra("plant");
            currentUser = (User) i.getSerializableExtra("user");
            TextView plantName = findViewById(R.id.plant_name);
            plantName.setText(plant.getName());
        }
    }

    public void updateOrCreateFriendship(){
        String url = getResources().getString(R.string.api_url) + "friendships/users/" + currentUser.getId() + "/plants/" + plant.getId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.PUT, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.length() != 0) {
                                int friendLvl = response.getJSONObject(0).getInt("friend_lvl");
                                boolean created = response.getBoolean(1);
                                updateUserExp(friendLvl, created);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d( "volley","Oops: " + error);
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public void updateUserExp(int friend_lvl, boolean created){
        String url = getResources().getString(R.string.api_url) + "users/" + currentUser.getId();

        JSONObject postData = new JSONObject();
        try {
            postData.put("exp", newExpValue(friend_lvl, created));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            response.getInt("updated");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d( "volley","Oops: " + error);
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private int newExpValue(int friend_lvl, boolean created) {
        int newExp = currentUser.getExp();
        if (created == true){
            newExp = newExp + 50; //50 de xp se a amizade for nova!
            Toast.makeText(this, "+" + 50 + " Experiência" , Toast.LENGTH_SHORT).show();
        }
        else {
            //10 de xp mais o nivel de amizade se já forem amigos!
            int addedExp = 10 + friend_lvl;
            newExp = newExp + addedExp;
            Toast.makeText(this, "+" + addedExp + " Experiência" , Toast.LENGTH_SHORT).show();
        }
        currentUser.setExp(newExp);
        return newExp;
    }


    public void recordInteractions(View v){
        if(!isRecording){
            isRecording = true;
            currentProgress = 0;
            ObjectAnimator animationShow = ObjectAnimator.ofFloat(redRecordBtn, "alpha", 1);
            animationShow.setDuration(1);
            animationShow.start();
            ObjectAnimator animationShow1 = ObjectAnimator.ofFloat(recordProgressBar, "alpha", 1);
            animationShow1.setDuration(1);
            animationShow1.start();
            recordProgressTimer = new Timer();
            final TimerTask updateProgress = new TimerTask() {
                @Override
                public void run() {
                    if(currentProgress < 100){
                        currentProgress++;
                        recordProgressBar.setProgress(currentProgress);
                    }else{
                        recordProgressTimer.cancel();
                        recordProgressTimer.purge();
                    }
                }
            };
            recordProgressTimer.schedule(updateProgress,0,200);
            recorderTimer = new Timer();
            TimerTask saveRecord = new TimerTask() {
                @Override
                public void run() {
                    isRecording = false;
                    JSONArray jsonRecording = new JSONArray();
                    for (Note note:recording) {
                        JSONObject newNote = new JSONObject();
                        try {
                            newNote.put("voice", note.getVoice());
                            newNote.put("pitch", note.getPitch());
                            newNote.put("delay", note.getDelay());
                            jsonRecording.put(newNote);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject postData = new JSONObject();
                    //Recording - eg. {"name": "SonataVerde", "code": "exampleSongCode", "plant_id": 1, "user_id": 1}
                    try {
                        postData.put("name","Canção com " + plant.getName());
                        postData.put("code", Hexa.JSONArrayToHex(jsonRecording));
                        postData.put("plant_id", plant.getId());
                        postData.put("user_id", currentUser.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = getResources().getString(R.string.api_url) + "recordings";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(interactionContext, "Cassete Gravada com Sucesso! :)", Toast.LENGTH_SHORT).show();
                                    ObjectAnimator animationShow = ObjectAnimator.ofFloat(redRecordBtn, "alpha", 0);
                                    animationShow.setDuration(1);
                                    animationShow.start();
                                    ObjectAnimator animationShow1 = ObjectAnimator.ofFloat(recordProgressBar, "alpha", 0);
                                    animationShow1.setDuration(1);
                                    animationShow1.start();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d( "volley","Oops: " + error);
                                }
                            })
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }};
                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                    recorderTimer.cancel();
                    recorderTimer.purge();
                }
            };
            recorderTimer.schedule(saveRecord, 20000);
            SERecording = System.currentTimeMillis();
        }else{
            recorderTimer.cancel();
            recorderTimer.purge();
            recordProgressTimer.cancel();
            recordProgressTimer.purge();
            isRecording = false;
            ObjectAnimator animationShow = ObjectAnimator.ofFloat(redRecordBtn, "alpha", 0);
            animationShow.setDuration(1);
            animationShow.start();
            ObjectAnimator animationShow1 = ObjectAnimator.ofFloat(recordProgressBar, "alpha", 0);
            animationShow1.setDuration(1);
            animationShow1.start();
            Toast.makeText(interactionContext, "Gravação Cancelada! :(", Toast.LENGTH_SHORT).show();
        }

    }

    public void addNoteToRecording(int voice, float pitch){
        EERecording = System.currentTimeMillis();
        long delay = EERecording - SERecording;
        recording.add(new Note(voice,pitch, delay));
        SERecording = System.currentTimeMillis();
    }

    public void hideNavBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void changeToMainActivity(View v) {
        if (dinoTimer != null){
            dinoTimer.cancel();
            dinoTimer.purge();
        }
        timer.cancel();
        timer.purge();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_out_top, R.anim.slide_in_bottom);
        finish();
    }
}

