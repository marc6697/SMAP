package space.badboyin.smap.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import space.badboyin.smap.Model.DetailTransaksiPembelian;
import space.badboyin.smap.Model.Laporan;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.LaporanActivity;
import space.badboyin.smap.utilities.TextUtil;

public class LaporanOrderFragment extends Fragment {

    private View view;
    private LaporanActivity activity;

    private RecyclerView recyclerView;
    private List<TransaksiPembelian> ori_Transaksi_pembelians = new ArrayList<>();
    private final List<TransaksiPembelian> transaksiPembelians = new ArrayList<>();
    private final List<Laporan> laporans = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    private Button btn_bulan;
    private Button btn_tahun;
    int mode = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_laporan_order, container, false);
        activity = (LaporanActivity) getActivity();
        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new OrderAdap());
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
                        filter();
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
                filter();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        builder.showYearOnly().build().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_pembelian").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ori_Transaksi_pembelians.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransaksiPembelian transaksiPembelian = ds.getValue(TransaksiPembelian.class);
                    if (transaksiPembelian != null && transaksiPembelian.getStatus_pembeian().equals("SELESAI"))
                        ori_Transaksi_pembelians.add(ds.getValue(TransaksiPembelian.class));
                }
                filter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void filter() {
        if (recyclerView == null || recyclerView.getAdapter() == null) return;

        if (mode == 1) {
            btn_bulan.setText(TextUtil.formatMonthYear(calendar.getTimeInMillis()));
            btn_tahun.setText("Tahunan");
            ((TextView) view.findViewById(R.id.tgl_txt)).setText("Tanggal");
        } else if (mode == 2) {
            btn_tahun.setText(TextUtil.formatYear(calendar.getTimeInMillis()));
            btn_bulan.setText("Bulanan");
            ((TextView) view.findViewById(R.id.tgl_txt)).setText("Bulan");
        }

        transaksiPembelians.clear();
        for (TransaksiPembelian t : ori_Transaksi_pembelians) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(t.getTime());
            Log.d("time", "" + t.getTime());
            if (mode == 1 && cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                transaksiPembelians.add(t);
            } else if (mode == 2 && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                transaksiPembelians.add(t);
            }
        }
        Collections.sort(transaksiPembelians, new Comparator<TransaksiPembelian>() {
            @Override
            public int compare(TransaksiPembelian o1, TransaksiPembelian o2) {
                return (int) (o1.getTime() - o2.getTime());
            }
        });
        countTotal();
    }


    private double total_transaksi = 0;

    private void countTotal() {
        total_transaksi = 0;
        for (TransaksiPembelian t : transaksiPembelians) {
            total_transaksi += t.getTotal_harga();
        }
        activity.setOrder("Total Rp. " + TextUtil.formatCurrencyIdn(total_transaksi));

        laporans.clear();
        if (transaksiPembelians.isEmpty() && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        for (final TransaksiPembelian t : transaksiPembelians) {
            FirebaseDatabase.getInstance().getReference().child("data_detail_pembelian").child(t.getId_pembelian()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int jum_b = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        DetailTransaksiPembelian dt = ds.getValue(DetailTransaksiPembelian.class);
                        if (dt != null) {
                            jum_b += dt.getJumlah_beli();
                        }
                    }
                    if (mode == 1) {
                        addToLaporanHarian(t, jum_b);
                    } else if (mode == 2) {
                        addToLaporanTahunan(t, jum_b);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void addToLaporanHarian(TransaksiPembelian t, int jum_b) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t.getTime());
        for (int w = 0; w < laporans.size(); w++) {
            if (laporans.get(w).getTanggal() == cal.get(Calendar.DAY_OF_YEAR) && laporans.get(w).getTahun() == cal.get(Calendar.YEAR)) {
                laporans.get(w).setJumlah_barang(laporans.get(w).getJumlah_barang() + jum_b);
                laporans.get(w).setOrder(t);
                if (recyclerView.getAdapter() != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
                return;
            }
        }
        Laporan laporan = new Laporan();
        laporan.setOrder(t);
        laporan.setJumlah_barang(jum_b);
        laporan.setTanggal(cal.get(Calendar.DAY_OF_YEAR));
        laporan.setTahun(cal.get(Calendar.YEAR));
        laporan.setTanggal_long(cal.getTimeInMillis());
        laporans.add(laporan);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void addToLaporanTahunan(TransaksiPembelian t, int jum_b) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t.getTime());
        for (int w = 0; w < laporans.size(); w++) {
            if (laporans.get(w).getBulan() == cal.get(Calendar.MONTH) && laporans.get(w).getTahun() == cal.get(Calendar.YEAR)) {
                laporans.get(w).setJumlah_barang(laporans.get(w).getJumlah_barang() + jum_b);
                laporans.get(w).setOrder(t);
                if (recyclerView.getAdapter() != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
                return;
            }
        }
        Laporan laporan = new Laporan();
        laporan.setOrder(t);
        laporan.setJumlah_barang(jum_b);
        laporan.setTanggal(cal.get(Calendar.DAY_OF_YEAR));
        laporan.setTahun(cal.get(Calendar.YEAR));
        laporan.setBulan(cal.get(Calendar.MONTH));
        laporan.setTanggal_long(cal.getTimeInMillis());
        laporans.add(laporan);
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public class OrderAdap extends RecyclerView.Adapter<OrderAdap.OrderHolder> {
        @NonNull
        @Override
        public OrderAdap.OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OrderAdap.OrderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan_2, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdap.OrderHolder holder, int position) {
            holder.draw(laporans.get(position), position % 2 == 1);
        }

        @Override
        public int getItemCount() {
            return laporans.size();
        }

        public class OrderHolder extends RecyclerView.ViewHolder {
            public OrderHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Laporan data, boolean even) {
                Calendar calen = Calendar.getInstance();
                calen.setTimeInMillis(calendar.getTimeInMillis());
                itemView.setBackgroundColor(even ? Color.WHITE : activity.getResources().getColor(R.color.grey));
                calen.set(Calendar.DATE, data.getTanggal());
                if (mode == 1) {
                    ((TextView) itemView.findViewById(R.id.tanggal)).setText(TextUtil.formatTanggal(data.getTanggal_long()));
                } else if (mode == 2) {
                    ((TextView) itemView.findViewById(R.id.tanggal)).setText(TextUtil.formatMonthYear(data.getTanggal_long()));
                }
                ((TextView) itemView.findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getTotal()));
                ((TextView) itemView.findViewById(R.id.jumlah_transaksi)).setText(data.getJumlah_transaksi() + "");
                ((TextView) itemView.findViewById(R.id.jumlah_barang)).setText(data.getJumlah_barang() + "");

            }
        }
    }
}
