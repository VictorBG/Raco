package com.victorbg.racofib.data.background;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


public class Notify {

  public NotificationCompat.Builder getNotificationBuilder() {
    return builder;
  }

  public enum NotificationImportance {MIN, LOW, HIGH, MAX}

  public interface DefaultChannelKeys {

    String
        ID = "notify_channel_id",
        NAME = "notify_channel_name",
        DESCRIPTION = "notify_channel_description";
  }

  private Context context;

  private String channelId;
  private String channelName;
  private String channelDescription;

  private @DrawableRes int largeIcon, bigPicture;
  private String title, content;
  private int id, smallIcon, oreoImportance, importance, color;
  private Intent action;
  private long[] vibrationPattern;
  private boolean autoCancel, vibration;
  private NotificationCompat.Builder builder;
  private boolean ongoing;

  private Notify(Context _context) {
    this.context = _context;

    ApplicationInfo applicationInfo = this.context.getApplicationInfo();

    this.id = (int) System.currentTimeMillis();

    try {

      this.channelId = getStringResourceByKey(context, Notify.DefaultChannelKeys.ID);
      this.channelName = getStringResourceByKey(context, Notify.DefaultChannelKeys.NAME);
      this.channelDescription = getStringResourceByKey(context, Notify.DefaultChannelKeys.DESCRIPTION);

    } catch (Resources.NotFoundException e) {
      throw new RuntimeException("Channel was not found");
    }

    this.title = "Notify";
    this.content = "Hello world!";
    this.largeIcon = applicationInfo.icon;
    this.smallIcon = applicationInfo.icon;
    this.bigPicture = -1;

    builder = new NotificationCompat.Builder(context, channelId);

    this.color = -1;
    this.action = null;
    this.vibration = true;
    this.vibrationPattern = new long[]{0, 250, 250, 250};
    this.autoCancel = false;
    this.setImportanceDefault();
  }

  public static Notify create(@NonNull Context context) {
    return new Notify(context);
  }

  public void show() {
    if (context == null) {
      return;
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager == null) {
      return;
    }

    builder.setAutoCancel(this.autoCancel)
        .setDefaults(Notification.DEFAULT_SOUND)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setContentText(content)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
        .setOngoing(ongoing);

    Bitmap largeIconBitmap = getBitmapFromRes(this.context,  largeIcon);

    if (largeIconBitmap != null) {
      builder.setLargeIcon(largeIconBitmap);
    }

    if (bigPicture != -1) {
      Bitmap bigPictureBitmap = getBitmapFromRes(this.context,bigPicture);

      if (bigPictureBitmap != null) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle().bigPicture(bigPictureBitmap)
            .setSummaryText(content);
        bigPictureStyle.bigLargeIcon(largeIconBitmap);
        builder.setStyle(bigPictureStyle);
      }
    }

    int realColor = color == -1 ? Color.BLACK : context.getResources().getColor(color, null);

    builder.setColor(realColor);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(
          channelId, channelName, oreoImportance
      );
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(realColor);
      notificationChannel.setDescription(this.channelDescription);
      notificationChannel.setVibrationPattern(this.vibrationPattern);
      notificationChannel.enableVibration(this.vibration);

      notificationManager.createNotificationChannel(notificationChannel);
    } else {
      builder.setPriority(this.importance);
    }

    if (this.vibration) {
      builder.setVibrate(this.vibrationPattern);
    } else {
      builder.setVibrate(new long[]{0});
    }

    if (this.action != null) {
      PendingIntent pi = PendingIntent.getActivity(context, id, this.action, PendingIntent.FLAG_CANCEL_CURRENT);
      builder.setContentIntent(pi);
    }
    notificationManager.notify(id, builder.build());
  }

  public Notify setTitle(@NonNull String title) {
    if (!title.trim().isEmpty()) {
      this.title = title.trim();
    }
    return this;
  }

  public Notify setContent(@NonNull String content) {
    if (!content.trim().isEmpty()) {
      this.content = content.trim();
    }
    return this;
  }

  public Notify setChannelId(@NonNull String channelId) {
    if (!channelId.trim().isEmpty()) {
      this.channelId = channelId.trim();
      this.builder.setChannelId(channelId);
    }
    return this;
  }

  public Notify setChannelName(@NonNull String channelName) {
    if (!channelName.trim().isEmpty()) {
      this.channelName = channelName.trim();
    }
    return this;
  }

  public Notify setChannelDescription(@NonNull String channelDescription) {
    if (!channelDescription.trim().isEmpty()) {
      this.channelDescription = channelDescription.trim();
    }
    return this;
  }

  public Notify setImportance(@NonNull Notify.NotificationImportance importance) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      switch (importance) {
        case MIN:
          this.oreoImportance = NotificationManager.IMPORTANCE_MIN;
          break;

        case LOW:
          this.importance = Notification.PRIORITY_LOW;
          this.oreoImportance = NotificationManager.IMPORTANCE_LOW;
          break;

        case HIGH:
          this.importance = Notification.PRIORITY_HIGH;
          this.oreoImportance = NotificationManager.IMPORTANCE_HIGH;
          break;

        case MAX:
          this.importance = Notification.PRIORITY_MAX;
          this.oreoImportance = NotificationManager.IMPORTANCE_MAX;
          break;
      }
    }
    return this;
  }

  private void setImportanceDefault() {
    this.importance = Notification.PRIORITY_DEFAULT;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      this.oreoImportance = NotificationManager.IMPORTANCE_DEFAULT;
    }
  }

  public Notify enableVibration(boolean vibration) {
    this.vibration = vibration;
    return this;
  }

  public Notify setAutoCancel(boolean autoCancel) {
    this.autoCancel = autoCancel;
    return this;
  }

  public Notify setVibrationPattern(long[] vibrationPattern) {
    this.vibrationPattern = vibrationPattern;
    return this;
  }

  public Notify setColor(@ColorRes int color) {
    this.color = color;
    return this;
  }

  public Notify setSmallIcon(@DrawableRes int smallIcon) {
    this.smallIcon = smallIcon;
    return this;
  }

  public Notify setLargeIcon(@DrawableRes int largeIcon) {
    this.largeIcon = largeIcon;
    return this;
  }

  public Notify setBigPicture(@DrawableRes int bigPicture) {
    this.bigPicture = bigPicture;
    return this;
  }


  public Notify setAction(@NonNull Intent action) {
    this.action = action;
    return this;
  }

  public Notify setId(int id) {
    this.id = id;
    return this;
  }

  public int getId() {
    return id;
  }

  public Notify setOngoing(boolean ongoing) {
    this.ongoing = ongoing;
    return this;
  }

  public boolean isOngoing() {
    return ongoing;
  }

  public String getChannelId() {
    return channelId;
  }

  public String getChannelName() {
    return channelName;
  }

  public String getChannelDescription() {
    return channelDescription;
  }

  public static void cancel(@NonNull Context context, int id) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager != null) {
      notificationManager.cancel(id);
    }
  }

  public static void cancelAll(@NonNull Context context) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager != null) {
      notificationManager.cancelAll();
    }
  }

  private String getStringResourceByKey(@NonNull Context context, @NonNull String resourceKey) {
    int resId = context.getResources().getIdentifier(resourceKey, "string", context.getPackageName());
    return context.getResources().getString(resId);
  }

  private Bitmap getBitmapFromRes(@NonNull Context context, int res) {
    return BitmapFactory.decodeResource(context.getResources(), res);
  }
}



