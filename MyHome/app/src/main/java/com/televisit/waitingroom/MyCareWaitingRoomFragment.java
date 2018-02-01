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

import com.americanwell.sdk.activity.VideoVisitConstants;
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
import com.whinc.widget.ratingbar.RatingBar;

import java.util.Map;

import timber.log.Timber;

import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_FINISHED_EXTRAS;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_RESULT_CODE;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_APP_SERVER_DISCONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_VIDEO_DISCONNECTED;

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
    private VisitTransferType currentTransferType = VisitTransferType.NONE;
    private boolean transferAccepted = false;
    private TextView costInfo;
    private TextView waitingCount;
    private ProgressBar progressBar;

    public enum VisitTransferType {
        PROVIDER_DECLINE_AND_TRANSFER,
        SUGGESTED_TRANSFER,
        POST_VISIT_TRANSFER,
        QUICK_TRANSFER,
        NONE;

        public static VisitTransferType toVisitTransferType(String enumString) {
            try {
                return valueOf(enumString);
            } catch (Exception ex) {
                // For error cases
                return NONE;
            }
        }
    }

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


        boolean visitEnded = false;
        if (getActivity().getIntent() != null) {
            visitEnded = handleVisitEnd(getActivity().getIntent().getBundleExtra(VISIT_FINISHED_EXTRAS));
        }

        if ((!visitEnded) && patient != null) {
            startVisit(patient.getAddress(), getVisitFinishedIntent());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        try {
            abandonVisit("onDestroyView");
        } catch (Exception ex) {
            Timber.e(ex);
        }
        super.onDestroyView();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_WAITING_ROOM;
    }

    private boolean handleVisitEnd(Bundle visitExtras) {

        if (visitExtras == null) {
            Timber.d("wait. onCreate. visitExtras is NULL ");
            return false;
        }

        Timber.d("wait. onCreate. VISIT_RESULT_CODE = " + visitExtras.getInt(VISIT_RESULT_CODE));
        Timber.d("wait. onCreate. VISIT_STATUS_APP_SERVER_DISCONNECTED = " + visitExtras.getBoolean(VISIT_STATUS_APP_SERVER_DISCONNECTED));
        Timber.d("wait. onCreate. VISIT_STATUS_VIDEO_DISCONNECTED = " + visitExtras.getBoolean(VISIT_STATUS_VIDEO_DISCONNECTED));

        if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_READY_FOR_SUMMARY) {
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_PROVIDER_GONE) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_provider_gone));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_CONSUMER_CANCEL) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_canceled));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_FAILED) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_failed));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_TIMED_OUT) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_timed_out));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_NETWORK_FAILURE) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_network_failure));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_FAILED_TO_END) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_failed_to_end));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_PERMISSIONS_NOT_GRANTED) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_permissions_not_granted));
            isVisitEnd = true;
        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_DECLINED) {
            CommonUtil.showToast(getContext(), getString(R.string.waiting_room_declined));
            isVisitEnd = true;
        }

        if (isVisitEnd) {
            visitCompleted();
            return true;
        }

        return false;
    }

    private Intent getVisitFinishedIntent() {
        Intent intent = new Intent(getContext(), NavigationActivity.class);
        intent.putExtra("VISIT", "FINISHED");
        return intent;
    }

    private void goBackToDashboard() {
        //((NavigationActivity) getActivity()).getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_NOW, null);

        getActivity().getSupportFragmentManager().popBackStack();  //WaitingRoom fragment
        getActivity().getSupportFragmentManager().popBackStack();  //Intake accept privacy policy fragment
        getActivity().getSupportFragmentManager().popBackStack();  //Choose_Doctor fragment
    }

    private void visitCompleted() {
        CommonUtil.showToast(getActivity(), getActivity().getString(R.string.visit_completed));

        //Put in handler to avoid IllegalStateException: https://stackoverflow.com/a/41953519/2128921
        TealiumUtil.trackEvent(Constants.VIDEO_VISIT_END_EVENT, null);

        removeVisitNotification(getContext());

        goToVisitSummary();
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

    private void startNewVisit(final Address location, @Nullable final Intent visitFinishedIntent) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        Timber.d("wait. Starting new transferred visit....");
        AwsNetworkManager.getInstance().startVideoVisit(AwsManager.getInstance().getVisit(), location, visitFinishedIntent, this);

        CommonUtil.showToast(getContext(), getString(R.string.visit_transferred_waiting_for) + " " + AwsManager.getInstance().getVisit().getAssignedProvider().getFullName());

        progressBar.setVisibility(View.GONE);
    }

    private void startVisit(final Address location, @Nullable final Intent visitFinishedIntent) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        abandonVisit("startVisit");

        Timber.d("wait. Starting visit....");
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

    public void abandonVisit(String from) {
        Timber.d("wait. abandonVisit. from = " + from);

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
            Timber.d("wait. Abandon visit..."
                    + ". endReason = " + AwsManager.getInstance().getVisit().getEndReason()
                    + ". hasVisitTransfer = " + AwsManager.getInstance().getVisit().hasVisitTransfer()
                    + ". DeclineAndTransfer = " + AwsManager.getInstance().getVisit().getDeclineAndTransfer()
                    + ". SuggestedTransfer = " + AwsManager.getInstance().getVisit().getSuggestedTransfer()
            );
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

            Timber.d("wait. Cancelling visit..."
                    + ". endReason = " + AwsManager.getInstance().getVisit().getEndReason()
                    + ". hasVisitTransfer = " + AwsManager.getInstance().getVisit().hasVisitTransfer()
                    + ". DeclineAndTransfer = " + AwsManager.getInstance().getVisit().getDeclineAndTransfer()
                    + ". SuggestedTransfer = " + AwsManager.getInstance().getVisit().getSuggestedTransfer()
            );

            AwsManager.getInstance().getAWSDK().getVisitManager().cancelVisit(
                    AwsManager.getInstance().getVisit(),
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null) {
                                Timber.d("wait. Visit cancelled successfully!!");
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

        abandonVisit("cancelVisit");
    }

    public void setVisitIntent(final Intent intent) {

        Timber.d("wait. setVisitIntent. hasExtras = " + intent.getExtras());
        Timber.d("wait. setVisitIntent. hasExtras = " + intent.getExtras());
        Timber.d("wait. setVisitIntent. hasExtras = " + intent.getExtras());

        Timber.d("wait. setVisitIntent"
                + ". endReason = " + AwsManager.getInstance().getVisit().getEndReason()
                + ". hasVisitTransfer = " + AwsManager.getInstance().getVisit().hasVisitTransfer()
                + ". DeclineAndTransfer = " + AwsManager.getInstance().getVisit().getDeclineAndTransfer()
                + ". SuggestedTransfer = " + AwsManager.getInstance().getVisit().getSuggestedTransfer()
        );

        createVisitNotification(getContext(), getActivity(), intent);

        // start activity
        startActivity(intent);

        isVisitEnd = true;
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
            Timber.d("wait. onProviderEntered. currentTransferType = " + currentTransferType);
            setVisitIntent(intent);
        }
    }

    @Override
    public void onStartVisitEnded(@NonNull String s) {
        Timber.d("wait. onStartVisitEnded. s = " + s);

        if (s != null && s.equalsIgnoreCase("PROVIDER_DECLINE_AND_TRANSFER")) {

            currentTransferType = VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER;
            startTransfer(currentTransferType);

        } else if (s != null && s.equalsIgnoreCase("PROVIDER_DECLINE")) {

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
        } else if (i == 0) {
            waitingCount.setText(getString(R.string.you_are_next_patient));
        }

        waitingCount.setContentDescription(waitingCount.getText());
        waitingCount.invalidate();
        waitingCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuggestedTransfer() {
        Timber.d("wait. onSuggestedTransfer ");
        startTransfer(VisitTransferType.SUGGESTED_TRANSFER);
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

    private void startTransfer(VisitTransferType transferType) {

        if (AwsManager.getInstance().getAWSDK() == null || !AwsManager.getInstance().getAWSDK().isInitialized()) {
            return;
        }

        if (AwsManager.getInstance().getVisit() != null) {

            VisitTransfer visitTransfer;
            if (AwsManager.getInstance().getVisit().getDeclineAndTransfer() != null) {
                visitTransfer = AwsManager.getInstance().getVisit().getDeclineAndTransfer();
            } else {
                visitTransfer = AwsManager.getInstance().getVisit().getSuggestedTransfer();
            }

            if (visitTransfer != null) {
                //as per SDK, isQuick() is true if the Provider is eligible for a "quick" transfer, meaning prior intake data can be re-used.
                /*if (visitTransfer.isQuick()) {
                    transferType = VisitTransferType.QUICK_TRANSFER;
                }*/
                showVisitTransferDialog(transferType, visitTransfer);
            }
        }
    }

    private void showVisitTransferDialog(final VisitTransferType transferType, VisitTransfer visitTransfer) {

        transferAlertDialogBuilder = new AlertDialog.Builder(getContext());

        View dialogView = getLayoutInflater().inflate(R.layout.visit_transfer_dialog, null);
        transferAlertDialogBuilder.setView(dialogView);

        CircularImageView doctorImage = (CircularImageView) dialogView.findViewById(R.id.doc_image);
        TextView providerName = (TextView) dialogView.findViewById(R.id.provider_name);
        TextView providerSpeciality = (TextView) dialogView.findViewById(R.id.provider_speciality);
        TextView waitingCount = (TextView) dialogView.findViewById(R.id.waiting_count);
        RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.rate_provider);

        TextView transferMessage = (TextView) dialogView.findViewById(R.id.transfer_message);
        TextView detailedMessage = (TextView) dialogView.findViewById(R.id.detailed_message);

        TextView cancelVisit = (TextView) dialogView.findViewById(R.id.cancel_visit);
        TextView cancelTransferAndWait = (TextView) dialogView.findViewById(R.id.cancel_transfer_and_wait);
        Button continueTransfer = (Button) dialogView.findViewById(R.id.continue_transfer);

        //transferAlertDialogBuilder.setTitle(getString(R.string.visit_transfer));
        transferMessage.setText(getString(R.string.you_have_been_transferred));

        Timber.d("wait. TransferType = " + transferType.name());

        if (transferType == VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER) {

            detailedMessage.setVisibility(View.GONE);
            cancelTransferAndWait.setVisibility(View.GONE);
            transferMessage.setText(getString(R.string.you_have_been_transferred));

        } else if (transferType == VisitTransferType.QUICK_TRANSFER) {

            cancelTransferAndWait.setVisibility(View.VISIBLE);
            transferMessage.setText(getString(R.string.talk_to_someone_sooner));
            detailedMessage.setText((visitTransfer.getProvider() != null) ? visitTransfer.getProvider().getFullName() + " " + getString(R.string.may_better_help_you) : "");

        } else if (transferType == VisitTransferType.SUGGESTED_TRANSFER) {

            cancelTransferAndWait.setVisibility(View.GONE);
            transferMessage.setText(getString(R.string.you_have_been_transferred));
            detailedMessage.setText((visitTransfer.getProvider() != null) ? visitTransfer.getProvider().getFullName() + " " + getString(R.string.may_have_shorter_wait_time) : "");
        }

        transferMessage.setContentDescription(transferMessage.getText());

        if (visitTransfer.getProvider() != null) {

            if (visitTransfer.getProvider().getWaitingRoomCount() != null && visitTransfer.getProvider().getWaitingRoomCount() > 0) {
                waitingCount.setText(CommonUtil.getWaitingQueueText(visitTransfer.getProvider().getWaitingRoomCount()));

            } else if (visitTransfer.getProvider().getVisibility().equals(ProviderVisibility.WEB_AVAILABLE)) {
                waitingCount.setText(getString(R.string.you_are_next_patient));

            } else if (visitTransfer.getProvider().getVisibility().equals(ProviderVisibility.WEB_BUSY)) {
                waitingCount.setText("Currently " + getString(R.string.busy));
            }
            waitingCount.setContentDescription(waitingCount.getText());

            if (visitTransfer.getProvider().getTotalRatings() >= 0) {
                ratingBar.setCount(visitTransfer.getProvider().getTotalRatings());
            }

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

        cancelVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transferAlertDialog != null) {
                    transferAlertDialog.dismiss();
                }
                transferAccepted = false;
                declineTransferVisit(transferType);
            }
        });

        cancelTransferAndWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transferAlertDialog != null) {
                    transferAlertDialog.dismiss();
                }
                transferAccepted = false;
            }
        });

        transferAlertDialogBuilder.setCancelable(false);
        transferAlertDialog = transferAlertDialogBuilder.show();
    }

    private void declineTransferVisit(VisitTransferType transferType) {

        Timber.d("wait. declineTransferVisit. transferType = " + transferType.toString()
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
            abandonVisit("declineTransferVisit");
            goBackToDashboard();
        }
    }

    private void transferVisit(VisitTransferType transferType) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        Timber.d("wait. transferVisit. transferType = " + transferType);

        if (transferType == null) {

            if (AwsManager.getInstance().getVisit().getDeclineAndTransfer() != null) {
                transferType = VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER;
            } else {
                transferType = VisitTransferType.SUGGESTED_TRANSFER;
            }
            Timber.d("wait. transferVisit. type = " + transferType);
        }

        progressBar.setVisibility(View.VISIBLE);

        if (transferType == VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptDeclineAndTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);

        } else if (transferType == VisitTransferType.POST_VISIT_TRANSFER) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptPostVisitTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);

        } else if (transferType == VisitTransferType.SUGGESTED_TRANSFER) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptSuggestedTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);
        }
    }

    private VisitTransferCallback visitTransferCallback = new VisitTransferCallback() {

        //If the provider is eligible for a quick transfer, a new Visit will be returned in the VisitTransferCallback.
        //If the provider is NOT eligible for a quick transfer, a VisitContext will be returned in the VisitTransferCallback.

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

            currentTransferType = VisitTransferType.QUICK_TRANSFER;

            AwsManager.getInstance().setVisit(visit);

            progressBar.setVisibility(View.GONE);

            startNewVisit(patient.getAddress(), getVisitFinishedIntent());
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
                Timber.d("wait. Visit transferred successfully!!");
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
            Timber.d("wait. onValidationFailure");
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
