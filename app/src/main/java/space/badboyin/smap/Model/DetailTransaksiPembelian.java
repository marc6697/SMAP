package space.badboyin.smap.Model;

import com.google.firebase.database.Exclude;

public class DetailTransaksiPembelian {
    private String id_detail_pembelian;
    private String id_pembelian;
    private int jumlah_beli;
    private double harga_beli;
    private String id_keramik;
    @Exclude
    private Keramik keramik;

    public void setKeramik(Keramik keramik) {
        this.keramik = keramik;
    }

    @Exclude
    public Keramik getKeramik() {
        return keramik;
    }

    public String getId_detail_pembelian() {
        return id_detail_pembelian;
    }

    public void setId_detail_pembelian(String id_detail_pembelian) {
        this.id_detail_pembelian = id_detail_pembelian;
    }

    public String getId_pembelian() {
        return id_pembelian;
    }

    public void setId_pembelian(String id_pembelian) {
        this.id_pembelian = id_pembelian;
    }

    public int getJumlah_beli() {
        return jumlah_beli;
    }

    public void setJumlah_beli(int jumlah_beli) {
        this.jumlah_beli = jumlah_beli;
    }

    public double getHarga_beli() {
        return harga_beli;
    }

    public void setHarga_beli(double harga_beli) {
        this.harga_beli = harga_beli;
    }

    public String getId_keramik() {
        return id_keramik;
    }

    public void setId_keramik(String id_keramik) {
        this.id_keramik = id_keramik;
    }
}
