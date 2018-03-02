package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class OwnValueFormatter implements ValueFormatter{

    /** decimalformat for formatting */
    private DecimalFormat mFormat;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public OwnValueFormatter(int digits) {

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value) {
        // avoid memory allocations here (for performance)
        return mFormat.format(value);
    }
}
