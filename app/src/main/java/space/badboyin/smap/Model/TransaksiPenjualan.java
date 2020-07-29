package space.badboyin.smap.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;

import space.badboyin.smap.utilities.TextUtil;

@IgnoreExtraProperties
public class TransaksiPenjualan {
    private String id_penjualan;
    private String nama_pembeli;
    private String alamat_pembeli;
    private String no_telp_pembeli;
    private double total_transaksi;
    private String id_user;
    private String tanggal_penjualan;
    private String status;

    @Exclude
    private long time;

    @Exclude
    public long getTime() {
        if (time == 0)
            time = TextUtil.formatTanggal(getTanggal_penjualan());
        return time;
    }

    public void setTanggal_penjualan(String tanggal_penjualan) {
        this.tanggal_penjualan = tanggal_penjualan;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal_penjualan() {
        return tanggal_penjualan;
    }

    public String getNama_pembeli() {
        return nama_pembeli;
    }

    public void setNama_pembeli(String nama_pembeli) {
        this.nama_pembeli = nama_pembeli;
    }

    public String getAlamat_pembeli() {
        return alamat_pembeli;
    }

    public void setAlamat_pembeli(String alamat_pembeli) {
        this.alamat_pembeli = alamat_pembeli;
    }

    public String getNo_telp_pembeli() {
        return no_telp_pembeli;
    }

    public void setNo_telp_pembeli(String no_telp_pembeli) {
        this.no_telp_pembeli = no_telp_pembeli;
    }

    public double getTotal_transaksi() {
        return total_transaksi;
    }

    public void setTotal_transaksi(double total_transaksi) {
        this.total_transaksi = total_transaksi;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_penjualan() {
        return id_penjualan;
    }

    public void setId_penjualan(String id_penjualan) {
        this.id_penjualan = id_penjualan;
    }
}
