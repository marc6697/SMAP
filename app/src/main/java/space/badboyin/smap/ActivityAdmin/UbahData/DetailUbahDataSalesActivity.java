package space.badboyin.smap.ActivityAdmin.UbahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.R;

public class DetailUbahDataSalesActivity extends AppCompatActivity {
    public static final String TAG = DetailUbahDataSalesActivity.class.getSimpleName();
    private Sales sales;

    EditText nama_supplier, nomor_supplier, nama_perusahaan, lokasi_perusahaan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ubah_data_sales);
        String key = getIntent().getStringExtra(TAG);
        nama_supplier = findViewById(R.id.txt_input_nama_sales);
        nomor_supplier = findViewById(R.id.txt_input_nomor_sales);
        nama_perusahaan = findViewById(R.id.txt_input_nama_perusahaan);
        nama_perusahaan.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        lokasi_perusahaan = findViewById(R.id.txt_input_lokasi_perusahaan);
        FirebaseDatabase.getInstance().getReference().child("data_salesman").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sales = dataSnapshot.getValue(Sales.class);
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findViewById(R.id.btn_tambah_data_sales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan((Button) v);
            }
        });
        findViewById(R.id.btn_kembali).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_hapus_data_sales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapus();
            }
        });
    }

    private void simpan(final Button btn) {

        final String field_nama_sales = nama_supplier.getText().toString();
        final String field_nomor_sales = nomor_supplier.getText().toString();
        final String field_nama_perusahaan = nama_perusahaan.getText().toString();
        final String field_lokasi_perusahaan = lokasi_perusahaan.getText().toString();


        if (field_nama_sales.isEmpty()) {
            Toast.makeText(this, "Nama Sales Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (field_nomor_sales.isEmpty()) {
            Toast.makeText(this, "Nomor Sales Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (field_nama_perusahaan.isEmpty()) {
            Toast.makeText(this, "Nama Perusahaan Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (field_lokasi_perusahaan.isEmpty()) {
            Toast.makeText(this, "Lokasi Perusahaan Masih Kosong", Toast.LENGTH_LONG).show();
        }
        else
            sales.setNama_sales(nama_supplier.getText().toString());
            sales.setNama_perusahaan(nama_perusahaan.getText().toString());
            sales.setNomor_sales(nomor_supplier.getText().toString());
            sales.setLokasi_perusahaan(lokasi_perusahaan.getText().toString());
            btn.setEnabled(false);
            btn.setText("LOADING");
            FirebaseDatabase.getInstance().getReference().child("data_salesman").child(sales.getId_sales()).setValue(sales)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            btn.setEnabled(true);
                            btn.setText("SIMPAN DATA");
                            Toast.makeText(DetailUbahDataSalesActivity.this, "Berhasil menambah Sales !", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    btn.setEnabled(true);
                    btn.setText("SIMPAN DATA");
                }
            });
        }


    private void hapus() {
        FirebaseDatabase.getInstance().getReference().child("salesman_data").child(sales.getId_sales()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailUbahDataSalesActivity.this, "Berhasil menghapus Sales !", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });
    }

    private void init() {
        nama_supplier.setText(sales.getNama_sales());
        nomor_supplier.setText(sales.getNomor_sales());
        nama_perusahaan.setText(sales.getNama_perusahaan());
        lokasi_perusahaan.setText(sales.getLokasi_perusahaan());
    }
}
