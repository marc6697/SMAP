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
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.LoadingDialog;

public class TambahMerkActivity extends AppCompatActivity {

    private Button btn_buat, btn_kembali_menu;
    private EditText nama_kategori;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_merk);

        nama_kategori = findViewById(R.id.txt_input_merk);
        btn_buat = findViewById(R.id.btn_tambah_merk);
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
                    Toast.makeText(TambahMerkActivity.this, "Nama Kategori Masih Kosong", Toast.LENGTH_LONG).show();
                    return;
                }
                final Merk merk = new Merk();
                merk.setId_merk(FirebaseDatabase.getInstance().getReference().child("data_merk").push().getKey());
                merk.setNama_merk(nama_kategori.getText().toString());
                btn_buat.setEnabled(false);
                if (loading != null && loading.isShowing())
                    loading.dismiss();
                loading = new LoadingDialog(TambahMerkActivity.this);
                loading.show();
                FirebaseDatabase.getInstance().getReference().child("data_merk").child(merk.getId_merk()).setValue(merk)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(TambahMerkActivity.this, "Berhasil Menambah", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(TambahMerkActivity.this, "Error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
