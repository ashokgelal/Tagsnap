package com.ashokgelal.tagsnap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ashokgelal.tagsnap.model.TagInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends SherlockActivity {
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private TextView mDescriptionTextView;
    private Spinner mCategorySpinner;
    private ImageView mPreviewImage;
    private TagInfo mCurrentTagsnap;
    private Uri mSelectedPictureUri;
    private ImageLoader mImageLoader;

    private static Uri getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Tagsnap");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        setContentView(R.layout.details);
        setupActionBar();
        setupDefaultValuesFromBundle();
        setupImageButtons();
        if (savedInstanceState != null) {
            mSelectedPictureUri = savedInstanceState.getParcelable("camera_output_path");
            if(mSelectedPictureUri != null)
                mImageLoader.displayImage(String.format("file://%s", mSelectedPictureUri.getPath()), mPreviewImage);
        }
    }

    private void setupDefaultValuesFromBundle() {
        Bundle extras = getIntent().getExtras();
        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mCategorySpinner = (Spinner) findViewById(R.id.category);
        mPreviewImage = (ImageView) findViewById(R.id.previewImage);
        mCurrentTagsnap = extras.getParcelable("taginfo");

        mDescriptionTextView.setText(mCurrentTagsnap.getDescription());


        // set the default category, if we have any
        String cat = mCurrentTagsnap.getCategory();
        if (cat != null && !cat.equals("")) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) mCategorySpinner.getAdapter();
            mCategorySpinner.setSelection(adapter.getPosition(cat));
        }

        // set the picture preview, if we have any
        Uri uri = mCurrentTagsnap.getPictureUri();
        if (uri != null) {
            File file = new File(uri.getPath());
            if (file.exists()) {
                mSelectedPictureUri = uri;
                mImageLoader.displayImage(String.format("file://%s", mSelectedPictureUri.getPath()), mPreviewImage);
            }
        }

        ((TextView) findViewById(R.id.detailsAddress1)).setText(mCurrentTagsnap.getAddress1());
        ((TextView) findViewById(R.id.detailsAddress2)).setText(mCurrentTagsnap.getAddress2());
        ((TextView) findViewById(R.id.detailsLatitude)).setText(String.valueOf(mCurrentTagsnap.getLatitude()));
        ((TextView) findViewById(R.id.detailsLongitude)).setText(String.valueOf(mCurrentTagsnap.getLongitude()));
    }

    private void setupImageButtons() {
        // check, and enable camera button if we have camera
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);
            cameraButton.setVisibility(View.VISIBLE);
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takePictureWithCamera();
                }
            });
        }
        ImageButton galleryButton = (ImageButton) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPictureFromGallery();
            }
        });
    }

    private void takePictureWithCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mSelectedPictureUri = getOutputMediaFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedPictureUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void pickPictureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                mImageLoader.displayImage(String.format("file://%s", mSelectedPictureUri.getPath()), mPreviewImage);
            } else if (requestCode == GALLERY_REQUEST) {
                // get the uri to the selected file
                Uri image = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String filePath = cursor.getString(columnIndex);
                cursor.close();
                mSelectedPictureUri = Uri.fromFile(new File(filePath));
                mImageLoader.displayImage(String.format("file://%s", mSelectedPictureUri.getPath()), mPreviewImage);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("camera_output_path", mSelectedPictureUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.discard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discard:
                // finish this activity with a CANCELLED status
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // put/update the details, put it to intent data, and finish this activity
                Intent data = new Intent();
                mCurrentTagsnap.setDescription(mDescriptionTextView.getText().toString());
                mCurrentTagsnap.setCategory(mCategorySpinner.getSelectedItem().toString());
                mCurrentTagsnap.setPictureUri(mSelectedPictureUri);
                data.putExtra("taginfo", mCurrentTagsnap);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        // set to display our custom action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView);
    }
}
