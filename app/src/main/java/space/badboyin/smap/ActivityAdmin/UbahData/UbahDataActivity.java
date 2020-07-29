package space.badboyin.smap.ActivityAdmin.UbahData;

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

public class UbahDataActivity extends AppCompatActivity {

    CardView btn_ubah_keramik,btn_ubah_supplier,btn_ubah_kategori,btn_ubah_ukuran;
    Button btn_kembali;
    ImageView img_ubah;
    View header;
    TextView textheader;
    Animation topToBottom,bottomToTop,fade,app_splash,fadeleft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data);
        header=findViewById(R.id.header);
        textheader=findViewById(R.id.text_PB_judul);

        btn_ubah_keramik=findViewById(R.id.btn_ubah_keramik);
        btn_ubah_supplier=findViewById(R.id.btn_ubah_supplier);
        btn_ubah_kategori =findViewById(R.id.btn_ubah_kategori);
        btn_ubah_ukuran=findViewById(R.id.btn_ubah_ukuran);
        btn_kembali=findViewById(R.id.btn_back_to_home);
        img_ubah=findViewById(R.id.img_ubah_data);


        //loadanim
        bottomToTop= AnimationUtils.loadAnimation(this,R.anim.bottomtotop);
        app_splash= AnimationUtils.loadAnimation(this,R.anim.app_splash);
        topToBottom= AnimationUtils.loadAnimation(this,R.anim.toptothebottom);
        fade= AnimationUtils.loadAnimation(this,R.anim.fade_animation);
        fadeleft= AnimationUtils.loadAnimation(this,R.anim.fade_from_left);

        //run animation
        header.startAnimation(topToBottom);
        textheader.startAnimation(topToBottom);
        btn_ubah_keramik.startAnimation(fadeleft);
        btn_ubah_ukuran.startAnimation(fadeleft);
        btn_ubah_kategori.startAnimation(fadeleft);
        btn_ubah_supplier.startAnimation(fadeleft);
        btn_kembali.startAnimation(bottomToTop);
        img_ubah.startAnimation(fade);
        //textjudul.startAnimation(toptobottom);
       // textcaption.startAnimation(toptobottom);
       // btn_masuk.startAnimation(bottomtotop);



        btn_ubah_keramik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keUbahDataKeramik=new Intent(UbahDataActivity.this, UbahDataKeramikActivity.class);
                startActivity(keUbahDataKeramik);
            }
        });

        btn_ubah_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keUbahDataSupplier=new Intent(UbahDataActivity.this, UbahDataSalesActivity.class);
                startActivity(keUbahDataSupplier);
            }
        });

        btn_ubah_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keUbahKategori=new Intent(UbahDataActivity.this, UbahDataKategoriActivity.class);
                startActivity(keUbahKategori);
            }
        });

        btn_ubah_ukuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keUbahUkuran=new Intent(UbahDataActivity.this, UbahDataUkuranActivity.class);
                startActivity(keUbahUkuran);
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keMenuUtama=new Intent(UbahDataActivity.this, MenuAdminActivity.class);
                startActivity(keMenuUtama);
            }
        });
    }
}
