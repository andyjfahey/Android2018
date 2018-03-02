package uk.co.healtht.healthtouch.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ViewUtils
{
	public static int dipToPixels(Resources res, int nDips)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, nDips, res.getDisplayMetrics());
	}

	public static int dipToPixels(Context res, int nDips)
	{
		return dipToPixels(res.getResources(), nDips);
	}

	public static void setText(View parent, int id, CharSequence text)
	{
		TextView tv = (TextView) parent.findViewById(id);
		if (tv != null)
		{
			tv.setText(text);

			if (text != null && tv instanceof EditText)
			{
				((EditText) tv).setSelection(text.length());
			}
		}
	}

	public static String getEditText(View parent, int id)
	{
		EditText et = (EditText) parent.findViewById(id);
		return et.getText().toString();
	}

	public static String getEditText(View parent, EditText editText)
	{

		return editText.getText().toString();
	}

	public static boolean validateFieldsEmpty(View parent, int errorId, int... fields)
	{
		boolean res = true;
		for (int id : fields)
		{
			EditText et = (EditText) parent.findViewById(id);
			if (et.getText().toString().isEmpty())
			{
				et.requestFocus();
				et.setError(parent.getResources().getString(errorId));
				res = false;
			}
		}

		return res;
	}

	public static boolean validateAllFieldsEmpty(View parent, int... fields)
	{
		boolean res = true;
		for (int id : fields)
		{
			EditText et = (EditText) parent.findViewById(id);
			res = res && (et == null || TextUtils.isEmpty(et.getText()));
		}

		return res;
	}

	public static boolean validateFieldsSame(View parent, int errorId, int... fields)
	{
		String valStr = getEditText(parent, fields[0]);
		boolean res = true;
		for (int id : fields)
		{
			EditText et = (EditText) parent.findViewById(id);
			if (!valStr.equals(et.getText().toString()))
			{
				res = false;
			}
		}

		if (!res)
		{
			for (int id : fields)
			{
				EditText et = (EditText) parent.findViewById(id);
				et.requestFocus();
				et.setError(parent.getResources().getString(errorId));
			}
		}

		return res;
	}

	public static boolean validateFieldsEmail(View parent, int errorId, int... fields)
	{
		boolean res = true;
		for (int id : fields)
		{
			EditText et = (EditText) parent.findViewById(id);
			if (!TextUtils.isValidEmail(et.getText().toString()))
			{
				et.requestFocus();
				et.setError(parent.getResources().getString(errorId));
				res = false;
			}
		}

		return res;
	}

	public static Bitmap getViewBitmap(View view)
	{
		view.clearFocus();
		view.setPressed(false);
		boolean willNotCache = view.willNotCacheDrawing();
		view.setWillNotCacheDrawing(false);
		int color = view.getDrawingCacheBackgroundColor();
		view.setDrawingCacheBackgroundColor(0);

		if (color != 0)
		{
			view.destroyDrawingCache();
		}
		view.buildDrawingCache();
		Bitmap cacheBitmap = view.getDrawingCache();
		if (cacheBitmap == null)
		{
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		view.destroyDrawingCache();
		view.setWillNotCacheDrawing(willNotCache);
		view.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}
}
