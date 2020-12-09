package it.cnr.chlcam;

import it.cnr.chlcam.dao.CROCAMDAOResult;
import it.cnr.chlcam.database.CROCAMSchema;
import it.cnr.chlcam.database.Database;
import it.cnr.chlcam.model.Result;
import it.cnr.chlcam.util.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class ShowLogActivity extends Activity {

	private MenuItem actionShare;
	private SlidingMenu slidingMenu;	
	private Result result;
	private Database db;
	private CROCAMDAOResult dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_logs);

		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);		
        slidingMenu.setMenu(R.layout.slidingmenu_logs);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
		db = Database.create(this);		
		dao = new CROCAMDAOResult(db.open());
		editResult(dao.findMax());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final Context context = this;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_share_log, menu);
        actionShare = menu.findItem(R.id.action_share);
        actionShare.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        	public boolean onMenuItemClick(MenuItem item) {	
        		if (result != null) {
            		Intent sendIntent = new Intent();
            		sendIntent.setAction(Intent.ACTION_SEND);
            		sendIntent.putExtra(Intent.EXTRA_TEXT, result.toLongView(getApplicationContext()));
            		sendIntent.setType("text/plain");
            		startActivity(sendIntent);        			
        		}
        		return true;
        	}
        });
        menu.findItem(R.id.action_delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
        	public boolean onMenuItemClick(MenuItem item) {
        		if (result != null) 
        			Utils.Confirm(context, "Are you sure you want to clear the log?", deleteLog(), dummy());
        		return true;
        	}
        });
        return super.onCreateOptionsMenu(menu);
	}
	
	public Runnable deleteLog(){
		final Activity context = this;
        return new Runnable() {
            public void run() {
        		dao.delete(result.getId(), CROCAMSchema.TABLENAME_RESULTS);
        		context.finish();
        		Intent intent = new Intent(context,ShowLogActivity.class);
        		startActivity(intent);				
            }
          };
    }

    public Runnable dummy(){
        return new Runnable() {
            public void run() {
            }
          };
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

	public SlidingMenu getSlidingMenu() {
		return slidingMenu;
	}

	public void editResult(Result result) {
		if (result != null) {
			this.result = result;
			((TextView)findViewById(R.id.log)).setText(result.toLongView(this.getApplicationContext()));			
		}
	}
	
}
