package com.example.applanta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity implements PlantRecyclerAdapter.ItemClickListener {
    private User currentUser;
    private Context context;
    private PlantRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private List<Plant> plants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavBar();
        setContentView(R.layout.activity_diary);
        context = this;
        Intent intent = getIntent();
        onHandleIntent(intent);
        hideRecyclerView();
        plants = new ArrayList<>();
        getPlants();
    }

    protected void onHandleIntent(Intent i) {
        if (i != null) {
            currentUser = (User) i.getSerializableExtra("user");
        }
    }

    public void getPlants() {
        String url = getResources().getString(R.string.api_url) + "users/" + currentUser.getId() + "/friendships";
        final PlantRecyclerAdapter.ItemClickListener clickListener = this;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.length() > 0) {
                                for (int i = 0; i < response.length(); i++){
                                    Plant plant = new Plant(response.getJSONObject(i), "collection");
                                    plants.add(plant);
                                }
                                recyclerView = findViewById(R.id.recycler_view);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                adapter = new PlantRecyclerAdapter(context, plants);
                                adapter.setClickListener(clickListener);
                                recyclerView.setAdapter(adapter);
                                showRecyclerView();
                            }else{
                                //if no plants in diary insert placeholder from here
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

    public void changeToMainActivity(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
    public void changeToRecordActivity(View v) {
        Intent i = new Intent(this, RecordsActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
    public void onWindowFocusChanged(boolean hasFocus) {
        hideNavBar();
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

    @Override
    public void onItemClick(View view, int position) {
        View descBox = view.findViewById(R.id.plant_desc_box);
        TextView descText = view.findViewById(R.id.plant_desc);
        float alpha = descBox.getAlpha();
        //If it's already been shown (alpha = 1), hide the description.
        if (alpha == 0) {
            ObjectAnimator animationLowerDescBox = ObjectAnimator.ofFloat(descBox, "translationY", 300f);
            animationLowerDescBox.setDuration(1);
            ObjectAnimator animationLowerDescText = ObjectAnimator.ofFloat(descText, "translationY", 300f);
            animationLowerDescText.setDuration(1);
            ObjectAnimator animationShowDescBox = ObjectAnimator.ofFloat(descBox, "alpha", 1f);
            animationShowDescBox.setDuration(1);
            ObjectAnimator animationShowDescText = ObjectAnimator.ofFloat(descText, "alpha", 1f);
            animationShowDescText.setDuration(1);
            ObjectAnimator animationRaiseDescBox = ObjectAnimator.ofFloat(descBox, "translationY", 0f);
            animationLowerDescBox.setDuration(300);
            ObjectAnimator animationRaiseDescText = ObjectAnimator.ofFloat(descText, "translationY", 0f);
            animationLowerDescText.setDuration(300);
            AnimatorSet animatorSetShowDesc = new AnimatorSet();
            animatorSetShowDesc.playTogether(animationLowerDescBox, animationLowerDescText);
            animatorSetShowDesc.playTogether(animationShowDescBox, animationShowDescText);
            animatorSetShowDesc.playTogether(animationRaiseDescBox, animationRaiseDescText);
            animatorSetShowDesc.play(animationShowDescBox).after(animationLowerDescBox);
            animatorSetShowDesc.play(animationRaiseDescBox).after(animationShowDescBox);
            animatorSetShowDesc.start();
        }
        else{
            ObjectAnimator animationLowerDescBox = ObjectAnimator.ofFloat(descBox, "translationY", 300f);
            animationLowerDescBox.setDuration(300);
            ObjectAnimator animationLowerDescText = ObjectAnimator.ofFloat(descText, "translationY", 300f);
            animationLowerDescText.setDuration(300);
            ObjectAnimator animationHideDescBox = ObjectAnimator.ofFloat(descBox, "alpha", 0f);
            animationHideDescBox.setDuration(1);
            ObjectAnimator animationHideDescText = ObjectAnimator.ofFloat(descText, "alpha", 0f);
            animationHideDescText.setDuration(1);
            AnimatorSet animatorSetShowDesc = new AnimatorSet();
            animatorSetShowDesc.playTogether(animationLowerDescBox, animationLowerDescText);
            animatorSetShowDesc.playTogether(animationHideDescBox, animationHideDescText);
            animatorSetShowDesc.play(animationHideDescBox).after(animationLowerDescBox);
            animatorSetShowDesc.start();
        }
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
}