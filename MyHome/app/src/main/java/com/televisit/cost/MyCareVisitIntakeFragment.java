package com.televisit.cost;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.prokarma.myhome.utils.TealiumUtil;
import com.televisit.AwsManager;

import java.util.ArrayList;
import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareVisitIntakeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareVisitIntakeFragment extends BaseFragment {

    public static final String MY_CARE_INTAKE_TAG = "my_care_intake_tag";

    private LinearLayout intakeLayout;
    private EditText couponText;
    private ProgressBar progressBar;
    private TextView costInfo;
    private TextInputEditText reasonPhone;
    private TextInputEditText reasonForVisit;
    private TextInputLayout reasonLayout;
    private TextInputLayout phoneLayout;
    private RelativeLayout privacyPolicyLayout;
    private TextView privacyLink;
    private AppCompatCheckBox agreePrivacyPolicyCheck;
    private AppCompatCheckBox agreeLegalDependentCheck;
    private RelativeLayout agreeLegalDependentLayout;
    private boolean providerUnavailable = false;
    private boolean providerBusy = false;

    public static final int REQUEST_CHECK_SETTINGS = 200;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 100;

    private ArrayList<String> missingPermissions = new ArrayList<>();
    private ArrayList<String> missingPermissionsText = new ArrayList<>();
    private StringBuilder sbMissingPermissionsText = new StringBuilder();
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog = null;
    boolean dialogShown = false;

    public MyCareVisitIntakeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareVisitCostFragment.
     */
    public static MyCareVisitIntakeFragment newInstance() {
        return new MyCareVisitIntakeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.intake));

        View view = inflater.inflate(R.layout.fragment_my_care_intake, container, false);

        intakeLayout = (LinearLayout) view.findViewById(R.id.whole_layout);
        costInfo = (TextView) view.findViewById(R.id.costInfo);
        couponText = (EditText) view.findViewById(R.id.coupon_code_edit_text);
        progressBar = (ProgressBar) view.findViewById(R.id.cost_progress);
        reasonPhone = (TextInputEditText) view.findViewById(R.id.reasonPhone);
        reasonForVisit = (TextInputEditText) view.findViewById(R.id.reasonForVisit);
        reasonLayout = (TextInputLayout) view.findViewById(R.id.reason_layout);
        phoneLayout = (TextInputLayout) view.findViewById(R.id.phone_layout);

        privacyPolicyLayout = (RelativeLayout) view.findViewById(R.id.agree_privacy_policy_layout);

        privacyLink = (TextView) view.findViewById(R.id.agree_privacy_policy_text2);
        agreePrivacyPolicyCheck = (AppCompatCheckBox) view.findViewById(R.id.agree_privacy_policy_check);
        agreeLegalDependentCheck = (AppCompatCheckBox) view.findViewById(R.id.agree_legal_dependent_check);
        agreeLegalDependentLayout = (RelativeLayout) view.findViewById(R.id.agree_legal_dependent_layout);

        if (AwsManager.getInstance().isDependent()) {
            agreeLegalDependentLayout.setVisibility(View.VISIBLE);
        } else {
            agreeLegalDependentLayout.setVisibility(View.GONE);
        }

        reasonPhone.addTextChangedListener(new PhoneAndDOBFormatter(reasonPhone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));

        if (ProfileManager.getProfile() != null) {
            reasonPhone.setText(ProfileManager.getProfile().phoneNumber);
        }

        // 33162: Android Visit notes given by user is not showing up on WEB Providers intake screen
        // hardcoded the visit cost temporarirly as the cost should go into new screen on its own
        // visit object needs to be created after taking the reason_for_visit which is on NEXT click
        // if we donot want to hardcode cost, and rather fetch it from visit object, then visit needs to be created on load of this screen;
        // which is before taking reason for visit info from user
        if (AwsManager.getInstance().isDependent()) {
            costInfo.setText(getString(R.string.visit_cost_desc_dependent) + "0.00");
        } else {
            costInfo.setText(getString(R.string.visit_cost_desc) + "0.00");
        }

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        privacyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.openPdfUrl(getContext(), Constants.MY_CARE_PRIVACY_POLICY_URL);

                //try to avoid webview and use apps installed on the device for PDF viewing.
                /*if (getActivity() instanceof NavigationActivity) {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_PRIVACY_POLICY, null);
                }*/
            }
        });

        privacyPolicyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isAccessibilityEnabled(getContext())) {
                    privacyLink.performClick();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtil.hideSoftKeyboard(getActivity());
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_INTAKE;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.next_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.next:
                nextClick();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_INTAKE_SCREEN, null);
    }

    private void nextClick() {
        if (alertDialog != null) {
            alertDialog.cancel();
            dialogShown = false;
        }
        phoneLayout.setError(null);
        reasonLayout.setError(null);

        if (providerBusy) {
            CommonUtil.showToast(getContext(), getString(R.string.provider_busy));
            return;
        }
        if (providerUnavailable) {
            CommonUtil.showToast(getContext(), getString(R.string.provider_unavailable));
            return;
        }

        if (isAdded()) {

            if (AwsManager.getInstance().getVisitContext() != null && !CommonUtil.isEmptyString(reasonForVisit.getText().toString())) {
                AwsManager.getInstance().getVisitContext().setOtherTopic(reasonForVisit.getText().toString());
            }

            if (!CommonUtil.isValidMobile(reasonPhone.getText().toString().trim())) {
                phoneLayout.setError(getString(R.string.field_must_be_completed));

            } else if (!agreePrivacyPolicyCheck.isChecked()) {
                CommonUtil.showToast(getActivity(), getString(R.string.my_care_privacy_policy_accept));

            } else if (!agreeLegalDependentCheck.isChecked() && AwsManager.getInstance().isDependent()) {
                CommonUtil.showToast(getActivity(), getString(R.string.my_care_legal_dependent_accept));

            } else if (!hasPermissionsForVideoVisit()) {

            } else if (CommonUtil.isValidMobile(reasonPhone.getText().toString().trim())
                    && agreePrivacyPolicyCheck.isChecked()) {

                createVisit();
            }
        }
    }

    private void loadCostScreen() {
        if (AwsManager.getInstance().getVisit() != null) {
            ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_COST, null);
        }
    }

    private void loadWaitingRoom() {
        if (AwsManager.getInstance().getVisit() != null) {
            ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
        }
    }

    private boolean hasPermissionsForVideoVisit() {

        boolean shouldShowInfo = false;
        missingPermissions = new ArrayList<>();
        missingPermissionsText = new ArrayList<>();
        sbMissingPermissionsText = new StringBuilder();
        String permissionText = "";

        String[] requiredPermissions = AwsManager.getInstance().getAWSDK().getRequiredPermissions();

        if (requiredPermissions != null && requiredPermissions.length != 0) {

            for (String requiredPermission : requiredPermissions) {

                if (ContextCompat.checkSelfPermission(getContext(), requiredPermission) != PackageManager.PERMISSION_GRANTED) {

                    missingPermissions.add(requiredPermission);

                    permissionText = CommonUtil.getReadablePermissionString(requiredPermission);

                    if (!missingPermissionsText.contains(permissionText)) {
                        missingPermissionsText.add(permissionText);
                        sbMissingPermissionsText.append("\n" + permissionText);
                    }

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), requiredPermission)) {
                        shouldShowInfo = true;
                    }
                }
            }
        }

        if (missingPermissions.isEmpty()) {
            return true;
        }

        String[] perms = missingPermissions.toArray(new String[missingPermissions.size()]);
        ActivityCompat.requestPermissions(getActivity(), perms, REQUEST_PERMISSIONS_REQUEST_CODE);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {

            boolean allGranted = true;
            if (grantResults == null || grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you receive empty arrays.
                allGranted = false;
            }

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                }
            }

            if (allGranted) {

                AppPreferences.getInstance().setBooleanPreference(Constants.AMWELL_SDK_ALL_PERMISSIONS_GRANTED, true);

                if (alertDialog != null) {
                    alertDialog.cancel();
                }

                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    if (dialogShown) {
                        nextClick();
                        //((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
                    }
                } else {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
                }

                dialogShown = false;

            } else {
                showPermissionSettingsDialog();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    nextClick();
                }
                break;
        }
    }

    private void showPermissionSettingsDialog() {

        alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle(getString(R.string.permissions_title));
        alertDialogBuilder.setMessage(getString(R.string.permissions_following) + "\n" + sbMissingPermissionsText);

        alertDialogBuilder.setPositiveButton(getString(R.string.permissions_settings_open), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivityForResult(intent, REQUEST_CHECK_SETTINGS);
            }
        });
        alertDialogBuilder.setNeutralButton(getString(R.string.permissions_decline), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                goBackToDashboard();
            }
        });

        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.show();
        dialogShown = true;
    }

    private void goBackToDashboard() {

        if (alertDialog != null) {
            alertDialog.cancel();
            dialogShown = false;
        }

        //fix 29231: When permissions to Camera/Mic are denied user must return to MCN start screen

        getActivity().getSupportFragmentManager().popBackStack();  //Intake accept privacy policy fragment
        getActivity().getSupportFragmentManager().popBackStack();  //Choose_Doctor fragment

        CommonUtil.showToast(getContext(), getResources().getString(R.string.permissions_insufficient));
    }

    private void createVisit() {

        providerBusy = false;
        providerUnavailable = false;

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
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
                            AwsManager.getInstance().setVisitCostCouponApplied(null);

                            progressBar.setVisibility(View.GONE);

                            // DONOT remove this code, it is required for 1.7; cost screen is not required for 1.6, hence always skipping it and laoding waiting_room
                            /*updateVisitCost();

                            if (AwsManager.getInstance().getVisit() != null
                                    && AwsManager.getInstance().getVisit().getVisitCost() != null
                                    && AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost() > 0) {
                                //CommonUtil.showToast(getContext(), getString(R.string.your_cost_is_not_free));

                                loadCostScreen();
                            } else {
                                loadWaitingRoom();
                            }*/

                            loadWaitingRoom();

                        } else {
                            progressBar.setVisibility(View.GONE);

                            Timber.e("createOrUpdateVisit. Something failed during video visit! :/");
                            Timber.e("SDK Error: " + sdkError);

                            if (sdkError != null && sdkError.toString() != null
                                    && (sdkError.toString().toLowerCase().contains("provider unavailable")
                                    || sdkError.toString().toLowerCase().contains("consumer_already_in_visit"))) {

                                providerUnavailable = true;
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
                        intakeLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void updateVisitCost() {

        if (AwsManager.getInstance().getVisit() == null || AwsManager.getInstance().getVisit().getVisitCost() == null) {
            return;
        }

        if (AwsManager.getInstance().isDependent()) {
            costInfo.setText(getString(R.string.visit_cost_desc_dependent) +
                    CommonUtil.formatAmount(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
        } else {
            costInfo.setText(getString(R.string.visit_cost_desc) +
                    CommonUtil.formatAmount(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
        }
    }
}
