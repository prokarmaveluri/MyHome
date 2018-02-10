package com.prokarma.myhome.features.televisit.cost;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.manager.SDKCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareVisitCostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareVisitCostFragment extends BaseFragment {

    public static final String MY_CARE_COST_TAG = "my_care_cost_tag";

    private LinearLayout wholeLayout;
    private ProgressBar progressBar;
    private TextView costInfo;

    private LinearLayout paymentLayout;
    private EditText couponText;
    private Button applyButton;
    private TextView couponInfo;

    private LinearLayout couponLayout;
    private TextView paymentMethodInfo;
    private Button paymentMethodButton;

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
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.title_visit_cost));

        View view = inflater.inflate(R.layout.fragment_my_care_cost, container, false);

        wholeLayout = (LinearLayout) view.findViewById(R.id.whole_layout);
        costInfo = (TextView) view.findViewById(R.id.costInfo);

        couponLayout = (LinearLayout) view.findViewById(R.id.coupon_layout);
        couponText = (EditText) view.findViewById(R.id.coupon_code_edit_text);
        applyButton = (Button) view.findViewById(R.id.apply_button);
        couponInfo = (TextView) view.findViewById(R.id.coupon_info);

        paymentLayout = (LinearLayout) view.findViewById(R.id.payment_layout);
        paymentMethodInfo = (TextView) view.findViewById(R.id.payment_method_info);
        paymentMethodButton = (Button) view.findViewById(R.id.payment_method_button);

        progressBar = (ProgressBar) view.findViewById(R.id.cost_progress);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponText.getText().toString().length() > 0
                        && AwsManager.getInstance().getVisit() != null) {
                    applyCoupon(couponText.getText().toString());
                }
            }
        });

        paymentMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_CREDIT_CARD, null);
            }
        });

        updateVisitCost();

        getPaymentMethod();
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
        TealiumUtil.trackView(Constants.MCN_COST_SCREEN, null);
    }

    private void nextClick() {
        if (isAdded() && AwsManager.getInstance().getVisit() != null) {

            if (AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost() > 0
                    && AwsManager.getInstance().getPaymentMethod() == null) {
                CommonUtil.showToast(getContext(), "Please add payment method to proceed.");
                return;
            }

            if (AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost() > 0
                    && AwsManager.getInstance().getPaymentMethod() != null
                    && AwsManager.getInstance().getPaymentMethod().isExpired()) {
                CommonUtil.showToast(getContext(), "Card Expired. Please update the information.");
                return;
            }

            ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_WAITING_ROOM, null);
        }
    }

    private void applyCoupon(final String couponCode) {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (AwsManager.getInstance().getVisit() == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getVisitManager().applyCouponCode(
                AwsManager.getInstance().getVisit(),
                couponCode,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null && isAdded()) {

                            AwsManager.getInstance().setVisitCostCouponApplied(couponCode);

                            couponInfo.setText("Applied Coupon: " + couponCode.toUpperCase());
                            couponInfo.setContentDescription(couponInfo.getText());
                            couponInfo.setVisibility(View.VISIBLE);

                            updateVisitCost();
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
        costInfo.setContentDescription(costInfo.getText());

        if (AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost() > 0) {
            couponLayout.setVisibility(View.VISIBLE);
            paymentLayout.setVisibility(View.VISIBLE);
        } else {
            couponLayout.setVisibility(View.GONE);
            paymentLayout.setVisibility(View.GONE);
        }
    }

    private void getPaymentMethod() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getConsumerPaymentManager().getPaymentMethod(
                AwsManager.getInstance().getPatient(),
                new SDKCallback<PaymentMethod, SDKError>() {
                    @Override
                    public void onResponse(PaymentMethod paymentMethodInfo, SDKError sdkError) {
                        if (sdkError != null) {
                            Timber.e("getPaymentMethod. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                        }

                        AwsManager.getInstance().setPaymentMethod(paymentMethodInfo);

                        updatePaymentInfo();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("getPaymentMethod. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    public void updatePaymentInfo() {

        if (AwsManager.getInstance().getPaymentMethod() != null) {

            if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("visa")) {

                paymentMethodInfo.setCompoundDrawablePadding(15);
                paymentMethodInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_visa), null, null, null);

            } else if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("master")) {

                paymentMethodInfo.setCompoundDrawablePadding(15);
                paymentMethodInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_mastercard), null, null, null);

            } else if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("amex")) {

                paymentMethodInfo.setCompoundDrawablePadding(15);
                paymentMethodInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_amex), null, null, null);

            } else if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("discover")) {

                paymentMethodInfo.setCompoundDrawablePadding(15);
                paymentMethodInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_discover), null, null, null);
            } else {
                paymentMethodInfo.setCompoundDrawablePadding(0);
                paymentMethodInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

            final StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.payment_information_payment_method, AwsManager.getInstance().getPaymentMethod().getType(), AwsManager.getInstance().getPaymentMethod().getLastDigits()));

            if (AwsManager.getInstance().getPaymentMethod().isExpired()) {
                sb.append(" " + getString(R.string.payment_information_payment_method_expired));
            }

            paymentMethodInfo.setText(sb.toString());
            paymentMethodButton.setText(R.string.update_payment_method_button);
            paymentMethodInfo.setTypeface(paymentMethodInfo.getTypeface(), Typeface.BOLD);

        } else {
            paymentMethodInfo.setText(getString(R.string.no_payment_method));
            paymentMethodButton.setText(R.string.add_payment_method_button);
            paymentMethodInfo.setTypeface(paymentMethodInfo.getTypeface(), Typeface.BOLD);
        }

        paymentMethodInfo.setContentDescription(paymentMethodInfo.getText());
        paymentMethodButton.setContentDescription(paymentMethodButton.getText());
    }
}
