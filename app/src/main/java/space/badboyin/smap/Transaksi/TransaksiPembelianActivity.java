package space.badboyin.smap.Transaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.DetailTransaksiPembelian;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.DetailOrderDialog;
import space.badboyin.smap.dialogs.LoadingDialog;
import space.badboyin.smap.utilities.TextUtil;

public class TransaksiPembelianActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView recyclerView;
    private final List<Sales> sales = new ArrayList<>();
    private TransaksiPembelian transaksiPembelian;
    private final List<DetailTransaksiPembelian> details = new ArrayList<>();
    private Button simpan;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tansaksi_pembelian);
        spinner = findViewById(R.id.spinner);
        simpan = findViewById(R.id.simpan);

        findViewById(R.id.tambah_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.please().setKeramik(null);
                startActivity(new Intent(TransaksiPembelianActivity.this, BarangPickerActivity.class));
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransaksiPembelianActivity.this, TransaksiPenjualanActivity.class));
                finish();
            }
        });
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan();
            }
        });

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new KerAdap());
        FirebaseDatabase.getInstance().getReference().child("data_salesman").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sales.clear();
                for (DataSnapshot jenisSnapshot : dataSnapshot.getChildren()) {
                    Sales sp = jenisSnapshot.getValue(Sales.class);
                    sales.add(sp);
                }
                initSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (App.please().getTransaksiPembelian() == null) {
            transaksiPembelian = new TransaksiPembelian();
            transaksiPembelian.setId_pembelian("O2001" + System.currentTimeMillis());
        } else {
            transaksiPembelian = App.please().getTransaksiPembelian();
            App.please().setTransaksiPembelian(null);
            FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(transaksiPembelian.getId_pembelian()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    details.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        details.add(ds.getValue(DetailTransaksiPembelian.class));
                    }
                    if (recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
                    hitungTotal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if (transaksiPembelian.getStatus_pembeian() != null && transaksiPembelian.getStatus_pembeian().equals("SELESAI")) {
                simpan.setEnabled(false);
                spinner.setEnabled(false);
                findViewById(R.id.tambah_barang).setEnabled(false);
                findViewById(R.id.reset).setEnabled(false);
            }
        }
        ((TextView) findViewById(R.id.no_order)).setText(transaksiPembelian.getId_pembelian());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initSpinner() {
        final List<String> data = new ArrayList<>();
        data.add("- Pilih Sales Keramik -");
        int index = 0;
        for (int w = 0; w < sales.size(); w++) {
            data.add(sales.get(w).getNama_sales() + " - " + sales.get(w).getNama_perusahaan());
            if (transaksiPembelian.getId_sales() != null && transaksiPembelian.getId_sales().equals(sales.get(w).getId_sales())) {
                index = w + 1;
            }
        }

        ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<String>(TransaksiPembelianActivity.this, android.R.layout.simple_spinner_item, data);
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(kategoriAdapter);
        spinner.setSelection(index);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void simpan() {
        if (transaksiPembelian.getStatus_pembeian() != null && transaksiPembelian.getStatus_pembeian().equals("SELESAI")) {
            onBackPressed();
            return;
        }
        if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Sales boleh kosong !", Toast.LENGTH_LONG).show();
        } else if (details.isEmpty()) {
            Toast.makeText(this, "Barang tidak boleh kosong !", Toast.LENGTH_LONG).show();
        } else {
            transaksiPembelian.setId_user(FirebaseAuth.getInstance().getCurrentUser().getUid());
            transaksiPembelian.setTotal_harga(hitungTotal());
            transaksiPembelian.setTanggal_pembelian(TextUtil.formatTanggal(System.currentTimeMillis()));
            transaksiPembelian.setId_sales(sales.get(spinner.getSelectedItemPosition() - 1).getId_sales());
            transaksiPembelian.setStatus_pembeian("PENDING");
            if (loading != null && loading.isShowing())
                loading.dismiss();
            loading = new LoadingDialog(this);
            loading.show();
            FirebaseDatabase.getInstance().getReference().child("data_pembelian").child(transaksiPembelian.getId_pembelian()).setValue(transaksiPembelian)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pushDetail();
                            } else {
                                loading.dismiss();
                                Toast.makeText(TransaksiPembelianActivity.this, "Error. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    private void pushDetail() {
        FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(transaksiPembelian.getId_pembelian()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    for (DetailTransaksiPembelian dt : details) {
                        dt.setId_pembelian(transaksiPembelian.getId_pembelian());
                        dt.setId_detail_pembelian(FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(dt.getId_pembelian()).push().getKey());
                        FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(dt.getId_pembelian()).child(dt.getId_detail_pembelian()).setValue(dt);
                    }
                    loading.dismiss();
                    Toast.makeText(TransaksiPembelianActivity.this, "Transaksi Pembelian Berhasil, Silahkan mengkonfirmasi kalau transaksiPembelian sudah datang.!", Toast.LENGTH_LONG).show();
                    onBackPressed();
                } else {
                    loading.dismiss();
                    Toast.makeText(TransaksiPembelianActivity.this, "Error. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.please().getKeramik() != null) {
            addKeramik(App.please().getKeramik());
            App.please().setKeramik(null);
        }
    }

    private double hitungTotal() {
        double jum = 0;
        for (DetailTransaksiPembelian dt : details) {
            jum += dt.getHarga_beli() * dt.getJumlah_beli();
        }
        ((TextView) findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(jum));
        return jum;
    }

    private void delete(DetailTransaksiPembelian data) {
        details.remove(data);
        recyclerView.getAdapter().notifyDataSetChanged();
        hitungTotal();
    }

    private void addKeramik(final Keramik keramik) {
        new DetailOrderDialog(this, keramik, new DetailOrderDialog.OnDetailOrderListener() {
            @Override
            public void onResult(int jum, int harga) {
                DetailTransaksiPembelian detail = new DetailTransaksiPembelian();
                detail.setHarga_beli(harga);
                detail.setJumlah_beli(jum);
                detail.setKeramik(keramik);
                detail.setId_keramik(keramik.getId_keramik());
                for (int w = 0; w < details.size(); w++) {
                    if (details.get(w).getId_keramik().equals(keramik.getId_keramik())) {
                        details.set(w, detail);
                        if (recyclerView.getAdapter() != null)
                            recyclerView.getAdapter().notifyDataSetChanged();
                        hitungTotal();
                        return;
                    }
                }
                details.add(detail);
                if (recyclerView.getAdapter() != null)
                    recyclerView.getAdapter().notifyDataSetChanged();
                hitungTotal();
            }
        }).show();
    }

    public class KerAdap extends RecyclerView.Adapter<KerAdap.KerHolder> {

        @NonNull
        @Override
        public KerAdap.KerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KerAdap.KerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull KerAdap.KerHolder holder, int position) {
            holder.draw(details.get(position));
        }

        @Override
        public int getItemCount() {
            return details.size();
        }

        private void loadDetail(final DetailTransaksiPembelian data, final View itemView) {
            if (data.getKeramik().getKategori() != null) {
                ((TextView) itemView.findViewById(R.id.txt_queue_jenis_keramik)).setText(data.getKeramik().getKategori().getKategori_keramik());
            } else {
                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(data.getKeramik().getId_kategori()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kategori k = dataSnapshot.getValue(Kategori.class);
                        if (k != null) {
                            data.getKeramik().setKategori(k);
                            ((TextView) itemView.findViewById(R.id.txt_queue_jenis_keramik)).setText(k.getKategori_keramik());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            if (data.getKeramik().getUkuran() != null) {
                ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik))
                        .setText(((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).getText()
                                + "(" + data.getKeramik().getUkuran().getNama_ukuran() + ")");
            } else
                FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(data.getKeramik().getId_kategori()).child(data.getKeramik().getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Ukuran u = dataSnapshot.getValue(Ukuran.class);
                        if (u != null) {
                            ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik))
                                    .setText(((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).getText()
                                            + "(" + u.getNama_ukuran() + ")");
                            data.getKeramik().setUkuran(u);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            if (data.getKeramik().getMerk() != null) {
                ((TextView) itemView.findViewById(R.id.txt_queue_merk_keramik)).setText(data.getKeramik().getMerk().getNama_merk());
            } else
                FirebaseDatabase.getInstance().getReference().child("data_merk").child(data.getKeramik().getId_merk()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Merk m = dataSnapshot.getValue(Merk.class);
                        if (m != null) {
                            data.getKeramik().setMerk(m);
                            ((TextView) itemView.findViewById(R.id.txt_queue_merk_keramik)).setText(m.getNama_merk());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }

        public class KerHolder extends RecyclerView.ViewHolder {
            public KerHolder(@NonNull View itemView) {
                super(itemView);
                ((TextView) itemView.findViewById(R.id.jumlah)).setText("Jumlah TransaksiPembelian");
                ((TextView) itemView.findViewById(R.id.harga_satuan)).setText("Harga Beli Satuan");
            }

            public void draw(final DetailTransaksiPembelian data) {
                ((TextView) itemView.findViewById(R.id.txt_queue_satuan)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getHarga_beli()));
                ((TextView) itemView.findViewById(R.id.txt_queue_total)).setText(TextUtil.formatCurrencyIdn(data.getJumlah_beli() * data.getHarga_beli()));
                if (data.getKeramik() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).setText(data.getKeramik().getNama_keramik() + " ");
                    loadDetail(data, itemView);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("data_keramik").child(data.getId_keramik()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Keramik k = dataSnapshot.getValue(Keramik.class);
                            if (k != null) {
                                ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).setText(k.getNama_keramik() + " ");
                                data.setKeramik(k);
                                loadDetail(data, itemView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                ((TextView) itemView.findViewById(R.id.txt_queue_jumlah)).setText(data.getJumlah_beli() + " ");


                if (transaksiPembelian.getStatus_pembeian() != null && transaksiPembelian.getStatus_pembeian().equals("SELESAI")) {
                    itemView.findViewById(R.id.delete).setEnabled(false);
                }
                itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(data);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        transaksiPembelian = null;
        finish();
    }
}
