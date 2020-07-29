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
import space.badboyin.smap.Model.DetailTransaksiPenjualan;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.RiwayatTransaksiActivity;
import space.badboyin.smap.Transaksi.TransaksiPenjualanActivity;
import space.badboyin.smap.dialogs.LoadingDialog;
import space.badboyin.smap.utilities.TextUtil;

public class PenjualanKonfirmasiFragment extends Fragment {
    private RecyclerView recyclerView;
    private View view;
    private String cari = "";
    private final List<TransaksiPenjualan> transaksiPenjualans = new ArrayList<>();
    private final List<TransaksiPenjualan> ori_transaksiPenjualans = new ArrayList<>();
    private LoadingDialog loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_penjualan_konfirm, container, false);

        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RiAdap());
        ((EditText) view.findViewById(R.id.cari)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cari = s.toString();
                init();
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
        if (view != null)
            init();
    }

    public void set(List<TransaksiPenjualan> trans) {
        ori_transaksiPenjualans.clear();
        ori_transaksiPenjualans.addAll(trans);
        Collections.sort(ori_transaksiPenjualans, new Comparator<TransaksiPenjualan>() {
            @Override
            public int compare(TransaksiPenjualan o1, TransaksiPenjualan o2) {
                return o2.getTanggal_penjualan().compareTo(o1.getTanggal_penjualan());
            }
        });
        if (view != null)
            init();
    }

    private void init() {
        transaksiPenjualans.clear();
        if (cari.isEmpty()) {
            transaksiPenjualans.addAll(ori_transaksiPenjualans);
        } else {
            cari = cari.toLowerCase();
            for (TransaksiPenjualan t : ori_transaksiPenjualans) {
                if (t.getId_penjualan().toLowerCase().contains(cari) || t.getNama_pembeli().toLowerCase().contains(cari)
                        || t.getAlamat_pembeli().toLowerCase().contains(cari) || t.getNo_telp_pembeli().toLowerCase().contains(cari)) {
                    transaksiPenjualans.add(t);
                }
            }
        }
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void confirm(final TransaksiPenjualan data) {
        data.setStatus("SELESAI");
        FirebaseDatabase.getInstance().getReference().child("data_penjualan").child(data.getId_penjualan()).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (getActivity() != null)
                            ((RiwayatTransaksiActivity) getActivity()).refresh();
                    }
                });
    }

    private void delete(final TransaksiPenjualan data) {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
        loading = new LoadingDialog(getActivity());
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
                ((RiwayatTransaksiActivity)getActivity()).refresh();
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
            holder.draw(transaksiPenjualans.get(position));
        }

        @Override
        public int getItemCount() {
            return transaksiPenjualans.size();
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
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Pilih Aksi")
                                .setPositiveButton("Ubah Detail", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        App.please().setTransaksiPenjualan(data);
                                        getActivity().startActivity(new Intent(getActivity(), TransaksiPenjualanActivity.class));
                                    }
                                }).setNegativeButton("Konfirmasi Transaksi Penjualan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confirm(data);
                            }
                        }).setNeutralButton("Batalkan Transaksi Penjualan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete(data);
                            }
                        }).show();
                    }
                });
            }
        }
    }
}
