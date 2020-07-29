package space.badboyin.smap.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import space.badboyin.smap.R;

public class ProfileActivity extends AppCompatActivity {
    private Animation app_splash, toptobottom, bottomtotop;

    private EditText nama;
    private Button btn_simpan, btn_add_avatar;
    private ImageView avatar;
    private View header;
    private TextView judul;
    private LinearLayout content;
    private Uri photo_location;
    private Integer photo_max = 1;

    //private final String[] list_posisi = new String[]{"Pramuniaga", "Gudang"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_simpan = findViewById(R.id.simpan);
        header = findViewById(R.id.header);
        btn_add_avatar = findViewById(R.id.btn_avatar);
        judul = findViewById(R.id.txt_judul);
        content = findViewById(R.id.contentsection);
        nama = findViewById(R.id.txt_avatar_nama);
        avatar = findViewById(R.id.img_avatar);

        animation();

        btn_add_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
            }
        });

       // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_posisi);
       // posisi.setAdapter(adapter);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nama1 = nama.getText().toString();
                //final String posisi1 = (String) posisi.getSelectedItem();

                if (nama1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Mohon Mengisi Nama Anda..", Toast.LENGTH_SHORT).show();
                } else {
                    simpan();
                }

            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            finish();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        
        database.child("users_data").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals("nama")) {

                        nama.setText(ds.getValue(String.class));

                    }
                  /*  else if (ds.getKey().equals("posisi")) {
                        String pos = ds.getValue(String.class);
                        for (int w = 0; w < list_posisi.length; w++) {
                            if (list_posisi[w].equals(pos)) {
                                posisi.setSelection(w);
                                break;
                            }
                        */
                     else if (ds.getKey().equals("url_avatar")) {
                        Picasso.get().load(ds.getValue(String.class))
                                .into(avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void simpan() {
        btn_simpan.setText("LOADING");
        btn_simpan.setEnabled(false);
        if (photo_location != null) {
            final StorageReference fotoRef = storage.child("foto_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + getFileExtension(photo_location));
            fotoRef.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uri_photo = uri.toString();
                            database.child("users_data").child(user.getUid()).child("url_avatar").setValue(uri_photo);
                            database.child("users_data").child(user.getUid()).child("nama").setValue(nama.getText().toString());
                            //database.child("users_data").child(user.getUid()).child("posisi").setValue(posisi.getSelectedItem());
                            if (!((EditText) findViewById(R.id.password)).getText().toString().isEmpty()) {
                                ubahPassword();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Profile Berhasil Diubah", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    btn_simpan.setText("SIMPAN");
                    btn_simpan.setEnabled(true);
                }
            });
        } else {
            database.child("users_data").child(user.getUid()).child("nama").setValue(nama.getText().toString());
           // database.child("users_data").child(user.getUid()).child("posisi").setValue(posisi.getSelectedItem());
            if (!((EditText) findViewById(R.id.password)).getText().toString().isEmpty()) {
                ubahPassword();
            } else {
                Toast.makeText(ProfileActivity.this, "Profile Berhasil Diubah", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }

    }

    private void ubahPassword() {
        user.updatePassword(((EditText) findViewById(R.id.password)).getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Data Berhasil Diubah", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(ProfileActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            btn_simpan.setText("SIMPAN");
                            btn_simpan.setEnabled(true);
                        }
                    }
                });
    }

    private StorageReference storage;
    private DatabaseReference database;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void animation() {
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        toptobottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        bottomtotop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);
        header.startAnimation(toptobottom);
        judul.startAnimation(toptobottom);
        content.startAnimation(bottomtotop);
        btn_simpan.startAnimation(bottomtotop);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void findPhoto() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Picasso.get().load(photo_location).centerCrop().fit().into(avatar);
        }
    }
}
