package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by andyj on 12/01/2018.
 */

public class TrackerCircularBarWhiteFont extends TrackerCircularBar {

    public TrackerCircularBarWhiteFont(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TrackerCircularBarWhiteFont(Context context) {
        super(context);
    }

    public TrackerCircularBarWhiteFont(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrackerCircularBarWhiteFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getTextColor()
    {
        return 0xFFFFFFFF;
    }
}
