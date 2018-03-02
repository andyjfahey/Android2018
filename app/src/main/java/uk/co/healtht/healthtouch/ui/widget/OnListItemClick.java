package uk.co.healtht.healthtouch.ui.widget;

import android.view.View;

public interface OnListItemClick<T> {
    void onItemListClicked(int position, View clickedView, T itemData);
}
