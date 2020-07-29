package space.badboyin.smap.ActivityAdmin.UbahData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.dialogs.LoadingDialog;

public class DetailUbahDataKeramikActivity extends AppCompatActivity {
    public static final String TAG = DetailUbahDataKeramikActivity.class.getSimpleName();
    private Keramik keramik;

    private EditText nama, hargaJual, stok, id_keramik;

    private Button btn_create, btn_add_picture, btn_kembali_menu;
    private ImageView gambar_keramik;
    private Spinner spinnerjenis, spinnerukuran, spinnermerk;
    private Uri photo_location;
    private Integer photo_max = 1;


    private StorageReference storage_upload;

    private List<Kategori> kategoris = new ArrayList<>();
    private List<Ukuran> ukurans = new ArrayList<>();
    private List<Merk> merks = new ArrayList<>();
    private LoadingDialog loading;
    boolean okkat, okmerk, okuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ubah_data_keramik);
        String key = getIntent().getStringExtra(TAG);
        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keramik = dataSnapshot.getValue(Keramik.class);
                load();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        gambar_keramik = findViewById(R.id.img_keramik);

        nama = findViewById(R.id.txt_input_nama);
        hargaJual = findViewById(R.id.txt_input_harga_jual);
        stok = findViewById(R.id.txt_input_stok);
        id_keramik = findViewById(R.id.txt_input_id);
        spinnerjenis = findViewById(R.id.spinner_jenis_keramik);
        spinnerukuran = findViewById(R.id.spinner_ukuran_keramik);
        spinnermerk = findViewById(R.id.spinner_merk);


        btn_create = findViewById(R.id.btn_tambah_data);
        btn_add_picture = findViewById(R.id.btn_pic_keramik);
        btn_kembali_menu = findViewById(R.id.btn_kembali);
        btn_kembali_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btn_add_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPhoto();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan();
            }
        });
    }

    private void simpan() {
        final String field_nama = nama.getText().toString();
        final String field_harga_jual = hargaJual.getText().toString();
        final String field_stok = stok.getText().toString();
        final String field_id = id_keramik.getText().toString();

        if (field_id.isEmpty()) {
            Toast.makeText(this, "SKU Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (field_nama.isEmpty()) {
            Toast.makeText(this, "Nama Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (field_harga_jual.isEmpty()) {
            Toast.makeText(this, "Harga Jual Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (field_stok.isEmpty()) {
            Toast.makeText(this, "Stock Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (kategoris.isEmpty()) {
            Toast.makeText(this, "Jenis Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (merks.isEmpty()) {
            Toast.makeText(this, "Merk Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else if (ukurans.isEmpty()) {
            Toast.makeText(this, "Ukuran Keramik Masih Kosong", Toast.LENGTH_LONG).show();
        } else {
            keramik.setHarga_jual(Double.parseDouble(hargaJual.getText().toString()));
            keramik.setStock(Integer.parseInt(stok.getText().toString()));
            keramik.setNama_keramik(nama.getText().toString());
            keramik.setId_kategori(kategoris.get(spinnerjenis.getSelectedItemPosition()).getId_kategori());
            keramik.setId_ukuran(ukurans.get(spinnerukuran.getSelectedItemPosition()).getId_ukuran());
            keramik.setId_merk(merks.get(spinnermerk.getSelectedItemPosition()).getId_merk());

            loading = new LoadingDialog(this);
            loading.show();
            FirebaseDatabase.getInstance().getReference().child("data_keramik").child(keramik.getId_keramik()).setValue(keramik).
                    addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (photo_location != null)
                                uploadImage(keramik);
                            else {
                                Toast.makeText(DetailUbahDataKeramikActivity.this, "Berhasil menambah keramik !", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                onBackPressed();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.dismiss();
                }
            });

        }
    }

    private void load() {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
        loading = new LoadingDialog(this);
        loading.show();
        okkat = false;
        okmerk = false;
        okuk = false;

        FirebaseDatabase.getInstance().getReference().child("data_kategori").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kategoris.clear();
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    kategoris.add(category.getValue(Kategori.class));
                }
                initSpinnerKategori();
                okkat = true;
                if (okkat && okmerk && okuk)
                    init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("data_ukuran").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ukurans.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Ukuran u = ds.getValue(Ukuran.class);
                            if (u != null) ukurans.add(u);
                        }
                        initSpinnerUkuran();
                        okuk = true;
                        if (okkat && okmerk && okuk)
                            init();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
                initSpinnerMerk();
                okmerk = true;
                if (okkat && okmerk && okuk)
                    init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
        id_keramik.setText(keramik.getId_keramik());
        nama.setText(keramik.getNama_keramik());
        hargaJual.setText(keramik.getHarga_jual() + "");
        stok.setText(keramik.getStock() + "");
        Picasso.get().load(keramik.getUrl_gambar())
                .into(gambar_keramik);

    }

    private void initSpinnerKategori() {
        ArrayList<String> name_list = new ArrayList<>();
        int pos = 0;
        for (int w = 0; w < kategoris.size(); w++) {
            if (kategoris.get(w).getId_kategori().equals(keramik.getId_kategori())) {
                pos = w;
            }
            name_list.add(kategoris.get(w).getKategori_keramik());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name_list);
        spinnerjenis.setAdapter(adapter);
        spinnerjenis.setSelection(pos);
    }

    private void initSpinnerMerk() {
        ArrayList<String> name_list = new ArrayList<>();
        int pos = 0;
        for (int w = 0; w < merks.size(); w++) {
            if (merks.get(w).getId_merk().equals(keramik.getId_merk())) {
                pos = w;
            }
            name_list.add(merks.get(w).getNama_merk());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name_list);
        spinnermerk.setAdapter(adapter);
        spinnermerk.setSelection(pos);
    }

    private void initSpinnerUkuran() {
        int pos = 0;
        ArrayList<String> name_list = new ArrayList<>();
        for (int w = 0; w < ukurans.size(); w++) {
            if (ukurans.get(w).getId_ukuran().equals(keramik.getId_ukuran())) {
                pos = w;
            }
            name_list.add(ukurans.get(w).getNama_ukuran());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name_list);
        spinnerukuran.setAdapter(adapter);
        spinnerukuran.setSelection(pos);
    }

    private void uploadImage(final Keramik keramik) {

        storage_upload = FirebaseStorage.getInstance().getReference()
                .child("gambar_keramik").child(keramik.getId_keramik());
        final StorageReference storageReference1 = storage_upload.child(System.currentTimeMillis() + "." + getFileExtension(photo_location));
        storageReference1.putFile(photo_location).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //  String uri_photo = uri.toString();
                        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(keramik.getId_keramik()).child("url_gambar").setValue(uri.toString());
                        Toast.makeText(DetailUbahDataKeramikActivity.this, "Berhasil menambah keramik !", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        onBackPressed();
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void findPhoto() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Picasso.get().load(photo_location).centerCrop().fit().into(gambar_keramik);
        }
    }

}
