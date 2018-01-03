package com.televisit.waitingroom;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsStartVideoVisit;
import java.util.Map;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareWaitingRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareWaitingRoomFragment extends BaseFragment implements AwsStartVideoVisit {

    public static final String MY_CARE_WAITING_TAG = "my_care_waiting_tag";
    private NotificationManager notificationManager;
    public static final int ONGOING_NOTIFICATION_ID = 12345;

    private Consumer patient;
    private boolean isVisitEnd = false;


    public MyCareWaitingRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareServicesFragment.
     */
    public static MyCareWaitingRoomFragment newInstance() {
        MyCareWaitingRoomFragment fragment = new MyCareWaitingRoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_care_waiting_room, container, false);

        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.waiting_room_title));
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        patient = AwsManager.getInstance().getPatient() != null ? AwsManager.getInstance().getPatient() : AwsManager.getInstance().getConsumer();
        isVisitEnd = false;

        if (patient != null) {
            startVisit(patient.getAddress(), null);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisitEnd) {
            CommonUtil.showToast(getActivity(), getActivity().getString(R.string.visit_completed));

            //Put in handler to avoid IllegalStateException: https://stackoverflow.com/a/41953519/2128921
            goToVisitSummary();
        }
    }

    @Override
    public void onDestroyView() {
        try {
            Timber.d("waitingroom. onDestroyView. abandonVisit ");
            abandonVisit();
        } catch (Exception ex) {
            Timber.e(ex);
        }
        super.onDestroyView();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_WAITING_ROOM;
    }

    private void startVisit(final Address location,
                            @Nullable final Intent visitFinishedIntent) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        abandonVisit();

        Timber.d("Starting visit....");
        AwsNetworkManager.getInstance().startVideoVisit(AwsManager.getInstance().getVisit(), location, visitFinishedIntent, this);
    }

    public void abandonVisit() {

        //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
        //after that error, SDK throws following: IllegalArgumentException: sdk initialization is missing

        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        // called by onDestroy()
        // this is to ensure we don't have any polling hanging out when it shouldn't be
        AwsManager.getInstance().getAWSDK().getVisitManager().abandonCurrentVisit();
    }

    public void cancelVisit() {

        //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
        //after that error, SDK throws following: IllegalArgumentException: sdk initialization is missing

        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        if (AwsManager.getInstance().getVisit() != null) {

            //if we donot cancel on backbutton, we are getting following error:
            //SDK Error: The consumer is already active in a visit, End the active visit and try again

            Timber.d("Cancelling visit...");

            AwsManager.getInstance().getAWSDK().getVisitManager().cancelVisit(
                    AwsManager.getInstance().getVisit(),
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null) {
                                Timber.d("Visit cancelled successfully!!");
                            } else {
                                Timber.e("cancelVisit. Something failed! :/");
                                Timber.e("SDK Error: " + sdkError);
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("cancelVisit. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                        }
                    });
        }

        abandonVisit();
    }

    public void setVisitIntent(final Intent intent) {
        // set up ongoing notification
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon(R.drawable.ic_local_hospital_white_18dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.video_console_ongoing_notification,
                        AwsManager.getInstance().getVisit().getAssignedProvider().getFullName()))
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        notificationManager.notify(ONGOING_NOTIFICATION_ID, builder.build());

        isVisitEnd = true;
        // start activity
        startActivity(intent);
    }

    private void goToVisitSummary() {
        Handler uiHandler = new Handler();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    ((NavigationActivity) getActivity()).loadFragment(
                            Constants.ActivityTag.VIDEO_VISIT_SUMMARY, null);
                }
            }
        });
    }

    @Override
    public void onValidationFailure(@NonNull Map<String, String> map) {
        Timber.d("waitingroom. onValidationFailure. " );
    }

    @Override
    public void onProviderEntered(@NonNull Intent intent) {
        if (intent != null) {
            Timber.d("waitingroom. onProviderEntered. " );
            setVisitIntent(intent);
        }
        else {
            Timber.d("waitingroom. onProviderEntered. intent is NULL " );
        }
    }

    @Override
    public void onStartVisitEnded(@NonNull String s) {
        Timber.d("waitingroom. onStartVisitEnded. s = " + s);
//                        if (isAdded()) {
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(SummaryFragment.VISIT_END_REASON_KEY, visitEndReason);
//                            ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.PREVIOUS_VISIT_SUMMARY, bundle);
//                        }
    }

    @Override
    public void onPatientsAheadOfYouCountChanged(int i) {
        Timber.d("waitingroom. onPatientsAheadOfYouCountChanged. i = " + i);
    }

    @Override
    public void onSuggestedTransfer() {
        Timber.d("waitingroom. onSuggestedTransfer " );
    }

    @Override
    public void onChat(@NonNull ChatReport chatReport) {
        Timber.d("waitingroom. onPollFailure " + chatReport.toString());
    }

    @Override
    public void onPollFailure(@NonNull Throwable throwable) {
        Timber.d("waitingroom. onPollFailure " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @Override
    public void onResponse(Void aVoid, SDKError sdkError) {
        Timber.d("waitingroom. onResponse " + sdkError);
    }

    @Override
    public void onFailure(Throwable throwable) {
        Timber.d("waitingroom. onFailure " + throwable.getMessage());
        throwable.printStackTrace();
    }
}
