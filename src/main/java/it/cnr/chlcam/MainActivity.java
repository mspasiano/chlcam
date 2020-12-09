package it.cnr.chlcam;

import it.cnr.chlcam.database.Database;
import it.cnr.chlcam.model.Result;
import it.cnr.chlcam.util.Utils;

import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import de.akquinet.android.androlog.Log;

public class MainActivity extends Activity implements CallbackResult{

    private Uri imageUri;
	/**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializes the logging
        Log.init();
        // Log a message (only on dev platform)
        Log.i(this, "onCreate");
        getActionBar().setDisplayShowTitleEnabled(false);
        //Initialize DB
    	Database.create(getApplicationContext());
        setContentView(R.layout.main);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();        
        if (Intent.ACTION_SEND.equals(action) && type != null) {
        	handleSendImage(intent); // Handle single image being sent
        }         
    }
    
    private void handleSendImage(Intent intent) {
    	Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		String[] arrayOfString = { "_data" };
		Cursor localCursor = getContentResolver().query(imageUri, arrayOfString, null, null, null);
		localCursor.moveToFirst();
		String str2 = localCursor.getString(localCursor.getColumnIndex(arrayOfString[0]));
		String fileName = str2.substring(str2.lastIndexOf("/") + 1);
		localCursor.close();
		Bitmap selectedImage = BitmapFactory.decodeFile(str2);        		
		String exifOrientation = null;
		try {
			ExifInterface oldExif = new ExifInterface(str2);
    		exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
		} catch (IOException e) {
		}
		Utils.startCaptureImage(this, selectedImage, Utils.getLocationFromImage(str2), exifOrientation, null, fileName);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        menu.findItem(R.id.action_camera).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				goOpenCamera(getCurrentFocus());
				return true;
			}
		});
        menu.findItem(R.id.action_gallery).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				goOpenGallery(getCurrentFocus());
				return true;
			}
		});        	
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				goOpenSettings(getCurrentFocus());			
				return true;
			}
		});        	      	
        menu.findItem(R.id.action_gps).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				goOpenGPS(getCurrentFocus());			
				return true;
			}
		});        	

        menu.findItem(R.id.action_help).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				goOpenInfo(getCurrentFocus());			
				return true;
			}
		});        	
        menu.findItem(R.id.action_logs).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				goOpenLogs(getCurrentFocus());			
				return true;
			}
		});        	

        return super.onCreateOptionsMenu(menu);
    }    
    
	private void goOpenGPS(View currentFocus) {
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
		startActivity(intent);		
	}
    
    public void goOpenSettings(View view) {
		Intent intent = new Intent(this,SettingsActivity.class);
		startActivity(intent);				
    }

    public void goOpenLogs(View view) {
		Intent intent = new Intent(this,ShowLogActivity.class);
		startActivity(intent);				
    }

    public void goOpenInfo(View view) {
    	// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialog.setContentView(R.layout.info);
		dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_menu_info_details);
		dialog.setTitle("Info...");
		
		Button dialogButton = (Button) dialog.findViewById(R.id.infoButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();		
    }
    
    public void goOpenCamera(View view) {
        imageUri = Utils.goOpenCamera(this);
    }

    public void goOpenGallery(View view) {
    	imageUri = null;
    	Utils.goOpenGallery(this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Utils.onActivityResult(this, imageUri, requestCode, resultCode, data);
    }

	public Result getResult() {
		return null;
	}        
}