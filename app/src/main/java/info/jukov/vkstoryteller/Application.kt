package info.jukov.vkstoryteller

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * User: jukov
 * Date: 17.09.2017
 * Time: 16:15
 */

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
