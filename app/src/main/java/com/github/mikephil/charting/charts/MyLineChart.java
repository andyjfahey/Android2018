package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.renderer.MyXAxisRenderer;

public class MyLineChart extends LineChart {
    private GridLimitShader mGridLimitShader;
    private float maxThreshold;
    private float minThreshold;

    public MyLineChart(Context context) {
        super(context);
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mXAxisRenderer = new MyXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer);
        mGridLimitShader = new GridLimitShader(this);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGridLimitShader.setMax(getAxisLeft().getAxisMaxValue());
        mGridLimitShader.setMin(getAxisLeft().getAxisMinValue());
        mGridLimitShader.setMaxThreshold(maxThreshold);
        mGridLimitShader.setMinThreshold(minThreshold);
        mGridLimitShader.drawGridBackground(canvas);
    }

    @Override
    protected void drawGridBackground(Canvas c) {
        super.drawGridBackground(c);
    }

    public void setMaxThreshold(float maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public void setMinThreshold(float minThreshold) {
        this.minThreshold = minThreshold;
    }
}
