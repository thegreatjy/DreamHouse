package org.techtown.project_savedreamhouse;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static org.techtown.project_savedreamhouse.NotiFilter.atclno;
import static org.techtown.project_savedreamhouse.MainActivity.atclno2;
import static org.techtown.project_savedreamhouse.NotiFilter.n;
import static org.techtown.project_savedreamhouse.MainActivity.n2;


//새 매물 알림을 위한 코드
public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    public static final String NOTIFICATION_CHANNEL_ID="10001";

    private String atclNum;
    private int count;

    Intent notificationIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //여기부터
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //atclno에 값이 있으면 saleDetail.java로 이어짐.
        if(NotiFilter.atclno!=null){
            atclNum= NotiFilter.atclno;
            count = NotiFilter.n;

            notificationIntent = new Intent(context,SaleDetail.class);
            notificationIntent.putExtra("houseID", NotiFilter.atclno);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }else if(atclno2!=null){
            atclNum=atclno2;
            count= n2;

            notificationIntent = new Intent(context,SaleDetail.class);
            notificationIntent.putExtra("houseID",atclno2);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else{
            notificationIntent = new Intent(context,FindSale.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context,101,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //새 매물이 1개 이상일 경우에만 알림
        if(count >0){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.heart)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_foreground))
                    .setContentTitle("구해줘 꿈의집!")
                    .setContentText("원하는 매물이 "+count+"개 등록되었습니다. 확인해 보세요!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                builder.setSmallIcon(R.drawable.heart);
                CharSequence channelName="노티피케이션 채널";
                String description="오레오 이상을 위한 것임.";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,channelName,importance);
                channel.setDescription(description);

                assert notificationManager !=null;
                notificationManager.createNotificationChannel(channel);
            }else builder.setSmallIcon(R.mipmap.ic_launcher);

            assert notificationManager != null;
            notificationManager.notify(1234,builder.build());
        }

    }

}