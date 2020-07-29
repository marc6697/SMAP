package space.badboyin.smap.Model;

import com.google.firebase.database.Exclude;

public class Keramik {
    private String id_keramik;
    private String id_kategori;
    private String id_merk;
    private String id_ukuran;
    private String nama_keramik;
    private String url_gambar;
    private double harga_jual;
    private double harga_beli;
    private int stock;
    private int terjual;

    public double getHarga_beli() {
        return harga_beli;
    }

    public void setHarga_beli(double harga_beli) {
        this.harga_beli = harga_beli;
    }

    @Exclude
    private Merk merk;
    @Exclude
    private Ukuran ukuran;
    @Exclude
    private Kategori kategori;

    @Exclude
    public Merk getMerk() {
        return merk;
    }

    public void setMerk(Merk merk) {
        this.merk = merk;
    }

    @Exclude
    public Ukuran getUkuran() {
        return ukuran;
    }

    public void setUkuran(Ukuran ukuran) {
        this.ukuran = ukuran;
    }

    @Exclude
    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public String getId_keramik() {
        return id_keramik;
    }

    public void setId_keramik(String id_keramik) {
        this.id_keramik = id_keramik;
    }

    public String getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getId_merk() {
        return id_merk;
    }

    public void setId_merk(String id_merk) {
        this.id_merk = id_merk;
    }

    public String getId_ukuran() {
        return id_ukuran;
    }

    public void setId_ukuran(String id_ukuran) {
        this.id_ukuran = id_ukuran;
    }

    public String getNama_keramik() {
        return nama_keramik;
    }

    public void setNama_keramik(String nama_keramik) {
        this.nama_keramik = nama_keramik;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public void setUrl_gambar(String url_gambar) {
        this.url_gambar = url_gambar;
    }

    public double getHarga_jual() {
        return harga_jual;
    }

    public void setHarga_jual(double harga_jual) {
        this.harga_jual = harga_jual;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getTerjual() {
        return terjual;
    }

    public void setTerjual(int terjual) {
        this.terjual = terjual;
    }
}
