package space.badboyin.smap.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import space.badboyin.smap.R;

public class WelcomeActivity extends AppCompatActivity {

    Button btn_masuk, btn_daftar;
    Animation toptobottom, bottomtotop, fade;
    ImageView logo_solo, logo_wayang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //openDialog();
        //load animation
        toptobottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        bottomtotop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade_list);

        logo_solo = findViewById(R.id.img_logo);
        logo_wayang = findViewById(R.id.img_wayang);
        btn_masuk = findViewById(R.id.btn_login);
        btn_daftar = findViewById(R.id.btn_daftar);

        //run animation
        logo_solo.startAnimation(toptobottom);
        //logo_wayang.startAnimation(toptobottom);
        btn_masuk.startAnimation(bottomtotop);
        btn_daftar.startAnimation(bottomtotop);


        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_sign_in = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(go_to_sign_in);
            }
        });


        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_register = new Intent(WelcomeActivity.this, DaftarAkunActivity.class);
                startActivity(go_to_register);
            }
        });
    }
}
