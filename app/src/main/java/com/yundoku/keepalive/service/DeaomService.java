package com.yundoku.keepalive.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.yundoku.keepalive.util.SPUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;

public class DeaomService extends Service {


    /**
     * JobService执行任务的时间间隔，分别测试10秒，10分钟，1小时，6小时，1天的间隔，JobService执行任务分别有延迟现象，并不能非常准确的执行。
     */
    private static final long KEEP_ALIVE_INTERVAL_TIME = 10 * 1000L;
    //    private static final long KEEP_ALIVE_INTERVAL_TIME = 10 * 60 * 1000L;
    //    private static final long KEEP_ALIVE_INTERVAL_TIME = 60 * 60 * 1000L;
    //    private static final long KEEP_ALIVE_INTERVAL_TIME = 6 * 60 * 60 * 1000L;
    //    private static final long KEEP_ALIVE_INTERVAL_TIME = 24 * 60 * 60 * 1000L;
    private static final int JOB_ID = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        keepAliveScheduler();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startDeaomService(getApplicationContext());
    }

    private void keepAliveScheduler() {
        updateKeepAliveEvent(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("zhang", "DeaomService --> keepAliveScheduler--> date: " + new Date(System.currentTimeMillis()));
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(getPackageName(), DeaomJobService.class.getName()))
                    .setPeriodic(KEEP_ALIVE_INTERVAL_TIME)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        } else {
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, getActionIntent(getApplicationContext()), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_ALIVE_INTERVAL_TIME, KEEP_ALIVE_INTERVAL_TIME, pendingIntent);
        }
    }

    /**
     * 上传保活事件
     */
    public static void updateKeepAliveEvent(Context context) {
        if (context == null) return;
        long lastUpdateTime = (long) SPUtil.get(context, "EVENT_LAST_UPDATE_TIME_SP_KEY", 0L);
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0 || currentTime - lastUpdateTime >= KEEP_ALIVE_INTERVAL_TIME) {
            SPUtil.put(context, "EVENT_LAST_UPDATE_TIME_SP_KEY", currentTime);
            Log.e("zhang", "DeaomService --> updateKeepAliveEvent-->currentTime: " + new Date(currentTime));
            writeFileToLocal(context, currentTime);
        }
    }

    /**
     * 将执行时间写入文件，方便测试数据搜集
     */
    private static void writeFileToLocal(final Context context, final long time) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                File dir = context.getExternalCacheDir();
                if (dir != null && dir.exists()) {
                    File file = new File(dir, "keep-alive.txt");
                    FileWriter write = null;
                    try {
                        write = new FileWriter(file, true);
                        write.write("update event time: " + new Date(time).toString() + "\n");
                        write.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (write != null) {
                            try {
                                write.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private Intent getActionIntent(Context context) {
        Intent intent = new Intent(context, DeaomService.class);
        intent.setAction("com.yiba.alarm_Action");
        return intent;
    }

    public static void startDeaomService(Context context) {
        Intent intent = new Intent(context, DeaomService.class);
        context.startService(intent);
    }
}
