package photoViewer.com.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
    @Singleton MainContract.Presenter provideMainPresenter() {
        return new MainPresenter();
    }


}
