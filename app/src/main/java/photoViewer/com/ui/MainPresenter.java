package photoViewer.com.ui;

import java.util.ArrayList;
import java.util.List;

import photoViewer.com.model.Photo;

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
        view.askPermissions();
    }

    @Override public void onCreate() {
        if (!view.hashPermissions()) {
            view.askPermissions();
        }
    }

    private void loadPhotos() {
        view.showProgressBar(true);

        //todo
//        view.showNoData(true);
        view.showData(getSimpleData());
        view.showData(view.getFilePaths());

        view.showProgressBar(false);
    }

    private List<Photo> getSimpleData() {
        List<Photo> result = new ArrayList<>();
        final Photo photo = new Photo();
        photo.fileName = "name";
        photo.fileSize = "size";
        photo.fileHash = "hash";
        photo.path = "https://images.techhive.com/images/article/2015/10/android-m-app-permissions-100620586-large.jpg";
        result.add(photo);
        return result;
    }
}
