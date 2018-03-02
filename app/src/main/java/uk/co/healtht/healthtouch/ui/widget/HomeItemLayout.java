package uk.co.healtht.healthtouch.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.utils.ViewUtils;


public class HomeItemLayout extends LinearLayout
{
	private View imageView, textView, overlayView;


	public HomeItemLayout(Context context)
	{
		super(context);
		init();
	}

	public HomeItemLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public HomeItemLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public HomeItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init()
	{
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		setMinimumHeight(ViewUtils.dipToPixels(getResources(), 80));
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		this.imageView = getChildAt(0);
		// Child 1 is the separator
		this.textView = getChildAt(2);
		this.overlayView = new View(getContext());

		overlayView.setBackgroundResource(R.drawable.item_home_overlay);
		addView(overlayView);

		// Make sure this view don't mess with the measure
		overlayView.getLayoutParams().height = 0;
		overlayView.getLayoutParams().width = 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int pad = getMeasuredWidth() / 20;
		imageView.setPadding(pad, 0, pad, 0);
		textView.setPadding(pad, 0, 0, 0);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);

		overlayView.layout(0, 0, getWidth(), getHeight());
	}
}
