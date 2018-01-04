package photoViewer.com.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

import photoViewer.com.PhotoObserver;
import photoViewer.com.PhotoViewApp;
import photoViewer.com.R;
import photoViewer.com.helpers.PermissionDelegate;
import photoViewer.com.model.Photo;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @Inject MainContract.Presenter presenter;
    private ProgressBar progressBar;
    private PermissionDelegate delegate;
    private View noPermissionsView;
    private RecyclerView recyclerView;
    private View noDataView;
    private PhotoObserver observer;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noPermissionsView = findViewById(R.id.noPermissionsView);
        noDataView = findViewById(R.id.noData);

        observer = new PhotoObserver(this);

        Button btnPermission = findViewById(R.id.btnPermission);

        PhotoViewApp.component(this).inject(this);

        delegate = new PermissionDelegate(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (delegate.shouldShowRationale()) {
                    askPermissions();
                } else {
                    openAppSettings();
                }
            }
        });

        presenter.setView(this);
        presenter.onCreate();
        recyclerView.setAdapter(new PhotoAdapter(this));
    }

    @Override protected void onStart() {
        super.onStart();
        observer.start();
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.bind();
    }

    @Override protected void onPause() {
        super.onPause();
        presenter.unBind();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (delegate.onPermissionResult(requestCode, permissions, grantResults)) {
            presenter.permissionsGranted();
        } else {
            /*if (delegate.shouldShowRationale()) {

            }*/
            presenter.permissionsDenied();
        }
    }

    @Override public void showProgressBar(Boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override public boolean hashPermissions() {
        return delegate.hasPermission();
    }

    @Override public void askPermissions() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.explain_why_we_should_have_permission)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        delegate.requestPermission();
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override public void showNoPermissionView(boolean show) {
        noPermissionsView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override public void showData(List<Photo> photos) {
        ((PhotoAdapter) recyclerView.getAdapter()).update(photos);
    }

    @Override public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    @Override public void showNoData(boolean show) {
        noDataView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        observer.stop();
    }

    @Override public ArrayList<Photo> getFilePaths() {
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.TITLE,
        };
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<>();
        ArrayList<String> resultIAV = new ArrayList<>();
        ArrayList<Photo> result = new ArrayList<>();

        String[] directories = null;
        try {
            c = getContentResolver().query(u, projection, null, null, null);
            if ((c != null) && (c.moveToFirst())) {
                String data = c.getString(0);
                String size = c.getString(1);
                String name = c.getString(2);
                String title = c.getString(3);

                do {
                    String tempDir = c.getString(0);
                    tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                    try {
                        dirList.add(tempDir);
                    } catch (Exception e) {

                    }
                }
                while (c.moveToNext());
                directories = new String[dirList.size()];
                dirList.toArray(directories);

            }
        } finally {
            if (c != null) {
                c.close();
            }
        }


        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();

                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            ) {


                        String path = "file:" + imagePath.getAbsolutePath();

                        resultIAV.add(path);
                        result.add(Photo.create(imagePath.getName(), imagePath.getTotalSpace(), "some hash", path));
                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
