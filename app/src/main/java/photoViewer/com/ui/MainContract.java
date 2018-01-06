package photoViewer.com.ui;

import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public interface MainContract {

    interface View {

        void showProgressBar(Boolean show);

        boolean hashPermissions();

        void askPermissions();

        void showNoPermissionView(boolean show);

        void openAppSettings();

        void showNoData(boolean show);

        void showError(Throwable t);

        void addPhoto(Photo photo);

        void clearData();

        void addPhotoTop(Photo photo);
    }

    interface Presenter {

        void setView(View view);

        void bind();

        void unBind();

        void permissionsGranted();

        void permissionsDenied();

        void onCreate();
    }
}
