package com.example.helperapp.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helperapp.R;
import com.example.helperapp.adapters.WorkflowSuggestionAdapter;
import com.example.helperapp.customviews.CustomImageView;
import com.example.helperapp.customviews.CustomView;
import com.example.helperapp.models.WorkflowDB;
import com.example.helperapp.models.WorkflowSuggestionModel;
import com.example.helperapp.utils.Constants;
import com.example.helperapp.utils.MessageEvent;
import com.example.helperapp.utils.NotifyEvents;
import com.example.helperapp.utils.ViewMappingDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import floatingview.FloatingViewListener;
import floatingview.FloatingViewManager;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * ChatHead Service
 */
public class ChatHeadService extends Service implements FloatingViewListener, RecognitionListener {

    /**
     *
     */
    private static final String TAG = "ChatHeadService";

    /**
     * Intent key (Cutout safe area)
     */
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";

    /**
     *
     */
    private static final int NOTIFICATION_ID = 9083150;

    /**
     * FloatingViewManager
     */
    private FloatingViewManager mFloatingViewManager;
    private View mView;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    private ListView wordsList;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean listening = false;
    private View mainView;


    public int x_whatsapp_screen_1 = 1030;
    public int y_whatsapp_screen_1 = 70;
    public int radiusOfCircle = 80;


    private View currentView;
    private FrameLayout frameLayout;

    private static int gifAdjustment_x = 220;
    private static int gifAdjustment_y = 100;
    private GifImageView gifImageView;
    private TextToSpeech t1;
    public String currentShape = "circle";
    public Rect rect = null;
    private Boolean workFlowStarted = false;

    static {
        gifAdjustment_x = getScreenWidth() / 6;
        gifAdjustment_y = getScreenHeight() / 15;
    }

