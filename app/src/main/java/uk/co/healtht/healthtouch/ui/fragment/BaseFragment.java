package uk.co.healtht.healthtouch.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Analytics;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.api.ApiProvider;
import uk.co.healtht.healthtouch.api.ApiProviderListener;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.proto.Reply;
import uk.co.healtht.healthtouch.ui.FragmentLauncher;
import uk.co.healtht.healthtouch.ui.dialog.LoadingDialog;
import uk.co.healtht.healthtouch.ui.dialog.SyncDialog;
import uk.co.healtht.healthtouch.utils.ConstantsErrors;

public abstract class BaseFragment extends Fragment implements ApiProviderListener
{

	public static final int DIALOG_DISMISS_DELAY = 3000;
	public static final int RESULT_CANCEL = Activity.RESULT_CANCELED;
	public static final int RESULT_OK = Activity.RESULT_OK;
	public static final int RESULT_ERROR = Activity.RESULT_FIRST_USER + 1;
	protected FragmentLauncher fragmentLauncher;
	public ApiProvider apiProvider;
	protected Analytics analytics;
	protected LoadingDialog loadingDialog;
	protected Intent result;
	private EnumSet<FragFlags> flags = EnumSet.of(FragFlags.IS_FULL_SCREEN);
	private SyncDialog syncDialog;
	private Handler dialogHandler;
	private int titleTextId, titleColorId;
	private String customAction = "";

	private TextView title;
	protected View actionBar;

	protected ActionBar mActionBar;
	private int colorActionBar;

	protected ImageView bannerImage;
	private Picasso picasso;

	Runnable autoDismissSyncDialog = new Runnable()
	{
		@Override
		public void run()
		{
			if (syncDialog != null && !HTApplication.getInstance().isRequiredToSync())
			{
				syncDialog.dismiss();
			}
			else
			{
				dialogHandler.postDelayed(this, DIALOG_DISMISS_DELAY);
			}
		}
	};

