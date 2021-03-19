package com.example.applanta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


public class UserActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private ImageView signOutBttn;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        hidePopup();
        hideNavBar();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signOutBttn = findViewById(R.id.logout_btn);
        signOutBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()){
                            goToLoginActivity();
                        }else {
                            Toast.makeText(UserActivity.this, "Logout Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Intent intent = getIntent();
        onHandleIntent(intent);
    }


    protected void onHandleIntent(Intent i) {
        ImageView windowBlur = findViewById(R.id.window_blur);
        ProgressBar loadingSpinner = findViewById(R.id.loading_spinner);
        windowBlur.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.VISIBLE);

        if (i != null) {
            currentUser = (User) i.getSerializableExtra("user");

            String name = currentUser.getName();
            TextView userName = findViewById(R.id.user_text);
            userName.setText(name);

            int exp = currentUser.getExp();
            //for every 100 exp you gain 1 level
            int level = (int) Math.floor(exp / 100) + 1;
            ProgressBar expBar = findViewById(R.id.user_bar);
            TextView userLevel = findViewById(R.id.user_level);
            TextView userExp = findViewById(R.id.user_exp);
            expBar.setProgress(exp - (level - 1) * 100);
            userLevel.setText(getResources().getString(R.string.n_vel) + " " + level);
            userExp.setText(exp + " " + getResources().getString(R.string.exp));

            ImageView userIcon = findViewById(R.id.user_icon);
            ImageView achievementSlot1 = findViewById(R.id.achievement_slot_1);
            ImageView achievementSlot2 = findViewById(R.id.achievement_slot_2);
            ImageView achievementSlot3 = findViewById(R.id.achievement_slot_3);
            // Decidimos manter o código assim para melhor leitura
            if (level < 5) {
                userIcon.setImageResource(R.drawable.user_1);
            } else if (4 < level && level < 10) {
                userIcon.setImageResource(R.drawable.user_2);
                achievementSlot1.setImageResource(R.drawable.achievement_level_5);
            } else if (9 < level && level < 15) {
                userIcon.setImageResource(R.drawable.user_3);
                achievementSlot1.setImageResource(R.drawable.achievement_level_5);
                achievementSlot2.setImageResource(R.drawable.achievement_level_10);
            } else if (14 < level && level < 20) {
                userIcon.setImageResource(R.drawable.user_4);
                achievementSlot1.setImageResource(R.drawable.achievement_level_5);
                achievementSlot2.setImageResource(R.drawable.achievement_level_10);
            } else {
                userIcon.setImageResource(R.drawable.user_4);
                achievementSlot1.setImageResource(R.drawable.achievement_level_5);
                achievementSlot2.setImageResource(R.drawable.achievement_level_10);
                achievementSlot3.setImageResource(R.drawable.achievement_level_20);
            }

            int friendshipCount = currentUser.getFriendshipCount();
            ImageView achievementSlot4 = findViewById(R.id.achievement_slot_4);
            ImageView achievementSlot5 = findViewById(R.id.achievement_slot_5);
            ImageView achievementSlot6 = findViewById(R.id.achievement_slot_6);
            ImageView achievementSlot7 = findViewById(R.id.achievement_slot_7);
            ImageView achievementSlot8 = findViewById(R.id.achievement_slot_8);
            // Aqui decidimos fazer em one liners por ser apenas if's e fazerem apenas um setImageResource
            // A partir de um certo número de amigos, o utilizador é recompensado com um achievement esses números são:
            // 1, 5, 10, 25 e 50
            if (friendshipCount > 0) {
                achievementSlot4.setImageResource(R.drawable.achievement_friend_1);
            }
            if (friendshipCount > 4) {
                achievementSlot5.setImageResource(R.drawable.achievement_friend_5);
            }
            if (friendshipCount > 9) {
                achievementSlot6.setImageResource(R.drawable.achievement_friend_10);
            }
            if (friendshipCount > 24) {
                achievementSlot7.setImageResource(R.drawable.achievement_friend_25);
            }
            if (friendshipCount > 49) {
                achievementSlot8.setImageResource(R.drawable.achievement_friend_50);
            }
        }
        windowBlur.setVisibility(View.INVISIBLE);
        loadingSpinner.setVisibility(View.GONE);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        hideNavBar();
    }

    public void changeToMainActivity(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
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

    public void hidePopup() {
        //hide popup very fast, there's a lot of code here but it IS an animation so I guess that's okay...
        View achievementPopup = findViewById(R.id.achievement_popup);
        ImageView achievementSlotPopup = findViewById(R.id.achievement_slot_popup);
        TextView achievementTitle = findViewById(R.id.achievement_popup_title);
        TextView achievementDesc = findViewById(R.id.achievement_popup_desc);
        ObjectAnimator animationShow1 = ObjectAnimator.ofFloat(achievementPopup, "translationY", 500f);
        ObjectAnimator animationShow2 = ObjectAnimator.ofFloat(achievementSlotPopup, "translationY", 500f);
        ObjectAnimator animationShow3 = ObjectAnimator.ofFloat(achievementTitle, "translationY", 500f);
        ObjectAnimator animationShow4 = ObjectAnimator.ofFloat(achievementDesc, "translationY", 500f);
        AnimatorSet animSet1234 = new AnimatorSet();
        animSet1234.setDuration(1);
        animSet1234.playTogether(animationShow1, animationShow2, animationShow3, animationShow4);
        animSet1234.start();
    }

    public void showAchievementDesc(View v) {
        View achievementPopup = findViewById(R.id.achievement_popup);
        ImageView achievementSlotPopup = findViewById(R.id.achievement_slot_popup);
        TextView achievementTitle = findViewById(R.id.achievement_popup_title);
        TextView achievementDesc = findViewById(R.id.achievement_popup_desc);

        //simple animation when you click a popup, there's a lot of code here but it IS an animation so I guess that's okay...
        ObjectAnimator animationPop1 = ObjectAnimator.ofFloat(v, "scaleX", 1.16f);
        ObjectAnimator animationPop2 = ObjectAnimator.ofFloat(v, "scaleY", 1.16f);
        ObjectAnimator animationClose1 = ObjectAnimator.ofFloat(v, "scaleX", 1f);
        ObjectAnimator animationClose2 = ObjectAnimator.ofFloat(v, "scaleY", 1f);
        AnimatorSet animSetPop = new AnimatorSet();
        animSetPop.play(animationPop1).with(animationPop2);
        animSetPop.play(animationClose1).after(animationPop2);
        animSetPop.play(animationClose1).with(animationClose2);
        animSetPop.setDuration(200);
        animSetPop.start();

        animSetPop.start();

        switch (v.getId()) {
            case (R.id.achievement_slot_1):
                achievementSlotPopup.setImageResource(R.drawable.achievement_level_5);
                achievementTitle.setText(R.string.achievement_1_title);
                achievementDesc.setText(R.string.achievement_1_desc);
                break;
            case (R.id.achievement_slot_2):
                achievementSlotPopup.setImageResource(R.drawable.achievement_level_10);
                achievementTitle.setText(R.string.achievement_2_title);
                achievementDesc.setText(R.string.achievement_2_desc);
                break;
            case (R.id.achievement_slot_3):
                achievementSlotPopup.setImageResource(R.drawable.achievement_level_20);
                achievementTitle.setText(R.string.achievement_3_title);
                achievementDesc.setText(R.string.achievement_3_desc);
                break;
            case (R.id.achievement_slot_4):
                achievementSlotPopup.setImageResource(R.drawable.achievement_friend_1);
                achievementTitle.setText(R.string.achievement_4_title);
                achievementDesc.setText(R.string.achievement_4_desc);
                break;
            case (R.id.achievement_slot_5):
                achievementSlotPopup.setImageResource(R.drawable.achievement_friend_5);
                achievementTitle.setText(R.string.achievement_5_title);
                achievementDesc.setText(R.string.achievement_5_desc);
                break;
            case (R.id.achievement_slot_6):
                achievementSlotPopup.setImageResource(R.drawable.achievement_friend_10);
                achievementTitle.setText(R.string.achievement_6_title);
                achievementDesc.setText(R.string.achievement_6_desc);
                break;
            case (R.id.achievement_slot_7):
                achievementSlotPopup.setImageResource(R.drawable.achievement_friend_25);
                achievementTitle.setText(R.string.achievement_7_title);
                achievementDesc.setText(R.string.achievement_7_desc);
                break;
            case (R.id.achievement_slot_8):
                achievementSlotPopup.setImageResource(R.drawable.achievement_friend_50);
                achievementTitle.setText(R.string.achievement_8_title);
                achievementDesc.setText(R.string.achievement_8_desc);
                break;
        }
        ObjectAnimator animationShow1 = ObjectAnimator.ofFloat(achievementPopup, "translationY", 0f);
        ObjectAnimator animationShow2 = ObjectAnimator.ofFloat(achievementSlotPopup, "translationY", 0f);
        ObjectAnimator animationShow3 = ObjectAnimator.ofFloat(achievementTitle, "translationY", 0f);
        ObjectAnimator animationShow4 = ObjectAnimator.ofFloat(achievementDesc, "translationY", 0f);
        AnimatorSet animSet1234 = new AnimatorSet();
        animSet1234.setDuration(900);
        animSet1234.playTogether(animationShow1, animationShow2, animationShow3, animationShow4);
        animSet1234.start();
    }

    public void hideAchievementDesc(View v) {
        View achievementPopup = findViewById(R.id.achievement_popup);
        ImageView achievementSlotPopup = findViewById(R.id.achievement_slot_popup);
        TextView achievementTitle = findViewById(R.id.achievement_popup_title);
        TextView achievementDesc = findViewById(R.id.achievement_popup_desc);
        ObjectAnimator animationHide1 = ObjectAnimator.ofFloat(achievementPopup, "translationY", 500f);
        ObjectAnimator animationHide2 = ObjectAnimator.ofFloat(achievementSlotPopup, "translationY", 500f);
        ObjectAnimator animationHide3 = ObjectAnimator.ofFloat(achievementTitle, "translationY", 500f);
        ObjectAnimator animationHide4 = ObjectAnimator.ofFloat(achievementDesc, "translationY", 500f);
        AnimatorSet animSet1234 = new AnimatorSet();
        animSet1234.setDuration(900);
        animSet1234.playTogether(animationHide1, animationHide2, animationHide3, animationHide4);
        animSet1234.start();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(UserActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}