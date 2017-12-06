package com.televisit.cost;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.televisit.AwsManager;

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

    public MyCareVisitCostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareServicesFragment.
     */
    public static MyCareVisitCostFragment newInstance() {
        MyCareVisitCostFragment fragment = new MyCareVisitCostFragment();
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

        reasonPhone.addTextChangedListener(new PhoneAndDOBFormatter(reasonPhone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));
        reasonPhone.setText(ProfileManager.getProfile().phoneNumber);

        createVisit();

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponText.getText().toString().length() > 0
                        && AwsManager.getInstance().getVisit() != null)
                    applyCoupon(couponText.getText().toString());
            }
        });


        setHasOptionsMenu(true);
        return view;
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
                    } else if (CommonUtil.isValidMobile(reasonPhone.getText().toString()) && reasonForVisit.getText().toString().length() > 0) {
                        ((NavigationActivity) getActivity()).loadFragment(
                                Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);

                    } else if (!CommonUtil.isValidMobile(reasonPhone.getText().toString())) {
                        phoneLayout.setError(getString(R.string.enter_valid_phone_number));

                    } else if (reasonForVisit.getText().toString().length() <= 0) {
                        reasonLayout.setError(getString(R.string.enter_valid_reason_for_visit));
                    }
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void applyCoupon(String couponCode) {
        if (AwsManager.getInstance().getVisit() == null)
            return;
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
                                costInfo.setText(getString(R.string.visit_cost_desc) +
                                        AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost());
                            } else {
                                Timber.e("Something failed! :/");
                                Timber.e("SDK Error: " + sdkError);
                            }

                            intakeLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Timber.e("Something failed! :/");
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

        Timber.d("createVisit ");

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

                            costInfo.setText(getString(R.string.visit_cost_desc) +
                                    AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost());
                            intakeLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } else {
                            Timber.e("Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            intakeLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            if (sdkError.getMessage() != null && !sdkError.getMessage().isEmpty()) {
                                Toast.makeText(getContext(), sdkError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        intakeLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }
}
