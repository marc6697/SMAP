package space.badboyin.smap.Model;

public class Sales {
    private String id_sales;
    private String nama_sales;
    private String nomor_sales;
    private String nama_perusahaan;
    private String lokasi_perusahaan;

    public Sales() {
    }


    public String getId_sales() {
        return id_sales;
    }

    public void setId_sales(String id_sales) {
        this.id_sales = id_sales;
    }

    public String getNama_sales() {
        return nama_sales;
    }

    public void setNama_sales(String nama_sales) {
        this.nama_sales = nama_sales;
    }

    public String getNomor_sales() {
        return nomor_sales;
    }

    public void setNomor_sales(String nomor_sales) {
        this.nomor_sales = nomor_sales;
    }

    public String getNama_perusahaan() {
        return nama_perusahaan;
    }

    public void setNama_perusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }

    public String getLokasi_perusahaan() {
        return lokasi_perusahaan;
    }

    public void setLokasi_perusahaan(String lokasi_perusahaan) {
        this.lokasi_perusahaan = lokasi_perusahaan;
    }
}
