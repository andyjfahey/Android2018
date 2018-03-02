package uk.co.healtht.healthtouch.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManagerHack;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.proto.TitleData;
import uk.co.healtht.healthtouch.ui.dialog.NotificationDialog;
import uk.co.healtht.healthtouch.ui.fragment.BaseFragment;
import uk.co.healtht.healthtouch.utils.KeyboardUtil;

public class FragmentLauncherImpl implements FragmentLauncher
{
	public long touchAllowedAfterTime;
	public int pendingLaunchFragments;
	private NotificationDialog notificationDialog;
//	public DeepLink pendingDeepLink;
	public boolean inTestMode;
	private FragmentActivity activity;
	private DrawerLayout drawerLayout;
	private final Map<String, TitleData> titleDataMap = new HashMap<>();
	public boolean isDestroyed;

	public FragmentLauncherImpl(FragmentActivity activity)
	{
		notificationDialog = new NotificationDialog(activity);
		this.activity = activity;
	}

	public void setDrawerLayout(DrawerLayout drawerLayout)
	{
		this.drawerLayout = drawerLayout;
	}

	/**
	 * Replace the current main content pane fragment with a new one.
	 * This will by default put the transaction into the back-stack, and use the old Fragment to determine what the breadcrumb should be.
	 * Exceptions to this are if the new Fragment:
	 * 1. isRootOfStack(), then all the back stack entries will be popped.
	 * 2. if the new Fragment is on the middle of the back stack. The back stack will is popped until the fragment is displayed.
	 * 3. if the new Fragment does not exists, it will be added at the top of the stack.
	 *
	 * @param fragClass Content to replace the main content pane with
	 * @param data      The extras to pass to the new Fragment
	 */
	@Override
	public BaseFragment startFragment(Class<? extends BaseFragment> fragClass, Bundle data, Fragment callingFrag, int reqCode, boolean resetStack)
	{
		BaseFragment res = launchFragment(fragClass, data, callingFrag, reqCode);
		if (callingFrag != null)
		{
			res.setTargetFragment(callingFrag, reqCode);
		}

		res.setRootOfStack(resetStack);

		Crash.log(res.getClass().getName() + " -> " + data);

		if (drawerLayout != null)
		{
			drawerLayout.closeDrawer(Gravity.LEFT);
		}

		// Stop user from launching other fragment (e.g. double click on a button)
		disableTouch(300);

		return res;
	}

	@Override
	public void finishFragment(BaseFragment frag, int resultCode)
	{
		BaseFragment callingFrag = (BaseFragment) frag.getTargetFragment();
		int reqCode = frag.getTargetRequestCode();

		String fragName = frag.getClass().getName();
		popBackStack(fragName, true);

		if (callingFrag != null)
		{
			callingFrag.onActivityResult(reqCode, resultCode, frag.getResult());
		}
	}

	@Override
	public void setTitle(BaseFragment frag, CharSequence mainTitle, int colour)
	{
		String fragName = frag.getClass().getName();
		TitleData titleData = titleDataMap.get(fragName);
		if (titleData == null)
		{
			titleData = new TitleData();
			titleDataMap.put(fragName, titleData);
		}

		titleData.titleMain = mainTitle;
		titleData.colour = colour;

		BaseFragment topFragment = getTopFragment();
		if (topFragment == null)
		{
			return;
		}
		topFragment.initActionBar();

		if (topFragment == frag)
		{
			setTitle(frag);
		}
	}

	public void setTitle(BaseFragment frag)
	{
		// Note: Fragments may be created before the activity inflates the title/sub title
		if (titleDataMap != null)
		{
			String fragName = frag.getClass().getName();
			TitleData titleData = titleDataMap.get(fragName);
			if (titleData == null)
			{
				titleData = new TitleData();
			}

			if (titleData.titleMain == null)
			{
				BaseFragment parent = (BaseFragment) frag.getTargetFragment();
				if (parent != null)
				{
					setTitle(parent);
				}
			}
			else
			{
				frag.setActionBarTitle(titleData.titleMain.toString(), titleData.colour);
			}
		}
	}

