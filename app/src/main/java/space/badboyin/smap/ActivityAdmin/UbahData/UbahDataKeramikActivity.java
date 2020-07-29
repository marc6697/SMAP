package space.badboyin.smap.ActivityAdmin.UbahData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.utilities.TextUtil;

public class UbahDataKeramikActivity extends AppCompatActivity {
    private Spinner spinner_kategori;
    private RecyclerView recyclerView;
    private Kategori kategori;
    private List<Keramik> keramiks = new ArrayList<>();
    private List<Keramik> ori_keramiks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data_keramik);
        spinner_kategori = findViewById(R.id.spinner_jenis_keramik);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new KerAdap());
        FirebaseDatabase.getInstance().getReference().child("data_keramik").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final List<Kategori> kategoris = new ArrayList<>();
                final List<String> data = new ArrayList<>();
                data.add("- Semua Kategori -");
                for (DataSnapshot jenisSnapshot : dataSnapshot.getChildren()) {
                    Kategori kategori = jenisSnapshot.getValue(Kategori.class);
                    kategoris.add(kategori);
                    data.add(kategori.getKategori_keramik());
                }
                ArrayAdapter<String> kategoriAdapter = new ArrayAdapter<String>(UbahDataKeramikActivity.this, android.R.layout.simple_spinner_item, data);
                kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                if (!kategoris.isEmpty()) {
                    kategori = kategoris.get(0);
                    initList();
                }
                spinner_kategori.setAdapter(kategoriAdapter);
                spinner_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.putih));
                        if (position > 0) {
                            kategori = kategoris.get(position - 1);
                        } else kategori = null;
                        initList();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                initList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initList() {
        if (recyclerView.getAdapter() == null) return;
        keramiks.clear();
        if (kategori != null)
            for (Keramik k : ori_keramiks) {
                if (k.getId_kategori().equals(kategori.getId_kategori())) {
                    keramiks.add(k);
                }
            }
        else keramiks.addAll(ori_keramiks);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public class KerAdap extends RecyclerView.Adapter<KerAdap.KerHolder> {
        @NonNull
        @Override
        public KerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keramik, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull KerHolder holder, int position) {
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
                ((TextView) itemView.findViewById(R.id.txt_list_id)).setText(data.getId_keramik());
                ((TextView) itemView.findViewById(R.id.txt_list_nama)).setText(data.getNama_keramik());
                ((TextView) itemView.findViewById(R.id.txt_list_harga)).setText(TextUtil.formatCurrencyIdn(data.getHarga_jual()));
                ((TextView) itemView.findViewById(R.id.txt_list_stock)).setText(data.getStock()+"");
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
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(UbahDataKeramikActivity.this, DetailUbahDataKeramikActivity.class);
                        i.putExtra(DetailUbahDataKeramikActivity.TAG, data.getId_keramik());
                        startActivity(i);
                    }
                });
            }
        }
    }
}
