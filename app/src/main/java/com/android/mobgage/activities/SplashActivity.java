package com.android.mobgage.activities;

import com.android.mobgage.R;
import com.android.mobgage.fragments.IntroFragment;
import com.android.mobgage.interfaces.IBackCallback;
import com.android.mobgage.interfaces.IFinishCallback;
import com.android.mobgage.managers.DataManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashActivity extends Activity implements IFinishCallback
{	
	private boolean afterInitData = false;
	private boolean afterDelay = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		DataManager.getInstance().initData(this, this);
		getActionBar().hide();

		// Fade in animation of splash image
		ImageView splashImage = (ImageView) findViewById(R.id.splash_image);	
		Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in_animation);
		splashImage.startAnimation(fadeIn);
		
		new Handler().postDelayed(new Runnable() 
		{
		    @Override
		    public void run() 
		    {
		    	afterDelay = true;
		    	if(afterInitData)
		    	{
		    		continueNextScreen();
		    	}
		    }
		}, 1000 + fadeIn.getDuration());

	}
	
	
	@Override
	public void initDataFinished() 
	{
		afterInitData = true;
		if(afterDelay)
		{
			continueNextScreen();
		}
	}
	
	public void continueNextScreen()
	{
		// Fade out animation of splash image
		ImageView splashImage = (ImageView) findViewById(R.id.splash_image);
		Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out_animation);
		splashImage.startAnimation(fadeOut);
		
		new Handler().postDelayed(new Runnable() 
		{
		    @Override
		    public void run() 
		    {
		    	afterDelay = true;
		    	if(afterInitData)
		    	{
		    		if(DataManager.getInstance().isShowIntroScreen())
		            {
		    			IntroFragment info = new IntroFragment(true, new IBackCallback()
		    			{
							@Override
							public void back()
							{
								startMainACtivity();
							}
						});

						info.show(getFragmentManager(), "");
		            }
		    		else
		    		{
		    			startMainACtivity();
		    		}
		    	}
		    }
		}, fadeOut.getDuration());
	}

	@Override
	public void onBackPressed() {
		startMainACtivity();
	}

	public void startMainACtivity()
	{
		Intent intent = new Intent(SplashActivity.this, MobgageMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
	}
}
