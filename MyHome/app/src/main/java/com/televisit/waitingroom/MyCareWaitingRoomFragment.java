package com.televisit.waitingroom;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.manager.StartVisitCallback;
import com.americanwell.sdk.manager.ValidationReason;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.Constants;
import com.televisit.AwsManager;
import com.televisit.summary.SummaryFragment;

import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareWaitingRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareWaitingRoomFragment extends BaseFragment {

    public static final String MY_CARE_WAITING_TAG = "my_care_waiting_tag";
    private NotificationManager notificationManager;
    public static final int ONGOING_NOTIFICATION_ID = 12345;
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

        isVisitEnd = false;
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.waiting_room_title));

        //TODO: visit summary and feedback
//        startVisit(AwsManager.getInstance().getConsumer().getAddress(),
//                SummaryActivity.getSummaryIntent(getActivity()));

        startVisit(AwsManager.getInstance().getConsumer().getAddress(), null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisitEnd) {
            try {
                if (getActivity() != null) {
                    ((NavigationActivity) getActivity()).onBackPressed();
                }
            } catch (IllegalStateException ex) {
                Timber.e(ex);
            }
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_WAITING_ROOM;
    }

    private void startVisit(final Address location,
                            @Nullable final Intent visitFinishedIntent) {

        abandonVisit();

        Timber.e("Starting visit....");

        AwsManager.getInstance().getAWSDK().getVisitManager().startVisit(
                AwsManager.getInstance().getVisit(),
                location,
                visitFinishedIntent,
                new StartVisitCallback() {
                    @Override
                    public void onProviderEntered(@NonNull Intent intent) {
                        Timber.d("onProviderEntered " + intent);

                        if (intent != null) {
                            setVisitIntent(intent);
                        }
                    }

                    @Override
                    public void onStartVisitEnded(@NonNull VisitEndReason visitEndReason) {
                        Timber.d("onStartVisitEnded " + visitEndReason);

                        Toast.makeText(getContext(), "visit ended\n" + visitEndReason, Toast.LENGTH_LONG).show();

                        if (isAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(SummaryFragment.VISIT_END_REASON_KEY, visitEndReason);
                            ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.VISIT_SUMMARY, bundle);
                        }
                    }

                    @Override
                    public void onPatientsAheadOfYouCountChanged(int i) {
                        Timber.d("onPatientsAheadOfYouCountChanged " + i);
                    }

                    @Override
                    public void onSuggestedTransfer() {
                        Timber.d("onSuggestedTransfer ");
                    }

                    @Override
                    public void onChat(@NonNull ChatReport chatReport) {
                        Timber.d("onChat " + chatReport);
                    }

                    @Override
                    public void onPollFailure(@NonNull Throwable throwable) {
                        Timber.w("onPollFailure " + throwable);
                    }

                    @Override
                    public void onValidationFailure(Map<String, ValidationReason> map) {
                        Timber.w("onValidationFailure " + map);
                    }

                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        Timber.d("onResponse " + aVoid + " " + sdkError);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.w("onFailure " + throwable);
                    }
                }
        );
    }

//    private void cancelVisit() {
//        AwsManager.getInstance().getAWSDK().getVisitManager().cancelVisit(
//                AwsManager.getInstance().getVisit(),
//                new SDKCallback<Void, SDKError>() {
//                    @Override
//                    public void onResponse(Void aVoid, SDKError sdkError) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//
//                    }
//                }
//        );
//    }

    public void abandonVisit() {
        // called by onDestroy()
        // this is to ensure we don't have any polling hanging out when it shouldn't be
        AwsManager.getInstance().getAWSDK().getVisitManager().abandonCurrentVisit();
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

}
