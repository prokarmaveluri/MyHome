package com.televisit.cost;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.televisit.AwsManager;

import java.text.DecimalFormat;
import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareVisitCostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareVisitCostFragment extends BaseFragment {

    public static final String MY_CARE_COST_TAG = "my_care_cost_tag";

    private LinearLayout intakeLayout;
    private Button applyButton;
    private EditText couponText;
    private ProgressBar progressBar;
    private TextView costInfo;
    private TextInputEditText reasonPhone;
    private TextInputEditText reasonForVisit;
    private TextInputLayout reasonLayout;
    private TextInputLayout phoneLayout;
    private TextView privacyLink;
    private TextView policyLink;
    private AppCompatCheckBox agreePrivacyPolicyCheck;

    public MyCareVisitCostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareVisitCostFragment.
     */
    public static MyCareVisitCostFragment newInstance() {
        return new MyCareVisitCostFragment();
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

        View view = inflater.inflate(R.layout.fragment_my_care_cost, container, false);

        intakeLayout = (LinearLayout) view.findViewById(R.id.intake_layout);
        applyButton = (Button) view.findViewById(R.id.apply_button);
        costInfo = (TextView) view.findViewById(R.id.costInfo);
        couponText = (EditText) view.findViewById(R.id.coupon_code_edit_text);
        progressBar = (ProgressBar) view.findViewById(R.id.cost_progress);
        reasonPhone = (TextInputEditText) view.findViewById(R.id.reasonPhone);
        reasonForVisit = (TextInputEditText) view.findViewById(R.id.reasonForVisit);
        reasonLayout = (TextInputLayout) view.findViewById(R.id.reason_layout);
        phoneLayout = (TextInputLayout) view.findViewById(R.id.phone_layout);
        privacyLink = (TextView) view.findViewById(R.id.agree_privacy_policy_text2);
        policyLink = (TextView) view.findViewById(R.id.agree_privacy_policy_text3);
        agreePrivacyPolicyCheck = (AppCompatCheckBox) view.findViewById(R.id.agree_privacy_policy_check);

        reasonPhone.addTextChangedListener(new PhoneAndDOBFormatter(reasonPhone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));
        reasonPhone.setText(ProfileManager.getProfile().phoneNumber);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createVisit();

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponText.getText().toString().length() > 0
                        && AwsManager.getInstance().getVisit() != null) {
                    applyCoupon(couponText.getText().toString());
                }
            }
        });

        privacyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof NavigationActivity) {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_PRIVACY_POLICY, null);
                }
            }
        });

        policyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof NavigationActivity) {
                    ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_PRIVACY_POLICY, null);
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
        return Constants.ActivityTag.MY_CARE_COST;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.intake_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.next:
                phoneLayout.setError(null);
                reasonLayout.setError(null);

                if (isAdded() && AwsManager.getInstance().getVisit() != null) {

                    if (AwsManager.getInstance().getVisit().getVisitCost() != null && AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost() > 0) {

                        Toast.makeText(getContext(), "Your cost isn't free\nYou might want to apply a coupon...", Toast.LENGTH_LONG).show();

                    } else if (!CommonUtil.isValidMobile(reasonPhone.getText().toString().trim())) {
                        phoneLayout.setError(getString(R.string.field_must_be_completed));

                    } else if (reasonForVisit.getText().toString().trim().length() <= 0) {
                        reasonLayout.setError(getString(R.string.field_must_be_completed));

                    } else if (!agreePrivacyPolicyCheck.isChecked()) {
                        Toast.makeText(getActivity(), R.string.my_care_privacy_policy_accept, Toast.LENGTH_LONG).show();

                    } else if (CommonUtil.isValidMobile(reasonPhone.getText().toString().trim())
                            && reasonForVisit.getText().toString().trim().length() > 0
                            && agreePrivacyPolicyCheck.isChecked()) {

                        ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void applyCoupon(String couponCode) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
            return;
        }

        if (AwsManager.getInstance().getVisit() == null) {
            return;
        }

        try {
            intakeLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            AwsManager.getInstance().getAWSDK().getVisitManager().applyCouponCode(
                    AwsManager.getInstance().getVisit(),
                    couponCode,
                    new SDKCallback<Void, SDKError>() {
                        @Override
                        public void onResponse(Void aVoid, SDKError sdkError) {
                            if (sdkError == null && isAdded()) {
                                if (AwsManager.getInstance().isDependent()) {
                                    costInfo.setText(getString(R.string.visit_cost_desc_dependent) +
                                            AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost());
                                } else {
                                    costInfo.setText(getString(R.string.visit_cost_desc) +
                                            AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost());
                                }
                            } else {
                                Timber.e("applyCouponCode. Something failed! :/");
                                Timber.e("SDK Error: " + sdkError);
                            }

                            intakeLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("applyCouponCode. Something failed! :/");
                            Timber.e("Throwable = " + throwable);
                            intakeLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
        } catch (IllegalArgumentException ex) {
            Timber.e(ex);
            intakeLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void createVisit() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_network_msg, Toast.LENGTH_LONG).show();
            return;
        }

        intakeLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getVisitManager().createOrUpdateVisit(
                AwsManager.getInstance().getVisitContext(),
                new SDKValidatedCallback<Visit, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.d("createOrUpdateVisit. ValidationFailure " + map.toString());
                        intakeLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(Visit visit, SDKError sdkError) {
                        if (sdkError == null) {
                            Timber.d("createOrUpdateVisit. onResponse " + visit.getEndReason());
                            AwsManager.getInstance().setVisit(visit);

                            applyCoupon("Free");

                            DecimalFormat amountFormat = new DecimalFormat("0.00");
                            if (AwsManager.getInstance().isDependent()) {
                                costInfo.setText(getString(R.string.visit_cost_desc_dependent) +
                                        amountFormat.format(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
                            } else {
                                costInfo.setText(getString(R.string.visit_cost_desc) +
                                        amountFormat.format(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
                            }

                            intakeLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } else {
                            Timber.e("createOrUpdateVisit. Something failed during video visit! :/");
                            Timber.e("SDK Error: " + sdkError);

                            intakeLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            if (sdkError.getMessage() != null && !sdkError.getMessage().isEmpty()) {
                                Toast.makeText(getContext(), sdkError.getMessage(), Toast.LENGTH_LONG).show();

                            } else if (sdkError.getSDKErrorReason() != null && !sdkError.getSDKErrorReason().isEmpty()) {
                                Toast.makeText(getContext(), sdkError.getSDKErrorReason(), Toast.LENGTH_LONG).show();

                            } else if (sdkError.toString() != null && sdkError.toString().toLowerCase().contains("provider unavailable")) {
                                Toast.makeText(getContext(), "Provider unavailable \nPlease select a different provider.", Toast.LENGTH_LONG).show();

                            } else if (sdkError.toString() != null && !sdkError.toString().isEmpty()) {
                                Toast.makeText(getContext(), sdkError.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), getContext().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
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
}
