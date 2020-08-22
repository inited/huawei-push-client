package cz.inited.test.huawei.push;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.agconnect.config.LazyInputStream;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context ctx = this;

        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(ctx).getString("client/app_id");
                    Log.i(TAG, "GET appId:" + appId);
                    String token = HmsInstanceId.getInstance(ctx).getToken(appId, "HCM");
                    Log.i(TAG, "GET token:" + token);
                } catch (ApiException e) {
                    Log.e(TAG, "GET token failed, " + e);
                }
            }
        }.start();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(context);
        config.overlayWith(new LazyInputStream(context) {
            public InputStream get(Context context) {
                try {
                    return context.getAssets().open("agconnect-services.json");
                } catch (IOException e) {
                    Log.e(TAG, "Cannot open agconnect-services.json", e);
                    return null;
                }
            }
        });
    }

}