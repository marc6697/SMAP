package space.badboyin.smap.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

import space.badboyin.smap.R;

public class LoadingDialog extends AppCompatDialog {
    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
