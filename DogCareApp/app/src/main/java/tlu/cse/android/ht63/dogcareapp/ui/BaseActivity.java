package tlu.cse.android.ht63.dogcareapp.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tlu.cse.android.ht63.dogcareapp.R;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected boolean isUnavailable() {
        return isFinishing() || isDestroyed();
    }

    protected boolean isAlive() {
        return !isUnavailable();
    }

    private Dialog dialogLoading;

    protected void showLoading() {
        hideLoading();
        dialogLoading = new Dialog(this);
        dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLoading.setContentView(R.layout.progress_dialog);
        if (dialogLoading.getWindow() != null) {
            dialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialogLoading.setCanceledOnTouchOutside(false);
        dialogLoading.setCancelable(false);
        dialogLoading.show();
    }

    protected void hideLoading() {
        if (dialogLoading != null && dialogLoading.isShowing()) {
            dialogLoading.dismiss();
        }
    }

}
