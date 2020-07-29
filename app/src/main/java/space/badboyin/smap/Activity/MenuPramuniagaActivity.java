package space.badboyin.smap.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.shapeofview.shapes.CircleView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import space.badboyin.smap.App;
import space.badboyin.smap.ListData.ListKeramikActivitynew;
import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.PramuRiwayatTransaksiTodayActivity;
import space.badboyin.smap.Transaksi.PramuRiwayatTransaksiActivity;
import space.badboyin.smap.Transaksi.TransaksiPenjualanActivity;

public class MenuPramuniagaActivity extends AppCompatActivity {

    TextView textwaktu, texttanggal;
    CircleView profile;
    TextView txtnama, txtposisi;
    ImageView imgavatarhome;
    Button btn_signout;
    View header;
    LinearLayout nav_pramuniaga,nav_transaksi,nav_riwayat_transaksi,nav_riwayat_order,nav_barang;

    Animation topToBottom, bottomToTop, fade, fade_from_left, app_splash;


    private DatabaseReference reference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pramuniaga);

        header = findViewById(R.id.view2);
        textwaktu = findViewById(R.id.waktu);
        texttanggal = findViewById(R.id.tanggal);
        profile = findViewById(R.id.profile);
        txtnama = findViewById(R.id.txt_home_nama);
        txtposisi = findViewById(R.id.txt_home_posisi);
        imgavatarhome = findViewById(R.id.img_avatar_home);
        btn_signout = findViewById(R.id.btn_home_signout);
        nav_pramuniaga = findViewById(R.id.linearLayout2);

        bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        topToBottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade_animation);
        fade_from_left = AnimationUtils.loadAnimation(this, R.anim.fade_from_left);

        header.startAnimation(fade_from_left);
        textwaktu.startAnimation(fade);
        txtnama.startAnimation(fade);
        txtposisi.startAnimation(fade);
        profile.startAnimation(fade);
        texttanggal.startAnimation(fade);
        btn_signout.startAnimation(fade);
        nav_pramuniaga.startAnimation(app_splash);


        nav_transaksi = findViewById(R.id.nav_transaksi);
        nav_barang = findViewById(R.id.nav_data_barang);
        nav_riwayat_transaksi = findViewById(R.id.nav_riwayat_transaksi);
        nav_riwayat_order = findViewById(R.id.nav_riwayat_order);

        nav_transaksi.startAnimation(fade);
        nav_barang.startAnimation(fade);
        nav_riwayat_transaksi.startAnimation(fade);
        nav_riwayat_order.startAnimation(fade);


        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            signout();
            return;
        }
        settanggal();
        setwaktu();
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent go_to_signin = new Intent(MenuPramuniagaActivity.this, WelcomeActivity.class);
                go_to_signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(go_to_signin);
                finishAffinity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.child("users_data").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                App.please().setUser(user);
                if (user.getStatus() == null || user.getStatus().equals("Tidak Aktif")) {
                    Toast.makeText(getApplicationContext(), "User dengan Email ini tidak Aktif, silahkan menghubungi Admin", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent go_to_avatar = new Intent(getApplicationContext(), SplashScreenActivity.class);
                    go_to_avatar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(go_to_avatar);
                    finish();
                } else {

                    final String posisi = (String) dataSnapshot.child("posisi").getValue();
                    String nama = (String) dataSnapshot.child("nama").getValue();
                    String ava = (String) dataSnapshot.child("url_avatar").getValue();
                    if (nama != null && posisi != null) {
                        txtnama.setText(nama);
                        txtposisi.setText(posisi);
                        Picasso.get().load(ava).centerCrop()
                                .fit().into(imgavatarhome);

                        profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MenuPramuniagaActivity.this, ProfileActivity.class));
                            }
                        });

                        nav_transaksi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent goToTicket1 = new Intent(MenuPramuniagaActivity.this, TransaksiPenjualanActivity.class);
                                startActivity(goToTicket1);
                            }
                        });

                        nav_riwayat_transaksi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent goToTicket1 = new Intent(MenuPramuniagaActivity.this, PramuRiwayatTransaksiTodayActivity.class);
                                startActivity(goToTicket1);
                            }
                        });


                        nav_riwayat_order.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent goToTicket1 = new Intent(MenuPramuniagaActivity.this, PramuRiwayatTransaksiActivity.class);
                                startActivity(goToTicket1);
                            }
                        });

                        nav_barang.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent goToTicket1 = new Intent(MenuPramuniagaActivity.this, ListKeramikActivitynew.class);
                                startActivity(goToTicket1);
                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent go_to_signin = new Intent(this, WelcomeActivity.class);
        go_to_signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(go_to_signin);
        finishAffinity();
    }

    private void setwaktu() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            textwaktu.setText("Selamat Pagi,");

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            textwaktu.setText("Selamat Siang,");
            //Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 16 && timeOfDay < 19) {
            textwaktu.setText("Selamat Sore,");
            // Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 19 && timeOfDay < 24) {
            textwaktu.setText("Selamat Malam,");
            // Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
        }
    }

    private void settanggal() {
        Date today = Calendar.getInstance().getTime();//getting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd  yyyy");
        String date = formatter.format(today);
        texttanggal.setText(date);
    }
}
