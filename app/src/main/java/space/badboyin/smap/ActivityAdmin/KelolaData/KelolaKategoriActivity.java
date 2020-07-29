package space.badboyin.smap.ActivityAdmin.KelolaData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import space.badboyin.smap.ActivityAdmin.TambahData.TambahDataKeramikActivity;
import space.badboyin.smap.ActivityAdmin.TambahData.TambahKategoriActivity;
import space.badboyin.smap.ActivityAdmin.UbahData.UbahDataKategoriActivity;
import space.badboyin.smap.ActivityAdmin.UbahData.UbahDataKeramikActivity;
import space.badboyin.smap.R;

public class KelolaKategoriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_kategori);
        CardView btn_tambah, btn_ubah;
        Button btn_kembali;
        btn_tambah = findViewById(R.id.btn_tambah_kategori);
        btn_ubah = findViewById(R.id.btn_ubah_kategori);
        btn_kembali = findViewById(R.id.btn_back_to_home);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataKeramik = new Intent(KelolaKategoriActivity.this, TambahKategoriActivity.class);
                startActivity(keTambahDataKeramik);
            }
        });

        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent keTambahDataKeramik = new Intent(KelolaKategoriActivity.this, UbahDataKategoriActivity.class);
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
