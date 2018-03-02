package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

public class FpsView extends TextView {

    private long startTime;
    private long lastFrameTime;
    private int fps;
    private long glitchTime;
    private char[] fpsBuff = { '0', '0', ' ', 'f', 'p', 's', '*' }; // GC Optimisation

    public FpsView(Context context) {
        super(context);
        init(context);
    }

    public FpsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FpsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Setup the text, so it can be measured.
        // The char[] individual chars will be later changed, and that will be reflected on the paint
        setText(fpsBuff, 0, fpsBuff.length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long now = SystemClock.elapsedRealtime();
        long delay = now - startTime;

        if (delay >= 500l || fps >= 99) {
            startTime = now;
            fpsBuff[0] = (char) ('0' + (fps / 10));
            fpsBuff[1] = (char) ('0' + (fps % 10));
            fps = 0;
        }
        
        if (now - lastFrameTime > 1000L/20L) { // If a slow frame was rendered, set glitch detected.
            glitchTime = now;
        }
        lastFrameTime = now;
        
        fpsBuff[6] = (now - glitchTime < 500L) ? '*' : ' '; // If recent slow frame happened, show a '*' 

        fps += 2; // 2 since the refresh time is half second
        postInvalidateDelayed(5); // Just some delay to avoid flooding the UI thread
    }
}
