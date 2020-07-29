package space.badboyin.smap.Model;

import com.google.firebase.database.Exclude;

public class DetailTransaksiPenjualan {
    private String id_detail_penjualan;
    private String id_penjualan;
    private double harga_jual;
    private int jumlah_jual;
    private String id_keramik;

    public int getJumlah_jual() {
        return jumlah_jual;
    }

    public void setJumlah_jual(int jumlah_jual) {
        this.jumlah_jual = jumlah_jual;
    }

    @Exclude
    private Keramik keramik;

    public void setKeramik(Keramik keramik) {
        this.keramik = keramik;
    }

    @Exclude
    public Keramik getKeramik() {
        return keramik;
    }

    public String getId_detail_penjualan() {
        return id_detail_penjualan;
    }

    public void setId_detail_penjualan(String id_detail_penjualan) {
        this.id_detail_penjualan = id_detail_penjualan;
    }

    public String getId_penjualan() {
        return id_penjualan;
    }

    public void setId_penjualan(String id_penjualan) {
        this.id_penjualan = id_penjualan;
    }

    public double getHarga_jual() {
        return harga_jual;
    }

    public void setHarga_jual(double harga_jual) {
        this.harga_jual = harga_jual;
    }

    public String getId_keramik() {
        return id_keramik;
    }

    public void setId_keramik(String id_keramik) {
        this.id_keramik = id_keramik;
    }

}
