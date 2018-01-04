package photoViewer.com.helpers;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class PermissionDelegate {
    private final static String READ_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_CODE = 100;
    private AppCompatActivity activity;


    public PermissionDelegate(AppCompatActivity activity) {
        this.activity = activity;
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(activity, READ_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            activity.requestPermissions(new String[]{READ_STORAGE}, REQUEST_CODE);
        }
    }

    public boolean onPermissionResult(int requestCode, String[] permissions, int[] grandResults) {
        if (requestCode != REQUEST_CODE) return false;
        if (!permissions[0].equals(READ_STORAGE)) return false;

        return grandResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public boolean shouldShowRationale() {
        return Build.VERSION.SDK_INT >= 23 && activity.shouldShowRequestPermissionRationale(READ_STORAGE);
    }
}
