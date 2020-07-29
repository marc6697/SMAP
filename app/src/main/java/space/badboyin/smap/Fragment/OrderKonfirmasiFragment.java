package space.badboyin.smap.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.DetailTransaksiPembelian;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.TransaksiPembelianActivity;
import space.badboyin.smap.Transaksi.RiwayatOrderActivity;
import space.badboyin.smap.utilities.TextUtil;

public class OrderKonfirmasiFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private String cari = "";
    private List<TransaksiPembelian> ori_Transaksi_pembelians = new ArrayList<>();
    private final List<TransaksiPembelian> transaksiPembelians = new ArrayList<>();
    private RiwayatOrderActivity activity;

    public void setTransaksiPembelians(List<TransaksiPembelian> transaksiPembelians) {
        this.ori_Transaksi_pembelians = transaksiPembelians;
        Collections.sort(this.ori_Transaksi_pembelians, new Comparator<TransaksiPembelian>() {
            @Override
            public int compare(TransaksiPembelian o1, TransaksiPembelian o2) {
                return o2.getTanggal_pembelian().compareTo(o1.getTanggal_pembelian());
            }
        });
        filter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_confrim, container, false);
        activity = (RiwayatOrderActivity) getActivity();
        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new OrderAdap());
        ((EditText) view.findViewById(R.id.cari)).addTextChangedListener(new TextWatcher() {
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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        filter();
    }

    private void filter() {
        if (recyclerView == null || recyclerView.getAdapter() == null) return;
        transaksiPembelians.clear();
        if (cari.isEmpty()) {
            transaksiPembelians.addAll(ori_Transaksi_pembelians);
        } else {
            cari = cari.toLowerCase();
            for (TransaksiPembelian o : ori_Transaksi_pembelians) {
                if (o.getId_pembelian().toLowerCase().contains(cari) || o.getTanggal_pembelian().toLowerCase().contains(cari)
                        || o.getStatus_pembeian().toLowerCase().contains(cari) || o.getId_sales().toLowerCase().contains(cari)) {
                    transaksiPembelians.add(o);
                }
            }
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void confirmOrder(final TransaksiPembelian data) {
        new AlertDialog.Builder(activity)
                .setTitle("Konfirmasi TransaksiPembelian " + data.getId_pembelian())
                .setMessage("Pastikan Barang TransaksiPembelian Sudah datang dan diperiksa. TransaksiPembelian yang sudah dikonfirmasi tidak bisa diubah !")
                .setPositiveButton("Ya, Konfirmasi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doConfirm(data);
                    }
                }).setNegativeButton("Batal", null).show();
    }

    private void doConfirm(final TransaksiPembelian transaksiPembelian) {
        transaksiPembelian.setStatus_pembeian("SELESAI");
        FirebaseDatabase.getInstance().getReference().child("data_pembelian").child(transaksiPembelian.getId_pembelian()).setValue(transaksiPembelian)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            terimaBarang(transaksiPembelian);
                        } else {
                            Toast.makeText(activity, "GAGAL : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void terimaBarang(final TransaksiPembelian transaksiPembelian) {

        FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(transaksiPembelian.getId_pembelian()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DetailTransaksiPembelian> details = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    details.add(ds.getValue(DetailTransaksiPembelian.class));
                }
                tambahStock(details);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void tambahStock(List<DetailTransaksiPembelian> detailTransaksiPembelians) {
        for (final DetailTransaksiPembelian dt : detailTransaksiPembelians) {
            FirebaseDatabase.getInstance().getReference().child("data_keramik").child(dt.getId_keramik()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Keramik keramik = dataSnapshot.getValue(Keramik.class);
                    if (keramik != null) {
                        keramik.setStock(keramik.getStock() + dt.getJumlah_beli());
                        keramik.setHarga_beli(dt.getHarga_beli());
                        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(dt.getId_keramik()).setValue(keramik);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Toast.makeText(activity, "Berhasil Mengkonfirmasi Transaksi Pembelian", Toast.LENGTH_SHORT).show();
        activity.refresh();
    }

    private void deleteOrder(final TransaksiPembelian transaksiPembelian) {
        FirebaseDatabase.getInstance().getReference().child("data_pembelian").child(transaksiPembelian.getId_pembelian()).setValue(null);
        FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(transaksiPembelian.getId_pembelian()).setValue(null);
        activity.refresh();
    }

    public class OrderAdap extends RecyclerView.Adapter<OrderAdap.OrderHolder> {
        @NonNull
        @Override
        public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OrderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderx, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
            holder.draw(transaksiPembelians.get(position));
        }

        @Override
        public int getItemCount() {
            return transaksiPembelians.size();
        }

        public class OrderHolder extends RecyclerView.ViewHolder {
            public OrderHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final TransaksiPembelian data) {
                ((TextView) itemView.findViewById(R.id.status)).setText(data.getStatus_pembeian());
                ((TextView) itemView.findViewById(R.id.no_order)).setText(data.getId_pembelian());
                ((TextView) itemView.findViewById(R.id.tanggal)).setText(data.getTanggal_pembelian());

                ((TextView) itemView.findViewById(R.id.sales)).setText("-");
                ((TextView) itemView.findViewById(R.id.perusahaan)).setText("-");
                if (data.getSales() != null) {
                    ((TextView) itemView.findViewById(R.id.sales)).setText(data.getSales().getNama_sales());
                    ((TextView) itemView.findViewById(R.id.perusahaan)).setText(data.getSales().getNama_perusahaan());
                } else
                    FirebaseDatabase.getInstance().getReference().child("data_salesman").child(data.getId_sales()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Sales s = dataSnapshot.getValue(Sales.class);
                            if (s != null) {
                                ((TextView) itemView.findViewById(R.id.sales)).setText(s.getNama_sales());
                                ((TextView) itemView.findViewById(R.id.perusahaan)).setText(s.getNama_perusahaan());
                                data.setSales(s);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                ((TextView) itemView.findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getTotal_harga()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Pilih Aksi")
                                .setPositiveButton("Ubah Detail", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        App.please().setTransaksiPembelian(data);
                                        getActivity().startActivity(new Intent(getActivity(), TransaksiPembelianActivity.class));
                                    }
                                }).setNegativeButton("Konfirmasi Transaksi Pembelian", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirmOrder(data);
                            }
                        }).setNeutralButton("Batalkan Transaksi Pembelian", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteOrder(data);
                            }
                        }).show();
                    }
                });
            }
        }
    }

}
