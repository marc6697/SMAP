package space.badboyin.smap.ListData;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import space.badboyin.smap.Activity.ScanQRCodeActivity;
import space.badboyin.smap.App;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.utilities.TextUtil;

public class ListKeramikActivitynew extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    DatabaseReference reference;

    public static String item_string = "";
    View header;
    TextView textheader;
    Animation topToBottom, bottomToTop, fade, fade_from_left, app_splash;

    private List<Kategori> kategoris = new ArrayList<>();
    private List<Keramik> keramiks = new ArrayList<>();
    private List<Keramik> ori_keramiks = new ArrayList<>();
    private Kategori kategori;
    private RecyclerView recyclerView;
    private String cari = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_keramik_new);
        spinner = findViewById(R.id.spinner_kategori);
        header = findViewById(R.id.header);
        textheader = findViewById(R.id.message);
        //loadanim
        bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        topToBottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade_animation);
        fade_from_left = AnimationUtils.loadAnimation(this, R.anim.fade_from_left);
        spinner.setAnimation(fade);
        header.setAnimation(topToBottom);
        textheader.setAnimation(topToBottom);
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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(ListKeramikActivitynew.this, android.R.layout.simple_spinner_item, data);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(this);
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
                startActivity(new Intent(ListKeramikActivitynew.this, ScanQRCodeActivity.class));
            }
        });
    }

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
                return o1.getStock() - o2.getStock();
            }
        });
        recyclerView.getAdapter().notifyDataSetChanged();
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
                ((TextView) itemView.findViewById(R.id.txt_list_stock)).setText(data.getStock() + "");

                ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText("-");
                ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText("-");
                ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText("-");
                if (data.getKategori() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText(data.getKategori().getKategori_keramik());
                } else
                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(data.getId_kategori()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kategori k = dataSnapshot.getValue(Kategori.class);
                        if (k != null) {
                            ((TextView) itemView.findViewById(R.id.txt_list_jenis)).setText(k.getKategori_keramik());
                            data.setKategori(k);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (data.getUkuran()!= null) {
                    ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText(data.getUkuran().getNama_ukuran());
                } else
                FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(data.getId_kategori()).child(data.getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Ukuran u = dataSnapshot.getValue(Ukuran.class);
                        if (u != null) {
                            ((TextView) itemView.findViewById(R.id.txt_list_ukuran)).setText(u.getNama_ukuran());
                            data.setUkuran(u);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (data.getMerk() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_list_merk)).setText(data.getMerk().getNama_merk());
                } else
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
                Picasso.get().load(data.getUrl_gambar())
                        .into((ImageView) itemView.findViewById(R.id.image_keramik));

            }
        }
    }
}