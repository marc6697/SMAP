package space.badboyin.smap.ActivityAdmin.TambahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.ActivityAdmin.KelolaData.KelolaDataActivity;
import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.R;

public class TambahDataSalesActivity extends AppCompatActivity {

    Button btn_buat, btn_kembali;
    EditText nama_supplier, nomor_supplier, nama_perusahaan, lokasi_perusahaan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_sales);


        nama_supplier = findViewById(R.id.txt_input_nama_sales);
        nomor_supplier = findViewById(R.id.txt_input_nomor_sales);
        nama_perusahaan = findViewById(R.id.txt_input_nama_perusahaan);
        nama_perusahaan.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        lokasi_perusahaan = findViewById(R.id.txt_input_lokasi_perusahaan);

        btn_buat = findViewById(R.id.btn_tambah_data_sales);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataUtama = new Intent(TambahDataSalesActivity.this, KelolaDataActivity.class);
                startActivity(keTambahDataUtama);
            }
        });

        btn_buat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String field_nama_supplier = nama_supplier.getText().toString();

                final String field_nomor_supplier = nomor_supplier.getText().toString();
                final String field_nama_perusahaan = nama_perusahaan.getText().toString();
                final String field_lokasi_perusahaan = lokasi_perusahaan.getText().toString();


                if (field_nama_supplier.isEmpty()) {
                    Toast.makeText(TambahDataSalesActivity.this, "Nama Sales Masih Kosong", Toast.LENGTH_LONG).show();
                } else if (field_nomor_supplier.isEmpty()) {
                    Toast.makeText(TambahDataSalesActivity.this, "Nomor Sales Masih Kosong", Toast.LENGTH_LONG).show();
                } else if (field_nama_perusahaan.isEmpty()) {
                    Toast.makeText(TambahDataSalesActivity.this, "Nama Perusahaan Masih Kosong", Toast.LENGTH_LONG).show();
                } else if (field_lokasi_perusahaan.isEmpty()) {
                    Toast.makeText(TambahDataSalesActivity.this, "Lokasi Perusahaan Masih Kosong", Toast.LENGTH_LONG).show();
                } else {
                    final Sales sales = new Sales();
                    sales.setId_sales(FirebaseDatabase.getInstance().getReference().child("salesman_data").push().getKey());
                    sales.setNama_sales(nama_supplier.getText().toString());
                    sales.setNama_perusahaan(nama_perusahaan.getText().toString());
                    sales.setNomor_sales(nomor_supplier.getText().toString());
                    sales.setLokasi_perusahaan(lokasi_perusahaan.getText().toString());
                    btn_buat.setEnabled(false);
                    btn_buat.setText("LOADING");
                    FirebaseDatabase.getInstance().getReference().child("data_salesman").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().child(sales.getId_sales()).setValue(sales);
                            btn_buat.setEnabled(true);
                            btn_buat.setText("TAMBAH DATA");
                            Toast.makeText(TambahDataSalesActivity.this, "Berhasil menambah Sales !", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataUtama = new Intent(TambahDataSalesActivity.this, KelolaDataActivity.class);
                startActivity(keTambahDataUtama);
            }
        });
    }
}
