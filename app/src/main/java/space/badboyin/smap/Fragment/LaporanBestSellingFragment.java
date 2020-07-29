package space.badboyin.smap.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;
import com.squareup.picasso.Picasso;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import space.badboyin.smap.Model.DetailTransaksiPenjualan;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.LaporanActivity;
import space.badboyin.smap.utilities.TextUtil;

public class LaporanBestSellingFragment extends Fragment {

    private View view;
    private LaporanActivity activity;

    private RecyclerView recyclerView;
    private final List<TransaksiPenjualan> transaksiPenjualans = new ArrayList<>();
    private final List<TransaksiPenjualan> ori_transaksiPenjualans = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    private Button btn_bulan;
    private Button btn_tahun;
    int mode = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_laporan_best_selling, container, false);
        activity = (LaporanActivity) getActivity();
        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new KerAdap());
        btn_bulan = view.findViewById(R.id.btn_bulan);
        btn_bulan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMonth();
            }
        });
        btn_tahun = view.findViewById(R.id.btn_tahun);
        btn_tahun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYear();
            }
        });

        return view;
    }

    private void openMonth() {
        new RackMonthPicker(activity)
                .setLocale(Locale.ENGLISH)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month - 1);
                        mode = 1;
                        init();
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {

                    }
                }).show();
    }

    private void openYear() {
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(activity, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                calendar.set(Calendar.YEAR, selectedYear);
                mode = 2;
                init();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        builder.showYearOnly().build().show();
    }

    private final List<Keramik> keramiks = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_keramik").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keramiks.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Keramik k = ds.getValue(Keramik.class);
                    if (k != null) {
                        k.setTerjual(0);
                        keramiks.add(k);
                    }
                }
                openTransaksi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openTransaksi() {
        FirebaseDatabase.getInstance().getReference().child("data_penjualan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ori_transaksiPenjualans.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ori_transaksiPenjualans.add(ds.getValue(TransaksiPenjualan.class));
                }
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        if (mode == 1) {
            btn_bulan.setText(TextUtil.formatMonthYear(calendar.getTimeInMillis()));
            btn_tahun.setText("Tahunan");
        } else if (mode == 2) {
            btn_tahun.setText(TextUtil.formatYear(calendar.getTimeInMillis()));
            btn_bulan.setText("Bulanan");
        }
        transaksiPenjualans.clear();
        for (TransaksiPenjualan t : ori_transaksiPenjualans) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(t.getTime());
            if (mode == 1 && cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                transaksiPenjualans.add(t);
            } else if (mode == 2 && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                transaksiPenjualans.add(t);
            }
        }
        countTotal();
    }

    private double total_transaksi = 0;

    private void countTotal() {
        total_transaksi = 0;
        for (TransaksiPenjualan t : transaksiPenjualans) {
            total_transaksi += t.getTotal_transaksi();
        }
        activity.setBest("Total Rp. " + TextUtil.formatCurrencyIdn(total_transaksi));

        if (transaksiPenjualans.isEmpty() && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        for (int w = 0; w < keramiks.size(); w++) {
            keramiks.get(w).setTerjual(0);
        }
        for (final TransaksiPenjualan t : transaksiPenjualans) {
            FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(t.getId_penjualan()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        DetailTransaksiPenjualan dt = ds.getValue(DetailTransaksiPenjualan.class);
                        if (dt != null) {
                            addKeramik(dt.getId_keramik(), dt.getJumlah_jual());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void addKeramik(String idk, int jum) {
        for (int w = 0; w < keramiks.size(); w++) {
            if (keramiks.get(w).getId_keramik().equals(idk)) {
                keramiks.get(w).setTerjual(keramiks.get(w).getTerjual() + jum);

                Collections.sort(keramiks, new Comparator<Keramik>() {
                    @Override
                    public int compare(Keramik o1, Keramik o2) {
                        return (o2.getTerjual() - o1.getTerjual());
                    }
                });
                if (recyclerView.getAdapter() != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
                return;
            }
        }
    }

    public class KerAdap extends RecyclerView.Adapter<KerAdap.KerHolder> {
        @NonNull
        @Override
        public KerAdap.KerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KerAdap.KerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_selling, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull KerAdap.KerHolder holder, int position) {
            holder.draw(keramiks.get(position));
        }

        @Override
        public int getItemCount() {
            return keramiks.size();
        }

        public class KerHolder extends RecyclerView.ViewHolder {
            public KerHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Keramik data) {
                itemView.findViewById(R.id.item_my_ticket).setBackgroundResource(data.getStock() < 10 ? R.color.Merah : R.color.Abuabu);
                ((TextView) itemView.findViewById(R.id.txt_list_id)).setText(data.getId_keramik());
                ((TextView) itemView.findViewById(R.id.txt_list_nama)).setText(data.getNama_keramik());
                ((TextView) itemView.findViewById(R.id.terjual)).setText(data.getTerjual() + "");
                ((TextView) itemView.findViewById(R.id.txt_list_harga)).setText(TextUtil.formatCurrencyIdn(data.getHarga_jual()));
                ((TextView) itemView.findViewById(R.id.txt_list_stock)).setText(data.getStock() + " ");

                ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText("-");
                ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText("-");
                ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText("-");

                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(data.getId_kategori()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kategori k = dataSnapshot.getValue(Kategori.class);
                        if (k != null) {
                            ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText(k.getKategori_keramik());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(data.getId_kategori()).child(data.getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Ukuran u = dataSnapshot.getValue(Ukuran.class);
                        if (u != null) {
                            ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText(u.getNama_ukuran());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference().child("data_merk").child(data.getId_merk()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Merk m = dataSnapshot.getValue(Merk.class);
                        if (m != null) {
                            ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText(m.getNama_merk());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Picasso.get().load(data.getUrl_gambar())
                        .into((ImageView) itemView.findViewById(R.id.image_keramik));

            }
        }
    }
}
