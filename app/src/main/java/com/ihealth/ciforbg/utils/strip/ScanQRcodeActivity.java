package com.ihealth.ciforbg.utils.strip;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ihealth.ciforbg.R;
import com.ihealth.ciforbg.camera.CameraManager;
import com.ihealth.ciforbg.utils.L;
import com.ihealth.ciforbg.utils.decoding.CaptureActivityHandler;
import com.ihealth.ciforbg.utils.decoding.InactivityTimer;
import com.ihealth.ciforbg.view.ViewfinderView;

public class ScanQRcodeActivity extends Activity implements Callback{
    private final String TAG = "ScanQRcodeActivity";

    public static final int RESULT_CODE_SUCCESS  = 1;
    public static final int RESULT_CODE_FAILED = 2;

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtNote;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
//	private RelativeLayout txtNoteRelative;
//	private RelativeLayout allview;
//	private RelativeLayout btnReturnRelative;
//	private LinearLayout mainview;
	
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        L.i(TAG,"onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan_qrcode);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtNote = (TextView)findViewById(R.id.txtNote);
		txtNote.setText(" ");

		mSurfaceView = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceView.setFocusable(true);
        mSurfaceHolder = mSurfaceView.getHolder();
		
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		if (hasSurface) {
			initCamera(mSurfaceHolder);
		} else {
			mSurfaceHolder.addCallback(this);
//			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
		
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.setResult(RESULT_CODE_FAILED);
            finish();
            return true;
        }
        return false;
    }

    @Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
        L.i(TAG,"onDestroy");
        super.onDestroy();
	}
	
	/**
	 * When the beep has ScanQRcodeActivity.this.finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	private void initCamera(SurfaceHolder surfaceHolder) {

		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
		holder.removeCallback(this);
	}
	
	public void handleDecode(Result obj, Bitmap barcode) {
        L.i(TAG,obj.getText());
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		Bundle bundle = new Bundle();
		bundle.putString("zxingText", obj.getText());
		Intent intent = getIntent();
		intent.putExtras(bundle);
		this.setResult(RESULT_CODE_SUCCESS, intent);
		finish();
	}
	
	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}
}
