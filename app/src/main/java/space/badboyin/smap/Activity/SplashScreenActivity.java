package space.badboyin.smap.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;

public class SplashScreenActivity extends AppCompatActivity {

    Animation app_splash, fade;
    ImageView app_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade_animation);

        app_icon = findViewById(R.id.app_icon);

        app_icon.startAnimation(app_splash);
        checkUser();
    }

    private void checkUser() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference().child("users_data").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                if (user.getUrl_avatar() == null || user.getUrl_avatar().isEmpty()) {

                                    Intent go_to_avatar = new Intent(SplashScreenActivity.this, BuatAvatarActivity.class);
                                    go_to_avatar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(go_to_avatar);
                                    finish();
                                } else {
                                    App.please().setUser(user);
                                    Intent go_to_home = null;
                                    if (user.getPosisi().equals("Admin")){
                                        go_to_home = new Intent(getApplicationContext(), MenuAdminActivity.class);
                                    }else if(user.getPosisi().equals("Pramuniaga")){
                                        go_to_home = new Intent(getApplicationContext(), MenuPramuniagaActivity.class);
                                    }
                                    else
                                        go_to_home = new Intent(getApplicationContext(), MenuGudangActivity.class);
                                    go_to_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(go_to_home);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }, 2000); //2000ms=2 detik
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //direct dari splash ke activity welcome

                    Intent starthome = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    starthome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(starthome);

                    finish();
                }
            }, 2000); //2000ms=2 detik
        }
    }

}
