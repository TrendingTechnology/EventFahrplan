package nerd.tuxmobil.fahrplan.congress.alarms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.ligi.tracedroid.logging.Log;

import java.util.Random;

import nerd.tuxmobil.fahrplan.congress.MyApp;
import nerd.tuxmobil.fahrplan.congress.autoupdate.UpdateService;
import nerd.tuxmobil.fahrplan.congress.contract.BundleKeys;
import nerd.tuxmobil.fahrplan.congress.exceptions.BuilderException;
import nerd.tuxmobil.fahrplan.congress.notifications.NotificationHelper;
import nerd.tuxmobil.fahrplan.congress.repositories.AppRepository;
import nerd.tuxmobil.fahrplan.congress.schedule.MainActivity;

public final class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_SESSION = "nerd.tuxmobil.fahrplan.congress.ALARM_SESSION";

    public static final String ALARM_UPDATE = "nerd.tuxmobil.fahrplan.congress.ALARM_UPDATE";

    public static final String ALARM_DELETE = "de.machtnix.fahrplan.ALARM";

    private static final String LOG_TAG = "AlarmReceiver";

    private static final int DEFAULT_REQUEST_CODE = 0;

    @SuppressWarnings("java:S2245")
    @Override
    public void onReceive(Context context, Intent intent) {

        MyApp.LogDebug(LOG_TAG, "Received alarm = " + intent.getAction() + ".");

        if (intent.getAction().equals(ALARM_SESSION)) {
            String sessionId = intent.getStringExtra(BundleKeys.ALARM_SESSION_ID);
            Log.d(LOG_TAG, "sessionId = " + sessionId + ", intent = " + intent);
            int day = intent.getIntExtra(BundleKeys.ALARM_DAY, 1);
            long when = intent
                    .getLongExtra(BundleKeys.ALARM_START_TIME, System.currentTimeMillis());
            String title = intent.getStringExtra(BundleKeys.ALARM_TITLE);
            //Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();

            AppRepository appRepository = AppRepository.INSTANCE;

            Intent launchIntent = MainActivity.createLaunchIntent(context, sessionId, day);
            PendingIntent contentIntent = PendingIntent
                    .getActivity(context, DEFAULT_REQUEST_CODE, launchIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationHelper notificationHelper = new NotificationHelper(context);
            Uri soundUri = appRepository.readAlarmToneUri();
            NotificationCompat.Builder builder = notificationHelper.getSessionAlarmNotificationBuilder(contentIntent, title, when, soundUri);
            boolean isInsistentAlarmsEnabled = appRepository.readInsistentAlarmsEnabled();
            MyApp.LogDebug(LOG_TAG, "Preference 'isInsistentAlarmsEnabled' = " + isInsistentAlarmsEnabled + ".");
            notificationHelper.notify(new Random().nextInt(), builder, isInsistentAlarmsEnabled);

            appRepository.deleteAlarmForSessionId(sessionId);

            appRepository.notifyAlarmsChanged();
        } else if (intent.getAction().equals(ALARM_UPDATE)) {
            UpdateService.start(context);
        }
    }

    private static Intent getAddAlarmIntent(@NonNull Context context,
                                            @NonNull String sessionId,
                                            int day,
                                            @NonNull String title,
                                            long startTime) {
        return getAlarmIntent(context, sessionId, day, title, startTime, ALARM_SESSION);
    }

    private static Intent getDeleteAlarmIntent(@NonNull Context context,
                                               @NonNull String sessionId,
                                               int day,
                                               @NonNull String title,
                                               long startTime) {
        return getAlarmIntent(context, sessionId, day, title, startTime, ALARM_DELETE);
    }

    private static Intent getAlarmIntent(@NonNull Context context,
                                         @NonNull String sessionId,
                                         int day,
                                         @NonNull String title,
                                         long startTime,
                                         @NonNull String intentAction) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(BundleKeys.ALARM_SESSION_ID, sessionId);
        intent.putExtra(BundleKeys.ALARM_DAY, day);
        intent.putExtra(BundleKeys.ALARM_TITLE, title);
        intent.putExtra(BundleKeys.ALARM_START_TIME, startTime);
        intent.setAction(intentAction);
        intent.setData(Uri.parse("alarm://" + sessionId));
        return intent;
    }

    public static class AlarmIntentBuilder {

        private Context context = null;
        private String sessionId = null;
        private int day = Integer.MAX_VALUE;
        private String title = null;
        private long startTime = Long.MIN_VALUE;
        private boolean isAddAlarmIntent = true;
        private boolean addMethodWasInvoked = false;
        private boolean deleteMethodWasInvoked = false;

        public AlarmIntentBuilder setContext(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public AlarmIntentBuilder setSessionId(@NonNull String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public AlarmIntentBuilder setDay(int day) {
            this.day = day;
            return this;
        }

        public AlarmIntentBuilder setTitle(@NonNull String title) {
            this.title = title;
            return this;
        }

        public AlarmIntentBuilder setStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public AlarmIntentBuilder setIsAddAlarm() {
            this.isAddAlarmIntent = true;
            this.addMethodWasInvoked = true;
            return this;
        }

        public AlarmIntentBuilder setIsDeleteAlarm() {
            this.isAddAlarmIntent = false;
            this.deleteMethodWasInvoked = true;
            return this;
        }

        public Intent build() {
            if (context == null) {
                throw new BuilderException("Field 'context' is not set.");
            }
            if (sessionId == null) {
                throw new BuilderException("Field 'sessionId' is not set.");
            }
            if (day == Integer.MAX_VALUE) {
                throw new BuilderException("Field 'day' is not set.");
            }
            if (title == null) {
                throw new BuilderException("Field 'title' is not set.");
            }
            if (startTime == Long.MIN_VALUE) {
                throw new BuilderException("Field 'startTime' is not set.");
            }
            if (addMethodWasInvoked && deleteMethodWasInvoked) {
                throw new BuilderException("Either call 'setIsAddAlarm()' OR 'setIsDeleteAlarm()' - not both.");
            }
            if (isAddAlarmIntent) {
                return AlarmReceiver.getAddAlarmIntent(context, sessionId, day, title, startTime);
            } else {
                return AlarmReceiver.getDeleteAlarmIntent(context, sessionId, day, title, startTime);
            }
        }

    }

}
