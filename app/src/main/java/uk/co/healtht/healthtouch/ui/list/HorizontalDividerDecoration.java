package uk.co.healtht.healthtouch.ui.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import uk.co.healtht.healthtouch.R;

public class HorizontalDividerDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        if (paint == null) {
            // Initialise fields
            paint = new Paint();

            paint.setColor(parent.getResources().getColor(R.color.menu_separator));
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
        }
    }
}
