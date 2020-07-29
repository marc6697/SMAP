package space.badboyin.smap.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import space.badboyin.smap.App;
import space.badboyin.smap.Model.DetailTransaksiPenjualan;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.DetailPenjualanActivity;
import space.badboyin.smap.utilities.TextUtil;

public class PenjualanRiwayatFragment extends Fragment {
    private RecyclerView recyclerView;
    private View view;
    private Calendar calendar = Calendar.getInstance();
    private String tanggal = "";
    private String cari = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_penjualan_riwayat, container, false);

        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RiAdap());
        view.findViewById(R.id.btn_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker v, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tanggal = getDate();
                        ((TextView)view.findViewById(R.id.btn_calendar)).setText(tanggal);
                        init();
                    }
                }, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
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

    private String getDate() {
        return calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.YEAR);
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
        List<TransaksiPenjualan> trans = new ArrayList<>();
        if (tanggal.isEmpty()) {
            trans.addAll(ori_transaksiPenjualans);
        } else {
            for (TransaksiPenjualan t : ori_transaksiPenjualans) {
                String[] dt = t.getTanggal_penjualan().split(" ");
                String[] d = dt[0].split("-");
                if (calendar.get(Calendar.YEAR) == Integer.parseInt(d[2]) && calendar.get(Calendar.MONTH) == Integer.parseInt(d[1]) - 1
                        && calendar.get(Calendar.DATE) == Integer.parseInt(d[0])) {
                    trans.add(t);
                }
            }
        }
        if (cari.isEmpty()) {
            transaksiPenjualans.addAll(trans);
        } else {
            cari = cari.toLowerCase();
            for (TransaksiPenjualan t : trans) {
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

    private final List<TransaksiPenjualan> transaksiPenjualans = new ArrayList<>();
    private final List<TransaksiPenjualan> ori_transaksiPenjualans = new ArrayList<>();

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
                itemView.findViewById(R.id.status).setVisibility(data.getStatus().equals("SELESAI") ? View.GONE : View.VISIBLE);
                ((TextView) itemView.findViewById(R.id.status)).setText("Pending");
                ((TextView) itemView.findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getTotal_transaksi()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.please().setTransaksiPenjualan(data);
                        Intent i = new Intent(getActivity(), DetailPenjualanActivity.class);
                        startActivity(i);
                    }
                });
            }
        }
    }
}
