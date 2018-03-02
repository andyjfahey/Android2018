package uk.co.healtht.healthtouch.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;

public interface FragmentLauncher {

    BaseFragment startFragment(Class<? extends BaseFragment> fragmentClass, Bundle data, Fragment callingFrag, int reqCode, boolean resetStack);

    void finishFragment(BaseFragment frag, int result);

    void setTitle(BaseFragment frag, CharSequence mainTitle, int colour);

    void updateOptionsMenu();

    void showNotification(String title, String text, String actionText);

    /**
     * During an animation, we may need to disable touch for a certain amount of time.
     *
     * @param milliSecs For how long time we want to disable the touch
     */
    void disableTouch(int milliSecs);

//    DeepLink getPendingLink();
//
//    void setPendingLink(DeepLink link);
}
