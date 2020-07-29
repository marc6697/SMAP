package space.badboyin.smap.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.utilities.TextUtil;

public class DetailOrderDialog extends AppCompatDialog {

    public interface OnDetailOrderListener {
        void onResult(int jum, int harga);
    }

    private final Keramik keramik;
    private final OnDetailOrderListener callback;

    public DetailOrderDialog(Context context, Keramik keramik, OnDetailOrderListener callback) {
        super(context);
        this.keramik = keramik;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detail_order);
        ((TextView) findViewById(R.id.txt_list_id)).setText(keramik.getId_keramik());
        ((TextView) findViewById(R.id.txt_list_nama)).setText(keramik.getNama_keramik());
        ((TextView) findViewById(R.id.txt_list_harga_beli)).setText(TextUtil.formatCurrencyIdn(keramik.getHarga_beli()));
        ((TextView) findViewById(R.id.txt_list_stock)).setText(keramik.getStock() + "");

        ((TextView) findViewById(R.id.txt_list_merk)).setText("-");
        ((TextView) findViewById(R.id.txt_list_jenis)).setText("-");
        ((TextView) findViewById(R.id.txt_list_ukuran)).setText("-");

        if (keramik.getKategori() != null)
            ((TextView) findViewById(R.id.txt_list_jenis)).setText(keramik.getKategori().getKategori_keramik());
        else
            FirebaseDatabase.getInstance().getReference().child("data_kategori").child(keramik.getId_kategori()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Kategori k = dataSnapshot.getValue(Kategori.class);
                    if (k != null) {
                        keramik.setKategori(k);
                        ((TextView) findViewById(R.id.txt_list_jenis)).setText(k.getKategori_keramik());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        if (keramik.getUkuran() != null) {
            ((TextView) findViewById(R.id.txt_list_ukuran)).setText(keramik.getUkuran().getNama_ukuran());
        } else
            FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(keramik.getId_kategori()).
                    child(keramik.getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Ukuran u = dataSnapshot.getValue(Ukuran.class);
                    if (u != null) {
                        keramik.setUkuran(u);
                        ((TextView) findViewById(R.id.txt_list_ukuran)).setText(u.getNama_ukuran());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        if (keramik.getMerk() != null)
            ((TextView) findViewById(R.id.txt_list_merk)).setText(keramik.getMerk().getNama_merk());
        else
            FirebaseDatabase.getInstance().getReference().child("data_merk").child(keramik.getId_merk()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Merk m = dataSnapshot.getValue(Merk.class);
                            if (m != null) {
                                keramik.setMerk(m);
                                ((TextView) findViewById(R.id.txt_list_merk)).setText(m.getNama_merk());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        ((TextView) findViewById(R.id.txt_list_harga)).setText(TextUtil.formatCurrencyIdn(keramik.getHarga_jual()));
        findViewById(R.id.tambah_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edjum = findViewById(R.id.jumlah);
                if (edjum.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Jumlah masih kosong !", Toast.LENGTH_LONG).show();
                    return;
                } else if (Integer.parseInt(edjum.getText().toString()) == 0) {
                    Toast.makeText(getContext(), "Jumlah Tidak boleh 0 !", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText edharga = findViewById(R.id.harga);
                if (edharga.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Harga masih kosong !", Toast.LENGTH_LONG).show();
                    return;
                } else if (Integer.parseInt(edharga.getText().toString()) == 0) {
                    Toast.makeText(getContext(), "Harga Tidak boleh 0 !", Toast.LENGTH_LONG).show();
                    return;
                }
                callback.onResult(Integer.parseInt(edjum.getText().toString()), Integer.parseInt(edharga.getText().toString()));
                dismiss();
            }
        });
    }
}
