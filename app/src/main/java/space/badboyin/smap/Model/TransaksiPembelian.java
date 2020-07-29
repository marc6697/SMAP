package space.badboyin.smap.Model;


import com.google.firebase.database.Exclude;

import space.badboyin.smap.utilities.TextUtil;

public class TransaksiPembelian {
    private String id_pembelian;
    private String id_sales;
    private String status_pembeian;
    private double total_harga;
    private String tanggal_pembelian;
    private String id_user;

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_user() {
        return id_user;
    }

    @Exclude
    private Sales sales;

    @Exclude
    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    @Exclude
    private long time;

    @Exclude
    public long getTime() {
            if (time == 0)
            time = TextUtil.formatTanggal(getTanggal_pembelian());
        return time;
    }

    public String getId_pembelian() {
        return id_pembelian;
    }

    public void setId_pembelian(String id_pembelian) {
        this.id_pembelian = id_pembelian;
    }

    public String getId_sales() {
        return id_sales;
    }

    public void setId_sales(String id_sales) {
        this.id_sales = id_sales;
    }

    public String getStatus_pembeian() {
        return status_pembeian;
    }

    public void setStatus_pembeian(String status_pembeian) {
        this.status_pembeian = status_pembeian;
    }

    public double getTotal_harga() {
        return total_harga;
    }

    public void setTotal_harga(double total_harga) {
        this.total_harga = total_harga;
    }

    public String getTanggal_pembelian() {
        return tanggal_pembelian;
    }

    public void setTanggal_pembelian(String tanggal_pembelian) {
        this.tanggal_pembelian = tanggal_pembelian;
    }
}
