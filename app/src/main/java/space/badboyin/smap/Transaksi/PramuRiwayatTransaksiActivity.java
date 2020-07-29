package space.badboyin.smap.Transaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Fragment.OrderRiwayatFragment;
import space.badboyin.smap.Fragment.PenjualanRiwayatFragment;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.Model.TransaksiPenjualan;
import space.badboyin.smap.R;

public class PramuRiwayatTransaksiActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pramu_riwayat_transaksi);

        tabLayout = findViewById(R.id.PB_tablayout);
        viewPager = findViewById(R.id.PB_viewpager);

        viewPagerAdapter = new TabAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
        refresh();
    }

    public void refresh() {
        FirebaseDatabase.getInstance().getReference().child("data_penjualan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                penjualans.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransaksiPenjualan o = ds.getValue(TransaksiPenjualan.class);
                    if (o != null) {
                        penjualans.add(o);
                    }
                }
                viewPagerAdapter.setItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("data_pembelian").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pembelians.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransaksiPembelian t = ds.getValue(TransaksiPembelian.class);
                    if (t != null) {
                        pembelians.add(t);
                    }
                }
                viewPagerAdapter.setItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private final List<TransaksiPembelian> pembelians = new ArrayList<>();
    private final List<TransaksiPenjualan> penjualans = new ArrayList<>();

    public class TabAdapter extends FragmentPagerAdapter {
        private int numOfTabs;
        private final PenjualanRiwayatFragment riwayatFragment = new PenjualanRiwayatFragment();
        private final OrderRiwayatFragment orderRiwayatFragment = new OrderRiwayatFragment();

        public void setItems() {
            orderRiwayatFragment.set(pembelians);
            riwayatFragment.set(penjualans);
            notifyDataSetChanged();
        }

        public TabAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return riwayatFragment;
                case 1:
                    return orderRiwayatFragment;
                default:
                    return riwayatFragment;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}
