package photoViewer.com;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
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

    public PhotoObserver(final Context context) {
        this.context = context;

        observer = new ContentObserver(new Handler()) {

            @Override public boolean deliverSelfNotifications() {
                return false;
            }

            @Override public void onChange(boolean selfChange) {
                super.onChange(selfChange);
            }

            @Override public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                readMediaStore(context, uri);
            }
        };
    }

    private void readMediaStore(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(uri,
                null,
                null,
                null,
                "date_added DESC")) {
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    final Photo photo = new Photo();

                    photo.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    photo.fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
                    photo.fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));

                    observable.onNext(photo);
                }
            }
        }
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
