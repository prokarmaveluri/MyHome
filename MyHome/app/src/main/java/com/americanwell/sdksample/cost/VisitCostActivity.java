/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.cost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.intake.BaseIntakeActivity;
import com.americanwell.sdksample.profile.AddCreditCardActivity;
import com.americanwell.sdksample.visit.WaitingRoomActivity;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity to show visit cost and allow coupon code entry during intake
 */
@RequiresPresenter(VisitCostPresenter.class)
public class VisitCostActivity extends BaseIntakeActivity<VisitCostPresenter> {

    private static final int REQ_UPDATE_PAYMENT_METHOD = 1000;

    @BindView(R.id.summary_text_view)
    TextView summaryTextView;
    @BindView(R.id.visit_cost_label)
    TextView visitCostLabel;
    @BindView(R.id.coupon_code_layout)
    View couponCodeLayout;
    @BindView(R.id.coupon_code_text_layout)
    TextInputLayout couponCodeTextLayout;
    @BindView(R.id.coupon_code_edit_text)
    EditText couponCodeEditText;
    @BindView(R.id.apply_button)
    Button applyButton;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.payment_method_prompt)
    TextView paymentMethodPrompt;
    @BindView(R.id.payment_method_text_view)
    TextView paymentMethodTextView;
    @BindView(R.id.update_payment_method_button)
    Button updatePaymentMethodButton;
    @BindView(R.id.deferred_billing_prompt)
    TextView deferredBillingPrompt;
    @BindView(R.id.visit_cost_layout)
    View visitCostLayout;

    @BindString(R.string.visit_cost_coupon_validation)
    String couponValidation;
    @BindString(R.string.visit_cost_coupon_applied)
    String couponApplied;
    @BindString(R.string.payment_method_no_cost)
    String paymentMethodNoCost;
    @BindString(R.string.no_payment_method)
    String noPaymentMethod;
    @BindString(R.string.payment_information_payment_method_expired)
    String paymentMethodExpired; // since AWSDK 3.1
    @BindString(R.string.visit_cost_no_coupon_entered)
    String noCouponEntered;

    public static Intent makeIntent(@NonNull final Context context, @NonNull final VisitContext visitContext) {
        final Intent intent = new Intent(context, VisitCostActivity.class);
        intent.putExtra(EXTRA_VISIT_CONTEXT, visitContext);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_visit_cost);
    }

    @OnClick(R.id.fab)
    public void next() {
        if (!getPresenter().isCouponApplied() && getPresenter().hasCouponCode()) {
            setCouponCodeError(true);
        }
        else if (!getPresenter().requiresPayment() || getPresenter().hasPaymentMethod()) {
            startActivity(WaitingRoomActivity.makeIntent(this, getPresenter().getVisit()));
            finish();
        }
    }

    public void setShowCouponCode(boolean show) {
        couponCodeLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.apply_button)
    public void applyCoupon() {
        getPresenter().applyCoupon();
    }

    @OnTextChanged(value = R.id.coupon_code_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onCouponCodeChanged(final CharSequence couponCode) {
        setCouponCodeError(false);
        getPresenter().setCouponCode(couponCode.toString().trim());
    }

    public void setCouponCodeError(boolean invalid) {
        couponCodeTextLayout.setError(invalid ? couponValidation : null);
    }

    public void setCouponApplied() {
        couponCodeEditText.setEnabled(false);
        applyButton.setEnabled(false);
        Toast.makeText(this, couponApplied, Toast.LENGTH_SHORT).show();
    }

    public void setCouponCode(final String couponCode) {
        couponCodeEditText.setText(couponCode);
    }

    public void showNoCouponEntered() {
        Toast.makeText(this, noCouponEntered, Toast.LENGTH_SHORT).show();
    }

    public void setSummary(String summaryCost) {
        summaryTextView.setText(summaryCost);
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        if (paymentMethod != null) {
            final StringBuilder sb = new StringBuilder();
            final String paymentText = getResources().getString(R.string.payment_information_payment_method,
                    paymentMethod.getType(),
                    paymentMethod.getLastDigits());
            sb.append(paymentText);

            if (paymentMethod.isExpired()) { // since AWSDK 3.1
                sb.append(" ");
                sb.append(paymentMethodExpired);
            }

            paymentMethodTextView.setText(sb.toString());
        }
        else {
            paymentMethodTextView.setText(noPaymentMethod);
        }

        setFab();
    }

    @OnClick(R.id.update_payment_method_button)
    public void onClickUpdatePaymentMethod() {
        startActivityForResult(AddCreditCardActivity.makeIntent(this), REQ_UPDATE_PAYMENT_METHOD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_UPDATE_PAYMENT_METHOD && resultCode == AddCreditCardActivity.RESULT_CREDIT_CARD_ADDED) {
            final PaymentMethod paymentMethod = data.getParcelableExtra(AddCreditCardActivity.EXTRA_PAYMENT_METHOD);
            getPresenter().setPaymentMethod(paymentMethod);
        }
    }

    public void setRequiresPayment(boolean requiresPayment) {
        updatePaymentMethodButton.setEnabled(requiresPayment);
        paymentMethodTextView.setVisibility(requiresPayment ? View.VISIBLE : View.GONE);
        if (!requiresPayment) {
            paymentMethodPrompt.setText(paymentMethodNoCost);
        }
        setFab();
    }

    public void setFab() {
        boolean showFab = true;
        if (!getPresenter().canPay()) {
            showFab = false;
        }

        fab.setEnabled(showFab);
        fab.setVisibility(showFab ? View.VISIBLE : View.GONE);
    }

    public void setEligibilityError(final SDKError eligibilityError) {
        if (eligibilityError != null) {
            sampleUtils.handleError(this, eligibilityError);
        }
    }

    public void setDeferredBillingPrompt(final String prompt) {
        if (TextUtils.isEmpty(prompt)) {
            deferredBillingPrompt.setVisibility(View.GONE);
        }
        else {
            deferredBillingPrompt.setVisibility(View.VISIBLE);
            deferredBillingPrompt.setText(prompt);
        }
    }

    public void setShowCostSummary(final boolean showCostSummary) {
        visitCostLayout.setVisibility(showCostSummary ? View.VISIBLE : View.GONE);
    }

}