    private TextToSpeech ttobj;
    private String packegeLauncherName;
    private boolean startedAppFormLauncher = false;
    private boolean guruMessageActive = false;
    private View containerView;
    private String currentAppLaunchedFromLauncher = "";
    CustomImageView iconView;
    private FloatingViewManager.Options options;
    private boolean workflowRequestedbyUser = false;
    private ArrayList<String> allTextsOnScreen = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        iconView = (CustomImageView) inflater.inflate(R.layout.widget_chathead, null, false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent1 = new Intent(ChatHeadService.this, DialogActivity.class);
//                intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent1);


                Log.d(TAG, getString(R.string.chathead_click_message));
                if (frameLayout != null)
                    frameLayout.removeAllViews();

                boolean enabled = isAccessibilityServiceEnabled(ChatHeadService.this, MyAccessibilityService.class);
                if (!enabled) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(ChatHeadService.this, "Accessibility not enabled", Toast.LENGTH_SHORT).show();
                    return;
                }
                workflowRequestedbyUser = true;
                createWindow();
            }
        });

        setupTextToSpeech();

        packegeLauncherName = getMainLauncherPackageName();


        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));
        options = new FloatingViewManager.Options();
        options.overMargin = (int) (16 * metrics.density);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconView.setZ(10f);
        }
        mFloatingViewManager.addViewToWindow(iconView, options);

        // Resident activation
        startForeground(NOTIFICATION_ID, createNotification(this));

        EventBus.getDefault().register(this);

        return START_REDELIVER_INTENT;
    }

    private void setupTextToSpeech() {
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttobj.setLanguage(Locale.forLanguageTag("hin"));
        }
    }


    public void createWindow() {

        Intent intent = new Intent(ChatHeadService.this, MyAccessibilityService.class);
        startService(intent);

        mainView = setLayoutForMainView(intent);
        //starting workflow as soon as icon is clicked because we do not need to get user input owf which workflow to start

        if (startedAppFormLauncher) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    appLaunchedFromLauncherFirstScreen();
                    startedAppFormLauncher = false;
                }
            }, 2000);

        } else startWorkflow(new WorkflowSuggestionModel(""));

        mView = new MyLoadView(ChatHeadService.this);

        Log.e("height", "" + getScreenHeight());

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, (getScreenHeight() - getScreenHeight() / 5), 10, 10,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.CENTER;
        mParams.setTitle("Window test");

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //mWindowManager.addView(mainView, mParams);
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }


    View setLayoutForMainView(final Intent intent) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View v = inflater.inflate(R.layout.activity_main_search, null, false);
        ImageView imageView = v.findViewById(R.id.record);
        ImageView closeWindow = v.findViewById(R.id.closeWindow);
        TextView tvRecordedText = v.findViewById(R.id.recorded_text);
        RecyclerView recyclerView = v.findViewById(R.id.rvWorkflowSuggestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatHeadService.this));
        ArrayList<WorkflowSuggestionModel> workflowSuggestionModels = new ArrayList<>();
        WorkflowSuggestionAdapter workflowSuggestionAdapter = new WorkflowSuggestionAdapter(workflowSuggestionModels, ChatHeadService.this);

        recyclerView.setAdapter(workflowSuggestionAdapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startWorkflow(new WorkflowSuggestionModel("Start Uber promotions workflow"));
                return;


                //registerListener(imageView, workflowSuggestionAdapter, tvRecordedText, workflowSuggestionModels);


            }
        });

        closeWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent);
                mWindowManager.removeView(v);
            }
        });


        return v;
    }

    private void registerListener(final ImageView imageView, final WorkflowSuggestionAdapter workflowSuggestionAdapter, final TextView tvRecordedText, final ArrayList<WorkflowSuggestionModel> workflowSuggestionModels) {
        imageView.setEnabled(false);

        if (listening) {
            mSpeechRecognizer.stopListening();
            tvRecordedText.setText("Processing. please wait...");
            listening = false;
            imageView.setEnabled(false);
            return;
        }
        listening = true;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(ChatHeadService.this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //mSpeechRecognizerIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"hi"});

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getApplicationContext().getPackageName());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.e("ready", "ready");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e("begin", "ready");
                imageView.setEnabled(true);


            }

            @Override
            public void onRmsChanged(float v) {
                Log.e("rms", "" + v);

            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.e("buffer", "ready");

            }

            @Override
            public void onEndOfSpeech() {
                Log.e("end", "end of speech");


            }

            @Override
            public void onError(int i) {
                Log.e("error", "" + i);
                imageView.setEnabled(true);
                tvRecordedText.setText("error code: " + i);


            }

            @Override
            public void onResults(Bundle bundle) {
                imageView.setEnabled(true);

                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.e("matches", matches.toString());
                tvRecordedText.setText(matches.get(0));

                WorkflowDB workflowDB = new WorkflowDB();

                workflowSuggestionModels.clear();
                //check matches from class
                workflowSuggestionModels.addAll(workflowDB.getWorkflow(matches.get(0)));
                workflowSuggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.e("event", bundle.toString());

            }
        });
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinishFloatingView() {
        stopSelf();
        Log.d(TAG, getString(R.string.finish_deleted));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        if (isFinishing) {
            Log.d(TAG, getString(R.string.deleted_soon));
        } else {
            Log.d(TAG, getString(R.string.touch_finished_position, x, y));
        }
    }

    private void destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            if (frameLayout != null)
                frameLayout.removeAllViews();
            mFloatingViewManager = null;
        }
    }


    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_floatingview_channel_id));
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.chathead_content_title));
        builder.setContentText(context.getString(R.string.content_text));
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }

    // voice command callbacks
    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Toast.makeText(getBaseContext(), "Voice recording starts", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.e("matches", matches.toString());
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    public class MyLoadView extends View {

        private Paint mPaint;

        public MyLoadView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setTextSize(50);
            mPaint.setARGB(200, 200, 200, 200);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawText("test test test", 0, 100, mPaint);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    public void startWorkflow(WorkflowSuggestionModel workflow) {

        if (mWindowManager != null)
            ;//mWindowManager.removeView(mainView);
        else mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        mView = new MyLoadView(ChatHeadService.this);
        Log.e("height", "" + getScreenHeight());
        final LayoutInflater inflater = LayoutInflater.from(this);
        containerView = inflater.inflate(R.layout.workflow_suggestion_layout, null, false);

        if (ViewMappingDB.currentApp.equalsIgnoreCase(Constants.PLAYSTORE)) {
            frameLayout = containerView.findViewById(R.id.mainContainer);
            createPlaystoreAppInstallPage();

            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 10, 10,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT);

        } else {

            LayoutInflater inflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater1.inflate(R.layout.chat_workflow_options_layout, null, true);
            //LinearLayout travelDirection = v.findViewById(R.id.travelDirection);
            //LinearLayout notInterested = v.findViewById(R.id.notInterested);
//            travelDirection.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mWindowManager.removeView(containerView);
//                    mParams = new WindowManager.LayoutParams(
//                            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 10, 10,
//                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                            PixelFormat.TRANSLUCENT);
//                    mParams.gravity = Gravity.CENTER;
//                    currentView = v;
//                    mWindowManager.addView(containerView, mParams);
//                    currentShape = "circle";
//                    if (ViewMappingDB.currentApp.equalsIgnoreCase(Constants.GOOGLEMAPS)) {
//                        createMapMicPage();
//                    } else if (ViewMappingDB.currentApp.equalsIgnoreCase(Constants.YOUTUBE)) {
//                        createYoutubeFirstPage();
//                    } else if (ViewMappingDB.currentApp.equalsIgnoreCase(Constants.PAYTM)) {
//                        createPaytmAddMoneyPage();
//                    }
//                }
//            });
//
//            notInterested.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mWindowManager.removeView(containerView);
//                }
//            });
            //TextView tvTitle = v.findViewById(R.id.tvTitle);
            //TextView tvFirstflow = v.findViewById(R.id.tvFirstflow);
//            if (currentAppLaunchedFromLauncher.equalsIgnoreCase(Constants.YOUTUBE)) {
//                tvTitle.setText("Youtube pe aap kya karna chahte hai?");
//                tvFirstflow.setText("Apni pasand ki video search karein");
//            }
//            if (ViewMappingDB.currentApp.equalsIgnoreCase(Constants.PAYTM)) {
//                tvTitle.setText("Paytm pe aap kya karna chahte hai?");
//                tvFirstflow.setText("Paytm wallet me paise add karein");
//            }


            frameLayout = containerView.findViewById(R.id.mainContainer);
            frameLayout.addView(v);
            currentView = v;

            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 10, 10,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

        }

        //}


        //showGif();


        mParams.gravity = Gravity.CENTER;
        mWindowManager.addView(containerView, mParams);
        guruMessageActive = false;
        resetCurrentShape();
    }


    void appLaunchedFromLauncherFirstScreen() {
        if (mWindowManager != null)
            ;//mWindowManager.removeView(mainView);
        else mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        mView = new MyLoadView(ChatHeadService.this);
        Log.e("height", "" + getScreenHeight());
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View containerView = inflater.inflate(R.layout.workflow_suggestion_layout, null, false);

        LayoutInflater inflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater1.inflate(R.layout.guru_message_popup, null, true);
        TextView textView = v.findViewById(R.id.text);
        ImageView imgClose = v.findViewById(R.id.close);
        if (currentAppLaunchedFromLauncher.equalsIgnoreCase(Constants.WHATSAPP)) {
            textView.setText("Namaste! \uD83D\uDE4F\n" +
                    "Whatsapp mei kahi bhi kuch bhi confusion ho to mujhse pooche!");
        } else if (currentAppLaunchedFromLauncher.equalsIgnoreCase(Constants.YOUTUBE)) {
            textView.setText("Namaste! \uD83D\uDE4F\n" +
                    "YouTube mei kahi bhi kuch bhi confusion ho to mujhse pooche!");
        } else if (currentAppLaunchedFromLauncher.equalsIgnoreCase(Constants.GOOGLEMAPS)) {
            textView.setText("Namaste! \uD83D\uDE4F\n" +
                    "Google maps mei kahi bhi kuch bhi confusion ho to mujhse pooche!");
        } else if (currentAppLaunchedFromLauncher.equalsIgnoreCase(Constants.PAYTM)) {
            textView.setText("Namaste! \uD83D\uDE4F\n" +
                    "PAYTM app mei kahi bhi kuch bhi confusion ho to mujhse pooche!");
        } else {
            textView.setText("Namaste! \uD83D\uDE4F\n" +
                    "Is app mei kahi bhi kuch bhi confusion ho to mujhse pooche!");
        }
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllViews();
            }
        });

        frameLayout = containerView.findViewById(R.id.mainContainer);
        frameLayout.addView(v);

        //showGif();

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 10, 10,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        currentView = v;
        mWindowManager.addView(containerView, mParams);
        resetCurrentShape();
        guruMessageActive = true;
    }

    void showGif() {
        gifImageView = new GifImageView(ChatHeadService.this);
        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                gifAdjustment_x,
                gifAdjustment_y));
        if (x_whatsapp_screen_1 <= gifAdjustment_x) {
            gifImageView.setImageResource(R.drawable.left_arrow);
            gifImageView.setX(x_whatsapp_screen_1 + gifAdjustment_x);
            gifImageView.setY(y_whatsapp_screen_1 - gifAdjustment_y + radiusOfCircle);

        } else {
            gifImageView.setImageResource(R.drawable.finger_right);
            gifImageView.setX(x_whatsapp_screen_1 - gifAdjustment_x - radiusOfCircle);
            gifImageView.setY(y_whatsapp_screen_1 - gifAdjustment_y + radiusOfCircle);


        }
        frameLayout.addView(gifImageView);

    }

    void showGif(int x, int y) {
        gifImageView = new GifImageView(ChatHeadService.this);
        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                gifAdjustment_x,
                gifAdjustment_y));

        gifImageView.setImageResource(R.drawable.finger_right);
        gifImageView.setX(x);
        gifImageView.setY(y);

        frameLayout.addView(gifImageView);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        Log.e("event", event.getEvent().toString());

        try {
            Log.e("event view id", event.getAccessibilityNodeInfo().getViewIdResourceName() == null ? "" : event.getAccessibilityNodeInfo().getViewIdResourceName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (event.getEvent().getSource() != null && event.getEvent().getSource().getViewIdResourceName() != null)
            Log.e("view source id", event.getEvent().getSource().getViewIdResourceName());

        if (event.getEvent().getContentDescription() != null)
            Log.e("describeContent", event.getEvent().getContentDescription().toString());

        Log.e("type", "" + event.getEvent().getEventType());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e("content change type", "" + event.getEvent().getContentChangeTypes());
        }

        if (event.getEvent().getPackageName().equals("com.whatsapp")) {
            return;
//            setIconVisibility(View.VISIBLE);
//            ViewMappingDB.currentApp = ViewMappingDB.WHATSAPP;
//            if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
//                    && event.getEvent().getContentDescription() != null
//                    && event.getEvent().getContentDescription().toString().equals("More options")) {
//                createWhatsappPage2();
//            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
//                    && event.getEvent().getContentDescription() != null
//                    && event.getEvent().getContentDescription().toString().equals("Location")) {
//                createGoogleMapsPAgeForStartNavigation();
//            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//                    && event.getEvent().getClassName() != null
//                    && event.getEvent().getClassName().equals("com.whatsapp.GroupMembersSelector")) {
//                createWhatsappPage3();
//            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
//                    && event.getEvent().getContentDescription() != null
//                    && event.getEvent().getContentDescription().toString().equals("Search")) {
//                createWhatsappPage4();
//            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
//                    && event.getEvent().getText() != null) {
//                createWhatsappPage5();
//            }
        } else if (event.getEvent().getPackageName().equals("com.ubercab.driver")) {
            setIconVisibility(View.VISIBLE);

            if (event.getAccessibilityNodeInfo() != null) {
                Rect rect = new Rect();
                event.getAccessibilityNodeInfo().getBoundsInScreen(rect);
                AccessibilityNodeInfo accessibilityNodeInfo = findChildNodes(event.getAccessibilityNodeInfo());
                if (event.getAccessibilityNodeInfo().getViewIdResourceName() != null)
                    Log.e("data in message event", event.getAccessibilityNodeInfo().getViewIdResourceName() + " " + rect.left + " " + rect.top);
            }
            if (event.getEvent().getClassName().equals("com.ubercab.carbon.core.CarbonActivity")) {
                ViewMappingDB.currentApp = ViewMappingDB.UBER_DRIVER;
            }

            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
                    && event.getEvent().getSource() != null && event.getEvent().getSource().getViewIdResourceName() != null
                    && event.getEvent().getSource().getViewIdResourceName().equals("com.ubercab.driver:id/profile_entry"))
                    || (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
                    && event.getEvent().getClassName().equals("android.widget.FrameLayout")
                    && event.getEvent().getText().size() == 0)) {
                createUberProfilePageView();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().get(0).equals("Earnings") || event.getEvent().getText().get(0).equals("Payments"))) {
                createUberEarningLandingPageView();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && event.getEvent().getText().get(0).equals("Promotions")) {
                createUberPromotionLandingPageView();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getSource() != null)
                    && (event.getEvent().getSource().getViewIdResourceName() != null)
                    && event.getEvent().getSource().getViewIdResourceName().equals("com.ubercab.driver:id/ub__tracker_entry_content_view")) {
                createUberQuestSwipePageRTL();
            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED
                    && (event.getEvent().getClassName().equals("androidx.viewpager.widget.ViewPager"))
                    && (event.getEvent().getFromIndex() == 2)
                    && ((event.getEvent().getToIndex() == 2))) {
                createUberQuestSwipePage2RTL();
            }
        } else if (event.getEvent().getPackageName().equals("com.google.android.apps.maps")) {

            setIconVisibility(View.VISIBLE);

            ViewMappingDB.currentApp = ViewMappingDB.GOOGLEMAPS;
            // save all views' rect on screen to a hashmap to access positions later
            if (event.getAccessibilityNodeInfo() != null) {
                Rect rect = new Rect();
                event.getAccessibilityNodeInfo().getBoundsInScreen(rect);
                AccessibilityNodeInfo accessibilityNodeInfo = findChildNodes(event.getAccessibilityNodeInfo());
                if (event.getAccessibilityNodeInfo().getViewIdResourceName() != null)
                    Log.e("data in message event", event.getAccessibilityNodeInfo().getViewIdResourceName() + " " + rect.left + " " + rect.top);

                System.out.println("map data " + ViewMappingDB.vieWHashMap);
            }

            //TODO: create a generic method for flow start and pass flow parameters to it
            if (!workflowRequestedbyUser) {
                return;
            }
            // Code to perform action on view

//            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) {
//                AccessibilityNodeInfo nodeInfo = event.getEvent().getSource();
//                if (nodeInfo == null) {
//                    return;
//                }
//
//                List<AccessibilityNodeInfo> list = nodeInfo
//                        .findAccessibilityNodeInfosByViewId("com.google.android.apps.maps:id/search_omnibox_edit_text");
//                for (AccessibilityNodeInfo node : list) {
//                    Log.i(TAG, "ACC::onAccessibilityEvent: left_button " + node);
//                    if (node.getText().toString().equalsIgnoreCase("android"))
//                        continue;
//                    Bundle arguments = new Bundle();
//                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "android");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        node.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT.getId(), arguments);
//                    }
//
//                }
//            }

            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Voice search"))) {
                workFlowStarted = true;
                createMapSecondPageViewForNavigation();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Directions"))) {
                removeAllViews();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Clear"))) {
                removeAllViews();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) &&
                    (event.getEvent().getContentDescription() != null
                            && event.getEvent().getContentDescription().length() > 0
                            && event.getEvent().getContentDescription().toString().contains("Open account menu"))) {
                removeAllViews();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
                    && event.getEvent().getClassName().toString().equalsIgnoreCase("android.widget.RelativeLayout")
                    && (event.getEvent().getText().size() > 0)) {
                if (workFlowStarted)
                    createMapThirdPageViewForNavigation();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Start"))) {
                createMapPageAfterStartClicked();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)) {
                removeAllViews();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) &&
                        (event.getEvent().getContentChangeTypes() == 7)) {
                    //showMultipleResponseView();
                }
            }
        } else if (event.getEvent().getPackageName().equals("com.google.android.permissioncontroller")) {
            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)) {
                //&& (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().contains("record audio"))) {
                createPermissionAllowView();
            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Allow"))) {

            }
        } else if (event.getEvent().getPackageName().equals("com.google.android.youtube")) {
            iconView.setVisibility(View.VISIBLE);


            ViewMappingDB.currentApp = ViewMappingDB.YOUTUBE;
            // save all views' rect on screen to a hashmap to access positions later
            if (event.getAccessibilityNodeInfo() != null) {
                Rect rect = new Rect();
                event.getAccessibilityNodeInfo().getBoundsInScreen(rect);
                AccessibilityNodeInfo accessibilityNodeInfo = findChildNodes(event.getAccessibilityNodeInfo());
                if (event.getAccessibilityNodeInfo().getViewIdResourceName() != null)
                    Log.e("data in message event", event.getAccessibilityNodeInfo().getViewIdResourceName() + " " + rect.left + " " + rect.top);

            }

            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Search"))) {
                workFlowStarted = true;


                createYoutubeSearchPressed();
            }
            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) {
                AccessibilityNodeInfo nodeInfo = event.getEvent().getSource();
                if (nodeInfo == null) {
                    return;
                }

                List<AccessibilityNodeInfo> list = nodeInfo
                        .findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/voice_search");
                for (AccessibilityNodeInfo node : list) {
                    Log.i(TAG, "ACC::onAccessibilityEvent: left_button " + node);
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        } else if (event.getEvent().getPackageName().equals("com.android.vending")) {
            iconView.setVisibility(View.VISIBLE);
            ViewMappingDB.currentApp = Constants.PLAYSTORE;
            allTextsOnScreen = new ArrayList<>();
            if (event.getAccessibilityNodeInfo() != null) {
                Rect rect = new Rect();
                event.getAccessibilityNodeInfo().getBoundsInScreen(rect);
                AccessibilityNodeInfo accessibilityNodeInfo = findChildNodes(event.getAccessibilityNodeInfo());
                if (event.getAccessibilityNodeInfo().getViewIdResourceName() != null)
                    Log.e("data in message event", event.getAccessibilityNodeInfo().getViewIdResourceName() + " " + rect.left + " " + rect.top);
            }

            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) &&
                    (event.getEvent().getText() != null
                            && event.getEvent().getText().size() > 0
                            && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Install"))) {
                removeAllViews();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) &&
                    (event.getEvent().getText() != null
                            && event.getEvent().getText().size() > 0
                            && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Open"))) {
                removeAllViews();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) {
                Log.e("", "installation complete");
                for (int i = 0; i < allTextsOnScreen.size(); i++) {
                    if (allTextsOnScreen.get(i).contains("100%")) {
                        createPlaystoreAppInstallCompletePage();
                    }
                }
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)) {
                removeAllViews();
            } else if (event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                removeAllViews();
            }

            //Todo: code for playstore app install page
        } else if (event.getEvent().getPackageName().equals("in.org.npci.upiapp")) {
            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_ANNOUNCEMENT)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Permissions screen"))) {
                createUPIPermissionScreen();
            }

        } else if (event.getEvent().getPackageName().equals("net.one97.paytm")) {
            if (event.getAccessibilityNodeInfo() != null) {
                AccessibilityNodeInfo accessibilityNodeInfo = event.getAccessibilityNodeInfo();
                accessibilityNodeInfo.refresh();
                Rect rect = new Rect();
                event.getAccessibilityNodeInfo().getBoundsInScreen(rect);
                accessibilityNodeInfo = findChildNodes(accessibilityNodeInfo);
                if (event.getAccessibilityNodeInfo().getViewIdResourceName() != null)
                    Log.e("data in message event", event.getAccessibilityNodeInfo().getViewIdResourceName() + " " + rect.left + " " + rect.top);
            }
            ViewMappingDB.currentApp = Constants.PAYTM;
            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getSource() != null && event.getEvent().getSource().getViewIdResourceName() != null
                    && event.getEvent().getSource().getViewIdResourceName().equalsIgnoreCase("net.one97.paytm:id/image_container_3"))) {
                createPaytmAddMoneyPage2();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) &&
                    (event.getEvent().getText() != null
                            && event.getEvent().getText().size() > 0
                            && event.getEvent().getText().get(0).toString().contains("Paytm Wallet"))) {
                createPaytmEnterMoneyPage2();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED) &&
                    (event.getEvent().getText() != null
                            && event.getEvent().getText().size() > 0
                            && event.getEvent().getText().get(0).toString().contains("Amount"))) {
                createPaytmEnterMoneyPage3();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) &&
                    (event.getEvent().getText() != null
                            && event.getEvent().getText().size() > 0
                            && event.getEvent().getText().get(0).toString().contains("Proceed to Add Money"))) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createPaytmEnterMoneyPage4();

                    }
                }, 2000);

            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) &&
                    (event.getEvent().getText() != null
                            && event.getEvent().getText().size() > 0
                            && event.getEvent().getText().get(0).toString().contains("Debit Card"))) {
                createPaytmEnterMoneyPageAddDebitCard();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED)) {
                removeAllViews();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) &&
                    (event.getEvent().getSource() != null && event.getEvent().getSource().getViewIdResourceName() != null
                            && event.getEvent().getSource().getViewIdResourceName().equalsIgnoreCase("net.one97.paytm:id/etCardNumber"))) {
                Log.e("card number", event.getEvent().getText().get(0).toString());
            }
        } else {
            if (event.getEvent().getPackageName().equals(packegeLauncherName)) {
                iconView.setVisibility(View.GONE);
                workflowRequestedbyUser = false;
            }

        }


        // When some other app is launched from launcher then this code will work
        Log.e("Package Name:", packegeLauncherName);

        if (event.getEvent().getPackageName().equals(packegeLauncherName)) {
            if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Maps"))) {
                currentAppLaunchedFromLauncher = Constants.GOOGLEMAPS;
                createMapLandedView();
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("WhatsApp"))) {
                currentAppLaunchedFromLauncher = Constants.WHATSAPP;
                createAppLandedView(Constants.WHATSAPP);
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("YouTube"))) {
                currentAppLaunchedFromLauncher = Constants.YOUTUBE;
                createAppLandedView(Constants.YOUTUBE);
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Ola"))) {
                currentAppLaunchedFromLauncher = Constants.OLA;
                createAppLandedView(Constants.OLA);
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Uber"))) {
                currentAppLaunchedFromLauncher = Constants.UBER;
                createAppLandedView(Constants.UBER);
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Swiggy"))) {
                currentAppLaunchedFromLauncher = Constants.SWIGGY;
                createAppLandedView(Constants.SWIGGY);
            } else if ((event.getEvent().getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)
                    && (event.getEvent().getText().size() > 0 && event.getEvent().getText().get(0).toString().equalsIgnoreCase("Zomato"))) {
                currentAppLaunchedFromLauncher = Constants.ZOMATO;
                createAppLandedView(Constants.ZOMATO);
            }
        }

    }


    private void setIconVisibility(int visibility) {
        mFloatingViewManager.moveTo(ChatHeadService.this);
        iconView.setVisibility(visibility);

    }

    String getMainLauncherPackageName() {
        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
    }


    private void showMultipleResponseView() {
        ttobj.speak("गुड जॉब। आपकी लोकेशन अवेलेबल है। किलिक इस्टार्ट टू नेविगेट।", TextToSpeech.QUEUE_FLUSH, null);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotifyEvents event) {
        Log.e("event in eventbus", event.getEventName());
        if (event.getEventName().equalsIgnoreCase(Constants.STARTWORKFLOW)) {
            currentAppLaunchedFromLauncher = Constants.PAYTM;
            //startWorkflow(new WorkflowSuggestionModel(""));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    appLaunchedFromLauncherFirstScreen();
                    startedAppFormLauncher = false;
                }
            }, 2000);
        } else if (event.getEventName().equalsIgnoreCase(Constants.REMOVEGURUMESSAGE)) {
            removeAllViews();
        } else if (event.getEventName().equalsIgnoreCase(Constants.VOICEINPUTCOMPLETE)) {
            Log.e("extra data->", event.getExtraData());
        }
    }

    public AccessibilityNodeInfo findChildNodes(AccessibilityNodeInfo nodeInfo) {

        //I highly recommend leaving this line in! You never know when the screen content will
        //invalidate a node you're about to work on, or when a parents child will suddenly be gone!
        //Not doing this safety check is very dangerous!
        if (nodeInfo == null) return null;
        //Log.e("node name", nodeInfo.toString());
        if (nodeInfo.getViewIdResourceName() != null) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            Log.e("bounds data", nodeInfo.getViewIdResourceName() + " " + rect.left + " " + rect.top);
            if (nodeInfo.getText() != null) {
                Log.e("text data", nodeInfo.getText().toString() + " " + nodeInfo.getViewIdResourceName());

                allTextsOnScreen.add(nodeInfo.getText().toString());

            }

            if (nodeInfo.getPackageName().toString().equalsIgnoreCase("com.ubercab.driver")) {
                ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName(), rect);
                if (nodeInfo.getText() != null) {
                    ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName() + "_" + nodeInfo.getText(), rect);
                }
            } else if (nodeInfo.getPackageName().toString().equalsIgnoreCase("com.google.android.apps.maps")) {
                ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName(), rect);
                if (nodeInfo.getText() != null) {
                    ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName() + "_" + nodeInfo.getText(), rect);
                }
            } else if (nodeInfo.getPackageName().toString().equalsIgnoreCase("com.google.android.youtube")) {
                ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName(), rect);
                if (nodeInfo.getText() != null) {
                    ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName() + "_" + nodeInfo.getText(), rect);
                }
            } else if (nodeInfo.getPackageName().toString().equalsIgnoreCase("com.android.vending")) {
                ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName(), rect);
                if (nodeInfo.getText() != null) {
                    ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName() + "_" + nodeInfo.getText(), rect);
                }
            } else if (nodeInfo.getPackageName().toString().equalsIgnoreCase("net.one97.paytm")) {
                ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName(), rect);
                if (nodeInfo.getText() != null) {
                    ViewMappingDB.vieWHashMap.put(nodeInfo.getViewIdResourceName() + "_" + nodeInfo.getText(), rect);
                }
            }
        }

        //Notice that we're searching for the TextView's simple name!
        //This allows us to find AppCompat versions of TextView as well
        //as 3rd party devs well names subclasses... though with perhaps
        //a few poorly named unintended stragglers!


        //Do other work!

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo result = findChildNodes(nodeInfo.getChild(i));

            if (result != null) return result;
        }

        return null;
    }

    void createWhatsappPage2() {
        frameLayout.removeAllViews();

        x_whatsapp_screen_1 = 700;
        y_whatsapp_screen_1 = 70;

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        frameLayout.addView(v);

    }

    void createWhatsappPage3() {
        frameLayout.removeAllViews();

        x_whatsapp_screen_1 = 1000;
        y_whatsapp_screen_1 = 70;

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        frameLayout.addView(v);

    }

    void createWhatsappPage4() {
        frameLayout.removeAllViews();

        x_whatsapp_screen_1 = 400;
        y_whatsapp_screen_1 = 70;

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        frameLayout.addView(v);

    }

    void createWhatsappPage5() {
        frameLayout.removeAllViews();

        x_whatsapp_screen_1 = 950;
        y_whatsapp_screen_1 = 1300;

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        frameLayout.addView(v);

    }

    void createUberProfilePageView() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

        x_whatsapp_screen_1 = getScreenWidth() / 6;
        y_whatsapp_screen_1 = getScreenHeight() / 7;
        radiusOfCircle = getScreenHeight() / 15;


        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        frameLayout.addView(v);
        showGif();
        frameLayout.addView(gifImageView);

    }

    void createUberEarningLandingPageView() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

        x_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.ubercab.driver:id/title_Promotions").left + 50;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.ubercab.driver:id/title_Promotions").top;

        radiusOfCircle = 100;
        currentShape = "square";
        rect = ViewMappingDB.vieWHashMap.get("com.ubercab.driver:id/title_Promotions");
        rect.top = rect.top - 100;

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        frameLayout.addView(v);
        resetCurrentShape();
        showGif();

    }

    void createUberPromotionLandingPageView() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        radiusOfCircle = 0;

        x_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.ubercab.driver:id/title_Promotions").left + 50;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.ubercab.driver:id/title_Promotions").top;

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;


        gifImageView = new GifImageView(ChatHeadService.this);
        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                330,
                330));
        gifImageView.setImageResource(R.drawable.tenor);
        gifImageView.setX(getScreenWidth() / 2 - 180);
        gifImageView.setY(getScreenHeight() / 2 - 80);
        frameLayout.addView(gifImageView);

        //frameLayout.addView(v);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                frameLayout.removeView(gifImageView);
            }
        }, 3000);
    }

    void createUberQuestSwipePageRTL() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

        x_whatsapp_screen_1 = getScreenWidth() / 2 - 150;
        y_whatsapp_screen_1 = getScreenHeight() / 2 - 150;

        radiusOfCircle = 0;


        gifImageView = new GifImageView(ChatHeadService.this);
        gifImageView.setLayoutParams(new LinearLayout.LayoutParams(
                350,
                350));
        gifImageView.setImageResource(R.drawable.guru_2);
        gifImageView.setX(x_whatsapp_screen_1);
        gifImageView.setY(y_whatsapp_screen_1);
        frameLayout.addView(gifImageView);

    }


    void createUberQuestSwipePage2RTL() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

    }

    // <--------------------------------------------------------Google maps workflows------------------------------------------------------------------------>
    void createMapMicPage() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.google.android.apps.maps:id/search_omnibox_one_google_account_disc").left - getScreenWidth() / 24;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.google.android.apps.maps:id/search_omnibox_one_google_account_disc").top - getScreenWidth() / 24;
        radiusOfCircle = getScreenWidth() / 12;
        ttobj.speak("माइक बटन प्रेस करें और जहां जाना है उस जगह का नाम बोलें", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Mic button press karein aur jaha jaana hai us jagah ka naam bole");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }


    // user is speaking, do not show any view
    void createMapSecondPageViewForNavigation() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

    }


    // view to press start button
    void createMapThirdPageViewForNavigation() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        else {
            createInitialFrameLayout();
            createMapThirdPageViewForNavigation();
        }
        ttobj.speak("स्टार्ट दबाकर डायरेक्शन देखें।", TextToSpeech.QUEUE_FLUSH, null);
        workFlowStarted = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect rect = ViewMappingDB.vieWHashMap.get("com.google.android.apps.maps:id/commute_tab_strip_button");
                x_whatsapp_screen_1 = (int) (200 * getDensity());//ViewMappingDB.googleMapsMap.get("com.google.android.apps.maps:id/commute_tab_strip_button").left;//getScreenWidth() / 2;
                y_whatsapp_screen_1 = ((rect == null) || (rect.top > getScreenHeight())) ? (getScreenHeight() - getScreenHeight() / 20) :
                        ViewMappingDB.vieWHashMap.get("com.google.android.apps.maps:id/commute_tab_strip_button").top - getScreenHeight() / 20;
                radiusOfCircle = getScreenHeight() / 18;
                TextView textView = getTextView("Start button dabakar directions dekhe");

                CustomView v = new CustomView(ChatHeadService.this);
                currentView = v;
                if (frameLayout != null)
                    addViewsToFrameLayout(v, textView);
            }
        }, 2000);


    }


    void createMapPageAfterStartClicked() {


        if (frameLayout != null)
            frameLayout.removeAllViews();
        ttobj.speak("नेविगेट करने के लिए गूगल की डाइरेक्शंस फॉलो करें।", TextToSpeech.QUEUE_FLUSH, null);


    }


    private void createGoogleMapsPAgeForStartNavigation() {
        createMapThirdPageViewForNavigation();
    }

    private void createInitialFrameLayout() {

        if (mWindowManager != null)
            ;//mWindowManager.removeView(mainView);
        else mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mView = new MyLoadView(ChatHeadService.this);
        workFlowStarted = true;


        final LayoutInflater inflater = LayoutInflater.from(this);
        View containerView = inflater.inflate(R.layout.workflow_suggestion_layout, null, false);
        CustomView v = new CustomView(ChatHeadService.this);
        frameLayout = containerView.findViewById(R.id.mainContainer);
        frameLayout.addView(v);


        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 10, 10,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.CENTER;
        currentView = v;
        mWindowManager.addView(containerView, mParams);
        currentShape = "circle";

    }

    // shows a message popout from guru when app is launched from app icon
    private void createMapLandedView() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

        startedAppFormLauncher = true;
        currentAppLaunchedFromLauncher = "";
        Log.e("map", "google map launched");
        createWindow();

    }

    // <--------------------------------------------------------Google maps workflows------------------------------------------------------------------------>


    private void removeViewsFromFrameLayout() {
        if (frameLayout != null) {
            frameLayout.removeView(currentView);
        }
    }

    void addViewsToFrameLayout(View... v) {
        for (int i = 0; i < v.length; i++) {
            frameLayout.addView(v[i]);

        }
    }

    TextView getTextView(String text) {
        TextView textView = new TextView(ChatHeadService.this);
        textView.setText(text);
        textView.setTextSize(22);

        textView.setTextColor(getResources().getColor(R.color.white));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 0, 20, 0);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    TextView getTextView(String text, int position) {
        TextView textView = new TextView(ChatHeadService.this);
        textView.setText(text);
        textView.setTextSize(22);
//        textView.setX(calculatexForTextView());
//        textView.setY(calculateYForTextView());
        textView.setTextColor(getResources().getColor(R.color.white));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (position == 1) {
            layoutParams.gravity = Gravity.BOTTOM;
        } else {
            layoutParams.gravity = Gravity.CENTER;
        }

        textView.setLayoutParams(layoutParams);
        if (position == 1) {
            textView.setPadding(20, 0, 20, 20);
        } else {
            textView.setPadding(20, 0, 20, 0);
        }
        textView.setGravity(Gravity.CENTER);
        return textView;
    }


    int calculatexForTextView() {
        int x = 0;
        if (x_whatsapp_screen_1 > getScreenWidth() / 2) {
            x = x_whatsapp_screen_1 - (int) (250 * getDensity());
        } else {
            x = x_whatsapp_screen_1;
        }
        return x;
    }

    int calculateYForTextView() {
        int y = 0;
        if (y_whatsapp_screen_1 > getScreenHeight() / 2) {
            y = y_whatsapp_screen_1 - (int) (250 * getDensity());
        } else {
            y = y_whatsapp_screen_1 + 100;
        }
        return y;
    }

    void createPermissionAllowView() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

        x_whatsapp_screen_1 = getScreenWidth() / 2;
        y_whatsapp_screen_1 = getScreenHeight();
        radiusOfCircle = getScreenHeight() / 17;


        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;
        if (frameLayout != null)
            frameLayout.addView(v);
        //showGif(getScreenWidth() / 2, getScreenHeight() - getScreenHeight() / 20);


    }

    void removeAllViews() {
        if (frameLayout != null)
            frameLayout.removeAllViews();

    }

    private void createAppLandedView(String appname) {
        if (frameLayout != null)
            frameLayout.removeAllViews();

        startedAppFormLauncher = true;
        Log.e("map", "app launched");
        createWindow();

    }

    //<<-----------------------------------------------------------YOUTUBE------------------------------------------------------------------->>
    private void createYoutubeFirstPage() {

        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.google.android.youtube:id/mobile_topbar_avatar").left - getScreenWidth() / 24;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.google.android.youtube:id/mobile_topbar_avatar").top - getScreenWidth() / 24;
        radiusOfCircle = getScreenWidth() / 12;
        ttobj.speak("Videos or Songs dhoondne k liye Search button press karein", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Videos or Songs dhoondne k liye Search button press karein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);

    }


    private void createYoutubeSearchPressed() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = getScreenWidth() - getScreenWidth() / 24;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.google.android.youtube:id/voice_search").top - getScreenWidth() / 24;
        radiusOfCircle = getScreenWidth() / 12;

        ttobj.speak("Ab mic button dabakar jo bhi aap dhundana chahte hai vo bole", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Ab mic button dabakar jo bhi aap dhundana chahte hai vo bole");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }

    //<--------------------------------------------------- Playstore------------------------------------------------------>>


    private void createPlaystoreAppInstallPage() {

        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.android.vending:id/right_button").left - getScreenWidth() / 24;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.android.vending:id/right_button").top - getScreenWidth() / 24;
        currentShape = "square";
        rect = ViewMappingDB.vieWHashMap.get("com.android.vending:id/right_button");
        rect.top = rect.top - 100;


        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("App install krne k liye Install button dabayein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
        resetCurrentShape();
    }

    private void createPlaystoreAppInstallCompletePage() {

        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.android.vending:id/right_button").left - getScreenWidth() / 24;
        y_whatsapp_screen_1 = ViewMappingDB.vieWHashMap.get("com.android.vending:id/right_button").top - getScreenWidth() / 24;
        currentShape = "square";
        rect = ViewMappingDB.vieWHashMap.get("com.android.vending:id/right_button");
        rect.top = rect.top - 100;


        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("App open krne k liye Open button dabayein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
        resetCurrentShape();
    }


    private void InstallPressedPlaystoreAppInstallPage() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
    }

    private void resetCurrentShape() {
        currentShape = "circle";

    }

    //<<<<<------------------------------------------BHIM UPI------------------------------------------->>>>>>>>

    private void createUPIPermissionScreen() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        x_whatsapp_screen_1 = getScreenWidth() - getScreenWidth() / 4;
        y_whatsapp_screen_1 = getScreenHeight() - getScreenWidth() / 16;
        radiusOfCircle = getScreenWidth() / 8;

        speakText("Permissions dene k liye button press karein");

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Permissions dene k liye button press karein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }

    private void speakText(String s) {
        if (ttobj != null)
            ttobj.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

    //<<<-------------------------------------------------PAYTM------------------------------------------------------->>>>
    private void createPaytmAddMoneyPage() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = getScreenWidth() - getScreenWidth() / 5;
        y_whatsapp_screen_1 = getScreenHeight() / 6 - getScreenWidth() / 24;
        radiusOfCircle = getScreenWidth() / 10;
        ttobj.speak("Add button ko touch karein", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Add button ko touch karein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }

    private void createPaytmAddMoneyPage2() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        workFlowStarted = true;
        x_whatsapp_screen_1 = getScreenWidth() / 2 - getScreenWidth() / 10;
        y_whatsapp_screen_1 = getScreenHeight() / 4;
        radiusOfCircle = getScreenWidth() / 8;
        ttobj.speak("Aage badhne k liye touch karein", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Add button ko touch karein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }

    private void createPaytmEnterMoneyPage2() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        //net.one97.paytm:id/amw_amount_input_et
//        Intent intent1 = new Intent(ChatHeadService.this, DialogActivity.class);
//        intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent1);
        x_whatsapp_screen_1 = getScreenWidth() / 3 - getScreenWidth() / 10;
        y_whatsapp_screen_1 = getScreenHeight() / 3;
        radiusOfCircle = getScreenWidth() / 8;
        ttobj.speak("Amount dalne k liye yahan touch karein", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Amount dalne k liye yahan touch karein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }


    private void createPaytmEnterMoneyPage3() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
    }

    private void createPaytmEnterMoneyPage4() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        int left = ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/txtDebitCreditCard") == null ? getScreenWidth() / 4 : ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/txtDebitCreditCard").left;
        int top = ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/txtDebitCreditCard") == null ? getScreenHeight() / 2 : ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/txtDebitCreditCard").top - getScreenHeight() / 12;

        x_whatsapp_screen_1 = left - getScreenWidth() / 24;
        y_whatsapp_screen_1 = top - getScreenWidth() / 20;

        radiusOfCircle = getScreenWidth() / 8;
        ttobj.speak("Agar aap ATM card se payment karna chahte hain to card add karne k liye 'Debit' ko touch karein", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Agar aap ATM card se payment karna chahte hain to card add karne k liye 'Debit' ko touch karein", 1);

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }

    private void createPaytmEnterMoneyPageAddDebitCard() {
        if (frameLayout != null)
            frameLayout.removeAllViews();
        //net.one97.paytm:id/tv_enter_card_number
        int left = ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/tv_enter_card_number") == null ? getScreenWidth() / 3 : ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/tv_enter_card_number").left + getScreenWidth() / 10;
        int top = ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/tv_enter_card_number") == null ? getScreenHeight() / 2 : ViewMappingDB.vieWHashMap.get("net.one97.paytm:id/tv_enter_card_number").top - getScreenHeight() / 12;

        x_whatsapp_screen_1 = left - getScreenWidth() / 24;
        y_whatsapp_screen_1 = top;

        radiusOfCircle = getScreenWidth() / 8;
        ttobj.speak("Card number dalne k liye yahan touch karein", TextToSpeech.QUEUE_FLUSH, null);

        CustomView v = new CustomView(ChatHeadService.this);
        currentView = v;

        TextView textView = getTextView("Card number dalne k liye yahan touch karein");

        if (frameLayout != null)
            addViewsToFrameLayout(v, textView);
    }

}