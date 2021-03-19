package com.example.applanta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RecordsActivity extends AppCompatActivity implements RecordRecyclerAdapter.ItemClickListener{
    private User currentUser;
    public static Context recordsContext;
    private RecordRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private List<Record> records;
    private Timer playerTimer;
    private int sampleExtrovert;
    private int sampleCalm;
    private int sampleHiper;
    private int samplePlayfull;
    private int sampleTimid;
    private int sampleUser;
    private int a;
    public static RelativeLayout deletePopup;
    public static TextView recordToRemove;
    public static int idToRemove;
    public static View decorView;
    public SoundPool soundPool;
    public JSONArray recordArray;
    public int shark;
    public int saxophone;
    public int GCluster;
    public int Feer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        hideNavBar();
        setContentView(R.layout.activity_records);
        recordsContext=this;
        Intent intent = getIntent();
        onHandleIntent(intent);
        hideRecyclerView();
        records = new ArrayList<>();
        hideDeletePopup();
        getRecords();


    }

    protected void onHandleIntent(Intent i) {
        if (i != null) {
            currentUser = (User) i.getSerializableExtra("user");  }
    }

    public void getRecords() {
        records = new ArrayList<>();
        String url = getResources().getString(R.string.api_url) + "users/" + currentUser.getId() + "/recordings";
        final RecordRecyclerAdapter.ItemClickListener clickListener = this;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.length() > 0) {
                                for (int i = 0; i < response.length(); i++){
                                    Record record = new Record(response.getJSONObject(i));
                                    records.add(record);
                                }
                                recyclerView = findViewById(R.id.recycler_view);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(recordsContext));
                                adapter = new RecordRecyclerAdapter(recordsContext, records);
                                adapter.setClickListener(clickListener);
                                recyclerView.setAdapter(adapter);
                                showRecyclerView();
                            }else{
                                recyclerView = findViewById(R.id.recycler_view);
                                recyclerView.setAlpha(0);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(recordsContext, (CharSequence) error, Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public void changeToDiaryActivity(View v) {
        Intent i = new Intent(this, DiaryActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        hideNavBar();
    }

    public void hideNavBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    public void hideRecyclerView() {
        //hide recycleViw very fast, there's a lot of code here but it IS an animation so I guess that's okay...
        recyclerView = findViewById(R.id.recycler_view);
        ObjectAnimator animationShow1 = ObjectAnimator.ofFloat(recyclerView, "translationY", 1800f);
        animationShow1.setDuration(1);
        AnimatorSet animSet1234 = new AnimatorSet();
        animSet1234.play(animationShow1);
        animSet1234.start();
    }

    public void showRecyclerView() {
        //show recycleView after the volley response, there's a lot of code here but it IS an animation so I guess that's okay...
        recyclerView = findViewById(R.id.recycler_view);
        ObjectAnimator animationShow2 = ObjectAnimator.ofFloat(recyclerView, "translationY", 0f);
        animationShow2.setDuration(900);
        AnimatorSet animSet1234 = new AnimatorSet();
        animSet1234.play(animationShow2);
        animSet1234.start();
    }

    public void hideDeletePopup() {
        deletePopup = findViewById(R.id.delete_popup);
        recordToRemove = findViewById(R.id.delete_popup_recording);
        ObjectAnimator animationHide = ObjectAnimator.ofFloat(deletePopup, "translationY", 500f);
        animationHide.setDuration(1);
        animationHide.start();
    }

    public void hideDeletePopup(View view) {
        //same as before but much slower, used when the use presses the cancel or the delete button
        deletePopup = findViewById(R.id.delete_popup);
        ObjectAnimator animationHide = ObjectAnimator.ofFloat(deletePopup, "translationY", 500f);
        animationHide.setDuration(900);
        animationHide.start();

        if (view == findViewById(R.id.delete_popup_remove)){
            deleteRecord();
        }
    }

    public void deleteRecord() {
        String url = getResources().getString(R.string.api_url) + "recordings/" + idToRemove;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            response.getString("message");
                            response.getString("destroyed");
                            getRecords();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(recordsContext, (CharSequence) error, Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void editRecord(String name, int id){
        hideNavBar();

        String url = "https://applanta-api.herokuapp.com/" + "recordings/" + id;

        JSONObject postData = new JSONObject();
        try {
            postData.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            response.getInt("updatedRecording");
                            Toast.makeText(recordsContext, message, Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(recordsContext, (CharSequence) error, Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void playRecord(String code, int id) throws JSONException, InterruptedException {
        recordArray = Hexa.fromHexToJSONArray(code);

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

        sampleUser = soundPool.load(recordsContext, R.raw.note_user, 1 );
        sampleExtrovert = soundPool.load(recordsContext, R.raw.sax, 1 );
        sampleCalm = soundPool.load(recordsContext, R.raw.note_calm, 1 );
        sampleHiper = soundPool.load(recordsContext, R.raw.note_hiper, 1 );
        samplePlayfull = soundPool.load(recordsContext, R.raw.note_playfull, 1 );
        sampleTimid = soundPool.load(recordsContext, R.raw.note_timid, 1 );
        a = soundPool.load(recordsContext, R.raw.a, 1 );

        playerTimer = new Timer();
        final TimerTask play = new TimerTask() {
            @Override
            public void run() {
                try {
                    player(recordArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        playerTimer.schedule(play,500);
    }

    public void player(JSONArray array) throws JSONException {
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    soundPool.play(array.getJSONObject(i).getInt("voice"), 1, 1, 1, 0, (float) array.getJSONObject(i).getDouble("pitch"));
                    if(i + 1 < array.length()){
                        TimeUnit.MILLISECONDS.sleep(array.getJSONObject(i + 1).getLong("delay"));
                    }
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}