package photoViewer.com.ui;

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
    }

    interface Presenter {

        void setView(View view);

        void bind();

        void unBind();

        void permissionsGranted();

        void permissionsDenied();

        void btnPermissionClicked();

        void onCreate();
    }
}
