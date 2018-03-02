
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class MyXAxisRenderer extends XAxisRenderer {

    public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    public void computeAxis(float xValAverageLength, List<String> xValues) {
        super.computeAxis(xValAverageLength, xValues);

        // The labels are rotated 45 degrees, we need to adjust the axis size
        mXAxis.mLabelWidth /= 1.5;
        mXAxis.mLabelHeight *= 1.8;
    }

    /**
     * draws the x-labels on the specified y-position
     *
     * @param pos
     */
    protected void drawLabels(Canvas c, float pos) {

        // pre allocate to save performance (dont allocate in loop)
        float[] position = new float[]{
                0f, 0f
        };
//        mXAxis.mAxisLabelModulus
        for (int i = mMinX; i <= mMaxX; i += 1) {
            position[0] = i;

            mTrans.pointValuesToPixel(position);

            if (mViewPortHandler.isInBoundsX(position[0])) {

                String label = mXAxis.getValues().get(i);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {
                //if (true) {

                    // avoid clipping of the last
                    if (i == mXAxis.getValues().size() - 1 && mXAxis.getValues().size() > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                        if (width > mViewPortHandler.offsetRight() * 2
                                && position[0] + width > mViewPortHandler.getChartWidth()) {
                            position[0] -= width / 2;
                        }

                        // avoid clipping of the first
                    }
                    else if (i == 0) {

                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        position[0] += width / 2;
                    }
                }

                // My code: Rotate the text
                c.save();
                c.rotate(-45, position[0], pos);
                Rect r = c.getClipBounds();
                c.clipRect(r.left, r.top - 100, r.right, c.getHeight());

                c.drawText(label, position[0],
                        pos,
                        mAxisLabelPaint);

                c.restore();
            }
        }
    }
}