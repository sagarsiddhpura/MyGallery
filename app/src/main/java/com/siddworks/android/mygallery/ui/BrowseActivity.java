package com.siddworks.android.mygallery.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.siddworks.android.mygallery.R;
import com.siddworks.android.mygallery.adapters.FolderAdapter;
import com.siddworks.android.mygallery.util.GridSpacesItemDecoration;
import com.siddworks.android.mygallery.util.ImageGalleryUtils;
import com.siddworks.android.mygallery.util.PaletteColorType;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class BrowseActivity extends AppCompatActivity  implements FolderAdapter.OnImageClickListener{

    private PaletteColorType mPaletteColorType;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private String TAG = "BrowseActivity";
    private String mParentPath;
    private String mTitle;
    private static final int FILE_CODE = 1331;
    private File[] fList;
    // array of supported imageExtensions (use a List if you prefer)
    public static ArrayList<String> imageExtensions = new ArrayList<>(Arrays.asList(new String[]{"gif", "png", "bmp", "jpg"}));
    // array of supported videoExtensions (use a List if you prefer)
    public static ArrayList<String> videoExtensions = new ArrayList<>(Arrays.asList(new String[]{
            "mp4", "m4a", "3gp", "mkv", "wav", "3gp"}));
    // endregion

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse_folders);

        bindViews();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mParentPath = extras.getString("path", null);
                mTitle = extras.getString("title", "");
            }
        }
        File parentFile = new File(mParentPath);
        final ArrayList<String> allExtensions = new ArrayList<>(imageExtensions);
        allExtensions.addAll(videoExtensions);
        fList = parentFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                File childFile = new File(current, name);

                if(childFile.isDirectory()) {
                    return true;
                } else {
                    String currentFileNameExtension = name.substring(
                            name.lastIndexOf('.')+1, name.length());
                    if(allExtensions.contains(currentFileNameExtension.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }
        });

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mTitle);
        }

        setUpRecyclerView();
    }
    // endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (id == R.id.action_select_folder) {
            showPicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPicker() {
        // This always works
        Intent i = new Intent(this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpRecyclerView();
    }

    // region ImageGalleryAdapter.OnImageClickListener Methods
    @Override
    public void onImageClick(int position) {
        File currentFile = fList[position];
        String currentFileName = currentFile.getName();

        if (currentFile.isFile()) {

            String currentFileNameExtension = currentFileName.substring(
                    currentFileName.lastIndexOf('.')+1, currentFileName.length());

            if(imageExtensions.contains(currentFileNameExtension.toLowerCase())) {
                File parent = new File(mParentPath);
                String[] allImages = parent.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                        File childFile = new File(current, name);
//                        Log.d(TAG, "accept() called with: " + "current = [" + current + "], name = [" + name + "]");
//                        Log.d(TAG, "current.isDirectory():"+childFile.isDirectory());
                        if(childFile.isDirectory()) {
                            return false;
                        } else {
                            Log.d(TAG, "Current file");
                            String currentFileNameExtension = name.substring(
                                    name.lastIndexOf('.')+1, name.length());
                            Log.d(TAG, "currentFileNameExtension:"+currentFileNameExtension);
                            if(imageExtensions.contains(currentFileNameExtension.toLowerCase())) {
                                Log.d(TAG, "accepted");
                                return true;
                            }
                        }
                        return false;
                    }
                });
                ArrayList<String> allImagesArray = new ArrayList<>(Arrays.asList(allImages));
                for (int i = 0; i < allImagesArray.size(); i++) {
                    String s = allImagesArray.get(i);
                    if(s.equals(currentFileName)) {
                        position = i;
                    }
                }

                Log.d(TAG, "images:"+allImagesArray);
                Log.d(TAG, "path:"+mParentPath);
                Log.d(TAG, "position:"+position);
                Intent intent = new Intent(this, FullScreenImageGalleryActivity.class);
                intent.putStringArrayListExtra("images", allImagesArray);
                intent.putExtra("position", position);
                intent.putExtra("path", mParentPath);
                startActivity(intent);
            }

            else if(videoExtensions.contains(currentFileNameExtension.toLowerCase())) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentFile.getAbsolutePath()));
                intent.setDataAndType(Uri.parse(currentFile.getAbsolutePath()), "video/*");
                startActivity(intent);
            }

        } else if (currentFile.isDirectory()) {
            Intent intent = new Intent(this, BrowseActivity.class);
            intent.putExtra("path", currentFile.getAbsolutePath());
            intent.putExtra("title", currentFileName);
            startActivity(intent);
        }
    }
    // endregion

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpRecyclerView() {
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(this)) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(BrowseActivity.this, numOfColumns));
        mRecyclerView.addItemDecoration(new GridSpacesItemDecoration(ImageGalleryUtils.dp2px(this, 2), numOfColumns));
        FolderAdapter folderAdapter = new FolderAdapter(fList);
        folderAdapter.setOnImageClickListener(this);

        mRecyclerView.setAdapter(folderAdapter);
    }
    // endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, " Activity.RESULT_OK");
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                Log.d(TAG, " EXTRA_ALLOW_MULTIPLE");
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    Log.d(TAG, " ICS");
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                String path = uri.getPath().toString(); // "file:///mnt/sdcard/FileName.mp3"
                showPath(path);

            }
        }
    }

    private void showPath(String path) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("Path", path).commit();

        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        Intent intent = new Intent(this, BrowseActivity.class);
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(directories));
        intent.putStringArrayListExtra("images", strings);
        intent.putExtra("path", path);
        intent.putExtra("title", file.getName());
        startActivity(intent);
        this.finish();
    }
}