	@Override
	public void showNotification(String title, String text, String actionText)
	{
		KeyboardUtil.hideKeyboard(activity);
		notificationDialog.showNotification(title, text, actionText);
	}

	@Override
	public void disableTouch(int milliSecs)
	{
		if (!inTestMode)
		{
			touchAllowedAfterTime = SystemClock.uptimeMillis() + milliSecs;
		}
	}

//	@Override
//	public DeepLink getPendingLink()
//	{
//		return pendingDeepLink;
//	}
//
//	@Override
//	public void setPendingLink(DeepLink link)
//	{
//		this.pendingDeepLink = link;
//	}

	private BaseFragment launchFragment(Class<? extends BaseFragment> fragClass, Bundle arguments, Fragment callingFrag, int reqCode)
	{
		String fragName = fragClass.getName();

		if (arguments == null)
		{
			arguments = new Bundle();
		}

		// Fragments that "own" a sub-fragment, are grouped. Though their child is the real top,
		// in terms of launching, as a group, the parent is the top.
		BaseFragment curFragment = getTopFragment();
		while (curFragment != null && curFragment.getTargetFragment() != null)
		{
			curFragment = (BaseFragment) curFragment.getTargetFragment();
		}

		if (curFragment != null && curFragment.getClass() == fragClass)
		{
			Bundle oldArguments = curFragment.getArguments();
			oldArguments.clear();
			oldArguments.putAll(arguments);

			curFragment.onNewArguments();

			return curFragment;
		}

		// Loop the fragment stack.
		// Note: We can't use findFragmentByTag() because that includes Fragments that are still
		// around but not in the back stack
		BaseFragment newTopFragment = getBackStackFragment(fragName);
		if (newTopFragment != null)
		{
			// Found fragment on the stack, so we are just setting the data
			setArguments(newTopFragment, arguments);
			popBackStack(fragName, false);
			return newTopFragment;
		}

		// Note: We were trying to get the fragments from the manager, but if they are there they be
		// on a weird state (ie finishing)
		BaseFragment newFragment = null;
		try
		{
			newFragment = fragClass.newInstance();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			Crash.logException(e);
		}

		setArguments(newFragment, arguments);

		final BaseFragment launchFrag = newFragment;
		pendingLaunchFragments++;
		HTApplication.getInstance().getUiHandler().post(new Runnable()
		{

			@Override
			public void run()
			{
				// This code needs to post on the UI thread because calling Transaction.commit() will call the fragment create/start/etc. Some Fragments need to
				// start other fragments, and when that happens, the fragment stack is on the wrong state, as the stack is only updated after everything on the
				// fragment was done. The same happens when a fragment starts a fragments and finishes itself (ie call finish() on itself)
				pendingLaunchFragments--;
				launchFragment(launchFrag);
			}
		});

		return newFragment;
	}

	public BaseFragment getTopFragment()
	{
		return (BaseFragment) activity.getSupportFragmentManager().findFragmentById(R.id.contentFragment);
	}

	private void launchFragment(BaseFragment newFragment)
	{
		if (activity.isFinishing() || isDestroyed)
		{
			return;
		}

		String fragName = newFragment.getClass().getName();

		// Note: We always keep at least one frag on the stack (Events). That is the reason we check for "1"
		boolean isRootFrag = newFragment.isRootOfStack();
		int topFragIdx = getBackStackEntryCount() - 1;
		if (isRootFrag && topFragIdx > 0)
		{
			topFragIdx = 0;
			popBackStackToRoot(false);
		}

		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		FragmentTransaction trans = fragmentManager.beginTransaction();
		if (getBackStackEntryCount() == 0)
		{
			trans.replace(R.id.contentFragment, newFragment, fragName);
		}
		else
		{
			trans.add(R.id.contentFragment, newFragment, fragName);

			if (newFragment.isFullScreen())
			{
				// Loop from the top most screen
				for (int i = topFragIdx; i >= 0; i--)
				{
					Fragment frag = getBackStackFragment(i);
					// 1 - Any fragment on the stack that is visible, can now be hidden
					// 2 - But, if we are resetting the stack, we don't want previously hidden fragments to be made visible again.
					if (!frag.isHidden() || isRootFrag)
					{
						trans.hide(frag);
					}
				}
			}
		}

		// We set an animation, but individual fragments can disable them, by overloading hasEnterAnimation() or onCreateAnimation()
		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		trans.addToBackStack(fragName);

		// Crash: using trans.commit(). Changed to commitAllowingStateLoss()
		// See: http://stackoverflow.com/questions/14177781/java-lang-illegalstateexception-can-not-perform-this-action-after-onsaveinstanc
		// and explanation on http://www.crashlytics.com/blog/3-key-ways-to-increase-android-support-library-stability/
		trans.commitAllowingStateLoss();

		// Note: We can only this because we are running one "start fragment" at a time. This will force the backstack to update.
		fragmentManager.executePendingTransactions();
	}

