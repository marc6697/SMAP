package space.badboyin.smap.ListData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.UserDialog;

public class ListUserActivity extends AppCompatActivity {

    private View header;
    private TextView textheader;
    private Animation topToBottom, bottomToTop, fade, fade_from_left, app_splash;
    private List<User> users = new ArrayList<>();
    private List<User> ori_users = new ArrayList<>();
    private String cari = "";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

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
        refresh();
    }

    private void refresh() {
        FirebaseDatabase.getInstance().getReference().child("users_data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ori_users.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null && !user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        ori_users.add(user);
                    }
                }
                cari();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void save(final User user) {
        Log.d("Save", "Save");
        FirebaseDatabase.getInstance().getReference().child("users_data").child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    refresh();
                } else {
                    Toast.makeText(ListUserActivity.this, "Error, Silahkan ulang kembali!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cari() {
        users.clear();
        if (cari.isEmpty())
            users.addAll(ori_users);
        else {
            cari = cari.toLowerCase();
            for (User s : ori_users) {
                if (s.getEmail().toLowerCase().contains(cari) || s.getNama().toLowerCase().contains(cari)
                        || s.getNo_telp().toLowerCase().contains(cari) || s.getPosisi().toLowerCase().contains(cari)) {
                    users.add(s);
                }
            }
        }
        if (recyclerView.getAdapter() != null) recyclerView.getAdapter().notifyDataSetChanged();
    }

    public class SupAdap extends RecyclerView.Adapter<SupAdap.SupHoder> {
        @NonNull
        @Override
        public SupAdap.SupHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SupAdap.SupHoder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout
                            .item_user, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SupAdap.SupHoder holder, int position) {
            holder.draw(users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public class SupHoder extends RecyclerView.ViewHolder {
            public SupHoder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final User data) {
                Picasso.get().load(data.getUrl_avatar())
                        .into((ImageView) itemView.findViewById(R.id.img));

                ((TextView) itemView.findViewById(R.id.status)).setText(data.getStatus());
                ((TextView) itemView.findViewById(R.id.email)).setText(data.getEmail());
                ((TextView) itemView.findViewById(R.id.posisi)).setText(data.getPosisi());
                ((TextView) itemView.findViewById(R.id.nama)).setText(data.getNama());
                ((TextView) itemView.findViewById(R.id.no_telp)).setText(data.getNo_telp());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.getPosisi() == null) {
                            Toast.makeText(ListUserActivity.this, "User belum lengkap!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UserDialog dialog = new UserDialog(v.getContext(), data, new UserDialog.OnUserCallback() {
                            @Override
                            public void onResult(String role, boolean active) {
                                data.setPosisi(role);
                                data.setStatus(active ? "Aktif" : "Tidak Aktif");
                                save(data);
                            }
                        });
                        dialog.show();
                    }
                });
            }
        }
    }
}
