package space.badboyin.smap.Transaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import space.badboyin.smap.Activity.ScanQRCodeActivity;
import space.badboyin.smap.App;
import space.badboyin.smap.ListData.ListKeramikActivitynew;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.utilities.TextUtil;

public class BarangPickerActivity extends AppCompatActivity {

    private Spinner spinner;
    private DatabaseReference reference;
    private List<Kategori> kategoris = new ArrayList<>();
    private List<Keramik> keramiks = new ArrayList<>();
    private List<Keramik> ori_keramiks = new ArrayList<>();
    private Kategori kategori;
    private RecyclerView recyclerView;
    private String cari = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_picker);
        spinner = findViewById(R.id.spinner_kategori);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new KerAdap());
        reference = FirebaseDatabase.getInstance().getReference().child("data_kategori");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> data = new ArrayList<>();
                data.add("- Semua Kategori -");
                kategoris.clear();
                for (DataSnapshot jenisSnapshot : dataSnapshot.getChildren()) {
                    Kategori k = jenisSnapshot.getValue(Kategori.class);
                    data.add(k.getKategori_keramik());
                    kategoris.add(k);
                }

                // Spinner areaSpinner = (Spinner) findViewById(R.id.spinner);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(BarangPickerActivity.this, android.R.layout.simple_spinner_item, data);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    kategori = null;
                else
                    kategori = kategoris.get(position - 1);
                initList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((EditText) findViewById(R.id.cari)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cari = s.toString();
                initList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.btn_scan_cari).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarangPickerActivity.this, ScanQRCodeActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_keramik").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ori_keramiks.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ori_keramiks.add(ds.getValue(Keramik.class));
                }
                if (App.please().getBarcode() != null)
                    cari = App.please().getBarcode();
                App.please().setBarcode(null);
                ((EditText) findViewById(R.id.cari)).setText(cari);
                initList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initList() {
        if (recyclerView.getAdapter() == null) return;
        List<Keramik> data = new ArrayList<>();
        if (kategori == null)
            data.addAll(ori_keramiks);
        else
            for (Keramik k : ori_keramiks) {
                if (k.getId_kategori().equals(kategori.getId_kategori())) {
                    data.add(k);
                }
            }
        keramiks.clear();
        cari = cari.toLowerCase();
        if (cari.isEmpty())
            keramiks.addAll(data);
        else for (Keramik k : data) {
            if (k.getNama_keramik().toLowerCase().contains(cari) || k.getId_keramik().toLowerCase().contains(cari)) {
                keramiks.add(k);
            }
        }
        Collections.sort(keramiks, new Comparator<Keramik>() {
            @Override
            public int compare(Keramik o1, Keramik o2) {
                return o2.getStock() - o1.getStock();
            }
        });
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class KerAdap extends RecyclerView.Adapter<KerAdap.KerHolder> {
        @NonNull
        @Override
        public KerAdap.KerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KerAdap.KerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keramik, parent, false));
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
                ((TextView) itemView.findViewById(R.id.txt_list_harga)).setText(TextUtil.formatCurrencyIdn(data.getHarga_jual()));
                ((TextView) itemView.findViewById(R.id.txt_list_stock)).setText(data.getStock() + " ");
                ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText("-");
                ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText("-");
                ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText("-");
                if (data.getKategori() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText(data.getKategori().getKategori_keramik());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("data_kategori").child(data.getId_kategori()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Kategori k = dataSnapshot.getValue(Kategori.class);
                            if (k != null) {
                                data.setKategori(k);
                                ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText(k.getKategori_keramik());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if (data.getUkuran() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText(data.getUkuran().getNama_ukuran());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(data.getId_kategori()).child(data.getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Ukuran u = dataSnapshot.getValue(Ukuran.class);
                            if (u != null) {
                                data.setUkuran(u);
                                ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText(u.getNama_ukuran());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if (data.getMerk() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText(data.getMerk().getNama_merk());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("data_merk").child(data.getId_merk()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Merk m = dataSnapshot.getValue(Merk.class);
                            if (m != null) {
                                data.setMerk(m);
                                ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText(m.getNama_merk());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                Picasso.get().load(data.getUrl_gambar())
                        .into((ImageView) itemView.findViewById(R.id.image_keramik));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        App.please().setKeramik(data);
                        onBackPressed();

                    }
                });
            }
        }
    }
}
