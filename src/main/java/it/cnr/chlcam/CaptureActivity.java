package it.cnr.chlcam;


import it.cnr.chlcam.dao.CROCAMDAOEquation;
import it.cnr.chlcam.dao.CROCAMDAOResult;
import it.cnr.chlcam.database.Database;
import it.cnr.chlcam.model.Equation;
import it.cnr.chlcam.model.Result;
import it.cnr.chlcam.util.Utils;
import it.cnr.chlcam.util.Utils.RGB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class CaptureActivity extends Activity implements CallbackResult{

	private Bitmap selectedImage;
    private ImageView content;
    private ImageView target;
    private Equation equation;
    private Integer pixel;
    private Address address;
    private SharedPreferences settings;
    private Dialog resultDialog;
    private Uri imageUri;
    
    private List<Result> results = new ArrayList<Result>();
    private Result appResult;
    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log a message (only on dev platform)
        // Initializes the logging
        Log.init();
        Log.i(this, "onCreate");
        setContentView(R.layout.capture);
        content = (ImageView) findViewById(R.id.content);
        
        target = (ImageView) findViewById(R.id.target);

        Uri imageURI = getIntent().getParcelableExtra("localUri");
        address = getIntent().getParcelableExtra("address");
		
        Result result = (Result) getIntent().getSerializableExtra("result");
        if (result != null)
        	results.add(result);
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;        
        selectedImage = BitmapFactory.decodeFile(imageURI.getPath(), options);
        
        Bitmap pointer = BitmapFactory.decodeResource(getResources(),
                R.drawable.mirino);
        loadPreferences();
        
        pointer = scaleBitmap(pointer, 
				60 + (int)Math.sqrt(pixel), 
				60 + (int)Math.sqrt(pixel));
        target.setImageBitmap(pointer);
        target.bringToFront();
        try {
            ExifInterface exif = new ExifInterface(imageURI.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            selectedImage = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {
        }        
        content.setImageBitmap(selectedImage);
        target.setX(selectedImage.getWidth() / 2);
        target.setY(selectedImage.getHeight() / 2);
        content.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
	        		target.setX(event.getX() - (target.getWidth()/2));
	        		target.setY(event.getY() - (target.getWidth()/2));									
				}
				return ((ZoomableImageView)content).onTouchZoomable(v, event);
			}
		});        
    }

    private void loadPreferences() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        String chlorophyllContent = settings.getString("chlorophyll_content", "-1");
        if (!chlorophyllContent.equalsIgnoreCase("-1")) {
        	Database db = Database.create(getApplicationContext());
        	CROCAMDAOEquation dao = new CROCAMDAOEquation(db.open());
        	equation = dao.findById(Long.valueOf(chlorophyllContent));
        }        
        pixel = settings.getInt("number_pixel", 1);		
	}

	public Utils.RGB getRGB(float[] coordinates) {
		float[] values = ((ZoomableImageView)content).getM();
		float relativeX = (((ZoomableImageView)content).getCurrPosition().x - values[2]) / values[0];
		float relativeY = (((ZoomableImageView)content).getCurrPosition().y - values[5]) / values[4];
		
		float targetX = relativeX;
		float targetY = relativeY;
		
		float x = selectedImage.getWidth() >  targetX ? targetX : selectedImage.getWidth() - 1;
		float y = selectedImage.getHeight() > targetY ? targetY : selectedImage.getHeight() - 1;
		coordinates[0] = x;
		coordinates[1] = y;
		try{
			return Utils.calculateRGB(selectedImage, (int)x, (int)y, (int)Math.sqrt(pixel), (int)Math.sqrt(pixel));					
		} catch (Exception _ex) {
			 Toast.makeText(content.getContext(), "Please pick inside the Image!", Toast.LENGTH_LONG).show();
			 return null;					
		}
    	
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_capture_actions, menu);
        
        menu.findItem(R.id.action_done).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				float[] coordinates = new float[2];
				Utils.RGB RGB = getRGB(coordinates);
				if (RGB == null)
					return false;
				double chlorophyll = 0;
		        if (equation != null) {
			        try {
						Calculable calc = new ExpressionBuilder(equation.getExpression())
						.withVariable("R", RGB.getR())
						.withVariable("G", RGB.getG())
						.withVariable("B", RGB.getB())
						.build();
						chlorophyll = calc.calculate();
					} catch (UnknownFunctionException e) {
						 Toast.makeText(content.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
						 return false;
					} catch (UnparsableExpressionException e) {
						 Toast.makeText(content.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
						 return false;
					}		        		        	
		        }
		        showResultDialog(chlorophyll, RGB);
				return true;
			}
		});
        return super.onCreateOptionsMenu(menu);
    }    

    private void showResultDialog(final double chlorophyll, final RGB rgb) {
    	if (equation == null) {
			Toast.makeText(getApplicationContext(), "Cannot display result please verify app settings.", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this,SettingsActivity.class);
			startActivityForResult(intent, Utils.RESULT_CHANGE_PREFERENCES);
			return;
    	}
    	// custom dialog
		resultDialog = new Dialog(this);
		resultDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		resultDialog.setContentView(R.layout.result);
		resultDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.logo);
		resultDialog.setTitle("Result...");
		Date currentDate = new Date();
		java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
		java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());		
		((TextView)resultDialog.findViewById(R.id.result_date)).setText(dateFormat.format(currentDate) + " " + timeFormat.format(currentDate));
		String addressOutput = "";
		if (address != null) {
        	for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
        		addressOutput += address.getAddressLine(i) + " ";						
			}
        	addressOutput += ", " + address.getCountryName();
        	((TextView)resultDialog.findViewById(R.id.result_location)).setText(addressOutput);
		}		
		final Result dummy = new Result(new Date(), addressOutput, 
				equation.getName(), equation.getExpression(), rgb.getR(), 
				rgb.getG(), rgb.getB(), chlorophyll);

		Button dialogButtonSave = (Button) resultDialog.findViewById(R.id.resultButtonSave);
		dialogButtonSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
	        	Database db = Database.create(getApplicationContext());
	        	CROCAMDAOResult dao = new CROCAMDAOResult(db.open());
	        	dao.insert(dummy);
				Toast.makeText(getApplicationContext(), "Result is saved as log file.", Toast.LENGTH_SHORT).show();	        	
				resultDialog.dismiss();
			}
		});
		((TextView)resultDialog.findViewById(R.id.result_equation)).setText(equation.getName());
		((TextView)resultDialog.findViewById(R.id.result_rgb)).setText("R " + rgb.getR() + " G " + rgb.getG() + " B " + rgb.getB());
		((TextView)resultDialog.findViewById(R.id.result_chlorophyll)).setText(getString(R.string.result_misura, String.valueOf(chlorophyll)));
		Button resultButtonAdd = (Button) resultDialog.findViewById(R.id.resultButtonAdd);
		if (settings.getBoolean("average", false)) {
			resultButtonAdd.setVisibility(View.VISIBLE);
			resultDialog.findViewById(R.id.result_count_group).setVisibility(View.VISIBLE);
			resultDialog.findViewById(R.id.result_average_rgb_group).setVisibility(View.VISIBLE);
			resultDialog.findViewById(R.id.result_average_chlorophyll_group).setVisibility(View.VISIBLE);					

			dummy.setLeafCount(results.size() + 1);
			calculateAverageRGB(dummy);
			((TextView)resultDialog.findViewById(R.id.result_count)).setText(String.valueOf(dummy.getLeafCount()));
			((TextView)resultDialog.findViewById(R.id.result_average_rgb)).setText("R " + dummy.getAverageR() + " G " + dummy.getAverageG() + " B " + dummy.getAverageB());
			((TextView)resultDialog.findViewById(R.id.result_average_chlorophyll)).setText(getString(R.string.result_misura, String.valueOf(dummy.getAverageChlorophyll())));
		
		}
		resultButtonAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				appResult = dummy;
				registerForContextMenu(arg0);
				openContextMenu(arg0);
			}
		});
		resultDialog.show();		
    }
    private void calculateAverageRGB(Result dummy) {
    	Integer averageR = 0, averageG = 0, averageB = 0; 
    	Double averageChlorophyll = Double.valueOf(0);
    	for (Result result : results) {
    		averageR = averageR + result.getLeafR();
    		averageG = averageG + result.getLeafG();
    		averageB = averageB + result.getLeafB();
    		averageChlorophyll = averageChlorophyll + result.getLeafChlorophyll();
    	}
		averageR = averageR + dummy.getLeafR();
		averageG = averageG + dummy.getLeafG();
		averageB = averageB + dummy.getLeafB();
		averageChlorophyll = averageChlorophyll + dummy.getLeafChlorophyll();
		dummy.setAverageR(averageR / (results.size() + 1));
		dummy.setAverageG(averageG / (results.size() + 1));
		dummy.setAverageB(averageB / (results.size() + 1));
		dummy.setAverageChlorophyll(averageChlorophyll / (results.size() + 1));
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	final Activity target = this; 
    	if (v.getId() == R.id.resultButtonAdd) {
    		PopupMenu popup = new PopupMenu(this, v);
    		popup.inflate(R.menu.activity_result_actions_add);
    		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
					case R.id.action_camera:
						imageUri = Utils.goOpenCamera(target);
						break;
					case R.id.action_gallery:
						Utils.goOpenGallery(target);
						break;
					case R.id.action_new_measure:
						results.add(appResult);
						resultDialog.dismiss();
						break;
					default:
						resultDialog.dismiss();
						break;
					} 
					return false;
				}
			});
            popup.show();
    	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Utils.RESULT_CHANGE_PREFERENCES) {
    		loadPreferences();
    	} else {
        	Utils.onActivityResult(this, imageUri, requestCode, resultCode, data);    		
    	}
    }        
    
    private Bitmap scaleBitmap(Bitmap b, int width, int height) {
    	float factorH = height / (float) b.getWidth();
        float factorW = width / (float) b.getWidth();
        float factorToUse = (factorH > factorW) ? factorW : factorH;
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), true);          
    }

	public Result getResult() {
		return appResult;
	}
}

