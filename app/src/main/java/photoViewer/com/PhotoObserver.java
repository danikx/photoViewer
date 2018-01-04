package photoViewer.com;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class PhotoObserver {
    private PublishSubject<Photo> observable = PublishSubject.create();
    private Context context;
    private ContentObserver observer;

    public PhotoObserver(Context context) {
        this.context = context;

        observer = new ContentObserver(new Handler()) {

            @Override public boolean deliverSelfNotifications() {
                Log.d("photo", "deliverSelfNotifications");
                return super.deliverSelfNotifications();
            }

            @Override public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Log.d("photo", "onChange");
            }

            @Override public void onChange(boolean selfChange, Uri uri) {
                Log.d("photo", "onChange" + uri.toString());
                super.onChange(selfChange, uri);
            }
        };
    }

    public void start() {
        context.getContentResolver().registerContentObserver(
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                true, observer);

        context.getContentResolver().registerContentObserver(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, observer);
    }

    public void stop() {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    public Observable<Photo> observable() {
        return observable;
    }
}
