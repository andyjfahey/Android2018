package uk.co.healtht.healthtouch.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import uk.co.healtht.healthtouch.utils.ViewUtils;

public class TrackerCircularBar extends View
{
	private static final int COLOUR_TEXT = 0xFF4C5356;
	private static final int COLOUR_BORDER = 0xFFD9D9D9;
	private static final int COLOUR_ARC_ALL = 0xFFEEEEEE;
	private static final int COLOUR_ARC_VALUE = 0xFF11B4B4;

	private Paint mArcBorderPaint, mArcPaint, mTextPaint;
	private Animator mDrawAnimator;
	private RectF rec;
	private int max;
	private int animatedValue;
	private int currentValue;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public TrackerCircularBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public TrackerCircularBar(Context context)
	{
		super(context);
		init();
	}

	public TrackerCircularBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public TrackerCircularBar(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		rec = new RectF();

		mArcBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcBorderPaint.setStyle(Paint.Style.STROKE);
		mArcBorderPaint.setStrokeWidth(ViewUtils.dipToPixels(getResources(), 6));
		mArcBorderPaint.setColor(COLOUR_BORDER);
		mArcBorderPaint.setStrokeCap(Paint.Cap.ROUND);

		mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setStyle(Paint.Style.STROKE);
		mArcPaint.setStrokeWidth(ViewUtils.dipToPixels(getResources(), 5));
		mArcPaint.setStrokeCap(Paint.Cap.ROUND);

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setColor(getTextColor());
		mTextPaint.setTextSize(ViewUtils.dipToPixels(getResources(), 14));

		max = 1; // Cannot be zero

		setWillNotDraw(false);
	}

	protected int getTextColor()
	{
		return 0xFF4C5356;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (mDrawAnimator == null)
		{
			mDrawAnimator = ObjectAnimator
					.ofInt(this, "animatedValue", 0, currentValue)
					.setDuration(750);
			mDrawAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
			mDrawAnimator.start();
		}

		rec.left = mArcBorderPaint.getStrokeWidth() / 2f;
		rec.top = mArcBorderPaint.getStrokeWidth() / 2f;
		rec.right = getWidth() - mArcBorderPaint.getStrokeWidth() / 2;
		rec.bottom = getHeight() - mArcBorderPaint.getStrokeWidth() / 2;

		final float TOTAL_SWEEP = 360 - 25 - 25;

		// draw outer border
		canvas.drawArc(rec, 90 + 25, TOTAL_SWEEP, false, mArcBorderPaint);

		float borderWidth = (mArcBorderPaint.getStrokeWidth() - mArcPaint.getStrokeWidth()) / 8f;
		rec.left += borderWidth;
		rec.top += borderWidth;
		rec.right -= borderWidth;
		rec.bottom -= borderWidth;

		// draw all outer arc
		mArcPaint.setColor(COLOUR_ARC_ALL);
		canvas.drawArc(rec, 90 + 25, TOTAL_SWEEP, false, mArcPaint);

		// Draw inner arc
		mArcPaint.setColor(COLOUR_ARC_VALUE);
		float sweepAngle = (animatedValue * TOTAL_SWEEP) / max;
		canvas.drawArc(rec, 90 + 25, sweepAngle, false, mArcPaint);

		canvas.drawText(animatedValue + "%", getWidth() / 2, (getHeight() + mTextPaint.getTextSize()) / 2, mTextPaint);
	}

	public void setCurrentValue(int value, int max)
	{
		this.currentValue = value;
		this.max = Math.max(max, value);

		mDrawAnimator = null;
		invalidate();
	}

	/**
	 * DONT USE THIS METHOD
	 *
	 * @param value
	 */
	public void setAnimatedValue(int value)
	{
		this.animatedValue = value;
		invalidate();
	}
}
