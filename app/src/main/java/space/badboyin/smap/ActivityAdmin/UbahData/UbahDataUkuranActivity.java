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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;

public class UbahDataUkuranActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data_ukuran);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new UkuranAdapter());
        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    private List<Ukuran> ukurans = new ArrayList<>();

    private void initList() {
        FirebaseDatabase.getInstance().getReference().child("data_ukuran").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ukurans.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Ukuran u = ds.getValue(Ukuran.class);
                    if (u != null) {
                        ukurans.add(u);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class UkuranAdapter extends RecyclerView.Adapter<UkuranAdapter.UkuranHolder> {
        @NonNull
        @Override
        public UkuranHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UkuranHolder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout
                            .item_ukuran_keramik, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UkuranHolder holder, int position) {
            holder.draw(ukurans.get(position));
        }

        @Override
        public int getItemCount() {
            return ukurans.size();
        }

        public class UkuranHolder extends RecyclerView.ViewHolder {
            public UkuranHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Ukuran ukuran) {
                ((TextView) itemView.findViewById(R.id.text_ukuran_keramik)).setText(ukuran.getNama_ukuran());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UbahDataUkuranActivity.this, DetailUbahDataUkuranActivity.class);
                        intent.putExtra(DetailUbahDataUkuranActivity.EXTRA_UKURAN, ukuran.getId_ukuran());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}