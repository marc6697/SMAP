package space.badboyin.smap.ActivityAdmin.UbahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.LoadingDialog;

public class DetailUbahDataUkuranActivity extends AppCompatActivity {
    public static final String EXTRA_UKURAN = "DetailUbahDataUkuranActivity_UKURAN";

    EditText ukuran_keramik;
    Button btn_ubah, btn_hapus, btn_kembali;
    private Ukuran ukuran;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ubah_data_ukuran);


        ukuran_keramik = findViewById(R.id.txt_input_ukuran);
        btn_ubah = findViewById(R.id.btn_ubah_ukuran);
        btn_hapus = findViewById(R.id.btn_hapus_ukuran);
        btn_kembali = findViewById(R.id.btn_kembali);

        String id_ukuran = getIntent().getStringExtra(EXTRA_UKURAN);
        FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(id_ukuran).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ukuran = dataSnapshot.getValue(Ukuran.class);
                if (ukuran != null)
                    init();
                else onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        ukuran_keramik.setText(ukuran.getNama_ukuran());
        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reference_ubah_ukuran=FirebaseDatabase.getInstance().getReference().child("Ukuran").child(UbahDataUkuranActivity.string_kategori_ukuran).child(intent_ukuran_keramik);
                if (ukuran_keramik.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ukuran masih kosong !", Toast.LENGTH_LONG).show();
                    //return;
                } else if (ukuran_keramik.getText().toString().equals(ukuran)) {
                    Toast.makeText(getApplicationContext(), "Ukuran masih sama !", Toast.LENGTH_LONG).show();
                } else {
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }
                    ukuran.setNama_ukuran(ukuran_keramik.getText().toString());
                    loading = new LoadingDialog(v.getContext());
                    loading.show();
                    FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(ukuran.getId_ukuran()).setValue(ukuran)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loading.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Sukses mengubah ukuran !", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        btn_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapus();
            }
        });
        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void hapus() {
        FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(ukuran.getId_ukuran()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Sukses menghapus ukuran " + ukuran_keramik.getText().toString(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }
}
