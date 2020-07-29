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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.R;

public class UbahDataKategoriActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Kategori> kategoris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data_kategori);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new KateAdapter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_kategori").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kategoris.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    kategoris.add(ds.getValue(Kategori.class));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class KateAdapter extends RecyclerView.Adapter<KateAdapter.KateHolder> {
        @NonNull
        @Override
        public KateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KateHolder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout
                            .item_kategori_keramik, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull KateHolder holder, int position) {
            holder.draw(kategoris.get(position));
        }

        @Override
        public int getItemCount() {
            return kategoris.size();
        }

        public class KateHolder extends RecyclerView.ViewHolder {
            public KateHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Kategori data) {
                ((TextView) itemView.findViewById(R.id.text_kategori_keramik)).setText(data.getKategori_keramik());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UbahDataKategoriActivity.this, DetailUbahDataKategoriActivity.class);
                        intent.putExtra(DetailUbahDataKategoriActivity.EXTRA_KATEGORI, data.getId_kategori());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}