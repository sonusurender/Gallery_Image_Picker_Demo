package com.galleryimagepicker_demo;

import java.io.FileNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	private final int select_photo = 1; // request code fot gallery intent

	private static ImageView gallery_image;

	private static TextView uriPath, realPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gallery_image = (ImageView) findViewById(R.id.gallery_imageview);

		uriPath = (TextView) findViewById(R.id.uri_path);
		realPath = (TextView) findViewById(R.id.real_path);

		// Implement click listener over button
		findViewById(R.id.change_image).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						// Intent to gallery
						Intent in = new Intent(Intent.ACTION_PICK);
						in.setType("image/*");
						startActivityForResult(in, select_photo);// start
																	// activity
																	// for
																	// result

					}
				});

	}

	protected void onActivityResult(int requestcode, int resultcode,
			Intent imagereturnintent) {
		super.onActivityResult(requestcode, resultcode, imagereturnintent);
		switch (requestcode) {
		case select_photo:
			if (resultcode == RESULT_OK) {
				try {

					Uri imageuri = imagereturnintent.getData();// Get intent
																// data

					uriPath.setText("URI Path: " + imageuri.toString());
					// Get real path and show over text view
					String real_Path = getRealPathFromUri(MainActivity.this,
							imageuri);
					realPath.setText("Real Path: " + real_Path);

					uriPath.setVisibility(View.VISIBLE);
					realPath.setVisibility(View.VISIBLE);

					Bitmap bitmap = decodeUri(MainActivity.this, imageuri, 300);// call
																				// deocde
																				// uri
																				// method
					// Check if bitmap is not null then set image else show
					// toast
					if (bitmap != null)
						gallery_image.setImageBitmap(bitmap);// Set image over
																// bitmap

					else
						Toast.makeText(MainActivity.this,
								"Error while decoding image.",
								Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {

					e.printStackTrace();
					Toast.makeText(MainActivity.this, "File not found.",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	// Method that deocde uri into bitmap. This method is necessary to deocde
	// large size images to load over imageview
	public static Bitmap decodeUri(Context context, Uri uri,
			final int requiredSize) throws FileNotFoundException {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(uri), null, o);

		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;

		while (true) {
			if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(context.getContentResolver()
				.openInputStream(uri), null, o2);
	}

	// Get Original image path
	public static String getRealPathFromUri(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
