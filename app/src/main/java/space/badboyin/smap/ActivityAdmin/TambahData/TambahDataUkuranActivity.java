package space.badboyin.smap.ActivityAdmin.TambahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.ActivityAdmin.KelolaData.KelolaDataActivity;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.LoadingDialog;

public class TambahDataUkuranActivity extends AppCompatActivity {
    EditText ukuran_keramik;
    Button btn_buat, btn_kembali;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_ukuran);

        ukuran_keramik = findViewById(R.id.txt_input_ukuran);

        btn_buat = findViewById(R.id.btn_tambah_ukuran);
        btn_kembali = findViewById(R.id.btn_kembali);


        btn_buat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ukuran_keramik.getText().toString().isEmpty()) {
                    Toast.makeText(TambahDataUkuranActivity.this, "Ukuran tidak boleh kosong", Toast.LENGTH_LONG).show();
                    return;
                }
                Ukuran u = new Ukuran();
                u.setId_ukuran(FirebaseDatabase.getInstance().getReference().child("data_ukuran").push().getKey());
                u.setNama_ukuran(ukuran_keramik.getText().toString());
                if (loading != null && loading.isShowing())
                    loading.dismiss();
                loading = new LoadingDialog(TambahDataUkuranActivity.this);
                loading.show();
                FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(u.getId_ukuran()).setValue(u)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loading.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(TambahDataUkuranActivity.this, "Berhasil menambah ukuran! ", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(TambahDataUkuranActivity.this, "Error! ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataUtama = new Intent(TambahDataUkuranActivity.this, KelolaDataActivity.class);
                startActivity(keTambahDataUtama);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}


