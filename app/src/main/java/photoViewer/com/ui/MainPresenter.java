package photoViewer.com.ui;


import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import photoViewer.com.PhotoObserver;
import photoViewer.com.PhotoRepository;
import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private MessageDigest md;
    private PhotoObserver observer;
    private PhotoRepository repository;
    private ArrayList<Photo> photos;
    private CompositeDisposable disposable = new CompositeDisposable();

    public MainPresenter(PhotoObserver observer, PhotoRepository repository) {
        this.observer = observer;
        this.repository = repository;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            view.showError(e);
        }

        disposable.add(
                observer.observable()
                        .distinct()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Consumer<Photo>() {
                                    @Override public void accept(Photo photo) throws Exception {
                                        if (!photos.contains(photo)) {
                                            view.showNoData(false);

                                            photo.fileHash = md5(photo.path);
                                            photo.fileSizeInString = humanReadableByteCount(photo.fileSize);

                                            view.addPhotoTop(photo);
                                        }
                                    }
                                },
                                new Consumer<Throwable>() {
                                    @Override public void accept(Throwable throwable) throws Exception {
                                        view.showError(throwable);
                                    }
                                }));
    }

    @Override public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override public void bind() {
        view.showNoPermissionView(!view.hashPermissions());
    }

    @Override public void unBind() {
        disposable.clear();
        if (observer != null) observer.stop();
    }

    @Override public void permissionsGranted() {
        view.showNoPermissionView(false);
        if (observer != null) observer.start();
        loadPhotos();
    }

    @Override public void permissionsDenied() {
        view.showNoPermissionView(true);
    }

    @Override public void onCreate() {

        if (!view.hashPermissions()) {
            view.askPermissions();

        } else {
            if (observer != null) observer.start();

            loadPhotos();
        }
    }

    private void loadPhotos() {
        view.clearData();
        view.showProgressBar(true);

        photos = repository.readPhotos();

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
                            photo.fileSizeInString = humanReadableByteCount(photo.fileSize);
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

    private static final String[] dictionary = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    private String humanReadableByteCount(double size) {
        int index;
        for (index = 0; index < dictionary.length; index++) {
            if (size < 1024) {
                break;
            }
            size = size / 1024;
        }
        return String.format("%.2f", size) + " " + dictionary[index];
    }
}
