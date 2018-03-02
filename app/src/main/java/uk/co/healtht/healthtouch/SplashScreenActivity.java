package uk.co.healtht.healthtouch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.healtht.healthtouch.model.delegate.HTBrandingDelegate;
import uk.co.healtht.healthtouch.model.entities.HTBranding;
import uk.co.healtht.healthtouch.util_helpers.PreferencesManager;
import uk.co.healtht.healthtouch.util_helpers.StringUtil;

/**
 * Created by HAYTHEM Suissi on 2/27/17.
 */

public class SplashScreenActivity extends AppCompatActivity
{

	private int SPLASH_TIME_OUT = 3000;
	private ImageView splashLogo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		splashLogo = (ImageView) findViewById(R.id.splashScreen);

		List<HTBranding> htBrandingList = new HTBrandingDelegate().getAllWhereDeleteAtIsNull();
		if (htBrandingList != null && htBrandingList.size() > 0)
		{
			HTBranding htBranding = htBrandingList.get(0); //get only first
			if (htBranding != null)
			{
				downloadImage(htBranding);
			}
		}

		startNextActivity();
	}

	private void downloadImage(HTBranding htBranding)
	{
		if (!StringUtil.isEmpty(htBranding.splash_url) && !htBranding.splash_url.equalsIgnoreCase("None"))
		{
			Picasso.with(SplashScreenActivity.this)
					.load(htBranding.splash_url)
					.placeholder(R.drawable.health_touch)
					.error(R.drawable.health_touch)
					.into(splashLogo);
		}
	}

	private void startNextActivity()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}
