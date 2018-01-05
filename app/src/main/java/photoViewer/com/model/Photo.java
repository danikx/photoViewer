package photoViewer.com.model;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class Photo {

    public String fileName;
    public long fileSize;
    public String fileSizeInString;
    public String fileHash;
    public String path;


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        return path != null ? path.equals(photo.path) : photo.path == null;
    }

    @Override public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
