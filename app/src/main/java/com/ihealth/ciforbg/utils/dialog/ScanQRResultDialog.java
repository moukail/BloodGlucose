package com.ihealth.ciforbg.utils.dialog;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.dataclass.Data_TB_StripBottleInfo;
import com.ihealth.ciforbg.utils.MeasurePresenter;
import com.ihealth.ciforbg.utils.strip.QRCodeTools;

public class ScanQRResultDialog extends Dialog {

    private static String TAG = "ScanQRResultDialog";
    private static int default_width = 300;
    private static int default_height = 300;
    private Context context;

    private TextView tv_manufactured_value;
    private TextView tv_valid_value;
    private TextView tv_strips_value;
    private TextView tv_manufactured, tv_valid, tv_strips;
    String[] stripNumStringArray25 = {"0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "19", "20", "21", "22", "23", "24", "25"};
    String[] stripNumStringArray10 = {"0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10"};
    private TextView btn_ok;
    private TextView btn_cancel;

    private String sampleDate; // 开瓶日期
    private String overTimeDate; // 过期日期
    private int stripsNum; // 瓶子试条数

    private Data_TB_StripBottleInfo mBottleInfo;
    private boolean isOver = false;
    private boolean isInvaliddata = false;
    private boolean isTen;
    private boolean isNewStrip = false;

    public ScanQRResultDialog(Context context,
                              int theme, Data_TB_StripBottleInfo bottleInfo, String sampleDate, String overTimeDate) {
        super(context, theme);
        this.context = context;
        this.sampleDate = sampleDate;
        this.overTimeDate = overTimeDate;
        this.stripsNum = bottleInfo.getStripLeft();
        this.isNewStrip = bottleInfo.isNewStrip();
        this.mBottleInfo = bottleInfo;
        if (mBottleInfo.getOverTime() * 1000 <= System.currentTimeMillis()) {
            isOver = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_scan_result);
//		Window window = getWindow();
//		WindowManager.LayoutParams params = window.getAttributes();
//		float density = getDensity(context);
//		params.width = (int) (default_width * density);
//		params.height = (int) (default_height * density);
//		params.gravity = Gravity.CENTER;
//		window.setAttributes(params);

        tv_manufactured = (TextView) findViewById(R.id.tv_manufactured);
        tv_valid = (TextView) findViewById(R.id.tv_valid);
        tv_strips = (TextView) findViewById(R.id.tv_strips);

        tv_manufactured_value = (TextView) findViewById(R.id.tv_manufactured_value);
        tv_valid_value = (TextView) findViewById(R.id.tv_valid_value);
        tv_strips_value = (TextView) findViewById(R.id.tv_strips_value);

        if (Locale.getDefault().getLanguage().equals("de")) {
            tv_manufactured.setTextSize(10);
            tv_valid.setTextSize(10);
            tv_strips.setTextSize(10);
            tv_manufactured_value.setTextSize(10);
            tv_valid_value.setTextSize(10);
            tv_strips_value.setTextSize(10);
        } else if (Locale.getDefault().getLanguage().equals("fr")) {
            tv_manufactured.setTextSize(10);
            tv_valid.setTextSize(10);
            tv_strips.setTextSize(10);
            tv_manufactured_value.setTextSize(10);
            tv_valid_value.setTextSize(10);
            tv_strips_value.setTextSize(10);
        } else if (Locale.getDefault().getLanguage().equals("it")) {
            tv_manufactured.setTextSize(10);
            tv_valid.setTextSize(10);
            tv_strips.setTextSize(10);
            tv_manufactured_value.setTextSize(10);
            tv_valid_value.setTextSize(10);
            tv_strips_value.setTextSize(10);
        } else if (Locale.getDefault().getLanguage().equals("es")) {
            tv_manufactured.setTextSize(10);
            tv_valid.setTextSize(10);
            tv_strips.setTextSize(10);
            tv_manufactured_value.setTextSize(10);
            tv_valid_value.setTextSize(10);
            tv_strips_value.setTextSize(10);
        } else if (Locale.getDefault().getLanguage().equals("tr")) {
            tv_manufactured.setTextSize(10);
            tv_valid.setTextSize(10);
            tv_strips.setTextSize(10);
            tv_manufactured_value.setTextSize(10);
            tv_valid_value.setTextSize(10);
            tv_strips_value.setTextSize(10);
        }
        tv_manufactured_value.setText(sampleDate);
        tv_valid_value.setText(overTimeDate);
        tv_strips_value.setText(stripsNum + "");

        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        if (isNewStrip) {
            btn_ok.setText("Open");
        } else {
            btn_ok.setText("OK");
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isNewStrip){
                    MeasurePresenter.getInstance().openBottle(mBottleInfo);
                } else {
                    MeasurePresenter.getInstance().saveBottleInfo(mBottleInfo);
                }
                ScanQRResultDialog.this.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanQRResultDialog.this.dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (isOver) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.alert));
            builder.setMessage(getContext().getString(R.string.strip_expired));
            builder.setPositiveButton(getContext().getString(R.string.ok),
                    new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ScanQRResultDialog.this.dismiss();
                        }
                    });
            AlertDialog mDialog = builder.create();
            Window mWindow = mDialog.getWindow();
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            mWindow.setGravity(Gravity.BOTTOM);

            lp.y = 220;
            mWindow.setAttributes(lp);
            mDialog.show();
            return;
        }
        if (stripsNum == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.alert));
            builder.setMessage(getContext().getString(
                    R.string.alert_stripnum0));
            builder.setPositiveButton(getContext().getString(R.string.ok),
                    new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//							SetStripNumWheel(null);
                        }
                    });
//            builder.setNegativeButton(getContext().getString(R.string.cancel),
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
            AlertDialog mDialog = builder.create();
            Window mWindow = mDialog.getWindow();
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            mWindow.setGravity(Gravity.BOTTOM);

            lp.y = 220;
            mWindow.setAttributes(lp);
            mDialog.show();
        }
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }
}
