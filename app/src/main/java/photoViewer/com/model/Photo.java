package photoViewer.com.model;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class Photo {

    public String fileName;
    public String fileSize;
    public String fileHash;
    public String path;

    public static Photo create(String name, long space, String hash, String path) {
        Photo r = new Photo();
        r.fileName = name;
        r.fileHash = hash;
        r.path = path;
        r.fileSize = "" + space;
        return r;
    }
}
