package uk.co.healtht.healthtouch.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

public class InputFilterWeight extends DigitsKeyListener {
    private int digits = 3;

    public InputFilterWeight() {
        super(false, true);
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int digitsStart, int digitsEnd) {
        CharSequence out = super.filter(source, start, end, dest, digitsStart, digitsEnd);

        if (out != null) {
            source = out;
            start = 0;
            end = out.length();
        }

        int len = end - start;
        if (len == 0) {
            return source;
        }

        int digitsLength = dest.length();
        for (int i = 0; i < digitsStart; i++) {
            if (dest.charAt(i) == '.') {
                return (digitsLength - (i + 1) + len > digits) ? "" : new SpannableStringBuilder(source, start, end);
            }
        }

        for (int i = start; i < end; ++i) {
            if (source.charAt(i) == '.') {
                if ((digitsLength - digitsEnd) + (end - (i + 1)) > digits)
                    return "";
                else
                    break;
            }
        }
        return new SpannableStringBuilder(source, start, end);
    }
}
