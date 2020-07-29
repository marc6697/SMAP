package space.badboyin.smap.ActivityAdmin.TambahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.R;

public class TambahKategoriActivity extends AppCompatActivity {
    Button btn_buat, btn_kembali_menu;
    EditText nama_kategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kategori);

        nama_kategori = findViewById(R.id.txt_input_kategori);
        btn_buat = findViewById(R.id.btn_tambah_kategori);
        btn_kembali_menu = findViewById(R.id.btn_kembali);

        btn_kembali_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_buat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nama_kategori.getText().toString().isEmpty()) {
                    Toast.makeText(TambahKategoriActivity.this, "Nama Kategori Masih Kosong", Toast.LENGTH_LONG).show();
                    return;
                }
                final Kategori kat = new Kategori();
                kat.setId_kategori(FirebaseDatabase.getInstance().getReference().child("data_kategori").push().getKey());
                kat.setKategori(nama_kategori.getText().toString());
                btn_buat.setEnabled(false);
                btn_buat.setText("LOADING");
                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(kat.getId_kategori()).setValue(kat)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(TambahKategoriActivity.this, "Berhasil Menambah", Toast.LENGTH_SHORT).show();
                                btn_buat.setEnabled(true);
                                btn_buat.setText("TAMBAH KATEGORI");
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btn_buat.setEnabled(true);
                        btn_buat.setText("TAMBAH KATEGORI");

                    }
                });
            }
        });

    }
}
