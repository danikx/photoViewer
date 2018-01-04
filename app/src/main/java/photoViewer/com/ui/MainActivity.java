package photoViewer.com.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
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
        presenter.onCreate();
        recyclerView.setAdapter(new PhotoAdapter(this));
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

    private ContentObserver observer;

    public void dd() {
        observer = new ContentObserver(new Handler()) {

            @Override public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }

            @Override public void onChange(boolean selfChange, Uri uri) {
                Log.d("photo", uri.toString());
                super.onChange(selfChange, uri);
            }
        };

        getContentResolver().registerContentObserver(
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                true, observer);

        getContentResolver().registerContentObserver(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, observer);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        getContentResolver().unregisterContentObserver(observer);
    }
}
