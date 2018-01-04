package photoViewer.com.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import javax.inject.Inject;

import photoViewer.com.PhotoViewApp;
import photoViewer.com.R;
import photoViewer.com.helpers.PermissionDelegate;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @Inject MainContract.Presenter presenter;
    private ProgressBar progressBar;
    private PermissionDelegate delegate;
    private View noPermissionsView;
    private RecyclerView recyclerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noPermissionsView = findViewById(R.id.noPermissionsView);
        Button btnPermission = findViewById(R.id.btnPermission);

        PhotoViewApp.component(this).inject(this);

        delegate = new PermissionDelegate(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);

        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                presenter.btnPermissionClicked();
            }
        });

        presenter.setView(this);
        presenter.onCreate();
//        recyclerView.setAdapter(new PhotoAdapter());
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
                        dialog.dismiss();
                        delegate.requestPermission();
                    }
                }).show();
    }

    @Override public void showNoPermissionView(boolean show) {
        noPermissionsView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
