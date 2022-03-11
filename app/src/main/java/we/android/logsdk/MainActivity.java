package we.android.logsdk;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import we.android.utils.log.KLog;
import we.android.utils.log.KConfig;

public class MainActivity extends Activity {
    private Thread mThread;
    public String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean needPermission = false;
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                needPermission = true;
            }
        }
        if (!needPermission) {
            log();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;
        if (1000 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {
            log();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThread.interrupt();
    }

    public void log() {
        KLog.init(this, new KConfig(KConfig.Builder
                .setTag("ASP_LOG")
                .setShowThead(true)
                .setShowLocation(true)
                .setSaveToFile(true)));

        KLog.i("how are u");

        KLog.v("im fine");

        KLog.d("thank u\n  yes\n no");

        KLog.json("{ \"key\": 3, \"value\": something}");

        KLog.d(Arrays.asList("foo", "bar"));

        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        map.put("key1", "value2");

        KLog.d(map);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    KLog.d("say your name say your name say your name \n " +
                            "say your name say your name say your name\n" + "say your name say your name say your name\n" +
                            "say your name say your name say your name\n");
                }
            }
        });
        mThread.start();
    }

}
