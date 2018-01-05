package photoViewer.com.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import photoViewer.com.PhotoObserver;
import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MessageDigest md;
    private PhotoObserver observer;

    public MainPresenter(PhotoObserver observer) {
        this.observer = observer;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            view.showError(e);
        }

        observer.observable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Photo>() {
                            @Override public void accept(Photo photo) throws Exception {
                                view.addPhoto(photo);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override public void accept(Throwable throwable) throws Exception {
                                view.showError(throwable);
                            }
                        });
    }

    @Override public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override public void bind() {
        view.showNoPermissionView(!view.hashPermissions());
        if (view.hashPermissions()) {
            loadPhotos();
            if (observer != null) observer.start();
        }
    }

    @Override public void unBind() {
        if (observer != null) observer.start();
    }

    @Override public void permissionsGranted() {
        view.showNoPermissionView(false);
        if (observer != null) observer.start();
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

    private void loadPhotos3() {
        view.showProgressBar(true);

        Observable.just(view.getPhotos())
                .subscribeOn(Schedulers.io())
                .map(new Function<ArrayList<Photo>, ArrayList<Photo>>() {
                    @Override public ArrayList<Photo> apply(ArrayList<Photo> photos) throws Exception {
                        for (Photo photo : photos) {
                            photo.fileHash = md5(photo.path);
                        }
                        return photos;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Photo>>() {
                    @Override public void accept(ArrayList<Photo> photos) throws Exception {
                        view.showData(photos);
                        view.showProgressBar(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override public void accept(Throwable throwable) throws Exception {
                        view.showError(throwable);
                    }
                });
    }

    private void loadPhotos() {
        view.showProgressBar(true);
        final ArrayList<Photo> photos = view.getPhotos();
        if (photos == null || photos.isEmpty()) {
            view.showNoData(true);
        } else {
            view.showNoData(false);

            Observable
                    .fromIterable(photos)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<Photo, Photo>() {

                        @Override public Photo apply(Photo photo) throws Exception {
                            photo.fileHash = md5(photo.path);
                            photo.fileSizeInString = humanReadableByteCount(photo.fileSize, true);
                            return photo;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Photo>() {
                        @Override public void accept(Photo photo) throws Exception {
                            view.addPhoto(photo);
                        }
                    }, new Consumer<Throwable>() {
                        @Override public void accept(Throwable throwable) throws Exception {
                            view.showError(throwable);
                        }
                    }, new Action() {
                        @Override public void run() throws Exception {
                            view.showProgressBar(false);
                        }
                    });
        }
    }

    private String md5(String filePath) {
        if (md == null) return "no md5";
        try {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                StringBuilder hexString = new StringBuilder();
                byte[] buffer = new byte[8192];
                int count;

                while ((count = fis.read(buffer)) > 0) md.update(buffer, 0, count);
                byte[] fileDigest = md.digest();

                for (byte digestByte : fileDigest)
                    hexString.append(String.format("%02x", digestByte));

                return hexString.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

//    private static final String[] dictionary = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    /*private String format(double size, int digits) {
        Log.d("photo", "l: " + size);

        int index;
        for (index = 0; index < dictionary.length; index++) {
            if (size < 1024) {
                break;
            }
            size = size / 1024;
        }
        final String format = String.format("%." + digits + "f", size) + " " + dictionary[index];
        Log.d("photo", " format- " + format);
        return format;
    }*/

    // stackoverflow
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
