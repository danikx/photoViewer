package photoViewer.com.ui;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;

    @Override public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override public void bind() {
        view.showNoPermissionView(!view.hashPermissions());
        if (view.hashPermissions()) loadPhotos();
    }

    @Override public void unBind() {

    }

    @Override public void permissionsGranted() {
        view.showNoPermissionView(false);
        loadPhotos();
    }

    @Override public void permissionsDenied() {
        view.showNoPermissionView(true);
    }

    @Override public void btnPermissionClicked() {
        if (!view.hashPermissions()) view.askPermissions();
    }

    @Override public void onCreate() {
        if (!view.hashPermissions()) {
            view.showNoPermissionView(true);
            view.askPermissions();
        }
    }

    private void loadPhotos() {
        view.showProgressBar(true);

        //todo

        view.showProgressBar(false);
    }
}