	public static void disableSplitTouch(View view)
	{
		if (view instanceof ViewGroup)
		{
			ViewGroup parent = (ViewGroup) view;
			parent.setMotionEventSplittingEnabled(false);
			for (int i = 0; i < parent.getChildCount(); i++)
			{
				disableSplitTouch(parent.getChildAt(i));
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// NOTE: onAttach() runs before onCreate()!
		fragmentLauncher = ((MainActivity) activity).getFragmentLauncher();
		apiProvider = HTApplication.getInstance().getApiProvider();
		loadingDialog = new LoadingDialog(activity);
		analytics = HTApplication.getInstance().getAnalytics();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		initActionBar();
		showSyncingDialog();
		addSyncProgress();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// Note: See note on MainActivity.dispatchTouchEvent()
		disableSplitTouch(view);

		if (getParentFragment() != null)
		{
			// Note: When setUserVisibleHint() is called for the first time, we still don't have a fragmentLauncher, so now is the time to update
			// onAttach() & onCreate() is to early, as fragments need to do initialization code there.
			updateOptionsMenu();
		}

		//TODO, Goto pending screen here
//		DeepLink pendingLink = fragmentLauncher.getPendingLink();
//		if (pendingLink != null)
//		{
//			fragmentLauncher.setPendingLink(null);
//			DeepLink.openLink(this, pendingLink, null);
//		}

		super.onViewCreated(view, savedInstanceState);
	}

	public Intent getResult()
	{
		return result;
	}

	public final boolean isRootOfStack()
	{
		return flags.contains(FragFlags.IS_ROOT_OF_STACK);
	}

	public void setRootOfStack(boolean isRoot)
	{
		setFlagValue(FragFlags.IS_ROOT_OF_STACK, isRoot);
	}

	public final boolean isFullScreen()
	{
		return flags.contains(FragFlags.IS_FULL_SCREEN);
	}

	@SuppressWarnings("unused")
	protected void setFullScreen(boolean isFullScreen)
	{
		setFlagValue(FragFlags.IS_FULL_SCREEN, isFullScreen);
	}

	protected void showSyncingDialog()
	{
		if (HTApplication.getInstance().isRequiredToSync() && apiProvider.getSettingsUser().getSession() != null)
		{
			syncDialog = new SyncDialog(getActivity());
			syncDialog.create().show();
			syncDialog.setDialogMessage(getActivity().getResources().getString(R.string.sync_dialog_text));

			//            apiProvider.syncWithWeb();

			dialogHandler = new Handler();
			dialogHandler.postDelayed(autoDismissSyncDialog, DIALOG_DISMISS_DELAY);
		}
	}


	protected void addSyncProgress()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (HTApplication.getInstance().isSyncing())
				{
					HTApplication.getInstance().setLoadingFragment(new WeakReference<>(BaseFragment.this));
					loadingDialog.show(true);
				}
			}
		});
	}

	public void removeSyncProgress()
	{
		Activity activity = getActivity();
		if (activity == null)
		{
			return;
		}

		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (!HTApplication.getInstance().isSyncing())
				{
					loadingDialog.hide(true);
				}
			}
		});
	}

	/**
	 * Should be implemented by child classes, to intercept the back key action.
	 *
	 * @return true if the back key was intercepted.
	 */
	public boolean onBackPressed()
	{
		return false;
	}

	public boolean hasEnterAnimation()
	{
		// Don't animate when entering root fragments by default. They are usually open from the menu.
		return !isRootOfStack();
	}

	public void onNewArguments()
	{

	}

	public void finish(int resultCode)
	{
		if (!isFinishing())
		{
			if (fragmentLauncher != null)
			{
				setFlagValue(FragFlags.IS_FINISHING, true);
				fragmentLauncher.finishFragment(this, resultCode);
			}
		}
	}

	public boolean isFinishing()
	{
		return flags.contains(FragFlags.IS_FINISHING);
	}

	public final boolean isTopFragment()
	{
		return flags.contains(FragFlags.IS_TOP);
	}

	public void onFragmentStackUpdate(BaseFragment topFrag, BaseFragment parentFrag)
	{
		boolean isTopFragment = (topFrag == this);
		setFlagValue(FragFlags.IS_TOP, isTopFragment);
		setMenuVisibility(isTopFragment && hasOptionsMenu());

		List<Fragment> childFragments = getChildFragmentManager().getFragments();
		if (childFragments != null)
		{
			for (Fragment frag : childFragments)
			{
				if (frag instanceof BaseFragment && frag.getUserVisibleHint())
				{ // frag can be null!
					BaseFragment baseFrag = (BaseFragment) frag;
					baseFrag.onFragmentStackUpdate(isTopFragment ? baseFrag : topFrag, this);
				}
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		boolean oldVisibility = getUserVisibleHint();

		super.setUserVisibleHint(isVisibleToUser);
		// If the visibility changes, we may no longer own the Option Menu
		if (oldVisibility != getUserVisibleHint())
		{
			updateOptionsMenu();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (BuildConfig.DEBUG)
		{
			EndPointProvider endPointProvider = apiProvider.containsListener(this);
			if (endPointProvider != null)
			{
				showNotification("ERROR", getClass().getName() + " Not unregistered from: " + endPointProvider.getEndPoint(), 0);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// Make sure that one fragment doesn't leak click events to the fragment bellow
		if (getView() != null)
		{
			getView().setClickable(true);
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		fragmentLauncher = null;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		loadingDialog.hide(true); // hiding was done on onDestroy, and the frame rate dropped from 60fps to 8fps when the hiding was done there...
		setTitle(null);
	}

	@Override
	public void startActivity(Intent intent)
	{
		try
		{
			super.startActivity(intent);
			disableTouch(300);
		}
		catch (Throwable e)
		{
			Crash.logException(e);
		}
	}

	public void startActivity(String url)
	{
		try
		{
			Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
			startActivity(intent);
		}
		catch (Throwable e)
		{
			Crash.logException(e);
		}
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		if (enter && !hasEnterAnimation())
		{
			Animation a = new Animation()
			{
			};
			a.setDuration(0);
			return a;
		}
		return super.onCreateAnimation(transit, enter, nextAnim);
	}

	@Override
	public void onDataLoaded(EndPointProvider provider, Object providerData)
	{
		if (providerData instanceof Reply)
		{
			Reply msg = (Reply) providerData;

			if (msg.message != null && msg.message instanceof String)
			{
				showToast((String) msg.message, false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onDataLoadedError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode)
	{
		if (BuildConfig.DEBUG)
		{
			System.out.println(">> onDataLoadedError " + provider.getEndPoint() + " debugMsg = " + debugMsg);
		}

		loadingDialog.hide(true);

		String message = null;
		if (errorObj != null)
		{
			message = (String) errorObj.get("message");
			List<String> fields = (List<String>) errorObj.get("fields");
			if (fields != null && setTextFieldErrors(fields, message))
			{
				// Error message was handled
				return;
			}
		}

		if (message == null)
		{
			try
			{
				message = getString(R.string.error_internet_down);
			}
			catch (IllegalStateException ex)
			{
				message = "";
			}

			if (BuildConfig.DEBUG)
			{
				message += ": " + debugMsg + ": " + provider.getEndPoint() + " code = " + errorCode;
			}
		}

		showNotification(null, message, R.string.btn_OK);

	}

	protected boolean setTextFieldErrors(List<String> fields, String message)
	{
		// TODO
		//        Drawable icon = getResources().getDrawable(R.drawable.chkbox_unchecked);
		//        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
		//        Drawable icon = null;
		return setTextFieldErrors(fields, message, null);
	}

	private boolean setTextFieldErrors(List<String> fields, String message, Drawable overrideIcon)
	{
		// Some of the fragments don't unregister their listeners when the View is destroyed (only when the fragment is destroyed). Ignore the error message.
		if (getView() == null)
		{
			return true;
		}

		// Lets find all text views that have tag set
		Map<String, TextView> textViews = new HashMap<>();
		findTextView(getView(), textViews);

		TextView firstErrorField = null;
		for (String field : fields)
		{
			TextView textView = textViews.get(field);
			if (textView != null)
			{
				if (overrideIcon == null)
				{
					textView.setError(message);
				}
				else
				{
					textView.setError(message, (message == null ? null : overrideIcon));
				}
			}

			if (firstErrorField == null)
			{
				firstErrorField = textView;
			}
		}

		if (firstErrorField != null)
		{
			firstErrorField.requestFocus();
		}

		return (firstErrorField != null);
	}

	private void findTextView(View view, Map<String, TextView> textViews)
	{
		if (view instanceof ViewGroup)
		{
			ViewGroup parent = (ViewGroup) view;

			for (int i = 0; i < parent.getChildCount(); i++)
			{
				findTextView(parent.getChildAt(i), textViews);
			}
		}
		else
		{
			Object tag = view.getTag();
			if (tag != null && view instanceof TextView && tag instanceof String)
			{
				textViews.put((String) tag, (TextView) view);
			}
		}
	}

	public BaseFragment startFragment(Class<? extends BaseFragment> fragmentClass, Bundle data)
	{
		if (fragmentLauncher != null)
		{
			return fragmentLauncher.startFragment(fragmentClass, data, null, 0, false);
		}

		return null;
	}

	@SuppressWarnings("unused")
	protected BaseFragment startFragmentAsRoot(Class<? extends BaseFragment> fragmentClass, Bundle data)
	{
		BaseFragment frag = startFragment(fragmentClass, data);
		if (frag != null)
		{
			frag.setRootOfStack(true);
		}

		return frag;
	}

	protected BaseFragment startFragmentForResult(int requestCode, Class<? extends BaseFragment> fragmentClass, Bundle data)
	{
		BaseFragment frag = null;
		if (fragmentLauncher != null)
		{
			frag = fragmentLauncher.startFragment(fragmentClass, data, this, requestCode, false);
		}

		return frag;
	}

	public void setTitle(CharSequence mainTitle, int colourId, String customAction)
	{
		this.customAction = customAction;
		setTitle(mainTitle, colourId);
	}

	public void setTitle(int titleResId, int colourId, String customAction)
	{
		this.customAction = customAction;
		setTitle(titleResId, colourId);
	}

	public void setTitle(CharSequence mainTitle, int colourId)
	{
		if (fragmentLauncher != null)
		{
			fragmentLauncher.setTitle(this, mainTitle, getResources().getColor(colourId));
		}
		mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
		if (mActionBar != null && colourId != 0)
		{
			colorActionBar = colourId;
			mActionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), colorActionBar)));
			mActionBar.show();
		}
	}

	public void setActionButton(int visibility, int resource)
	{
		TextView customActionViewBtn = (TextView) actionBar.findViewById(R.id.custom_action_btn);
		customActionViewBtn.setVisibility(visibility);
		customActionViewBtn.setText(resource);
	}

	public void setTitle(int titleResId, int colourId)
	{
		titleTextId = titleResId;
		titleColorId = colourId;

		if (fragmentLauncher != null)
		{
			setTitle(getString(titleResId), colourId);
		}
	}

	public void setTitleHome(CharSequence mainTitle)
	{
		setTitle(mainTitle, R.color.white);
	}

	public void setTitle(CharSequence mainTitle)
	{
		setTitle(mainTitle, R.color.title_bar);
	}

	public void setTitle(int titleResId)
	{
		setTitle(titleResId, R.color.title_bar);
	}

	public void updateOptionsMenu()
	{
		if (fragmentLauncher != null)
		{
			fragmentLauncher.updateOptionsMenu();
		}
	}

	public void showNotification(String title, String text, int actionTextId)
	{
		if (BuildConfig.DEBUG)
		{
			System.out.println("Notification: " + title + ": " + text);
		}

		Log.e("text", "text = " + text);
		if (text.contains(ConstantsErrors.AUTHENTIFICATION_ERROR))
		{
			text = getResources().getString(R.string.email_or_password_incrrect);
		}
		else if (text.contains(ConstantsErrors.ACCESS_SERVER_ERROR))
		{
			// If we can't access server no need to show dialog, just show old data
			//text = null;
			text = getResources().getString(R.string.please_verify_internet);
		}

		if (fragmentLauncher != null)
		{
			String actionText = (actionTextId > 0) ? getString(actionTextId) : null;
			fragmentLauncher.showNotification(title, text, actionText);
		}
	}

	public void showToast(String text, boolean isLong)
	{
		Context ctx = getActivity();
		if (ctx != null)
		{
			Toast.makeText(ctx, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
		}
	}

	public void disableTouch(int milliSecs)
	{
		if (fragmentLauncher != null)
		{
			fragmentLauncher.disableTouch(milliSecs);
		}
	}

	private void setFlagValue(FragFlags flag, boolean value)
	{
		if (value)
		{
			flags.add(flag);
		}
		else
		{
			flags.remove(flag);
		}
	}


	private enum FragFlags
	{
		IS_FINISHING, //
		IS_TOP, //
		IS_ROOT_OF_STACK, //
		IS_FULL_SCREEN, //
	}

	public void initActionBar()
	{
		View view = getActivity().getLayoutInflater().inflate(
				getActionBarLayout(), null);
		mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
		if (mActionBar != null)
		{
			mActionBar.setDisplayShowTitleEnabled(false);
			mActionBar.setDisplayShowCustomEnabled(true);
			if (colorActionBar == 0)
			{
				colorActionBar = R.color.white;
			}
			mActionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.white)));
			mActionBar.setCustomView(view);
		}
		// do not delete !!! It will be uncommented soon
		view.findViewById(R.id.ht_logo).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				onHealthTouchLogoPressed();
			}
		});

		initActionBarIndicator(view);
		initActionBarCustomAction(view);

		view.findViewById(R.id.security_iv).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startFragment(SecurityFragment.class, null);
			}
		});
		title = (TextView) view.findViewById(R.id.title_text);
		this.actionBar = view.findViewById(R.id.action_bar_root);
		bannerImage = (ImageView) view.findViewById(R.id.health_touch_logo);
		//		getBanneRIMG();
	}

	private void initActionBarIndicator(View view)
	{
		View indicatorView = view.findViewById(R.id.action_indicator);

		if (indicatorView != null)
		{
			indicatorView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					onHealthTouchLogoPressed();
				}
			});
		}
	}

	private void initActionBarCustomAction(View view)
	{
		View customActionView = view.findViewById(R.id.custom_action);
		TextView customActionViewBtn = (TextView) view.findViewById(R.id.custom_action_btn);

		if (customActionViewBtn != null)
		{
			customActionViewBtn.setVisibility((!customAction.equals("")) ? View.VISIBLE : View.INVISIBLE);
			customActionView.setVisibility((!customAction.equals("")) ? View.VISIBLE : View.INVISIBLE);
			customActionViewBtn.setText(customAction);
		}
	}

	public void setCustomActionListener(View.OnClickListener clickListener)
	{
		if (actionBar != null)
		{
			actionBar.findViewById(R.id.custom_action_btn).setOnClickListener(clickListener);
		}
	}

	protected void onHealthTouchLogoPressed()
	{
		getActivity().onBackPressed();
	}

	protected int getActionBarLayout()
	{
		return R.layout.noncustom_toolbar_view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	public void setActionBarTitle(String text, int color)
	{
		title.setText(text);
		if (mActionBar != null && getActivity() != null && colorActionBar != 0)
		{
			mActionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getActivity(), colorActionBar)));
		}
		actionBar.setBackgroundColor(color);
	}

	@Override
	public void onNoData(EndPointProvider endPointProvider)
	{
		finish(RESULT_CANCEL);
		Bundle data = new Bundle();
		data.putInt("titleText", titleTextId);
		data.putInt("titleColor", titleColorId);
		startFragment(NoDataFragment.class, data);
	}

	public abstract void reload();


	//	private void getBanneRIMG()
	//	{
	//		String bannerIMG = SharredUtils.getValue(getActivity(), ConstantsPref.Banner_IMG);
	//		if (bannerIMG != null && !bannerIMG.equalsIgnoreCase("None") && bannerImage != null)
	//		{
	//			chargeBanner(bannerIMG);
	//
	//		}
	//	}
	//
	//	private void chargeBanner(final String URL)
	//	{
	//		ViewTreeObserver vto = bannerImage.getViewTreeObserver();
	//		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
	//		{
	//			public boolean onPreDraw()
	//			{
	//				bannerImage.getViewTreeObserver().removeOnPreDrawListener(this);
	//				if (bannerImage.getMeasuredWidth() > 0 && bannerImage.getMeasuredHeight() > 0)
	//				{
	//					picasso.with(getActivity())
	//							.load(URL)
	//							.resize(bannerImage.getMeasuredWidth(), bannerImage.getMeasuredHeight())
	//							.placeholder(R.drawable.health_touch)
	//							.error(R.drawable.health_touch)
	//							.centerCrop()
	//							.into(bannerImage);
	//				}
	//
	//				return true;
	//			}
	//		});
	//	}
}