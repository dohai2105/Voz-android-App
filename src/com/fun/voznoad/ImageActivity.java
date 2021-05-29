package com.fun.voznoad;

import uk.co.senab.photoview.PhotoView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.fun.voznoad.R;
import com.fun.voznoad.common.AppController;

public class ImageActivity extends FragmentActivity {
	Bitmap bitmap = null;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.imagview_layout);
		Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		final PhotoView imgTouch = (PhotoView) findViewById(R.id.imgDisplay);
		com.android.volley.toolbox.ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		final ProgressBar imgProgress = (ProgressBar) findViewById(R.id.imgProcess);
		imageLoader.get(getIntent().getStringExtra("url"), new ImageListener() {
			 
			@Override
			public void onErrorResponse(VolleyError error) {
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				 if (response.getBitmap() != null) {
			            // load image into imageview
					 	imgProgress.setVisibility(View.GONE);
			        	imgTouch.setImageBitmap(response.getBitmap());
			        	bitmap = response.getBitmap();
			        	 
			        }
			}
		});
		Button saveImage=(Button) findViewById(R.id.saveImage);
		saveImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (bitmap!=null) {
					MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, getIntent().getStringExtra("url") , "vozImg");
					Toast.makeText(ImageActivity.this, "Saved", Toast.LENGTH_LONG).show();
				}
 			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

}
