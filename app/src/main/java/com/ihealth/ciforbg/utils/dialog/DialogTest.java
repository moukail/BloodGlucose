package com.ihealth.ciforbg.utils.dialog;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ihealth.ciforbg.R;

public class DialogTest extends Dialog {

	private static final String TAG = "DialogTest";
	private Context context;
	int[] tests = { R.drawable.test0, R.drawable.test1, R.drawable.test2,
			R.drawable.test3, R.drawable.test4,  R.drawable.test5};
	private ImageView img_test;
	private Timer timer;
	private TimerTask btTask;

	public DialogTest(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	private static final int CHAGER_IMG = 1;
	private static final int FINISH_IMG = 2;
	private static int count = 0;

	Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHAGER_IMG:
				Log.i(TAG, "count:" + count);
				img_test.setBackgroundResource(tests[count]);
				break;
			case FINISH_IMG:
				count = 0;
				if(btTask != null)
					btTask.cancel();
				if(timer != null)
					timer.cancel();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_test);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
//		float density = getDensity(context);
//		params.width = (int) (default_width * density);
//		params.height = (int) (default_height * density);
		params.gravity = Gravity.CENTER;
		params.dimAmount = 0.8f;
		params.alpha = 0.5f;
		window.setAttributes(params);
		
		img_test = (ImageView) findViewById(R.id.img_test);
		// img_test = (ImageView)findViewById(R.id.img_test);
		Log.i(TAG, "onCreate");
	}

	@Override
	protected void onStart() {
		super.onStart();
        timer30();
	}
	
	
	public void taskCancel(){
		
		
		if(btTask != null)
			btTask.cancel();
		if(timer != null)
			timer.cancel();
		
		
	}


	public void timer30() {
        timer = new Timer();
		btTask = new TimerTask() {
			public void run() {
				count++;
				if (count > 5) {
					Message msg1 = new Message();
					msg1.what = FINISH_IMG;
					uiHandler.sendMessage(msg1);
				} else {
					Message msg = new Message();
					msg.what = CHAGER_IMG;
					uiHandler.sendMessage(msg);
				}
			}
		};
		try {
			timer.schedule(btTask, 0, 1000);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

}
