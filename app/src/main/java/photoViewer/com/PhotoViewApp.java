package photoViewer.com;

import android.app.Application;
import android.content.Context;

import photoViewer.com.di.components.ApplicationComponent;
import photoViewer.com.di.components.DaggerApplicationComponent;
import photoViewer.com.di.modules.ApplicationModule;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

public class PhotoViewApp extends Application {
    private ApplicationComponent applicationComponent;

    @Override public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

    }

    @Override public void onTerminate() {
        super.onTerminate();

    }

    public static ApplicationComponent component(Context context) {
        final PhotoViewApp app = (PhotoViewApp) context.getApplicationContext();
        return app.applicationComponent;
    }
}
