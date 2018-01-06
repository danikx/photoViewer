package photoViewer.com;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.util.ArrayList;

import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/7/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class PhotoRepository {

    private Context context;

    public PhotoRepository(Context context) {

        this.context = context;
    }

    public ArrayList<Photo> readPhotos() {
        ArrayList<Photo> result = new ArrayList<>();
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
        };

        Cursor c = null;
        try {
            c = context.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection,
                            null,
                            null,
                            BaseColumns._ID + " DESC");
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
}
