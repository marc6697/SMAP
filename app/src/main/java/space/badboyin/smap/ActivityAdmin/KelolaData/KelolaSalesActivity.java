package space.badboyin.smap.ActivityAdmin.KelolaData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import space.badboyin.smap.ActivityAdmin.TambahData.TambahDataSalesActivity;
import space.badboyin.smap.ActivityAdmin.UbahData.UbahDataSalesActivity;
import space.badboyin.smap.R;

public class KelolaSalesActivity extends AppCompatActivity {
    CardView btn_tambah,btn_ubah;
    Button btn_kembali;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_sales);
        btn_tambah=findViewById(R.id.btn_tambah_sales);
        btn_ubah=findViewById(R.id.btn_ubah_sales);
        btn_kembali=findViewById(R.id.btn_back_to_home);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataKeramik=new Intent(KelolaSalesActivity.this, TambahDataSalesActivity.class);
                startActivity(keTambahDataKeramik);
            }
        });

        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataKeramik=new Intent(KelolaSalesActivity.this, UbahDataSalesActivity.class);
                startActivity(keTambahDataKeramik);
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
