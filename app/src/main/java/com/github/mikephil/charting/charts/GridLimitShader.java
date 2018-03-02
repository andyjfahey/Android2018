package com.github.mikephil.charting.charts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class GridLimitShader {
    private final BarLineChartBase chart;
    private Paint mGridLimitPaint;
    private float maxY;
    private float minY;
    private float maxThreshold;
    private float minThreshold;

    GridLimitShader(BarLineChartBase chart) {
        this.chart = chart;

        mGridLimitPaint = new Paint();
        mGridLimitPaint.setStyle(Paint.Style.FILL);
        mGridLimitPaint.setColor(Color.parseColor("#88F5C2C2")); // light grey
    }

    public void drawGridBackground(Canvas c) {
        RectF r = chart.mViewPortHandler.getContentRect();
        float step = (maxY - minY);
        float deltaMax = maxY - maxThreshold;
        float deltaMin = minThreshold - minY;

        c.drawRect(r.left, r.top, r.right, r.top + r.height() * (deltaMax / step), mGridLimitPaint);
        c.drawRect(r.left, r.bottom - r.height() * (deltaMin / step), r.right, r.bottom, mGridLimitPaint);
    }

    public void setMax(float maxY) {
        this.maxY = maxY;
    }

    public void setMin(float minY) {
        this.minY = minY;
    }

    public void setMaxThreshold(float maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public void setMinThreshold(float minThreshold){
        this.minThreshold = minThreshold;
    }
}
