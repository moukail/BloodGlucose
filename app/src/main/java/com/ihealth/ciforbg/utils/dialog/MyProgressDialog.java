package com.ihealth.ciforbg.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.ihealth.ciforbg.R;

/**
 * Created by lynn on 15-7-13.
 */
public class MyProgressDialog extends Dialog {
    private Context mContext;

    public MyProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
//		float density = getDensity(context);
//		params.width = (int) (default_width * density);
//		params.height = (int) (default_height * density);
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0.8f;
        params.alpha = 0.5f;
        window.setAttributes(params);
    }
}
