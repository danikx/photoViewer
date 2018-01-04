package photoViewer.com.di.components;

import javax.inject.Singleton;

import dagger.Component;
import photoViewer.com.di.modules.ApplicationModule;
import photoViewer.com.ui.MainActivity;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);

}
