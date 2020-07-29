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

import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.R;

public class UbahDataSalesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_data_sales);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SupAdapter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_salesman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sales.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    sales.add(ds.getValue(Sales.class));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private RecyclerView recyclerView;
    private List<Sales> sales = new ArrayList<>();

    public class SupAdapter extends RecyclerView.Adapter<SupAdapter.SupHolder> {
        @NonNull
        @Override
        public SupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SupHolder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout
                            .item_suppliers, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SupHolder holder, int position) {
            holder.draw(sales.get(position));
        }

        @Override
        public int getItemCount() {
            return sales.size();
        }

        public class SupHolder extends RecyclerView.ViewHolder {
            public SupHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Sales data) {
                ((TextView)itemView.findViewById(R.id.text_sales_nama)).setText(data.getNama_sales());
                ((TextView)itemView.findViewById(R.id.text_sales_pt)).setText(data.getNama_perusahaan());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(UbahDataSalesActivity.this, DetailUbahDataSalesActivity.class);
                        i.putExtra(DetailUbahDataSalesActivity.TAG, data.getId_sales());
                        startActivity(i);
                    }
                });
            }
        }
    }
}
