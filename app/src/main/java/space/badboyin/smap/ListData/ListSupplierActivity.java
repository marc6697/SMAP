package space.badboyin.smap.ListData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.Sales;
import space.badboyin.smap.R;

public class ListSupplierActivity extends AppCompatActivity {

    private View header;
    private TextView textheader;
    private Animation topToBottom, bottomToTop, fade, fade_from_left, app_splash;
    private List<Sales> sales = new ArrayList<>();
    private List<Sales> ori_sales = new ArrayList<>();
    private String cari = "";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_supplier);

        header = findViewById(R.id.header);
        textheader = findViewById(R.id.message);

        bottomToTop = AnimationUtils.loadAnimation(this, R.anim.bottomtotop);
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        topToBottom = AnimationUtils.loadAnimation(this, R.anim.toptothebottom);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade_animation);
        fade_from_left = AnimationUtils.loadAnimation(this, R.anim.fade_from_left);


        header.setAnimation(topToBottom);
        textheader.setAnimation(topToBottom);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SupAdap());
        ((EditText) findViewById(R.id.cari)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cari = s.toString();
                cari();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("data_salesman").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ori_sales.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ori_sales.add(ds.getValue(Sales.class));
                }
                cari();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cari() {
        sales.clear();
        if (cari.isEmpty())
            sales.addAll(ori_sales);
        else {
            cari = cari.toLowerCase();
            for (Sales s : ori_sales) {
                if (s.getNama_perusahaan().toLowerCase().contains(cari) || s.getNama_sales().toLowerCase().contains(cari)
                        || s.getNomor_sales().toLowerCase().contains(cari)) {
                    sales.add(s);
                }
            }
        }
        if (recyclerView.getAdapter() != null) recyclerView.getAdapter().notifyDataSetChanged();
    }

    public class SupAdap extends RecyclerView.Adapter<SupAdap.SupHoder> {
        @NonNull
        @Override
        public SupHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SupHoder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout
                            .item_suppliers, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SupHoder holder, int position) {
            holder.draw(sales.get(position));
        }

        @Override
        public int getItemCount() {
            return sales.size();
        }

        public class SupHoder extends RecyclerView.ViewHolder {
            public SupHoder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final Sales data) {
                ((TextView) itemView.findViewById(R.id.text_sales_no)).setText(data.getNomor_sales());
                ((TextView) itemView.findViewById(R.id.text_sales_nama)).setText(data.getNama_sales());
                ((TextView) itemView.findViewById(R.id.text_sales_pt)).setText(data.getNama_perusahaan());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent dialPhoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + data.getNomor_sales()));
                        startActivity(dialPhoneIntent);
                    }
                });
            }
        }
    }
}
