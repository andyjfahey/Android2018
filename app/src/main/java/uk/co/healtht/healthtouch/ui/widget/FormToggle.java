package uk.co.healtht.healthtouch.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import uk.co.healtht.healthtouch.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormToggle extends ToggleButton implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    protected List<Paint> squarePaints = new ArrayList<>();
    protected List<Paint> textPaints = new ArrayList<>();
    protected List<String> extraOptions = new ArrayList<>();
    protected Paint borderPaint;
    protected static final int BORDER_WIDTH = 5;

    public FormToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FormToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FormToggle(Context context) {
        super(context);
        init(context);
    }

    //TODO generify and abstract to enable >2 elements
    private void init(Context context) {
        generateColorPaints();
        generateTextPaints(context);

        borderPaint = new Paint();
        borderPaint.setColor(getResources().getColor(R.color.tiffany_blue));

        extraOptions.add("");
        extraOptions.add("");

        setOnClickListener(this);
        setOnCheckedChangeListener(this);
    }

    private void generateColorPaints() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getResources().getColor(R.color.tiffany_blue));
        Paint paintSecond = new Paint();
        paintSecond.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSecond.setColor(Color.WHITE);
        squarePaints.add(paint);
        squarePaints.add(paintSecond);
    }

    private void generateTextPaints(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        float px = 16 * density;
        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(px);
        Paint textPaintCyan = new Paint();
        textPaintCyan.setTextAlign(Paint.Align.CENTER);
        textPaintCyan.setColor(getResources().getColor(R.color.tiffany_blue));
        textPaintCyan.setTextSize(px);
        textPaints.add(textPaint);
        textPaints.add(textPaintCyan);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        int paintSize = squarePaints.size();
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);

        for (int i = 0; i < paintSize; i++) {
            Rect rect = new Rect(getWidth() / paintSize * i + BORDER_WIDTH, BORDER_WIDTH, getWidth() / (paintSize - i) - BORDER_WIDTH, getHeight() - BORDER_WIDTH);
            Paint textPaint = textPaints.get(i);
            canvas.drawRect(rect, squarePaints.get(i));
            canvas.drawText(extraOptions.get(i), rect.exactCenterX(), rect.exactCenterY() - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
        }
    }

    public void setExtraValueOne(String value) {
        extraOptions.remove(0);
        extraOptions.add(0, value);
    }

    public void setExtraValueTwo(String value) {
        extraOptions.remove(1);
        extraOptions.add(1, value);
    }

    @Override
    public void onClick(View v) {
        setChecked(isChecked());
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Collections.reverse(squarePaints);
        Collections.reverse(textPaints);
        invalidate();
    }
}
