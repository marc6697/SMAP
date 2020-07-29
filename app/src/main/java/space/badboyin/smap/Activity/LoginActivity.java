package space.badboyin.smap.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText email, txtpassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        email = findViewById(R.id.email);
        txtpassword = findViewById(R.id.txt_login_password);
        auth = FirebaseAuth.getInstance();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_login.setEnabled(false);
                btn_login.setText("Loading...");
                final String eml = email.getText().toString();
                final String password = txtpassword.getText().toString();

                if (eml.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Mohon isi Email anda..", Toast.LENGTH_SHORT).show();
                    btn_login.setEnabled(true);
                    btn_login.setText("MASUK");
                } else {
                    if (password.isEmpty()) {
                        Toast.makeText(getApplicationContext(), " Mohon isi password anda..", Toast.LENGTH_SHORT).show();
                        btn_login.setEnabled(true);
                        btn_login.setText("MASUK");
                    } else {
                        loging(eml, password);
                    }
                }
            }
        });
    }

    private void checkGo() {
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("users_data").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getUrl_avatar() != null && !user.getUrl_avatar().isEmpty()) {
                        if (user.getStatus().equals("Tidak Aktif")) {
                            btn_login.setEnabled(true);
                            btn_login.setText("MASUK");
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getApplicationContext(), "User dengan Email ini tidak Aktif, silahkan menghubungi Admin", Toast.LENGTH_LONG).show();
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
                    } else {
                        Intent go_to_avatar = new Intent(LoginActivity.this, BuatAvatarActivity.class);
                        go_to_avatar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(go_to_avatar);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loging(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkGo();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    btn_login.setEnabled(true);
                    btn_login.setText("MASUK");
                }
            }
        });
    }
}
