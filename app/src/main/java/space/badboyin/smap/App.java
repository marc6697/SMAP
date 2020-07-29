package space.badboyin.smap;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import space.badboyin.smap.Model.Keramik;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.Model.User;

public class App extends Application {
    private static volatile App app;
    public static final String TAG = App.class.getSimpleName();

    public static App please() {
        return app;
    }
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private String barcode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    private Keramik keramik;

    public Keramik getKeramik() {
        return keramik;
    }

    public void setKeramik(Keramik keramik) {
        this.keramik = keramik;
    }

    private TransaksiPenjualan transaksiPenjualan;

    public TransaksiPenjualan getTransaksiPenjualan() {
        return transaksiPenjualan;
    }

    public void setTransaksiPenjualan(TransaksiPenjualan transaksiPenjualan) {
        this.transaksiPenjualan = transaksiPenjualan;
    }

    private TransaksiPembelian transaksiPembelian;

    public TransaksiPembelian getTransaksiPembelian() {
        return transaksiPembelian;
    }

    public void setTransaksiPembelian(TransaksiPembelian transaksiPembelian) {
        this.transaksiPembelian = transaksiPembelian;
    }
}
