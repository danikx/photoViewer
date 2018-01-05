package photoViewer.com.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
    private TextView counter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("photo", "onCreate");
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        counter = findViewById(R.id.counter);
        progressBar = findViewById(R.id.progressBar);
        noPermissionsView = findViewById(R.id.noPermissionsView);
        noDataView = findViewById(R.id.noData);

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
        recyclerView.setAdapter(new PhotoAdapter(this));

        presenter.onCreate();
    }

    @Override protected void onStart() {
        super.onStart();
        Log.d("photo", "onStart");
        presenter.bind();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        Log.d("photo", "onDestroy");
        presenter.unBind();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (delegate.onPermissionResult(requestCode, permissions, grantResults)) {
            presenter.permissionsGranted();
        } else {
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

    @Override public void addPhoto(Photo photo) {
        final PhotoAdapter adapter = (PhotoAdapter) recyclerView.getAdapter();
        adapter.add(photo);
        counter.setText(String.valueOf(adapter.getItemCount()));
    }

    @Override public void addPhotoTop(Photo photo) {
        final PhotoAdapter adapter = (PhotoAdapter) recyclerView.getAdapter();
        adapter.addTop(photo);
        counter.setText(String.valueOf(adapter.getItemCount()));
        recyclerView.scrollToPosition(0);
    }

    @Override public void clearData() {
        ((PhotoAdapter) recyclerView.getAdapter()).clearData();
    }

    @Override public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    @Override public void showNoData(boolean show) {
        noDataView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override public ArrayList<Photo> getPhotos() {
        Log.d("photo", "getting photos");

        ArrayList<Photo> result = new ArrayList<>();

        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
        };

        Cursor c = null;
        try {
            c = getContentResolver().query(u, projection, null, null, BaseColumns._ID + " DESC");
            if ((c != null) && (c.moveToFirst())) {
                do {
                    long size = c.getLong(1);
                    if (0 == size) continue;

                    String filePath = c.getString(0);
                    String name = c.getString(2);

                    Photo p = new Photo();
                    p.fileName = name;
                    p.fileSize = size;
                    p.path = filePath;

                    result.add(p);

                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return result;
    }

    @Override public void showError(Throwable t) {
        final String message = t.getMessage();

        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message != null ? message : getString(R.string.error_occured))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        delegate.requestPermission();
                        dialog.dismiss();
                    }
                }).show();
    }
}
