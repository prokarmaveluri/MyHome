package com.televisit.waitingroom;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitTransfer;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.VisitTransferCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.prokarma.myhome.views.CircularImageView;
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
    public static final int ONGOING_VISIT_NOTIFICATION_ID = 12345;

    private Consumer patient;
    private boolean isVisitEnd = false;
    private AlertDialog.Builder transferAlertDialogBuilder;
    private AlertDialog transferAlertDialog = null;
    private String transferType;
    private boolean transferAccepted = false;
    private TextView costInfo;
    private TextView waitingCount;
    private ProgressBar progressBar;

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
        return new MyCareWaitingRoomFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_care_waiting_room, container, false);

        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.waiting_room_title));

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        costInfo = (TextView) view.findViewById(R.id.cost_info);
        waitingCount = (TextView) view.findViewById(R.id.waiting_count);

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
            TealiumUtil.trackEvent(Constants.VIDEO_VISIT_END_EVENT, null);

            removeVisitNotification(getContext());

            goToVisitSummary();
        }
    }

    @Override
    public void onDestroyView() {
        try {
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

    private void goBackToDashboard() {
        //((NavigationActivity) getActivity()).getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_NOW, null);

        getActivity().getSupportFragmentManager().popBackStack();  //WaitingRoom fragment
        getActivity().getSupportFragmentManager().popBackStack();  //Intake accept privacy policy fragment
        getActivity().getSupportFragmentManager().popBackStack();  //Choose_Doctor fragment
    }

    private void createVisit() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getVisitManager().createOrUpdateVisit(
                AwsManager.getInstance().getVisitContext(),
                new SDKValidatedCallback<Visit, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(Visit visit, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setVisit(visit);

                            //29194 and 29111: Android: Remove the 'Free" coupon code before going to the Production Environment
                            //applyCoupon("Free");

                            progressBar.setVisibility(View.GONE);

                        } else {
                            Timber.e("createOrUpdateVisit. Something failed during video visit! :/");
                            Timber.e("SDK Error: " + sdkError);

                            progressBar.setVisibility(View.GONE);

                            if (sdkError != null && sdkError.toString() != null
                                    && (sdkError.toString().toLowerCase().contains("provider unavailable")
                                    || sdkError.toString().toLowerCase().contains("consumer_already_in_visit"))) {

                                CommonUtil.showToast(getContext(), getString(R.string.provider_unavailable));

                            } else if (sdkError.getMessage() != null && !sdkError.getMessage().isEmpty()) {
                                CommonUtil.showToast(getContext(), sdkError.getMessage());

                            } else if (sdkError.getSDKErrorReason() != null && !sdkError.getSDKErrorReason().isEmpty()) {
                                CommonUtil.showToast(getContext(), sdkError.getSDKErrorReason());

                            } else if (sdkError.toString() != null && !sdkError.toString().isEmpty()) {
                                CommonUtil.showToast(getContext(), sdkError.toString());
                            } else {
                                CommonUtil.showToast(getContext(), getContext().getString(R.string.something_went_wrong));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("createOrUpdateVisit. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void startNewVisit(final Address location,
                               @Nullable final Intent visitFinishedIntent) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        Timber.d("Starting new transferred visit....");
        AwsNetworkManager.getInstance().startVideoVisit(AwsManager.getInstance().getVisit(), location, visitFinishedIntent, this);

        CommonUtil.showToast(getContext(), getString(R.string.visit_transferred_waiting_for) + " " + AwsManager.getInstance().getVisit().getAssignedProvider().getFullName());

        progressBar.setVisibility(View.GONE);
    }

    private void applyCoupon(String couponCode) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (AwsManager.getInstance().getVisit() == null) {
            return;
        }

        try {
            progressBar.setVisibility(View.VISIBLE);

            AwsManager.getInstance().getAWSDK().getVisitManager().applyCouponCode(
                    AwsManager.getInstance().getVisit(),
                    couponCode,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null && isAdded()) {
                            } else {
                                Timber.e("applyCouponCode. Something failed! :/");
                                Timber.e("SDK Error: " + sdkError);
                            }

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("applyCouponCode. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
        } catch (IllegalArgumentException ex) {
            Timber.e(ex);
            progressBar.setVisibility(View.GONE);
        }
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
        TealiumUtil.trackEvent(Constants.VIDEO_VISIT_START_EVENT, null);

        progressBar.setVisibility(View.GONE);

        if (AwsManager.getInstance().getVisit() != null && AwsManager.getInstance().getVisit().getVisitCost() != null) {
            if (AwsManager.getInstance().isDependent()) {
                costInfo.setText(getString(R.string.visit_cost_desc_dependent) +
                        CommonUtil.formatAmount(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
            } else {
                costInfo.setText(getString(R.string.visit_cost_desc) +
                        CommonUtil.formatAmount(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
            }
            costInfo.setContentDescription(costInfo.getText());
            costInfo.setVisibility(View.GONE);
        }

        if (AwsManager.getInstance().getVisit() == null || AwsManager.getInstance().getVisit().getAssignedProvider() == null) {
            updateWaitingQueue(AwsManager.getInstance().getVisit().getAssignedProvider().getWaitingRoomCount());
        }
    }

    public void abandonVisit() {
        Timber.d("wait. abandonVisit. ");

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        //29260: app is  crashing when turn off location,microphone,and camera in settings>try to login after deactivating the permission to the app
        //after that error, SDK throws following: IllegalArgumentException: sdk initialization is missing

        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        // called by onDestroy()
        // this is to ensure we don't have any polling hanging out when it shouldn't be
        AwsManager.getInstance().getAWSDK().getVisitManager().abandonCurrentVisit();

        removeVisitNotification(getContext());
    }

    public void cancelVisit() {
        Timber.d("wait. cancelVisit. ");

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

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

            TealiumUtil.trackEvent(Constants.VIDEO_VISIT_CANCELED_EVENT, null);
        }

        abandonVisit();
    }

    public void setVisitIntent(final Intent intent) {
        createVisitNotification(getContext(), getActivity(), intent);

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
    }

    @Override
    public void onProviderEntered(@NonNull Intent intent) {
        if (intent != null) {
            Timber.d("wait. onProviderEntered. ");
            setVisitIntent(intent);
        }
    }

    @Override
    public void onStartVisitEnded(@NonNull String s) {
        Timber.d("wait. onStartVisitEnded. s = " + s);

        if (s != null && s.equalsIgnoreCase("PROVIDER_DECLINE_AND_TRANSFER")) {
            transferType = s;
            startTransfer(s);
        }

        if (s != null && s.equalsIgnoreCase("PROVIDER_DECLINE")) {
            CommonUtil.showToast(getContext(), getString(R.string.visit_declined_by_provider));
            goBackToDashboard();
        }
    }

    @Override
    public void onPatientsAheadOfYouCountChanged(int i) {
        final int count = i;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Timber.d("wait. onPatientsAheadOfYouCountChanged. i = " + count);
                updateWaitingQueue(count);
            }
        });
    }

    private void removeVisitNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ONGOING_VISIT_NOTIFICATION_ID);
    }

    private void createVisitNotification(Context context, Activity activity, Intent intent) {

        removeVisitNotification(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // set up ongoing notification
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        builder.setSmallIcon(R.drawable.ic_local_hospital_white_18dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.video_console_ongoing_notification,
                        AwsManager.getInstance().getVisit().getAssignedProvider().getFullName()))
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        notificationManager.notify(ONGOING_VISIT_NOTIFICATION_ID, builder.build());
    }

    private void updateWaitingQueue(int i) {

        if (i > 0) {

            if (i == 1) {
                waitingCount.setText("There is " + i + " patient ahead of you.");
            } else {
                waitingCount.setText("There are " + i + " patients ahead of you.");
            }

            waitingCount.setContentDescription(waitingCount.getText());
            waitingCount.invalidate();
            waitingCount.setVisibility(View.VISIBLE);
            return;
        }
        else if (i == 0) {
            waitingCount.setText(getString(R.string.you_are_next_patient));
        }

        /*if (AwsManager.getInstance().getVisit() == null || AwsManager.getInstance().getVisit().getAssignedProvider() == null) {
            return;
        }
        if (AwsManager.getInstance().getVisit().getAssignedProvider().getWaitingRoomCount() != null
                && AwsManager.getInstance().getVisit().getAssignedProvider().getWaitingRoomCount() > 0) {
            waitingCount.setText("There are " + AwsManager.getInstance().getVisit().getAssignedProvider().getWaitingRoomCount() + " patients ahead of you.");

        } else if (AwsManager.getInstance().getVisit().getAssignedProvider().getVisibility().equals(ProviderVisibility.WEB_AVAILABLE)) {
            waitingCount.setText(getString(R.string.you_are_next_patient));
        }
        else if (AwsManager.getInstance().getVisit().getAssignedProvider().getVisibility().equals(ProviderVisibility.WEB_BUSY)) {
            waitingCount.setText("Currently " + getString(R.string.busy));
        }*/

        waitingCount.setContentDescription(waitingCount.getText());
        waitingCount.invalidate();
        waitingCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuggestedTransfer() {
        Timber.d("wait. onSuggestedTransfer ");
        startTransfer("SUGGESTED_TRANSFER");
    }

    @Override
    public void onChat(@NonNull ChatReport chatReport) {
    }

    @Override
    public void onPollFailure(@NonNull Throwable throwable) {
    }

    @Override
    public void onResponse(Void aVoid, SDKError sdkError) {
    }

    @Override
    public void onFailure(Throwable throwable) {
    }

    private void startTransfer(final String transferType) {

        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        if (AwsManager.getInstance().getVisit() != null) {

            VisitTransfer priorityTransfer;
            if (AwsManager.getInstance().getVisit().getDeclineAndTransfer() != null) {
                priorityTransfer = AwsManager.getInstance().getVisit().getDeclineAndTransfer();
            } else {
                priorityTransfer = AwsManager.getInstance().getVisit().getSuggestedTransfer();
            }

            if (priorityTransfer != null) {
                showVisitTransferDialog(transferType, priorityTransfer);
            }
        }
    }

    private void showVisitTransferDialog(final String transferType, VisitTransfer visitTransfer) {

        transferAlertDialogBuilder = new AlertDialog.Builder(getContext());

        View dialogView = getLayoutInflater().inflate(R.layout.visit_transfer_dialog, null);
        transferAlertDialogBuilder.setView(dialogView);

        //as per SDK, true if the Provider is eligible for a "quick" transfer, meaning prior intake data can be re-used.
        if (visitTransfer.isQuick()) {
            transferAlertDialogBuilder.setTitle(getString(R.string.visit_quick_transfer));
        } else {
            transferAlertDialogBuilder.setTitle(getString(R.string.visit_transfer));
        }

        CircularImageView doctorImage = (CircularImageView) dialogView.findViewById(R.id.doc_image);
        TextView providerName = (TextView) dialogView.findViewById(R.id.provider_name);
        TextView providerSpeciality = (TextView) dialogView.findViewById(R.id.provider_speciality);
        TextView waitingCount = (TextView) dialogView.findViewById(R.id.waiting_count);

        TextView messageView = (TextView) dialogView.findViewById(R.id.message);
        Button cancelTransfer = (Button) dialogView.findViewById(R.id.cancel_transfer);
        Button continueTransfer = (Button) dialogView.findViewById(R.id.continue_transfer);

        messageView.setContentDescription(messageView.getText());

        if (visitTransfer.getProvider() != null) {

            if (visitTransfer.getProvider().getWaitingRoomCount() != null && visitTransfer.getProvider().getWaitingRoomCount() > 0) {
                waitingCount.setText(CommonUtil.getWaitingQueueText(visitTransfer.getProvider().getWaitingRoomCount()));

            } else if (visitTransfer.getProvider().getVisibility().equals(ProviderVisibility.WEB_AVAILABLE)) {
                waitingCount.setText(getString(R.string.you_are_next_patient));

            } else if (visitTransfer.getProvider().getVisibility().equals(ProviderVisibility.WEB_BUSY)) {
                waitingCount.setText("Currently " + getString(R.string.busy));
            }
            waitingCount.setContentDescription(waitingCount.getText());

            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(visitTransfer.getProvider(), doctorImage, ProviderImageSize.EXTRA_LARGE)
                    .placeholder(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .error(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .build()
                    .load();

            providerName.setText(visitTransfer.getProvider().getFullName() + ", MD");
            providerName.setContentDescription(providerName.getText());

            if (visitTransfer.getProvider().getSpecialty() != null) {
                providerSpeciality.setVisibility(View.VISIBLE);
                providerSpeciality.setText(visitTransfer.getProvider().getSpecialty().getName());
                providerSpeciality.setContentDescription(providerSpeciality.getText());
            } else {
                providerSpeciality.setVisibility(View.GONE);
            }
        }

        continueTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transferAlertDialog != null) {
                    transferAlertDialog.dismiss();
                }
                transferAccepted = true;
                transferVisit(transferType);
            }
        });

        cancelTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transferAlertDialog != null) {
                    transferAlertDialog.dismiss();
                }
                transferAccepted = false;
                declineTransferVisit(transferType);
            }
        });

        transferAlertDialogBuilder.setCancelable(false);
        transferAlertDialog = transferAlertDialogBuilder.show();
    }

    private void updateVisit() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getVisitManager().createOrUpdateVisit(
                AwsManager.getInstance().getVisitContext(),
                new SDKValidatedCallback<Visit, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(Visit visit, SDKError sdkError) {
                        if (sdkError == null) {
                            AwsManager.getInstance().setVisit(visit);

                            progressBar.setVisibility(View.GONE);

                        } else {
                            Timber.e("wait. createOrUpdateVisit. Something failed during video visit! :/");
                            Timber.e("wait. SDK Error: " + sdkError);

                            progressBar.setVisibility(View.GONE);

                            if (sdkError.getMessage() != null && !sdkError.getMessage().isEmpty()) {
                                CommonUtil.showToast(getContext(), sdkError.getMessage());
                            } else if (sdkError.getSDKErrorReason() != null && !sdkError.getSDKErrorReason().isEmpty()) {
                                CommonUtil.showToast(getContext(), sdkError.getSDKErrorReason());
                            } else if (sdkError.toString() != null && sdkError.toString().toLowerCase().contains("provider unavailable")) {
                                CommonUtil.showToast(getContext(), getString(R.string.provider_unavailable));
                            } else if (sdkError.toString() != null && !sdkError.toString().isEmpty()) {
                                CommonUtil.showToast(getContext(), sdkError.toString());
                            } else {
                                CommonUtil.showToast(getContext(), getContext().getString(R.string.something_went_wrong));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("wait. createOrUpdateVisit. Something failed! :/");
                        Timber.e("wait. Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void declineTransferVisit(String transferType) {

        Timber.d("wait. transferVisit. transferType = " + transferType
                + ". hasVisitTransfer = " + AwsManager.getInstance().getVisit().hasVisitTransfer());

        if (AwsManager.getInstance().getVisit().hasVisitTransfer()) {

            if (!ConnectionUtil.isConnected(getActivity())) {
                CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
                return;
            }

            boolean dontSuggestTransferAgain = false;
            AwsManager.getInstance().getAWSDK().getVisitManager().declineTransfer(
                    AwsManager.getInstance().getVisit(),
                    dontSuggestTransferAgain, visitTransferCallback);
        } else {
            abandonVisit();
            goBackToDashboard();
        }
    }

    private void transferVisit(String transferType) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        Timber.d("wait. transferVisit. transferType = " + transferType);

        if (transferType == null) {

            if (AwsManager.getInstance().getVisit().getDeclineAndTransfer() != null) {
                transferType = "PROVIDER_DECLINE_AND_TRANSFER";
            } else {
                transferType = "SUGGESTED_TRANSFER";
            }
            Timber.d("wait. transferVisit. type = " + transferType);
        }

        progressBar.setVisibility(View.VISIBLE);

        if (transferType.equalsIgnoreCase("PROVIDER_DECLINE_AND_TRANSFER")) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptDeclineAndTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);
        } else if (transferType.equalsIgnoreCase("POST_VISIT_TRANSFER")) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptPostVisitTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);
        } else if (transferType.equalsIgnoreCase("SUGGESTED_TRANSFER")) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptSuggestedTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);
        }
    }

    private VisitTransferCallback visitTransferCallback = new VisitTransferCallback() {

        @Override
        public void onNewVisitContext(VisitContext visitContext) {

            Timber.d("wait. onNewVisitContext");

            setLegalTextsAccepted(true, visitContext);
            setShareHealthSummary(visitContext);

            AwsManager.getInstance().setVisitContext(visitContext);

            createVisit();

            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onStartNewVisit(Visit visit) {

            Timber.d("wait. onStartNewVisit");

            AwsManager.getInstance().setVisit(visit);

            progressBar.setVisibility(View.GONE);

            startNewVisit(patient.getAddress(), null);
        }

        @Override
        public void onVisitTransfer(Visit visit) {

            Timber.d("wait. onVisitTransfer");
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onProviderUnavailable() {
            CommonUtil.showToast(getContext(), "Transferred Provider not available");
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onResponse(Void aVoid, SDKError sdkError) {
            if (sdkError == null) {
                Timber.d("Visit transferred successfully!!");
            } else {
                Timber.e("transferVisit. Something failed! :/");
                Timber.e("SDK Error: " + sdkError);
            }
            progressBar.setVisibility(View.GONE);

            Timber.d("wait. transferAccepted = " + transferAccepted);
            if (!transferAccepted) {
                goBackToDashboard();
            }
        }

        @Override
        public void onValidationFailure(@NonNull Map<String, String> map) {
            Timber.d("onValidationFailure");
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Throwable throwable) {
            Timber.e("transferVisit. Something failed! :/");
            Timber.e("Throwable = " + throwable);
            progressBar.setVisibility(View.GONE);
        }
    };

    private void setLegalTextsAccepted(boolean accepted, VisitContext visitContext) {
        for (LegalText legalText : visitContext.getLegalTexts()) {
            legalText.setAccepted(accepted);
        }
    }

    private void setShareHealthSummary(VisitContext visitContext) {
        visitContext.setShareHealthSummary(true);
    }
}
