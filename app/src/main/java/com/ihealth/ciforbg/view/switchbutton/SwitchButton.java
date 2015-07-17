package com.ihealth.ciforbg.view.switchbutton;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

import com.ihealth.ciforbg.R;

/**
 * Created by lynn on 15-7-6.
 */
public class SwitchButton extends CheckBox{
    private final String TAG = "SwitchButton";

    private final int WIDTH = 512;
    private final int HEIGHT = 74;

    private float mRatioX = 1.0f;
    private float mRatioY = 1.0f;

    private Paint mPaint;

    private Bitmap mFrame;
    private Bitmap mBtnNormal;

    private float mFirstDownY; // 首次按下的Y

    private float mFirstDownX; // 首次按下的X

    private float mRealPos; // 图片的绘制位置

    private float mBtnPos; // 按钮的位置

    private float mBtnOnPos; // 开关打开的位置

    private float mBtnOffPos; // 开关关闭的位置

    private float mFrameWidth;

    private float mFrameHeight;

    private float mBtnWidth;

    private float mBtnHeight;

    private float mBtnInitPos;

    private int mClickTimeout;

    private int mTouchSlop;

    private final int MAX_ALPHA = 255;

    private int mAlpha = MAX_ALPHA;

    private boolean mChecked = false;

    private float mVelocity;
    private float mExtendOffsetY; // Y轴方向扩大的区域,增大点击区域
    private final float VELOCITY = 350;
    private final float EXTENDED_OFFSET_Y = 5;

    private RectF mSaveLayerRectF;

    private PorterDuffXfermode mXfermode;
    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public SwitchButton(Context context) {
        this(context,null);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        Resources resources = context.getResources();
        mFrame = BitmapFactory.decodeResource(resources, R.drawable.switch_button_frame);
        mBtnNormal = BitmapFactory.decodeResource(resources, R.drawable.switch_button_normal);

        mFrameWidth = mFrame.getWidth();
        mFrameHeight = mFrame.getHeight();

        mBtnWidth = mBtnNormal.getWidth();

        mBtnOffPos = mBtnWidth / 2;
        mBtnOnPos = mFrameWidth - mBtnWidth / 2;

        mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
        mRealPos = getRealPos(mBtnPos);

        final float density = getResources().getDisplayMetrics().density;
        mVelocity = (int) (VELOCITY * density + 0.5f);
//        mExtendOffsetY = (int) (EXTENDED_OFFSET_Y * density + 0.5f);
        mExtendOffsetY = EXTENDED_OFFSET_Y;

        mSaveLayerRectF = new RectF(0, 0, mFrame.getWidth(), mFrame.getHeight());

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(mRatioX, mRatioY);

        canvas.drawBitmap(mFrame, new Rect(0,0,mFrame.getWidth(),mFrame.getHeight()), new RectF(0.0f,0.0f,mFrameWidth,mFrameHeight), mPaint);

        canvas.drawBitmap(mBtnNormal,new Rect(0,0,mBtnNormal.getWidth(),mBtnNormal.getHeight()),new RectF(mRealPos,mExtendOffsetY,mRealPos + mBtnWidth,mBtnHeight),mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.mRatioX = width / WIDTH;
        this.mRatioY = height / HEIGHT;
        setMeasuredDimension(width, height);
    }

    private float getRealPos(float btnPos) {
        return btnPos - mBtnWidth / 2;
    }
}
