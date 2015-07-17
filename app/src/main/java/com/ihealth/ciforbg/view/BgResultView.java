package com.ihealth.ciforbg.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lynn on 15-7-3.
 */
public class BgResultView extends View {

    public final int UNIT_MG = 0;
    public final int UNIT_MMOL = 1;

    private String[] mUnits = new String[]{"mg/dL","mmol/L"};

    private final int WIDTH = 200;
    private final int HEIGHT = 200;

    private float mRatioX = 1.0f;
    private float mRatioY = 1.0f;

    private int radius;
    private int[] mCircleCenter;

    private PaintFlagsDrawFilter mFilter;

    private float mBgValue = 0;

    private int mUnit;

    public BgResultView(Context context) {
        super(context);
        initValue();
    }

    public BgResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue();
    }

    public BgResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValue();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        mRatioX = width / this.WIDTH;
        mRatioY = height / this.HEIGHT;
        setMeasuredDimension((int)width,(int)height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mFilter);
        canvas.scale(this.mRatioX, this.mRatioY);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#00ff00"));
        canvas.drawCircle(mCircleCenter[0], mCircleCenter[1], radius, paint);
        paint.setColor(Color.parseColor("#eef0f3"));
        canvas.drawCircle(mCircleCenter[0], mCircleCenter[1], radius - 10, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(75);
        paint.setTextAlign(Paint.Align.CENTER);
        if(mUnit == UNIT_MG) {
            canvas.drawText((int)mBgValue + "", WIDTH / 2, 110, paint);
        } else {
            canvas.drawText(mBgValue + "", WIDTH / 2, 110, paint);
        }
        paint.setColor(Color.parseColor("#cccccc"));
        paint.setTextSize(35);
        canvas.drawText(mUnits[mUnit], WIDTH / 2, 150, paint);
    }

    private void initValue(){
        mFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        radius = (WIDTH <= HEIGHT)?(WIDTH / 2):(HEIGHT / 2);
        mCircleCenter = new int[]{WIDTH / 2, HEIGHT / 2};
    }

    /**
     * 设置血糖值，单位mg/dL
     * @param bgResult 血糖结果
     */
    public void setBgResult(int bgResult){
        this.mUnit = UNIT_MG;
        this.mBgValue = bgResult;
        postInvalidate();
    }

    /**
     * 设置血糖值，单位mmol/L
     * @param bgResult
     */
    public void setBgResult(float bgResult){
        this.mUnit = UNIT_MMOL;
        this.mBgValue = bgResult;
        postInvalidate();
    }
}
