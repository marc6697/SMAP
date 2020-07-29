package space.badboyin.smap.ActivityAdmin.UbahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.R;

public class DetailUbahDataKategoriActivity extends AppCompatActivity {
    public static final String EXTRA_KATEGORI = "DetailUbahDataKategoriActivity";

    private Kategori kategori;
    private EditText nama_kategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ubah_data_kategori);
        String key = getIntent().getStringExtra(EXTRA_KATEGORI);

        nama_kategori = findViewById(R.id.txt_input_kategori);

        FirebaseDatabase.getInstance().getReference().child("data_kategori").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kategori = dataSnapshot.getValue(Kategori.class);
                if (kategori == null) {
                    onBackPressed();
                    return;
                }
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void init() {
        nama_kategori.setText(kategori.getKategori_keramik());
        //((TextView) findViewById(R.id.message)).setText("Ubah Kategori " + kategori.getKategori());
        findViewById(R.id.btn_tambah_kategori).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (nama_kategori.getText().toString().isEmpty()) {
                    Toast.makeText(DetailUbahDataKategoriActivity.this, "Nama Kategori Tidak Boleh Kosong", Toast.LENGTH_LONG).show();
                } else if (nama_kategori.getText().toString().equals(kategori.getKategori_keramik())) {
                    Toast.makeText(getApplicationContext(), "Nama masih sama !", Toast.LENGTH_LONG).show();
                    return;
                } else
                    kategori.setKategori(nama_kategori.getText().toString());
                v.setEnabled(false);
                ((TextView) v).setText("LOADING");

                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(kategori.getId_kategori()).setValue(kategori)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailUbahDataKategoriActivity.this, "Sukses mengubah kategori !", Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailUbahDataKategoriActivity.this, "Error " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        v.setEnabled(false);
                        ((TextView) v).setText("Ubah Kategori");

                    }
                });
            }
        });
        findViewById(R.id.btn_kembali).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_hapus_kategori).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(kategori.getId_kategori()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sukses menghapus kategori " + nama_kategori.getText().toString(), Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        }
                    }
                });
            }
        });
    }
}
