package photoViewer.com.ui;

import android.util.Log;

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
import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MessageDigest md;

    public MainPresenter() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

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

    private void loadPhotos3() {
        view.showProgressBar(true);

        //todo
//        view.showNoData(true);
//        view.showData(getSimpleData());

        Observable.just(view.getFilePaths())
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
                        view.showError();
                    }
                });
    }

    private void loadPhotos() {
        view.showProgressBar(true);

        Observable
                .fromIterable(view.getFilePaths())
                .subscribeOn(Schedulers.io())
                .map(new Function<Photo, Photo>() {

                    @Override public Photo apply(Photo photo) throws Exception {
//                        Log.d("photo", "thread: " + Thread.currentThread().getName());
                        photo.fileHash = md5(photo.path);
                        photo.fileSize = format(Long.decode(photo.fileSize), 2);
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
                        Log.e("photo", null, throwable);
                        view.showError();
                    }
                }, new Action() {
                    @Override public void run() throws Exception {
                        Log.d("photo", "completed");
                        view.showProgressBar(false);
                    }
                });
    }

    private String md5(String filePath) {
        if (md == null) return "no md5";
        try {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                StringBuilder hexString = new StringBuilder();
                byte[] buffer = new byte[1000];
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

    private static final String[] dictionary = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    private String format(double size, int digits) {
        Log.d("photo", "l: " + size);

        int index;
        for (index = 0; index < dictionary.length; index++) {
            if (size < 1024) {
                break;
            }
            size = size / 1024;
        }
        final String format = String.format("%." + digits + "f", size) + " " + dictionary[index];
        Log.d("photo",  " format- " + format);
        return format;
    }
}