	public int getBackStackEntryCount()
	{
		FragmentManager fragMan = activity.getSupportFragmentManager();
		return fragMan.getBackStackEntryCount();
	}

	public void popBackStackToRoot(boolean isInclusive)
	{
		if (!this.isDestroyed)
		{
			FragmentManager fragMan = activity.getSupportFragmentManager();
			int fragId = fragMan.getBackStackEntryAt(0).getId();
			FragmentManagerHack.disableStateSaving(fragMan);
			fragMan.popBackStack(fragId, isInclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
		}
	}

	private void setArguments(BaseFragment frag, Bundle args)
	{
		// Note: calling Fragment.setArguments() more than once throws an exception
		Bundle oldArgs = frag.getArguments();
		if (oldArgs == null)
		{
			frag.setArguments(args);
		}
		else
		{
			oldArgs.clear();
			oldArgs.putAll(args);
		}
	}

	@Override
	public void updateOptionsMenu()
	{
		int topFragIdx = getBackStackEntryCount() - 1;
		if (topFragIdx < 0)
		{
			return;
		}

		String lastFullScreenVisible = null;
		BaseFragment topFrag = getBackStackFragment(topFragIdx);

		for (int i = 0; i <= topFragIdx; i++)
		{
			BaseFragment frag = getBackStackFragment(i);

			if (frag != null)
			{
				if (!frag.isFinishing())
				{
					frag.onFragmentStackUpdate(topFrag, null);

					if (BuildConfig.DEBUG && !frag.isHidden())
					{
						if (frag.isFullScreen())
						{
							if (lastFullScreenVisible != null)
							{
								Toast.makeText(activity, "FRAG VISIBLE!! " + lastFullScreenVisible, Toast.LENGTH_LONG).show();
								if (inTestMode)
								{
									Crash.reportCrash("FRAG VISIBLE!! " + lastFullScreenVisible);
								}
							}
							lastFullScreenVisible = frag.getClass().getName();
						}
					}
				}
			}
		}
	}

	private void popBackStack(String fragName, boolean isInclusive)
	{
		FragmentManager fragMan = activity.getSupportFragmentManager();
		FragmentManagerHack.disableStateSaving(fragMan);
		fragMan.popBackStack(fragName, isInclusive ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
	}

	private BaseFragment getBackStackFragment(int idx)
	{
		FragmentManager fragMan = activity.getSupportFragmentManager();
		FragmentManager.BackStackEntry fragEntry = fragMan.getBackStackEntryAt(idx);
		return (BaseFragment) fragMan.findFragmentByTag(fragEntry.getName());
	}

	private BaseFragment getBackStackFragment(String fragName)
	{
		FragmentManager fragMan = activity.getSupportFragmentManager();
		for (int i = 0; i < fragMan.getBackStackEntryCount(); i++)
		{
			FragmentManager.BackStackEntry entry = fragMan.getBackStackEntryAt(i);
			if (TextUtils.equals(entry.getName(), fragName))
			{
				return (BaseFragment) fragMan.findFragmentByTag(fragName);
			}
		}

		return null;
	}
}
