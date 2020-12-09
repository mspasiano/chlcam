package it.cnr.chlcam.util;

import it.cnr.chlcam.CallbackResult;
import it.cnr.chlcam.CaptureActivity;
import it.cnr.chlcam.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public final class Utils {
    public static final int RESULT_LOAD_IMAGE = 800, RESULT_CAPTURE_IMAGE = 801, RESULT_CHANGE_PREFERENCES= 802;

	public static class RGB {
		private int r,g,b;

		public RGB(int r, int g, int b) {
			super();
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public int getR() {
			return r;
		}

		public int getG() {
			return g;
		}

		public int getB() {
			return b;
		}

		public void setR(int r) {
			this.r = r;
		}

		public void setG(int g) {
			this.g = g;
		}

		public void setB(int b) {
			this.b = b;
		}		
		
	}
	
	public static RGB calculateRGB(Bitmap bitmap, int x, int y) {
		int c = bitmap.getPixel(x, y);		
		return new RGB(Color.red(c), Color.green(c), Color.blue(c));
	}

	public static RGB calculateRGB(Bitmap bitmap, int x, int y, int width, int height) throws Exception{
		int r = 0, g = 0, b = 0;
		int[] pixels = new int[height * width];
		if (x + width > bitmap.getWidth() ){
			x = 0;
			width = bitmap.getWidth();
		}
		if (y + height > bitmap.getHeight()){
			y = 0;
			height = bitmap.getHeight();
		}		
		bitmap.getPixels(pixels, 0, width, x, y, width, height);
		int i = 0;
		for (; i < pixels.length; i++) {
			int c = pixels[i];
			r += Color.red(c);
			g += Color.green(c);
			b += Color.blue(c);			
		}
		return new RGB(r / i, g / i, b / i);
	}

    public static Uri getUriForSave(Context context) {
		String IMAGE_FOLDER = Environment.getExternalStorageDirectory() + "/"+ context.getString(R.string.app_name)+"/";
		File localFile = new File(IMAGE_FOLDER);
		if (!localFile.exists())
	          localFile.mkdir();    		
    	return Uri.fromFile(localFile);    	
    }
	
    public static Uri goOpenCamera(Activity context) {
    	Uri imageUri = Uri.fromFile(new File(getUriForSave(context).getPath() + "/" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.jpg'", Locale.ITALIAN).format(new Date())));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", true);
        context.startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
        return imageUri;
    }

    
    public static void goOpenGallery(Activity context) {
    	Intent imageGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	context.startActivityForResult(imageGallery, RESULT_LOAD_IMAGE);   
    }

    public static void onActivityResult(Activity context, Uri imageUri, int requestCode, int resultCode, Intent data) {
		Bitmap selectedImage = null;
		String exifOrientation = null;
		String fileName = null;
		Map<String, String> location = new HashMap<String, String>();
    	if (requestCode == Utils.RESULT_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
    		try {
    			selectedImage = MediaStore.Images.Media.getBitmap(
    					context.getContentResolver(), imageUri);
    			location.putAll(getLocationFromImage(imageUri.getPath()));
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}
    	if (requestCode == Utils.RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
			Uri localUri = data.getData();
    		String[] arrayOfString = { "_data" };
    		Cursor localCursor = context.getContentResolver().query(localUri, arrayOfString, null, null, null);
    		localCursor.moveToFirst();
    		String str2 = localCursor.getString(localCursor.getColumnIndex(arrayOfString[0]));
    		fileName = str2.substring(str2.lastIndexOf("/") + 1);
    		localCursor.close();
    		selectedImage = BitmapFactory.decodeFile(str2);        		
			try {
				ExifInterface oldExif = new ExifInterface(str2);
        		exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
			} catch (IOException e) {
			}
			location.putAll(getLocationFromImage(str2));
    	}
    	if (selectedImage != null)
    		startCaptureImage(context, selectedImage, location, exifOrientation, imageUri, fileName);
    }        

    public static void startCaptureImage(Activity context, Bitmap selectedImage, Map<String, String> location, String exifOrientation, Uri imageUri, String fileName) {
    	String str1 = null;
    	if (imageUri == null) {
        	str1 = getUriForSave(context).getPath() + "/" + (fileName != null? fileName : (new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.jpg'", Locale.ITALIAN).format(new Date())));
    		try {
              BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str1));
              selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, localBufferedOutputStream);
              localBufferedOutputStream.flush();
              localBufferedOutputStream.close();
              ExifInterface newExif = new ExifInterface(str1);
              if (exifOrientation != null) {
                 newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
              }
              if (!location.isEmpty() && location.get(ExifInterface.TAG_GPS_LATITUDE) != null ) {            	  
            	  newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, location.get(ExifInterface.TAG_GPS_LATITUDE)); 
                  newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, location.get(ExifInterface.TAG_GPS_LATITUDE_REF));
                  newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, location.get(ExifInterface.TAG_GPS_LONGITUDE));                	  
                  newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, location.get(ExifInterface.TAG_GPS_LONGITUDE_REF));
              }                  
              newExif.saveAttributes();
    		}catch (FileNotFoundException localFileNotFoundException){
              localFileNotFoundException.printStackTrace();
            }catch (IOException localIOException){
              localIOException.printStackTrace();
            }    		    		
    	} else {
    		str1 = imageUri.getPath();
    	}
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(str1);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

		Intent intent = new Intent(context, CaptureActivity.class);
		intent.putExtra("localUri", Uri.fromFile(new File(str1)));
		if (context instanceof CallbackResult)
			intent.putExtra("result", ((CallbackResult)context).getResult());
		try {
			ExifInterface exifInterface = new ExifInterface(str1);
			float[] output = new float[2];
			if (exifInterface.getLatLong(output)) {
	    		Geocoder gcd = new Geocoder(context.getApplicationContext(), Locale.getDefault());
					List<Address> addresses = gcd.getFromLocation(output[0], output[1], 1);
					if (!addresses.isEmpty())
						intent.putExtra("address", addresses.get(0));
			}
		} catch (IOException e) {
		}
		context.startActivity(intent);
    }

    public static Map<String, String> getLocationFromImage(String path) {
    	Map<String, String> result = new HashMap<String, String>();
    	try {
			ExifInterface exifInterface = new ExifInterface(path);
			result.put(ExifInterface.TAG_GPS_LATITUDE, exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
			result.put(ExifInterface.TAG_GPS_LATITUDE_REF, exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
			result.put(ExifInterface.TAG_GPS_LONGITUDE, exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
			result.put(ExifInterface.TAG_GPS_LONGITUDE_REF, exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
        } catch (IOException e1) {
		}    	
    	return result;
    }
    
    public static void Confirm(Context context, String message, final Runnable positive, final Runnable negative) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Confirmation");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            	positive.run();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                negative.run();
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }    
}