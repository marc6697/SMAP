package space.badboyin.smap.Transaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.DetailTransaksiPenjualan;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;
import space.badboyin.smap.utilities.TextUtil;

public class DetailPenjualanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransaksiPenjualan transaksiPenjualan;
    private final List<DetailTransaksiPenjualan> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penjualan);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new KerAdap());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (App.please().getTransaksiPenjualan() == null) {
            transaksiPenjualan = new TransaksiPenjualan();
            transaksiPenjualan.setId_penjualan("T1001" + System.currentTimeMillis());
        } else {
            transaksiPenjualan = App.please().getTransaksiPenjualan();
            App.please().setTransaksiPenjualan(null);

            ((TextView) findViewById(R.id.nama)).setText(transaksiPenjualan.getNama_pembeli());
            ((TextView) findViewById(R.id.alamat)).setText(transaksiPenjualan.getAlamat_pembeli());
            ((TextView) findViewById(R.id.no_telp)).setText(transaksiPenjualan.getNo_telp_pembeli());
            ((TextView) findViewById(R.id.no_transaksi)).setText(transaksiPenjualan.getId_penjualan());

            FirebaseDatabase.getInstance().getReference().child("users_data").child(transaksiPenjualan.getId_user())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            if (u != null) {
                                ((TextView) findViewById(R.id.pelayan)).setText(u.getEmail());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(transaksiPenjualan.getId_penjualan()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        details.add(ds.getValue(DetailTransaksiPenjualan.class));
                    }
                    if (recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
                    hitungTotal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        ((TextView) findViewById(R.id.no_transaksi)).setText(transaksiPenjualan.getId_penjualan());
    }

    private double hitungTotal() {
        double jum = 0;
        for (DetailTransaksiPenjualan k : details) {
            jum += k.getHarga_jual() * k.getJumlah_jual();
        }
        ((TextView) findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(jum));
        return jum;
    }

    public class KerAdap extends RecyclerView.Adapter<KerAdap.KerHolder> {
        @NonNull
        @Override
        public KerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull KerHolder holder, int position) {
            holder.draw(details.get(position));
        }

        @Override
        public int getItemCount() {
            return details.size();
        }

        private void loadDetail(final DetailTransaksiPenjualan data, final View itemView) {
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
                itemView.findViewById(R.id.delete).setVisibility(View.GONE);
            }

            public void draw(final DetailTransaksiPenjualan data) {
                ((TextView) itemView.findViewById(R.id.txt_queue_satuan)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getHarga_jual()));
                ((TextView) itemView.findViewById(R.id.txt_queue_total)).setText(TextUtil.formatCurrencyIdn((data.getJumlah_jual() * data.getHarga_jual())));

                ((TextView) itemView.findViewById(R.id.txt_queue_jumlah)).setText(data.getJumlah_jual() + " ");

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
            }
        }
    }
}
