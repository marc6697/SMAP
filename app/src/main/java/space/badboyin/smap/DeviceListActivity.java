package space.badboyin.smap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView recyclerView;
    private final List<BluetoothDevice> pairedDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);
        setResult(Activity.RESULT_CANCELED);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            for (BluetoothDevice bl : bluetoothAdapter.getBondedDevices()) {
                pairedDevices.add(bl);
            }
        }
        recyclerView.setAdapter(new BLAdapter());
    }

    private class BLAdapter extends RecyclerView.Adapter<BLAdapter.BlHolder> {
        @NonNull
        @Override
        public BlHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BlHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BlHolder holder, int position) {
            holder.draw(pairedDevices.get(position));
        }

        @Override
        public int getItemCount() {
            return pairedDevices.size();
        }

        class BlHolder extends RecyclerView.ViewHolder {
            public void draw(final BluetoothDevice data) {
                ((TextView) itemView).setText(data.getName() + "\n" + data.getAddress());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            bluetoothAdapter.cancelDiscovery();
                            Bundle mBundle = new Bundle();
                            mBundle.putString("DeviceAddress", data.getAddress());
                            Intent mBackIntent = new Intent();
                            mBackIntent.putExtras(mBundle);
                            setResult(Activity.RESULT_OK, mBackIntent);
                            finish();
                        } catch (Exception ex) {
                            Toast.makeText(itemView.getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            public BlHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }
}
