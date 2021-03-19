package com.example.applanta;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private User currentUser;
    protected static final String TAG = "bluetooth";
    private BeaconManager beaconManager;
    private String closestPlant = "none";
    //estado 0 (sem animação), estado 1 (com animação de presença), estado 2 (com animação de transição)
    private int syncAnimation = 0;
    private final int maxDistance = 2;
    private double beaconDistance;

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavBar();
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        Intent intent = getIntent();
        onHandleIntent(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw a beacon for the first time!");
            }
            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see a beacon");
            }
            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconDistance = beacons.iterator().next().getDistance();
                    Log.i(TAG, "The first beacon I see is about "+  beaconDistance+" meters away.");
                        if ( beaconDistance > maxDistance) {
                            Log.i(TAG, "I see a beacon that is more than 2 meters away.");
                            //"Está muito longe para interagir com a planta"
                            if(closestPlant.equals(beacons.iterator().next().getBluetoothAddress())){
                                closestPlant = "none";
                            }
                            //mudar variavel para animar o botão
                            syncAnimation = 0;
                        }else {
                            Log.i(TAG, "I see a beacon that is less than 2 meters away.");
                            //mudar variavel para animar o botão
                            syncAnimation = 1;
                            //guardar UUID da planta mais próxima
                            closestPlant = beacons.iterator().next().getBluetoothAddress();

                            //heart beat animation, the closer to the plant you are the bigger it gets
                            ImageView syncButton = findViewById(R.id.sync_flower);
                            ObjectAnimator animationBeatX = ObjectAnimator.ofFloat(syncButton, "scaleX", (float) (1.6-(beaconDistance * 0.28)));
                            ObjectAnimator animationBeatY = ObjectAnimator.ofFloat(syncButton, "scaleY", (float) (1.6-(beaconDistance * 0.28)));
                            ObjectAnimator animationRestX = ObjectAnimator.ofFloat(syncButton, "scaleX", 1f);
                            ObjectAnimator animationRestY = ObjectAnimator.ofFloat(syncButton, "scaleY", 1f);
                            animationBeatX.setDuration(50);
                            animationBeatY.setDuration(50);
                            animationRestX.setDuration(350);
                            animationRestY.setDuration(350);
                            ObjectAnimator animationBeatX2 = ObjectAnimator.ofFloat(syncButton, "scaleX", (float) (1.6-(beaconDistance * 0.28)));
                            ObjectAnimator animationBeatY2 = ObjectAnimator.ofFloat(syncButton, "scaleY", (float) (1.6-(beaconDistance * 0.28)));
                            ObjectAnimator animationRestX2 = ObjectAnimator.ofFloat(syncButton, "scaleX", 1f);
                            ObjectAnimator animationRestY2 = ObjectAnimator.ofFloat(syncButton, "scaleY", 1f);
                            animationBeatX2.setDuration(50);
                            animationBeatY2.setDuration(50);
                            animationRestX2.setDuration(850);
                            animationRestY2.setDuration(850);
                            AnimatorSet animSetHeartBeat = new AnimatorSet();
                            animSetHeartBeat.play(animationBeatX).with(animationBeatY);
                            animSetHeartBeat.play(animationRestX).after(animationBeatY);
                            animSetHeartBeat.play(animationRestX).with(animationRestY);
                            animSetHeartBeat.play(animationBeatX2).after(animationRestY);
                            animSetHeartBeat.play(animationBeatX2).with(animationBeatY2);
                            animSetHeartBeat.play(animationRestX2).after(animationBeatY2);
                            animSetHeartBeat.play(animationRestX2).with(animationRestY2);
                            animSetHeartBeat.start();
                        }
                }
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("uniqueServiceIdNumber1", null, null, null));
            beaconManager.startRangingBeaconsInRegion(new Region("uniqueServiceIdNumber1", null, null, null));
        } catch (RemoteException e) {    }
    }

    public void synchronizeWithPlant(View v){
        if(!closestPlant.equals("none")){
            syncAnimation = 2;
            //replace hard-code for closestPlant
            String url = getResources().getString(R.string.api_url) + "plants/" + closestPlant;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null) {
                                    Intent i = new Intent(getApplicationContext(), InteractionActivity.class);
                                    i.putExtra("plant", new Plant(response, "interaction"));
                                    i.putExtra("user", currentUser);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                    finish();
                                }else{
                                    closestPlant = "none";
                                    //notify user an error occurred
                                    Toast.makeText(MainActivity.this, "A planta não está registada! :(", Toast.LENGTH_SHORT).show();
                                    syncAnimation = 0;
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
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }else{
            syncAnimation = 0;
            Toast.makeText(getBaseContext(), "Não há Plantas por perto! :(", Toast.LENGTH_SHORT).show();
        }
    }


    protected void onHandleIntent(Intent i) {
        if (i != null) {
            currentUser = (User) i.getSerializableExtra("user");  }
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

    public void changeToUserActivity(View v) {
        Intent i = new Intent(this, UserActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void changeToDiaryActivity(View v) {
        Intent i = new Intent(this, DiaryActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}