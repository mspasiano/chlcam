package it.cnr.chlcam;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import it.cnr.chlcam.dao.CROCAMDAOEquation;
import it.cnr.chlcam.database.CROCAMSchema;
import it.cnr.chlcam.database.Database;
import it.cnr.chlcam.model.Equation;
import it.cnr.chlcam.model.EquationType;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class AddEquationActivity extends Activity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private SaveLoginTask mSaveTask = null;
	private MenuItem actionAdd;
	// UI references.
	private EditText mNameView;
	private EditText mEquationView;
	private RadioGroup mRegressionModelView;

	private View mSaveFormView;
	private View mSaveStatusView;
	private TextView mSaveStatusMessageView;

	private Long mId;
	private String mName, mEquation, mRegressionModel;
	private SlidingMenu slidingMenu;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_equation);

		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);		
        slidingMenu.setMenu(R.layout.slidingmenu);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);

		// Set up the login form.
		mNameView = (EditText) findViewById(R.id.name);
		mEquationView = (EditText) findViewById(R.id.equation);
		mRegressionModelView = (RadioGroup) findViewById(R.id.regression_model);
		
		mSaveFormView = findViewById(R.id.add_equation_form);
		mSaveStatusView = findViewById(R.id.save_status);
		mSaveStatusMessageView = (TextView) findViewById(R.id.save_status_message);

		findViewById(R.id.save_in_button).setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View view) {
					attemptSave(R.id.save_in_button);
				}
		});
		findViewById(R.id.delete_in_button).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptSave(R.id.delete_in_button);
					}
			});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_list_equation, menu);
        actionAdd = menu.findItem(R.id.action_add);
        actionAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        	public boolean onMenuItemClick(MenuItem item) {	
        		findViewById(R.id.delete_in_button).setVisibility(View.INVISIBLE);
        		actionAdd.setVisible(false);
        		mId = null;		
            	mNameView.setText("");
            	mEquationView.setText("");
            	mRegressionModelView.check(R.id.multiple_regression_model);        		
        		return true;
        	}
        });
		return super.onCreateOptionsMenu(menu);
	}
	@Override
    public void onBackPressed() {
        if ( slidingMenu.isMenuShowing()) {
            slidingMenu.toggle();
        }
        else {
            super.onBackPressed();
        }
    }
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            this.slidingMenu.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.slidingMenu.toggle();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptSave(Integer idButton) {
		if (mSaveTask != null) {
			return;
		}

		// Reset errors.
		mNameView.setError(null);
		mEquationView.setError(null);

		// Store values at the time of the login attempt.
		mName = mNameView.getText().toString();
		mEquation = mEquationView.getText().toString();
		int selectedId = mRegressionModelView.getCheckedRadioButtonId();
		mRegressionModel = ((RadioButton)findViewById(selectedId)).getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mEquation)) {
			mEquationView.setError(getString(R.string.error_field_required));
			focusView = mEquationView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mName)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mSaveStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mSaveTask = new SaveLoginTask(idButton);
			mSaveTask.execute((Void)null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mSaveStatusView.setVisibility(View.VISIBLE);
			mSaveStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSaveStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mSaveFormView.setVisibility(View.VISIBLE);
			mSaveFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSaveFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mSaveStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSaveFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class SaveLoginTask extends AsyncTask<Void, Void, Boolean> {
		private final int buttonId;
		public SaveLoginTask(int buttonId) {
			super();
			this.buttonId = buttonId;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
        	Database db = Database.create(getApplicationContext());
        	CROCAMDAOEquation dao = new CROCAMDAOEquation(db.open());
			if (buttonId == R.id.save_in_button) {
		        try {
					Calculable calc = new ExpressionBuilder(mEquation)
					.withVariable("R",254)
					.withVariable("G",254)
					.withVariable("B",254)
					.build();
					calc.calculate();
				} catch (UnknownFunctionException e) {
					 return false;
				} catch (UnparsableExpressionException e) {
					 return false;
				}
	        	if (mId == null)
	        		dao.insert(mName, mEquation, EquationType.valueFromLabel(mRegressionModel), false);
	        	else
	        		dao.update(mId, mName, mEquation, EquationType.valueFromLabel(mRegressionModel), false);
				
			} else {
				dao.delete(mId, CROCAMSchema.TABLENAME_EQUATIONS);
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mSaveTask = null;
			showProgress(false);

			if (success) {
				finish();
				Toast.makeText(getApplicationContext(), 
						buttonId == R.id.save_in_button ? 
								getString(R.string.equation_confirmed, mName) :
									getString(R.string.equation_deleted, mName), 
						Toast.LENGTH_SHORT).show();
			} else {
				mEquationView
						.setError(getString(R.string.error_invalid_equation));
				mEquationView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mSaveTask = null;
			showProgress(false);
		}
	}
	
	public void editEquation (Equation equation) {
		findViewById(R.id.delete_in_button).setVisibility(View.VISIBLE);
		actionAdd.setVisible(true);
		mId = equation.getId();		
    	mNameView.setText(equation.getName());
    	mEquationView.setText(equation.getExpression());
    	mRegressionModelView.check(
    			equation.getType() ==  EquationType.multiple_regression_model ? 
    					R.id.multiple_regression_model : 
    						R.id.single_regression_model);		
	}
	
	public SlidingMenu getSlidingMenu() {
		return slidingMenu;
	}
	
}
