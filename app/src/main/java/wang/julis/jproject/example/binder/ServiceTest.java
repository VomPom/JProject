package wang.julis.jproject.example.binder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/*******************************************************
 *
 * Created by julis.wang on 2021/05/13 18:57
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class ServiceTest extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
