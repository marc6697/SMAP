package space.badboyin.smap.Transaksi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import space.badboyin.smap.Fragment.LaporanBestSellingFragment;
import space.badboyin.smap.Fragment.LaporanOrderFragment;
import space.badboyin.smap.Fragment.LaporanTransaksiFragment;
import space.badboyin.smap.R;

public class LaporanActivity extends AppCompatActivity {

    private TextView judul;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tabtransaksi, taborder, tabbestselling;
    private OrderAdap viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        judul = findViewById(R.id.text_PB_judul);
        tabLayout = findViewById(R.id.PB_tablayout);
        viewPager = findViewById(R.id.PB_viewpager);
        tabtransaksi = findViewById(R.id.PB_item1);
        taborder = findViewById(R.id.PB_item2);
        tabbestselling = findViewById(R.id.PB_item3);
        transaksi_view = true;
        order_view = false;
        viewPagerAdapter = new OrderAdap(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    showTransaksi();
                } else if (tab.getPosition() == 1) {
                    showOrder();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean transaksi_view;
    private boolean order_view;
    private boolean best_view;

    private void showTransaksi() {
        transaksi_view = true;
        order_view = false;
        best_view = false;
        draw();
    }

    private void showOrder() {
        transaksi_view = false;
        order_view = true;
        best_view = false;
        draw();
    }

    private void showBest() {
        transaksi_view = false;
        order_view = false;
        best_view = true;
        draw();
    }

    private void draw() {
        if (transaksi_view) {
            findViewById(R.id.laba).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.laba)).setText(laba_transaksi);
            ((TextView) findViewById(R.id.total)).setText(total_transaksi);
        } else if (order_view) {
            findViewById(R.id.laba).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.total)).setText(total_order);
        } else if (best_view) {
            findViewById(R.id.laba).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.total)).setText(total_order);
        }
    }

    private String total_best = "";
    private String total_order = "";
    private String total_transaksi = "";
    private String laba_transaksi = "";

    public void setTransaksi(String total, String laba) {
        total_transaksi = total;
        laba_transaksi = laba;
        draw();
    }

    public void setBest(String total) {
        total_best = total;
        draw();
    }

    public void setOrder(String total) {
        total_order = total;
        draw();
    }

    public class OrderAdap extends FragmentPagerAdapter {
        private int numOfTabs;
        private final LaporanTransaksiFragment laporanTransaksiFragment = new LaporanTransaksiFragment();
        private final LaporanOrderFragment laporanOrderFragment = new LaporanOrderFragment();
        private final LaporanBestSellingFragment laporanBestSellingFragment = new LaporanBestSellingFragment();

        public OrderAdap(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return laporanTransaksiFragment;
                case 1:
                    return laporanOrderFragment;
                case 2:
                    return laporanBestSellingFragment;
                default:
                    return laporanTransaksiFragment;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}
