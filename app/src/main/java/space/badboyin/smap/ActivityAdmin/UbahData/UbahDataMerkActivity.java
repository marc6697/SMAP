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

import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.R;

public class UbahDataMerkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Merk> merks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data_merk);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MerkAdapter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_merk").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                merks.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Merk m = ds.getValue(Merk.class);
                    if (m != null) {
                        merks.add(m);
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class MerkAdapter extends RecyclerView.Adapter<MerkAdapter.MerkHolder> {

        @NonNull
        @Override
        public MerkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MerkHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merk, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MerkHolder holder, int position) {
            holder.draw(merks.get(position));
        }

        @Override
        public int getItemCount() {
            return merks.size();
        }

        public class MerkHolder extends RecyclerView.ViewHolder {
            public MerkHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Merk merk) {
                ((TextView) itemView.findViewById(R.id.text)).setText(merk.getNama_merk());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UbahDataMerkActivity.this, DetailUbahDataMerkActivity.class);
                        intent.putExtra(DetailUbahDataMerkActivity.EXTRA_MERK, merk.getId_merk());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
