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
import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.R;
import space.badboyin.smap.Transaksi.DetailPembelianActivity;
import space.badboyin.smap.Transaksi.RiwayatOrderActivity;
import space.badboyin.smap.Transaksi.TransaksiPembelianActivity;
import space.badboyin.smap.utilities.TextUtil;

public class OrderRiwayatFragment extends Fragment {
    private View view;

    private RecyclerView recyclerView;
    private String cari = "";
    private List<TransaksiPembelian> ori_Transaksi_pembelians = new ArrayList<>();
    private final List<TransaksiPembelian> transaksiPembelians = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private String tanggal = "";

    public void set(List<TransaksiPembelian> transaksiPembelians) {
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
        view = inflater.inflate(R.layout.fragment_order_riwayat, container, false);
        recyclerView = view.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new OrderAdap());
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
                        filter();
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
                filter();
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
        filter();
    }

    private void filter() {
        if (recyclerView == null || recyclerView.getAdapter() == null) return;
        transaksiPembelians.clear();
        List<TransaksiPembelian> ors = new ArrayList<>();
        if (cari.isEmpty()) {
            ors.addAll(ori_Transaksi_pembelians);
        } else {
            cari = cari.toLowerCase();
            for (TransaksiPembelian o : ori_Transaksi_pembelians) {
                if (o.getId_pembelian().toLowerCase().contains(cari) || o.getTanggal_pembelian().toLowerCase().contains(cari)
                        || o.getStatus_pembeian().toLowerCase().contains(cari) || o.getId_sales().toLowerCase().contains(cari)) {
                    ors.add(o);
                }
            }
        }
        if (tanggal.isEmpty()) {
            transaksiPembelians.addAll(ors);
        } else {
            for (TransaksiPembelian o : ors) {
                String[] dt = o.getTanggal_pembelian().split(" ");
                String[] d = dt[0].split("-");
                if (calendar.get(Calendar.YEAR) == Integer.parseInt(d[2]) && calendar.get(Calendar.MONTH) == Integer.parseInt(d[1]) - 1
                        && calendar.get(Calendar.DATE) == Integer.parseInt(d[0])) {
                    transaksiPembelians.add(o);
                }
            }
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    public class OrderAdap extends RecyclerView.Adapter<OrderAdap.OrderHolder> {
        @NonNull
        @Override
        public OrderAdap.OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OrderAdap.OrderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderx, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OrderAdap.OrderHolder holder, int position) {
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
                ((TextView) itemView.findViewById(R.id.tanggal)).setText(data.getTanggal_pembelian());
                ((TextView) itemView.findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getTotal_harga()));
                ((TextView) itemView.findViewById(R.id.no_order)).setText(data.getId_pembelian());

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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.please().setTransaksiPembelian(data);
                        itemView.getContext().startActivity(new Intent(itemView.getContext(), DetailPembelianActivity.class));
                    }
                });
            }
        }
    }
}
