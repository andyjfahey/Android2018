package uk.co.healtht.healthtouch.ui.widget;

import java.util.Calendar;

/**
 * Created by Vadim on 28.08.2015.
 */
public interface GraphActionListener {

    void onCustomDateSelected(Calendar startDate, Calendar endDate);

    void onNonCustomTabSelected();
}