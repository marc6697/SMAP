package space.badboyin.smap.Model;


import java.util.HashMap;
import java.util.Map;

public class Laporan {
    private int jumlah_barang;
    private int tanggal;
    private long tanggal_long;
    private double total;
    private Map<String, Boolean> transaksis = new HashMap<>();
    private int tahun;
    private int bulan;

    public int getBulan() {
        return bulan;
    }

    public void setBulan(int bulan) {
        this.bulan = bulan;
    }

    public int getJumlah_transaksi() {
        return transaksis.size();
    }
    public void setTransaksi(TransaksiPenjualan transaksiPenjualan) {
        if (transaksis.get(transaksiPenjualan.getId_penjualan())== null) {
            transaksis.put(transaksiPenjualan.getId_penjualan(), true);
            setTotal(getTotal() + transaksiPenjualan.getTotal_transaksi());
        }
    }

    public void setOrder(TransaksiPembelian transaksiPembelian) {
        if (transaksis.get(transaksiPembelian.getId_pembelian())== null) {
            transaksis.put(transaksiPembelian.getId_pembelian(), true);
            setTotal(getTotal() + transaksiPembelian.getTotal_harga());
        }
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public int getJumlah_barang() {
        return jumlah_barang;
    }

    public void setJumlah_barang(int jumlah_barang) {
        this.jumlah_barang = jumlah_barang;
    }

    public int getTanggal() {
        return tanggal;
    }

    public void setTanggal(int tanggal) {
        this.tanggal = tanggal;
    }

    public long getTanggal_long() {
        return tanggal_long;
    }

    public void setTanggal_long(long tanggal_long) {
        this.tanggal_long = tanggal_long;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
