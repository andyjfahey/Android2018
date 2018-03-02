package android.support.v4.app;

public class FragmentManagerHack {

    /**
     * When we popBackStack after state has been saved we cannot do it with allowStateLoss, so throws an exception.
     * This stops Fragment Manager from saving any fragment state. This is OK because we don't save fragment state.
     *
     * @param manager
     */
    public static void disableStateSaving(FragmentManager manager) {
        ((FragmentManagerImpl) manager).noteStateNotSaved();
    }

}
