package space.badboyin.smap.Transaksi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.DetailTransaksiPenjualan;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.LoadingDialog;
import space.badboyin.smap.utilities.TextUtil;


public class PramuRiwayatTransaksiTodayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pramu_riwayat_transaksi_today);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RiAdap());
        ((EditText) findViewById(R.id.cari)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cari = s.toString();
                filter();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadData();
    }

    private final List<TransaksiPenjualan> penjualans = new ArrayList<>();
    private final List<TransaksiPenjualan> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private String cari = "";
    private LoadingDialog loading;

    private void loadData() {
        FirebaseDatabase.getInstance().getReference().child("data_penjualan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                Calendar calendar = Calendar.getInstance();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransaksiPenjualan t = ds.getValue(TransaksiPenjualan.class);
                    if (t != null) {
                        String[] dt = t.getTanggal_penjualan().split(" ");
                        String[] d = dt[0].split("-");
                        if (calendar.get(Calendar.YEAR) == Integer.parseInt(d[2]) && calendar.get(Calendar.MONTH) == Integer.parseInt(d[1]) - 1
                                && calendar.get(Calendar.DATE) == Integer.parseInt(d[0])) {
                            data.add(t);
                        }
                    }
                }
                Collections.sort(data, new Comparator<TransaksiPenjualan>() {
                    @Override
                    public int compare(TransaksiPenjualan o1, TransaksiPenjualan o2) {
                        return o2.getTanggal_penjualan().compareTo(o1.getTanggal_penjualan());
                    }
                });
                filter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filter() {
        penjualans.clear();
        if (cari.isEmpty()) {
            penjualans.addAll(data);
        } else {
            cari = cari.toLowerCase();
            for (TransaksiPenjualan o : data) {
                if (o.getId_penjualan().toLowerCase().contains(cari)
                        || o.getStatus().toLowerCase().contains(cari) || o.getNama_pembeli().toLowerCase().contains(cari)) {
                    penjualans.add(o);
                }
            }
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void delete(final TransaksiPenjualan data) {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
        loading = new LoadingDialog(this);
        loading.show();
        FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(data.getId_penjualan()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DetailTransaksiPenjualan dp = ds.getValue(DetailTransaksiPenjualan.class);
                    if (dp != null) {
                        addStock(dp.getId_keramik(), dp.getJumlah_jual());
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(data.getId_penjualan()).setValue(null);
                FirebaseDatabase.getInstance().getReference().child("data_penjualan").child(data.getId_penjualan()).setValue(null);
                loading.dismiss();
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addStock(final String sku, final int stock) {
        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(sku).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Keramik k = dataSnapshot.getValue(Keramik.class);
                if (k != null) {
                    k.setStock(k.getStock() + stock);
                    k.setTerjual(k.getTerjual() - stock);
                    FirebaseDatabase.getInstance().getReference().child("data_keramik").child(sku).setValue(k);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class RiAdap extends RecyclerView.Adapter<RiAdap.RiHolder> {
        @NonNull
        @Override
        public RiAdap.RiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RiHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RiAdap.RiHolder holder, int position) {
            holder.draw(penjualans.get(position));
        }

        @Override
        public int getItemCount() {
            return penjualans.size();
        }

        public class RiHolder extends RecyclerView.ViewHolder {
            public RiHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final TransaksiPenjualan data) {
                ((TextView) itemView.findViewById(R.id.nama)).setText(data.getNama_pembeli());
                ((TextView) itemView.findViewById(R.id.alamat)).setText(data.getAlamat_pembeli());
                ((TextView) itemView.findViewById(R.id.telp)).setText(data.getNo_telp_pembeli());
                ((TextView) itemView.findViewById(R.id.no_transaksi)).setText(data.getId_penjualan());
                ((TextView) itemView.findViewById(R.id.tanggal)).setText(data.getTanggal_penjualan());
                ((TextView) itemView.findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getTotal_transaksi()));
                itemView.findViewById(R.id.status).setVisibility(data.getStatus().equals("SELESAI") ? View.GONE : View.VISIBLE);
                ((TextView) itemView.findViewById(R.id.status)).setText(data.getStatus());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!data.getStatus().equals("SELESAI")) {
                            App.please().setTransaksiPenjualan(data);
                            startActivity(new Intent(PramuRiwayatTransaksiTodayActivity.this, TransaksiPenjualanActivity.class));
                        }
                    }
                });
            }
        }
    }
}
