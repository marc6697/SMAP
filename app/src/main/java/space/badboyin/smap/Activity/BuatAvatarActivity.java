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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import space.badboyin.smap.App;
import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;

public class BuatAvatarActivity extends AppCompatActivity {
    Animation app_splash, toptobottom, bottomtotop;
    EditText nama;
    Button btn_buatakun, btn_add_avatar;
    ImageView avatar;
    View header;
    TextView judul;
    LinearLayout content, btn_back;

    Uri photo_location;
    Integer photo_max = 1;


    private Spinner posisi;
    private final String[] list_posisi = new String[]{"Pramuniaga", "Gudang"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_avatar);

        btn_buatakun = findViewById(R.id.btn_avatar_buat);
        btn_back = findViewById(R.id.btn_avatar_back);
        header = findViewById(R.id.header);
        btn_add_avatar = findViewById(R.id.btn_avatar);
        judul = findViewById(R.id.txt_judul);
        content = findViewById(R.id.contentsection);
        nama = findViewById(R.id.txt_avatar_nama);
        posisi = findViewById(R.id.txt_avatar_posisi);
        avatar = findViewById(R.id.img_avatar);

        animation();

        btn_add_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_posisi);
        posisi.setAdapter(adapter);

        btn_buatakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nama1 = nama.getText().toString();
                final String posisi1 = (String) posisi.getSelectedItem();

                //Validasi file
                if (nama1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Mohon Mengisi Nama Anda..", Toast.LENGTH_SHORT).show();

                } else if (nama1.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Nama pengguna minimal 4 Karakter..", Toast.LENGTH_SHORT).show();

                } else if (posisi1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Mohon Mengisi Posisi Anda..", Toast.LENGTH_SHORT).show();

                } else if (photo_location != null) {
                    simpan();
                } else {
                    Toast.makeText(getApplicationContext(), "Mohon Mengisi Foto..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void simpan() {
        btn_buatakun.setText("LOADING");
        btn_buatakun.setEnabled(false);
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
                        database.child("users_data").child(user.getUid()).child("posisi").setValue(posisi.getSelectedItem());
                        loadUser();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BuatAvatarActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                btn_buatakun.setText("SIMPAN");
                btn_buatakun.setEnabled(true);
            }
        });

    }

    private void loadUser() {
        FirebaseDatabase.getInstance().getReference().child("users_data").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Toast.makeText(BuatAvatarActivity.this, "Berhasil menyimpan, Silahkan Login Kembali", Toast.LENGTH_SHORT);
                            App.please().setUser(user);
                            Intent go_to_home = null;
                            if (user.getPosisi().equals("Admin"))
                                go_to_home = new Intent(getApplicationContext(), MenuAdminActivity.class);
                            else
                                go_to_home = new Intent(getApplicationContext(), MenuPramuniagaActivity.class);
                            go_to_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(go_to_home);
                            finish();
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

    private StorageReference storage;
    private DatabaseReference database;
    private FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            finish();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void animation() {
        //loadanim
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        toptobottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        bottomtotop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);


        //run animation
        header.startAnimation(toptobottom);
        judul.startAnimation(toptobottom);
        content.startAnimation(bottomtotop);
        btn_buatakun.startAnimation(bottomtotop);
        btn_back.startAnimation(bottomtotop);
    }

    String getFileExtension(Uri uri) {
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

        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null)
            ;
        {
            photo_location = data.getData();
            Picasso.get().load(photo_location).centerCrop().fit().into(avatar);
        }
    }

}
