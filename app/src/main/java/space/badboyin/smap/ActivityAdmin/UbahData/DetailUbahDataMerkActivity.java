package space.badboyin.smap.ActivityAdmin.UbahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.LoadingDialog;

public class DetailUbahDataMerkActivity extends AppCompatActivity {
    public final static String EXTRA_MERK = "MERK_ID";
    private Merk merk;
    private EditText nama_merk;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ubah_data_merk);
        nama_merk = findViewById(R.id.txt_input_merk);
        String key = getIntent().getStringExtra(EXTRA_MERK);
        FirebaseDatabase.getInstance().getReference().child("data_merk").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                merk = dataSnapshot.getValue(Merk.class);
                if (merk != null) {
                    init();
                } else onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onBackPressed();
            }
        });
    }

    private void init() {
        nama_merk.setText(merk.getNama_merk());
        findViewById(R.id.btn_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nama_merk.getText().toString().isEmpty()) {
                    Toast.makeText(DetailUbahDataMerkActivity.this, "Nama Merk Tidak Boleh Kosong", Toast.LENGTH_LONG).show();
                } else if (nama_merk.getText().toString().equals(merk.getNama_merk())) {
                    Toast.makeText(getApplicationContext(), "Nama masih sama !", Toast.LENGTH_LONG).show();
                    return;
                } else
                    merk.setNama_merk(nama_merk.getText().toString());
                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                }
                loading = new LoadingDialog(DetailUbahDataMerkActivity.this);
                loading.show();
                FirebaseDatabase.getInstance().getReference().child("data_merk").child(merk.getId_merk()).setValue(merk).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailUbahDataMerkActivity.this, "Sukses mengubah Merk !", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(DetailUbahDataMerkActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
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
        findViewById(R.id.btn_hapus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("data_merk").child(merk.getId_merk()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sukses menghapus kategori " + nama_merk.getText().toString(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
