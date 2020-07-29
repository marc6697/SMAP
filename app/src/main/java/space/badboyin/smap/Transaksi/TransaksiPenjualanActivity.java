package space.badboyin.smap.Transaksi;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import space.badboyin.smap.App;
import space.badboyin.smap.DeviceListActivity;
import space.badboyin.smap.Model.DetailTransaksiPenjualan;
import space.badboyin.smap.Model.Kategori;
import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.Merk;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.Model.Ukuran;
import space.badboyin.smap.R;
import space.badboyin.smap.UnicodeFormatter;
import space.badboyin.smap.dialogs.DetailTransaksiDialog;
import space.badboyin.smap.dialogs.LoadingDialog;
import space.badboyin.smap.utilities.TextUtil;


public class TransaksiPenjualanActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransaksiPenjualan transaksiPenjualan;
    private final List<DetailTransaksiPenjualan> details = new ArrayList<>();
    private Button simpan;
    private LoadingDialog loadingDialog;

    private class StockTemp {
        public StockTemp(int stock, String sku) {
            this.stock = stock;
            this.sku = sku;
        }

        int stock;
        String sku;
    }

    private final List<StockTemp> temps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_keramik);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new KerAdap());

        findViewById(R.id.tambah_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.please().setKeramik(null);
                startActivity(new Intent(TransaksiPenjualanActivity.this, BarangPickerActivity.class));
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TransaksiPenjualanActivity.this, TransaksiPenjualanActivity.class));
                finish();
            }
        });
        simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan();
            }
        });

        if (App.please().getTransaksiPenjualan() == null) {
            transaksiPenjualan = new TransaksiPenjualan();
            transaksiPenjualan.setId_penjualan("T1001" + System.currentTimeMillis());
        } else {
            transaksiPenjualan = App.please().getTransaksiPenjualan();
            App.please().setTransaksiPenjualan(null);
            ((EditText) findViewById(R.id.nama)).setText(transaksiPenjualan.getNama_pembeli());
            ((EditText) findViewById(R.id.alamat)).setText(transaksiPenjualan.getAlamat_pembeli());
            ((EditText) findViewById(R.id.telp)).setText(transaksiPenjualan.getNo_telp_pembeli());

            FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(transaksiPenjualan.getId_penjualan()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        details.add(ds.getValue(DetailTransaksiPenjualan.class));
                    }
                    createTemp();
                    if (recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
                    hitungTotal();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        ((TextView) findViewById(R.id.no_transaksi)).setText(transaksiPenjualan.getId_penjualan());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.please().getKeramik() != null) {
            addKeramik(App.please().getKeramik());
            App.please().setKeramik(null);
        }
    }


    private void createTemp() {
        temps.clear();
        for (DetailTransaksiPenjualan dp : details) {
            temps.add(new StockTemp(dp.getJumlah_jual(), dp.getId_keramik()));
        }
    }

    private void simpan() {
        String nama = ((EditText) findViewById(R.id.nama)).getText().toString();
        String alamat = ((EditText) findViewById(R.id.alamat)).getText().toString();
        String telp = ((EditText) findViewById(R.id.telp)).getText().toString();
        if (nama.isEmpty()) {
            Toast.makeText(this, "Nama Pelanggan tidak boleh kosong !", Toast.LENGTH_LONG).show();
        } else if (alamat.isEmpty()) {
            Toast.makeText(this, "Alamat Pelanggan tidak boleh kosong !", Toast.LENGTH_LONG).show();
        } else if (details.isEmpty()) {
            Toast.makeText(this, "Barang tidak boleh kosong !", Toast.LENGTH_LONG).show();
        } else if (telp.isEmpty()) {
            Toast.makeText(this, "Nomor Telepon Pelanggan tidak boleh kosong !", Toast.LENGTH_LONG).show();
        } else {
            transaksiPenjualan.setId_user(FirebaseAuth.getInstance().getCurrentUser().getUid());
            transaksiPenjualan.setTotal_transaksi(hitungTotal());
            if (transaksiPenjualan.getTanggal_penjualan() == null) {
                transaksiPenjualan.setTanggal_penjualan(TextUtil.formatTanggal(System.currentTimeMillis()));
                transaksiPenjualan.setStatus("PENDING");
            }
            transaksiPenjualan.setNama_pembeli(nama);
            transaksiPenjualan.setAlamat_pembeli(alamat);
            transaksiPenjualan.setNo_telp_pembeli(telp);
            if (loadingDialog != null && loadingDialog.isShowing())
                loadingDialog.dismiss();
            loadingDialog = new LoadingDialog(this);
            loadingDialog.show();
            FirebaseDatabase.getInstance().getReference().child("data_penjualan").child(transaksiPenjualan.getId_penjualan()).setValue(transaksiPenjualan)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pushDetail();
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(TransaksiPenjualanActivity.this, "Error. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    private void pushDetail() {
        FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(transaksiPenjualan.getId_penjualan()).setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            count = 0;
                            for (DetailTransaksiPenjualan dt : details) {
                                dt.setId_penjualan(transaksiPenjualan.getId_penjualan());
                                dt.setId_detail_penjualan(FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(dt.getId_penjualan()).push().getKey());
                                FirebaseDatabase.getInstance().getReference().child("data_detail_penjualan").child(dt.getId_penjualan()).child(dt.getId_detail_penjualan()).setValue(dt);
                                syncStock(dt);
                            }
                            loadingDialog.dismiss();
                            Toast.makeText(TransaksiPenjualanActivity.this, "TransaksiPenjualan Berhasil!", Toast.LENGTH_LONG).show();
                            askReceipt();
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(TransaksiPenjualanActivity.this, "Error. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void syncStock() {
        for (final StockTemp t : temps) {
            FirebaseDatabase.getInstance().getReference().child("data_keramik").child(t.sku).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Keramik k = dataSnapshot.getValue(Keramik.class);
                    if (k != null) {
                        k.setStock(k.getStock() + t.stock);
                        k.setTerjual(k.getTerjual() - t.stock);
                        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(k.getId_keramik()).setValue(k);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private int count;

    private void syncStock(final DetailTransaksiPenjualan d) {
        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(d.getId_keramik()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Keramik k = dataSnapshot.getValue(Keramik.class);
                if (k != null) {
                    int stock = d.getJumlah_jual();
                    for (StockTemp t : temps) {
                        if (t.sku.equals(d.getId_keramik())) {
                            stock -= d.getJumlah_jual();
                            temps.remove(t);
                            break;
                        }
                    }
                    if (stock != 0) {
                        k.setStock(k.getStock() - stock);
                        k.setTerjual(k.getTerjual() + stock);
                        FirebaseDatabase.getInstance().getReference().child("data_keramik").child(d.getId_keramik()).setValue(k);
                    }
                    count += 1;
                    if (count == details.size()) {
                        syncStock();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private AlertDialog dialog;

    private void askReceipt() {
        dialog = new AlertDialog.Builder(this).setTitle("Cetak Nota")
                .setMessage("Cetak Nota dari Transaksi ini ?")
                .setPositiveButton("Ya, Cetak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printReceipt();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).create();
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }


    private void addKeramik(final Keramik keramik) {
        new DetailTransaksiDialog(this, keramik, new DetailTransaksiDialog.OnDetailTransaksiListener() {
            @Override
            public void onResult(int jum) {
                DetailTransaksiPenjualan dp = new DetailTransaksiPenjualan();
                dp.setHarga_jual(keramik.getHarga_jual());
                dp.setJumlah_jual(jum);
                dp.setId_keramik(keramik.getId_keramik());
                dp.setKeramik(keramik);
                for (int w = 0; w < details.size(); w++) {
                    if (details.get(w).getId_keramik().equals(keramik.getId_keramik())) {
                        details.set(w, dp);
                        if (recyclerView.getAdapter() != null)
                            recyclerView.getAdapter().notifyDataSetChanged();
                        hitungTotal();
                        return;
                    }
                }
                details.add(dp);
                if (recyclerView.getAdapter() != null)
                    recyclerView.getAdapter().notifyDataSetChanged();
                hitungTotal();
            }
        }).show();
    }

    private double hitungTotal() {
        double jum = 0;
        for (DetailTransaksiPenjualan k : details) {
            jum += k.getHarga_jual() * k.getJumlah_jual();
        }
        ((TextView) findViewById(R.id.total)).setText("Rp. " + TextUtil.formatCurrencyIdn(jum));
        return jum;
    }

    private void delete(DetailTransaksiPenjualan data) {
        details.remove(data);
        recyclerView.getAdapter().notifyDataSetChanged();
        hitungTotal();
    }

    public class KerAdap extends RecyclerView.Adapter<KerAdap.KerHolder> {

        @NonNull
        @Override
        public KerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull KerHolder holder, int position) {
            holder.draw(details.get(position));
        }

        @Override
        public int getItemCount() {
            return details.size();
        }

        private void loadDetail(final DetailTransaksiPenjualan data, final View itemView) {
            if (data.getKeramik().getKategori() != null) {
                ((TextView) itemView.findViewById(R.id.txt_queue_jenis_keramik)).setText(data.getKeramik().getKategori().getKategori_keramik());
            } else {
                FirebaseDatabase.getInstance().getReference().child("data_kategori").child(data.getKeramik().getId_kategori()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Kategori k = dataSnapshot.getValue(Kategori.class);
                        if (k != null) {
                            data.getKeramik().setKategori(k);
                            ((TextView) itemView.findViewById(R.id.txt_queue_jenis_keramik)).setText(k.getKategori_keramik());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            if (data.getKeramik().getUkuran() != null) {
                ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik))
                        .setText(((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).getText()
                                + "(" + data.getKeramik().getUkuran().getNama_ukuran() + ")");
            } else
                FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(data.getKeramik().getId_kategori()).child(data.getKeramik().getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Ukuran u = dataSnapshot.getValue(Ukuran.class);
                        if (u != null) {
                            ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik))
                                    .setText(((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).getText()
                                            + "(" + u.getNama_ukuran() + ")");
                            data.getKeramik().setUkuran(u);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            if (data.getKeramik().getMerk() != null) {
                ((TextView) itemView.findViewById(R.id.txt_queue_merk_keramik)).setText(data.getKeramik().getMerk().getNama_merk());
            } else
                FirebaseDatabase.getInstance().getReference().child("data_merk").child(data.getKeramik().getId_merk()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Merk m = dataSnapshot.getValue(Merk.class);
                        if (m != null) {
                            data.getKeramik().setMerk(m);
                            ((TextView) itemView.findViewById(R.id.txt_queue_merk_keramik)).setText(m.getNama_merk());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }

        public class KerHolder extends RecyclerView.ViewHolder {
            public KerHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void draw(final DetailTransaksiPenjualan data) {
                ((TextView) itemView.findViewById(R.id.txt_queue_satuan)).setText("Rp. " + TextUtil.formatCurrencyIdn(data.getHarga_jual()));
                ((TextView) itemView.findViewById(R.id.txt_queue_total)).setText(TextUtil.formatCurrencyIdn((data.getJumlah_jual() * data.getHarga_jual())));

                ((TextView) itemView.findViewById(R.id.txt_queue_jumlah)).setText(data.getJumlah_jual() + " ");

                if (data.getKeramik() != null) {
                    ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).setText(data.getKeramik().getNama_keramik() + " ");
                    loadDetail(data, itemView);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("data_keramik").child(data.getId_keramik()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Keramik k = dataSnapshot.getValue(Keramik.class);
                            if (k != null) {
                                ((TextView) itemView.findViewById(R.id.txt_queue_nama_keramik)).setText(k.getNama_keramik() + " ");
                                data.setKeramik(k);
                                loadDetail(data, itemView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(data);
                    }
                });
            }
        }
    }


    //======================================================================== PRINT NOTA
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private ProgressDialog progressDialog;
    private BluetoothDevice bluetoothDevice;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private void loadDetail(final int index) {
        if (details.get(index).getKeramik().getUkuran() != null) {
            FirebaseDatabase.getInstance().getReference().child("data_ukuran").child(details.get(index).getKeramik().getId_kategori()).
                    child(details.get(index).getKeramik().getId_ukuran()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Ukuran u = dataSnapshot.getValue(Ukuran.class);
                    if (u != null) {
                        details.get(index).getKeramik().setUkuran(u);
                        checkReady();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (details.get(index).getKeramik().getMerk() != null) {
            FirebaseDatabase.getInstance().getReference().child("data_merk").child(details.get(index).getKeramik().getId_merk())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Merk m = dataSnapshot.getValue(Merk.class);
                            if (m != null) {
                                details.get(index).getKeramik().setMerk(m);
                                checkReady();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void checkReady() {
        for (int w = 0; w < details.size(); w++) {
            if (details.get(w).getKeramik() == null || details.get(w).getKeramik().getMerk() == null || details.get(w).getKeramik().getUkuran() == null) {
                return;
            }
        }
        printReceipt();
    }

    private void printReceipt() {
        if (dialog != null) dialog.dismiss();
        for (int w = 0; w < details.size(); w++) {
            if (details.get(w).getKeramik() != null && details.get(w).getKeramik().getMerk() != null && details.get(w).getKeramik().getUkuran() != null) {
                continue;
            }
            final int index = w;
            if (details.get(w).getKeramik() != null) {
                loadDetail(index);
            } else {
                FirebaseDatabase.getInstance().getReference().child("data_keramik").child(details.get(w).getId_keramik()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Keramik k = dataSnapshot.getValue(Keramik.class);
                        if (k != null) {
                            details.get(index).setKeramik(k);
                            loadDetail(index);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth tidak tersedia !", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,
                    REQUEST_ENABLE_BT);
        } else if (bluetoothDevice == null) {

            SharedPreferences sp = getSharedPreferences("BL", Context.MODE_PRIVATE);
            String addrs = sp.getString("ADDRS", null);
            if (addrs != null) {
                setBLAddress(addrs);
            } else
                scanDevices();
        } else {
            connectDevice();
        }
    }


    private void setBLAddress(String deviceAddress) {
        SharedPreferences sp = getSharedPreferences("BL", Context.MODE_PRIVATE);
        sp.edit().putString("ADDRS", deviceAddress).apply();
        bluetoothDevice = bluetoothAdapter
                .getRemoteDevice(deviceAddress);
        progressDialog = ProgressDialog.show(this,
                "Connecting...", bluetoothDevice.getName() + " : "
                        + bluetoothDevice.getAddress(), true, false);
        connectDevice();
    }

    private void connectDevice() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bluetoothSocket != null) {
                        closeSocket(bluetoothSocket);
                    }
                    bluetoothSocket = bluetoothDevice
                            .createRfcommSocketToServiceRecord(applicationUUID);
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothSocket.connect();
                    handler.sendEmptyMessage(0);
                    writeToPrinter();
                } catch (IOException eConnectException) {
                    Toast.makeText(TransaksiPenjualanActivity.this, "Could Not Connect To Device", Toast.LENGTH_SHORT).show();
                    closeSocket(bluetoothSocket);
                    scanDevices();
                    return;
                }
            }
        });
        t.start();
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Toast.makeText(TransaksiPenjualanActivity.this, "Printer Device Connected", Toast.LENGTH_SHORT).show();
        }
    };

    private void writeToPrinter() {
        if (bluetoothSocket != null) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        OutputStream os = bluetoothSocket
                                .getOutputStream();
                        String BILL = "";

                        BILL += "-------------------------------\n";
                        BILL += "      Tb. Sendang Miranti       \n" +
                                "    Jl. RA. Kartini, Sungkul    \n" +
                                "        Plumbungan 57222        \n" +
                                "      Sragen, Jawa Tengah       \n" +
                                "         (0271) 894093          \n";
                        BILL += "--------------------------------\n";
                        BILL += "No       : " + transaksiPenjualan.getId_penjualan() + " \n";
                        BILL += "Tanggal  : " + transaksiPenjualan.getTanggal_penjualan() + " \n";
                        BILL += "Nama     : " + transaksiPenjualan.getNama_pembeli() + " \n";
                        BILL += "Alamat   : " + transaksiPenjualan.getAlamat_pembeli() + " \n";
                        BILL += "Telp.    : " + transaksiPenjualan.getNo_telp_pembeli() + " \n";
                        BILL += "--------------------------------\n";
                        BILL += "Item          Jum Harga   Total   \n";
                        for (DetailTransaksiPenjualan dt : details) {
                            BILL +=
                                    dt.getKeramik().getMerk().getNama_merk() + " " + dt.getKeramik().getNama_keramik() + " (" + dt.getKeramik().getUkuran().getNama_ukuran() + ") " +
                                            dt.getJumlah_jual() + " X " +
                                            TextUtil.formatCurrencyIdn(dt.getHarga_jual()) + " : " +
                                            TextUtil.formatCurrencyIdn((dt.getJumlah_jual() * dt.getHarga_jual())) +
                                            "\n";

                        }
                        BILL += "--------------------------------\n";
                        BILL = BILL + "\n";

                        BILL = BILL + "Total  :" + " Rp. " + TextUtil.formatCurrencyIdn(transaksiPenjualan.getTotal_transaksi()) + "\n";

                        BILL += "--------------------------------\n";
                        if (App.please().getUser() != null) {
                            BILL += "--------------------------------\n";
                            BILL = BILL + "\n";

                            BILL = BILL + "Pelayan  :" + App.please().getUser().getNama() + "\n";

                            BILL += "--------------------------------\n";
                        }
                        BILL = BILL + "\n\n ";
                        os.write(BILL.getBytes());
                        //This is printer specific code you can comment ==== > Start

                        // Setting height
                        int gs = 29;
                        os.write(intToByteArray(gs));
                        int h = 104;
                        os.write(intToByteArray(h));
                        int n = 162;
                        os.write(intToByteArray(n));

                        // Setting Width
                        int gs_width = 29;
                        os.write(intToByteArray(gs_width));
                        int w = 119;
                        os.write(intToByteArray(w));
                        int n_width = 2;
                        os.write(intToByteArray(n_width));


                    } catch (Exception e) {
                        Log.e("MainActivity", "Exe ", e);
                    }
                    onBackPressed();
                }
            };
            t.start();
        }
    }

    private void scanDevices() {
        Intent connectIntent = new Intent(this,
                DeviceListActivity.class);
        startActivityForResult(connectIntent,
                REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int reqCode, int resCode,
                                 Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        Log.d("TEST", "onActivityResult");
        switch (reqCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resCode == Activity.RESULT_OK) {
                    try {
                        Log.d("TEST", "REQUEST_CONNECT_DEVICE");
                        Bundle extras = data.getExtras();
                        String deviceAddress = extras.getString("DeviceAddress");
                        Log.v("TEST", "Coming incoming address " + deviceAddress);
                        setBLAddress(deviceAddress);
                    } catch (Exception e) {

                    }
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resCode == Activity.RESULT_OK) {
                    scanDevices();
                } else {
                    Toast.makeText(TransaksiPenjualanActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();
        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    private void closeSocket(BluetoothSocket socket) {
        if (socket != null) {
            try {
                socket.close();
                Log.d("TEST", "SocketClosed");
            } catch (IOException ex) {
                Log.d("TEST", "CouldNotCloseSocket");
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        transaksiPenjualan = null;
        finish();
    }
}
