package space.badboyin.smap.ActivityAdmin.KelolaData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import space.badboyin.smap.Activity.MenuAdminActivity;
import space.badboyin.smap.R;

public class KelolaDataActivity extends AppCompatActivity {

    CardView btn_keramik, btn_sales, btn_ukuran, btn_kategori;
    Button btn_kembali;
    ImageView img_tambah;
    View header;
    TextView textheader;
    Animation topToBottom, bottomToTop, fade, app_splash, fadeleft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_data);
        header = findViewById(R.id.header);
        textheader = findViewById(R.id.text_PB_judul);
        btn_keramik = findViewById(R.id.btn_keramik);
        btn_kategori = findViewById(R.id.btn_kategori);
        btn_ukuran = findViewById(R.id.btn_ukuran);
        btn_sales = findViewById(R.id.btn_sales);
        btn_kembali = findViewById(R.id.btn_back_to_home);
        img_tambah = findViewById(R.id.img_tambah_data);


        //loadanim
        bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        topToBottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade_animation);
        fadeleft = AnimationUtils.loadAnimation(this, R.anim.fade_from_left);

        //run animation
        header.startAnimation(topToBottom);
        textheader.startAnimation(topToBottom);

        img_tambah.startAnimation(fade);


        btn_keramik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataKeramik = new Intent(KelolaDataActivity.this, KelolaKeramikActivity.class);
                startActivity(keTambahDataKeramik);
            }
        });

        btn_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataSupplier = new Intent(KelolaDataActivity.this, KelolaKategoriActivity.class);
                startActivity(keTambahDataSupplier);
            }
        });

        btn_ukuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahKategori = new Intent(KelolaDataActivity.this, KelolaUkuranActivity.class);
                startActivity(keTambahKategori);
            }
        });

        btn_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahUkuran = new Intent(KelolaDataActivity.this, KelolaSalesActivity.class);
                startActivity(keTambahUkuran);
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keMenuUtama = new Intent(KelolaDataActivity.this, MenuAdminActivity.class);
                startActivity(keMenuUtama);
            }
        });
        findViewById(R.id.btn_merk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KelolaDataActivity.this, KelolaMerkActivity.class);
                startActivity(intent);
            }
        });
    }
}
