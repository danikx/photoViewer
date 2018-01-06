package photoViewer.com.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import photoViewer.com.PhotoObserver;
import photoViewer.com.PhotoRepository;
import photoViewer.com.ui.MainContract;
import photoViewer.com.ui.MainPresenter;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

@Module
public class ApplicationModule {

    private Context ctx;

    public ApplicationModule(Context ctx) {
        this.ctx = ctx;
    }

    @Provides
    @Singleton Context provideContext() {
        return this.ctx;
    }

    @Provides
    @Singleton PhotoObserver providePhotoObserver(Context context) {
        return new PhotoObserver(context);
    }

    @Provides
    @Singleton PhotoRepository providePhotoRepository(Context context) {
        return new PhotoRepository(context);
    }

    @Provides
    @Singleton MainContract.Presenter provideMainPresenter(PhotoObserver observer, PhotoRepository repository) {
        return new MainPresenter(observer, repository);
    }
}
