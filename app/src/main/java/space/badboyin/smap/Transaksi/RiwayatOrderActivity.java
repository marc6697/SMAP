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

import space.badboyin.smap.Fragment.OrderKonfirmasiFragment;
import space.badboyin.smap.Fragment.OrderRiwayatFragment;
import space.badboyin.smap.Model.TransaksiPembelian;
import space.badboyin.smap.R;

public class RiwayatOrderActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private OrderAdap viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_order);
        tabLayout = findViewById(R.id.PB_tablayout);
        viewPager = findViewById(R.id.PB_viewpager);

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
        FirebaseDatabase.getInstance().getReference().child("data_pembelian").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TransaksiPembelian> needConfirm = new ArrayList<>();
                List<TransaksiPembelian> confirmed = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TransaksiPembelian o = ds.getValue(TransaksiPembelian.class);
                    if (o != null) {
                        if (o.getStatus_pembeian().equals("PENDING")) {
                            needConfirm.add(o);
                        } else confirmed.add(o);
                    }
                }
                viewPagerAdapter.setItems(needConfirm, confirmed);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class OrderAdap extends FragmentPagerAdapter {
        private int numOfTabs;
        private final OrderKonfirmasiFragment confirmFragment = new OrderKonfirmasiFragment();
        private final OrderRiwayatFragment riwayatPembelianFragment = new OrderRiwayatFragment();

        public void setItems(List<TransaksiPembelian> needConfirm, List<TransaksiPembelian> confirmed) {
            confirmFragment.setTransaksiPembelians(needConfirm);
            riwayatPembelianFragment.set(confirmed);
            notifyDataSetChanged();
        }

        public OrderAdap(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return confirmFragment;
                case 1:
                    return riwayatPembelianFragment;
                default:
                    return confirmFragment;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}
