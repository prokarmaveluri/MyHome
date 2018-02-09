package com.prokarma.myhome.features.televisit.waitingroom;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitTransfer;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.VisitTransferCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.features.televisit.AwsNetworkManager;
import com.prokarma.myhome.features.televisit.interfaces.AwsStartVideoVisit;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.prokarma.myhome.views.CircularImageView;
import com.whinc.widget.ratingbar.RatingBar;

import java.util.Map;

import timber.log.Timber;

import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_FINISHED_EXTRAS;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_RESULT_CODE;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_APP_SERVER_DISCONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_PROVIDER_CONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_VIDEO_DISCONNECTED;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareWaitingRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareWaitingRoomFragment extends BaseFragment implements AwsStartVideoVisit {

    public static final String MY_CARE_WAITING_TAG = "my_care_waiting_tag";
    private NotificationManager notificationManager;
    public static final String EXTRA_AWSDK_STATE = "awsdkState";

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

        if (getArguments() != null && getArguments().containsKey("VISIT")) {

            Timber.d("wait. has arguments visit = YES ");

            if (getActivity().getIntent() != null
                    && getActivity().getIntent().getBundleExtra(VISIT_FINISHED_EXTRAS) != null) {

                handleVisitEnd(getActivity().getIntent().getBundleExtra(VISIT_FINISHED_EXTRAS));
            }
        } else {
            Timber.d("wait. has arguments visit = NO ");

            if (patient != null) {
                startVisit(patient.getAddress(), getVisitFinishedIntent());
            }
        }

        return view;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_WAITING_ROOM;
    }

    private boolean handleVisitEnd(Bundle visitExtras) {

        if (visitExtras == null) {
            return false;
        }

        setAwsdkState(getActivity().getIntent().getBundleExtra(EXTRA_AWSDK_STATE));

        if (visitExtras.getParcelable(VISIT) != null) {
            AwsManager.getInstance().setVisit((Visit) visitExtras.getParcelable(VISIT));
        }

        Timber.d("wait. handleVisitEnd"
                + ". endReason = " + AwsManager.getInstance().getVisit().getEndReason()
                + ". hasVisitTransfer = " + AwsManager.getInstance().getVisit().hasVisitTransfer()
                + ". DeclineAndTransfer = " + AwsManager.getInstance().getVisit().getDeclineAndTransfer()
                + ". SuggestedTransfer = " + AwsManager.getInstance().getVisit().getSuggestedTransfer()
        );

        //Post Visit Transfer are in 1.7. hence commenting out until then.
        /*if (AwsManager.getInstance().getVisit().hasVisitTransfer() && isValidForPostVisitTransfer(visitExtras.getInt(VISIT_RESULT_CODE))) {

            currentTransferType = VisitTransferType.POST_VISIT_TRANSFER;
            startTransfer(currentTransferType);

            return true;
        }*/

        Timber.d("wait. onCreate. VISIT_RESULT_CODE = " + visitExtras.getInt(VISIT_RESULT_CODE));
        Timber.d("wait. onCreate. VISIT_STATUS_APP_SERVER_DISCONNECTED = " + visitExtras.getBoolean(VISIT_STATUS_APP_SERVER_DISCONNECTED));
        Timber.d("wait. onCreate. VISIT_STATUS_VIDEO_DISCONNECTED = " + visitExtras.getBoolean(VISIT_STATUS_VIDEO_DISCONNECTED));
        Timber.d("wait. onCreate. VISIT_STATUS_PROVIDER_CONNECTED = " + visitExtras.getBoolean(VISIT_STATUS_PROVIDER_CONNECTED));

        String toastMessage = "";
        if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_READY_FOR_SUMMARY) {
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_PROVIDER_GONE) {
            toastMessage = getString(R.string.waiting_room_provider_gone);
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_CONSUMER_CANCEL) {
            //CommonUtil.showToast(getContext(), getString(R.string.waiting_room_canceled));
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_FAILED) {
            //toastMessage = getString(R.string.waiting_room_failed);
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_TIMED_OUT) {
            toastMessage = getString(R.string.waiting_room_timed_out);
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_NETWORK_FAILURE) {
            toastMessage = getString(R.string.waiting_room_network_failure);
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_FAILED_TO_END) {
            //toastMessage = getString(R.string.waiting_room_failed_to_end);
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_PERMISSIONS_NOT_GRANTED) {
            toastMessage = getString(R.string.waiting_room_permissions_not_granted);
            isVisitEnd = true;

        } else if (visitExtras.getInt(VISIT_RESULT_CODE) == VideoVisitConstants.VISIT_RESULT_DECLINED) {
            toastMessage = getString(R.string.waiting_room_declined);
            isVisitEnd = true;
        }

        //Error handling ticket is in 1.7. will show the error message as per design
        if (!CommonUtil.isEmptyString(toastMessage)) {
            CommonUtil.showToast(getContext(), toastMessage);
        }

        if (isVisitEnd) {

            //Put in handler to avoid IllegalStateException: https://stackoverflow.com/a/41953519/2128921
            TealiumUtil.trackEvent(Constants.VIDEO_VISIT_END_EVENT, null);

            AwsNetworkManager.getInstance().removeVideoVisitNotification(getContext());
            goToVisitSummary();

            return true;
        }

        return false;
    }

    void setAwsdkState(final Bundle bundle) {
        if (!AwsManager.getInstance().getAWSDK().isInitialized()) {
            // in the case where the app has been destroyed and recreated during the visit console, we will have lost
            // our saved state, this will help restore it
            AwsManager.getInstance().getAWSDK().restoreInstanceState(bundle);
        }
    }

    private boolean isValidForPostVisitTransfer(int resultCode) {
        return (resultCode == VideoVisitConstants.VISIT_RESULT_PROVIDER_GONE ||
                resultCode == VideoVisitConstants.VISIT_RESULT_READY_FOR_SUMMARY ||
                resultCode == VideoVisitConstants.VISIT_RESULT_TIMED_OUT ||
                resultCode == VideoVisitConstants.VISIT_RESULT_FAILED ||
                resultCode == VideoVisitConstants.VISIT_RESULT_FAILED_TO_END ||
                resultCode == VideoVisitConstants.VISIT_RESULT_NETWORK_FAILURE ||
                resultCode == VideoVisitConstants.VISIT_RESULT_IVR_REQUESTED);
    }

    private Intent getVisitFinishedIntent() {

        Intent intent = new Intent(getContext(), NavigationActivity.class);
        intent.putExtra("VISIT", "FINISHED");

        // if the app gets killed during the visit console, for example, if a permission is revoked, this will help
        // us recover.  we write the awsdk state into the intent so we can pull it out later if needed
        final Bundle bundle = new Bundle();
        AwsManager.getInstance().getAWSDK().saveInstanceState(bundle);
        intent.putExtra(EXTRA_AWSDK_STATE, bundle);

        return intent;
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

    private void startNewVisit(final Address location, final Intent visitFinishedIntent) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        Timber.d("wait. Starting new transferred visit....");
        AwsNetworkManager.getInstance().startVideoVisit(AwsManager.getInstance().getVisit(), location, visitFinishedIntent, this);

        CommonUtil.showToast(getContext(), getString(R.string.visit_transferred_waiting_for) + " " + AwsManager.getInstance().getVisit().getAssignedProvider().getFullName());

        progressBar.setVisibility(View.GONE);
    }

    private void startVisit(final Address location, final Intent visitFinishedIntent) {

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

        AwsNetworkManager.getInstance().abandonVideoVisit();
        AwsNetworkManager.getInstance().removeVideoVisitNotification(getContext());
    }

    public void cancelVisit() {
        Timber.d("wait. cancelVisit. ");

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        AwsNetworkManager.getInstance().cancelVideoVisit(AwsManager.getInstance().getVisit(), null);
        TealiumUtil.trackEvent(Constants.VIDEO_VISIT_CANCELED_EVENT, null);

        abandonVisit("cancelVisit");
    }

    public void setVisitIntent(final Intent intent) {
        AwsNetworkManager.getInstance().createVisitNotification(getContext(), getActivity(), intent);
        startActivity(intent);
    }

    private void goToVisitSummary() {
        Handler uiHandler = new Handler();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.VIDEO_VISIT_SUMMARY, null);
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

    private void updateWaitingQueue(int i) {

        if (i > 0) {

            if (i == 1) {
                waitingCount.setText(i + " patient ahead");
            } else {
                waitingCount.setText(i + " patients ahead");
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

        currentTransferType = VisitTransferType.SUGGESTED_TRANSFER;
        startTransfer(currentTransferType);
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

            //as per SDK, isQuick() is true if the Provider is eligible for a "quick" transfer, meaning prior intake data can be re-used.
            /*if (visitTransfer.isQuick()) {
                transferType = VisitTransferType.QUICK_TRANSFER;
            }*/
            showVisitTransferDialog(transferType, visitTransfer);
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

        Provider providerObject = null; // = AwsManager.getInstance().getVisit().getAssignedProvider();
        if (visitTransfer != null && visitTransfer.getProvider() != null) {
            providerObject = visitTransfer.getProvider();
            Timber.d("wait. Transferred TO provider = " + providerObject.getFullName());
        }

        if (transferType == VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER) {

            detailedMessage.setVisibility(View.GONE);
            cancelTransferAndWait.setVisibility(View.GONE);
            transferMessage.setText(getString(R.string.you_have_been_transferred));

        } else if (transferType == VisitTransferType.QUICK_TRANSFER) {

            cancelTransferAndWait.setVisibility(View.VISIBLE);
            transferMessage.setText(getString(R.string.talk_to_someone_sooner));
            detailedMessage.setText((providerObject != null) ? providerObject.getFullName() + " " + getString(R.string.may_have_shorter_wait_time) : getString(R.string.may_have_shorter_wait_time));

        } else {
            //VisitTransferType.SUGGESTED_TRANSFER , VisitTransferType.POST_VISIT_TRANSFER

            cancelTransferAndWait.setVisibility(View.GONE);
            transferMessage.setText(getString(R.string.you_have_been_transferred));
            detailedMessage.setText((providerObject != null) ? providerObject.getFullName() + " " + getString(R.string.may_better_help_you) : getString(R.string.may_better_help_you));
        }

        transferMessage.setContentDescription(transferMessage.getText());

        if (providerObject != null) {

            if (providerObject.getWaitingRoomCount() != null && providerObject.getWaitingRoomCount() > 0) {
                waitingCount.setText(CommonUtil.getWaitingQueueText(providerObject.getWaitingRoomCount()));

            } else if (providerObject.getVisibility().equals(ProviderVisibility.WEB_AVAILABLE)) {
                waitingCount.setText(getString(R.string.you_are_next_patient));

            } else if (providerObject.getVisibility().equals(ProviderVisibility.WEB_BUSY)) {
                waitingCount.setText(getString(R.string.busy));
            }
            waitingCount.setContentDescription(waitingCount.getText());

            if (providerObject.getRating() >= 0) {
                ratingBar.setCount(providerObject.getRating());
            }

            AwsManager.getInstance().getAWSDK().getPracticeProvidersManager()
                    .newImageLoader(providerObject, doctorImage, ProviderImageSize.EXTRA_LARGE)
                    .placeholder(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .error(ContextCompat.getDrawable(getContext(), R.mipmap.img_provider_photo_placeholder))
                    .build()
                    .load();

            providerName.setText(providerObject.getFullName() + ", MD");
            providerName.setContentDescription(providerName.getText());

            if (providerObject.getSpecialty() != null) {
                providerSpeciality.setVisibility(View.VISIBLE);
                providerSpeciality.setText(providerObject.getSpecialty().getName());
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
                acceptTheTransfer(transferType);
            }
        });

        cancelVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transferAlertDialog != null) {
                    transferAlertDialog.dismiss();
                }
                transferAccepted = false;
                declineTheTransfer(transferType);
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

    private void declineTheTransfer(VisitTransferType transferType) {

        Timber.d("wait. declineTheTransfer. transferType = " + transferType.toString());

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
            abandonVisit("declineTheTransfer");
            goBackToDashboard();
        }
    }

    private void acceptTheTransfer(VisitTransferType transferType) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        Timber.d("wait. acceptTheTransfer. transferType = " + transferType);

        if (transferType == null) {

            if (AwsManager.getInstance().getVisit().getDeclineAndTransfer() != null) {
                transferType = VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER;

            } else if (AwsManager.getInstance().getVisit().getSuggestedTransfer() != null) {
                transferType = VisitTransferType.SUGGESTED_TRANSFER;
            }
            Timber.d("wait. acceptTheTransfer. type set = " + transferType);
        }

        progressBar.setVisibility(View.VISIBLE);

        if (transferType == VisitTransferType.PROVIDER_DECLINE_AND_TRANSFER) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptDeclineAndTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);

        } else if (transferType == VisitTransferType.SUGGESTED_TRANSFER) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptSuggestedTransfer(
                    AwsManager.getInstance().getVisit(),
                    visitTransferCallback);

        } else if (transferType == VisitTransferType.POST_VISIT_TRANSFER) {

            AwsManager.getInstance().getAWSDK().getVisitManager().acceptPostVisitTransfer(
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
