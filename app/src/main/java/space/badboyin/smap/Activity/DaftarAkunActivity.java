package space.badboyin.smap.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import space.badboyin.smap.R;

public class DaftarAkunActivity extends AppCompatActivity {

    Button btn_daftar;
    LinearLayout btn_back, content;
    View header;
    TextView headertext;
    EditText nomor, email, password;
    Animation app_splash, toptobottom, bottomtotop;


    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_akun);
        btn_daftar = findViewById(R.id.btn_daftar_buat);
        btn_back = findViewById(R.id.btn_daftar_back);
        header = findViewById(R.id.header);
        headertext = findViewById(R.id.headeractivity);
        content = findViewById(R.id.linearLayout2);
        nomor = findViewById(R.id.txt_daftar_nomortelepon);
        email = findViewById(R.id.txt_daftar_email);
        password = findViewById(R.id.txt_daftar_password);
        animation();
        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftar();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_home = new Intent(DaftarAkunActivity.this, WelcomeActivity.class);
                startActivity(go_to_home);
            }
        });
    }

    private void daftar() {
        btn_daftar.setEnabled(false);
        btn_daftar.setText("LOADING");
        final String password1 = password.getText().toString();
        final String email1 = email.getText().toString();
        final String notelp1 = nomor.getText().toString();
        if (notelp1.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Nomor Telepon Masih Kosong..", Toast.LENGTH_SHORT).show();
            btn_daftar.setText("BUAT AKUN");
            btn_daftar.setEnabled(true);
        } else if (notelp1.length()<10) {
            Toast.makeText(getApplicationContext(), "Nomor Telepon Terlalu Pendek", Toast.LENGTH_SHORT).show();
            btn_daftar.setText("BUAT AKUN");
            btn_daftar.setEnabled(true);
        }else if (email1.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email Masih Kosong..", Toast.LENGTH_SHORT).show();
            btn_daftar.setText("BUAT AKUN");
            btn_daftar.setEnabled(true);
        } else if (password1.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Masih Kosong..", Toast.LENGTH_SHORT).show();
            btn_daftar.setText("BUAT AKUN");
            btn_daftar.setEnabled(true);
        } else {
            auth.createUserWithEmailAndPassword(email1, password1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                               createUser();
                            } else {
                                btn_daftar.setEnabled(true);
                                btn_daftar.setText("BUAT AKUN");
                                Toast.makeText(DaftarAkunActivity.this, "Authentication failed. " + task.getException().getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void createUser() {
        reference.child("users_data").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child("no_telp").setValue(nomor.getText().toString());
                dataSnapshot.getRef().child("email").setValue(email.getText().toString());
                dataSnapshot.getRef().child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Intent go_to_avatar = new Intent(DaftarAkunActivity.this, BuatAvatarActivity.class);
                go_to_avatar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(go_to_avatar);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                btn_daftar.setText("BUAT AKUN");
                btn_daftar.setEnabled(true);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public void animation() {
        //loadanim
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        toptobottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        bottomtotop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);

        //run animation

        header.startAnimation(toptobottom);
        headertext.startAnimation(toptobottom);
        content.startAnimation(bottomtotop);
        btn_daftar.startAnimation(bottomtotop);
        btn_back.startAnimation(bottomtotop);
    }
}
