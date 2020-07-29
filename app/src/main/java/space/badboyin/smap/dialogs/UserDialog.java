package space.badboyin.smap.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialog;

import java.util.ArrayList;
import java.util.List;

import space.badboyin.smap.Model.User;
import space.badboyin.smap.R;


public class UserDialog extends AppCompatDialog {
    public interface OnUserCallback {
        void onResult(String role, boolean active);
    }

    private User user;
    private OnUserCallback callback;

    public UserDialog(Context context, User user, OnUserCallback callback) {
        super(context);
        this.user = user;
        this.callback = callback;
    }

    private Spinner spinner;
    private String role = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user);
        setTitle("Pengaturan Pengguna");
        spinner = findViewById(R.id.spin_role);
        List<String> arr = new ArrayList<>();
        arr.add("Admin");
        arr.add("Pramuniaga");
        arr.add("Gudang");
        int pos = user.getPosisi().equals("Pramuniaga") ? 1 : 0;
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arr);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(areasAdapter);
        spinner.setSelection(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        role = "Admin";
                        break;
                    case 1:
                        role = "Pramuniaga";
                        break;
                    case 2:
                        role = "Gudang";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ((CheckBox) findViewById(R.id.active)).setChecked(user.getStatus().equals("Aktif"));
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onResult(role, ((CheckBox) findViewById(R.id.active)).isChecked());
                dismiss();
            }
        });
    }
}
