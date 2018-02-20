package com.prokarma.myhome.features.televisit.cost;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.billing.CreatePaymentRequest;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.util.CreditCardUtil;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.features.televisit.AwsManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareVisitCostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareVisitCostFragment extends BaseFragment {

    public static final String MY_CARE_COST_TAG = "my_care_cost_tag";
    public static final String MODE = "visit_cost_mode";
    private static final String MODE_UPDATE_PAYMENT = "update_payment_method";

    private String mode = "";
    private LinearLayout wholeLayout;
    private ProgressBar progressBar;

    private LinearLayout costLayout;
    private TextView costInfo;

    private RelativeLayout couponLayout;
    private TextInputLayout couponInputLayout;
    private EditText couponEditText;
    private TextView apply;
    private TextView couponInfo;

    private LinearLayout paymentInfoLayout;
    private TextView cardInfo;
    private TextView cardExpired;
    private TextView updatePayment;

    private ImageView mcnImage;

    private LinearLayout wholePaymentLayout;
    private LinearLayout paymentLayout;
    private EditText paymentNameOnCard;
    private EditText paymentCardNumber;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private EditText paymentCvv;
    private EditText paymentZipcode;

    private TextInputLayout nameLayout;
    private TextInputLayout cardLayout;
    private TextInputLayout cvvLayout;
    private TextInputLayout payZipLayout;

    private LinearLayout billingAddressLayout;
    private TextInputEditText address1;
    private TextInputEditText address2;
    private TextInputEditText city;
    private Spinner state;
    private TextInputEditText zip;

    private TextInputLayout address1Layout;
    private TextInputLayout address2Layout;
    private TextInputLayout cityLayout;
    private TextInputLayout zipLayout;

    private CreditCardUtil creditCardUtil;

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

        if (getArguments() != null && getArguments().containsKey(MODE)) {
            mode = getArguments().getString(MODE);
        }
        if (!CommonUtil.isEmptyString(mode) && mode.equalsIgnoreCase(MODE_UPDATE_PAYMENT)) {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.title_visit_payment_update));
        } else {
            ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.title_visit_payment));
        }

        View view = inflater.inflate(R.layout.fragment_my_care_cost, container, false);

        wholeLayout = (LinearLayout) view.findViewById(R.id.whole_layout);
        costLayout = (LinearLayout) view.findViewById(R.id.cost_layout);
        costInfo = (TextView) view.findViewById(R.id.cost_info);

        couponLayout = (RelativeLayout) view.findViewById(R.id.coupon_layout);
        couponInputLayout = (TextInputLayout) view.findViewById(R.id.coupon_code_textinput);
        couponEditText = (EditText) view.findViewById(R.id.coupon_code_edittext);
        apply = (TextView) view.findViewById(R.id.apply_button);
        couponInfo = (TextView) view.findViewById(R.id.coupon_info);

        paymentInfoLayout = (LinearLayout) view.findViewById(R.id.payment_info_layout);
        cardInfo = (TextView) view.findViewById(R.id.card_info);
        cardExpired = (TextView) view.findViewById(R.id.card_expired);
        updatePayment = (TextView) view.findViewById(R.id.update_payment);


        mcnImage = (ImageView) view.findViewById(R.id.mcn_dashboard);
        progressBar = (ProgressBar) view.findViewById(R.id.cost_progress);

        wholePaymentLayout = (LinearLayout) view.findViewById(R.id.whole_payment_layout);
        paymentLayout = (LinearLayout) view.findViewById(R.id.payment_layout);
        paymentNameOnCard = (EditText) view.findViewById(R.id.name_edit_text);
        paymentCardNumber = (EditText) view.findViewById(R.id.card_edit_text);
        monthSpinner = (Spinner) view.findViewById(R.id.month_spinner);
        yearSpinner = (Spinner) view.findViewById(R.id.year_spinner);
        paymentCvv = (EditText) view.findViewById(R.id.cvv_edit_text);
        paymentZipcode = (EditText) view.findViewById(R.id.zipcode_edit_text);

        nameLayout = (TextInputLayout) view.findViewById(R.id.name_layout);
        cardLayout = (TextInputLayout) view.findViewById(R.id.card_layout);
        cvvLayout = (TextInputLayout) view.findViewById(R.id.cvv_layout);
        payZipLayout = (TextInputLayout) view.findViewById(R.id.pay_zip_layout);

        billingAddressLayout = (LinearLayout) view.findViewById(R.id.billing_address_layout);
        address1 = (TextInputEditText) view.findViewById(R.id.address);
        address2 = (TextInputEditText) view.findViewById(R.id.address2);
        city = (TextInputEditText) view.findViewById(R.id.city);
        state = (Spinner) view.findViewById(R.id.state);
        zip = (TextInputEditText) view.findViewById(R.id.zip);

        address1Layout = (TextInputLayout) view.findViewById(R.id.address_layout);
        address2Layout = (TextInputLayout) view.findViewById(R.id.address2_layout);
        cityLayout = (TextInputLayout) view.findViewById(R.id.city_layout);
        zipLayout = (TextInputLayout) view.findViewById(R.id.zip_layout);

        ViewGroup.LayoutParams layoutParams = zipLayout.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        zipLayout.setLayoutParams(layoutParams);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        apply.setVisibility(View.GONE);
        couponEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    apply.setVisibility(View.VISIBLE);
                } else {
                    apply.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponEditText.getText().toString().length() > 0 && AwsManager.getInstance().getVisit() != null) {
                    applyCoupon(couponEditText.getText().toString());
                }
            }
        });

        updatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(MODE, MODE_UPDATE_PAYMENT);
                ((NavigationActivity) getActivity()).loadFragment(Constants.ActivityTag.MY_CARE_UPDATE_PAYMENT, bundle);
            }
        });

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getYearsArray());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.notifyDataSetChanged();
        yearSpinner.setAdapter(yearAdapter);

        state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        monthSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });
        yearSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        updateVisitCost();

        if (!CommonUtil.isEmptyString(mode) && mode.equalsIgnoreCase(MODE_UPDATE_PAYMENT)) {
            showUpdatePaymentView();
        } else {
            getPaymentMethod();
        }

        populateCardInfo(AwsManager.getInstance().getPaymentMethod());
        populateProfileAddress(ProfileManager.getProfile());

        creditCardUtil = AwsManager.getInstance().getAWSDK().getCreditCardUtil();
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        if (!CommonUtil.isEmptyString(mode) && mode.equalsIgnoreCase(MODE_UPDATE_PAYMENT)) {
            return Constants.ActivityTag.MY_CARE_UPDATE_PAYMENT;
        } else {
            return Constants.ActivityTag.MY_CARE_PAYMENT;
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (!CommonUtil.isEmptyString(mode) && mode.equalsIgnoreCase(MODE_UPDATE_PAYMENT)) {
            inflater.inflate(R.menu.save_menu, menu);
        } else {
            inflater.inflate(R.menu.next_menu, menu);
        }
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

            case R.id.save:
                saveClick();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.MCN_COST_SCREEN, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtil.hideSoftKeyboard(getActivity());
    }


    private void showUpdatePaymentView() {

        wholePaymentLayout.setVisibility(View.VISIBLE);
        costLayout.setVisibility(View.GONE);
        couponLayout.setVisibility(View.GONE);
        couponInfo.setVisibility(View.GONE);
        paymentInfoLayout.setVisibility(View.GONE);
        mcnImage.setVisibility(View.GONE);
    }

    private String[] getYearsArray() {

        int yearFrom = Calendar.getInstance().get(Calendar.YEAR);
        int yearTo = Calendar.getInstance().get(Calendar.YEAR) + 10;

        ArrayList<String> alYears = new ArrayList<>();
        alYears.add("YYYY");
        for (int i = yearFrom; i <= yearTo; i++) {
            alYears.add(String.valueOf(i));
        }
        return alYears.toArray(new String[alYears.size()]);
    }

    private void populateCardInfo(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return;
        }

        if (!CommonUtil.isEmptyString(paymentMethod.getBillingName())) {
            paymentNameOnCard.setText(paymentMethod.getBillingName());
        }

        if (paymentMethod.getBillingAddress() != null) {

            if (!CommonUtil.isEmptyString(paymentMethod.getBillingAddress().getAddress1())) {
                address1.setText(paymentMethod.getBillingAddress().getAddress1());
            }
            if (!CommonUtil.isEmptyString(paymentMethod.getBillingAddress().getAddress2())) {
                address2.setText(paymentMethod.getBillingAddress().getAddress2());
            }
            if (!CommonUtil.isEmptyString(paymentMethod.getBillingAddress().getCity())) {
                city.setText(paymentMethod.getBillingAddress().getCity());
            }
            if (paymentMethod.getBillingAddress().getState() != null && paymentMethod.getBillingAddress().getState().getCode() != null) {
                for (int i = 0; i < state.getAdapter().getCount(); i++) {
                    if (paymentMethod.getBillingAddress().getState().getCode().equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                        state.setSelection(i);
                        break;
                    }
                }
            }
            if (!CommonUtil.isEmptyString(paymentMethod.getBillingAddress().getZipCode())) {
                zip.setText(paymentMethod.getBillingAddress().getZipCode());
            }
        }
    }

    private void populateProfileAddress(Profile profile) {

        if (profile.address != null && profile.address.line1 != null) {
            address1.setText(profile.address.line1);
        }

        if (profile.address != null && profile.address.line2 != null) {
            address2.setText(profile.address.line2);
        }

        if (profile.address != null && profile.address.city != null) {
            city.setText(profile.address.city);
        }

        if (profile.address != null && profile.address.stateOrProvince != null) {

            //Loop through states until we find a match, then set state spinner selection
            for (int i = 0; i < state.getAdapter().getCount(); i++) {
                if (profile.address.stateOrProvince.equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                    state.setSelection(i);
                    break;
                }
            }
        } else {
            state.setSelection(0);  //Placeholder is the first item in the array
        }

        if (profile.address != null && profile.address.zipCode != null) {
            zip.setText(profile.address.zipCode.trim());
        }
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

        if (couponCode.startsWith("-")) {
            couponInputLayout.setError(getString(R.string.invalid_coupon_code));
            progressBar.setVisibility(View.GONE);
            CommonUtil.hideSoftKeyboard(getContext(), couponEditText);
            return;
        }

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (AwsManager.getInstance().getVisit() == null || !isAdded()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        CommonUtil.hideSoftKeyboard(getContext(), couponEditText);

        AwsManager.getInstance().getAWSDK().getVisitManager().applyCouponCode(
                AwsManager.getInstance().getVisit(),
                couponCode,
                new SDKCallback<Void, SDKError>() {
                    @Override
                    public void onResponse(Void aVoid, SDKError sdkError) {
                        if (sdkError == null) {

                            AwsManager.getInstance().setVisitCostCouponApplied(couponCode);

                            couponInfo.setText("Applied Coupon: " + couponCode.toUpperCase());
                            couponInfo.setContentDescription(couponInfo.getText());

                            couponEditText.setText("");
                            couponInfo.setVisibility(View.VISIBLE);
                            couponInputLayout.setError(null);

                            updateVisitCost();
                        } else {
                            Timber.e("applyCouponCode. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);

                            //couponInfo.setVisibility(View.GONE);
                            couponInputLayout.setError(getString(R.string.invalid_coupon_code));
                        }
                        progressBar.setVisibility(View.GONE);
                        CommonUtil.hideSoftKeyboard(getContext(), couponEditText);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("applyCouponCode. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);

                        //couponInfo.setVisibility(View.GONE);
                        couponInputLayout.setError(null);
                        CommonUtil.hideSoftKeyboard(getContext(), couponEditText);
                    }
                }
        );
    }

    private void updateVisitCost() {

        Timber.d("cost. updateVisitCost ");

        if (AwsManager.getInstance().getVisit() == null || AwsManager.getInstance().getVisit().getVisitCost() == null) {
            return;
        }

        costInfo.setText(getString(R.string.visit_cost_text) +
                CommonUtil.formatAmount(AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost()));
        costInfo.setContentDescription(costInfo.getText());

        if (AwsManager.getInstance().getVisit().getVisitCost().getExpectedConsumerCopayCost() > 0) {
            couponLayout.setVisibility(View.VISIBLE);
            paymentInfoLayout.setVisibility(View.VISIBLE);
        } else {

            couponLayout.setVisibility(View.VISIBLE);
            paymentInfoLayout.setVisibility(View.GONE);

            wholePaymentLayout.setVisibility(View.GONE);
            mcnImage.setVisibility(View.VISIBLE);
        }

        if (!CommonUtil.isEmptyString(AwsManager.getInstance().getVisitCostCouponApplied())) {
            couponInfo.setText("Applied Coupon: " + AwsManager.getInstance().getVisitCostCouponApplied().toUpperCase());
            couponInfo.setContentDescription(couponInfo.getText());
            couponInfo.setVisibility(View.VISIBLE);
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

                cardInfo.setCompoundDrawablePadding(15);
                cardInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_visa), null, null, null);

            } else if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("master")) {

                cardInfo.setCompoundDrawablePadding(15);
                cardInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_mastercard), null, null, null);

            } else if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("amex")) {

                cardInfo.setCompoundDrawablePadding(15);
                cardInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_amex), null, null, null);

            } else if (AwsManager.getInstance().getPaymentMethod().getType().equalsIgnoreCase("discover")) {

                cardInfo.setCompoundDrawablePadding(15);
                cardInfo.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.card_discover), null, null, null);
            } else {
                cardInfo.setCompoundDrawablePadding(0);
                cardInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }


            if (AwsManager.getInstance().getPaymentMethod().isExpired()) {
                cardExpired.setVisibility(View.VISIBLE);
            }
            else {
                cardExpired.setVisibility(View.GONE);
            }

            final StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.payment_information_payment_method, AwsManager.getInstance().getPaymentMethod().getType(), AwsManager.getInstance().getPaymentMethod().getLastDigits()));
            cardInfo.setText(sb.toString());

            updatePayment.setText(R.string.update_payment_method_button);
            cardInfo.setTypeface(cardInfo.getTypeface(), Typeface.BOLD);

            wholePaymentLayout.setVisibility(View.GONE);
            mcnImage.setVisibility(View.GONE);
            cardInfo.setVisibility(View.VISIBLE);
            updatePayment.setVisibility(View.VISIBLE);

        } else {
            cardInfo.setText(getString(R.string.no_payment_method));
            updatePayment.setText(R.string.add_payment_method_button);
            cardInfo.setTypeface(cardInfo.getTypeface(), Typeface.BOLD);

            wholePaymentLayout.setVisibility(View.VISIBLE);
            mcnImage.setVisibility(View.GONE);
            cardInfo.setVisibility(View.GONE);
            updatePayment.setVisibility(View.GONE);
        }

        cardInfo.setContentDescription(cardInfo.getText());
        updatePayment.setContentDescription(updatePayment.getText());
    }


    private void saveClick() {

        if (isAdded() && AwsManager.getInstance().getPatient() != null) {

            CommonUtil.hideSoftKeyboard(getActivity());

            if (isValidPaymentMethod()) {
                savePaymentMethod();
            }
        }
    }

    private boolean isValidPaymentMethod() {
        boolean isValid = true;

        String isRequiredString = " is required";

        if (paymentNameOnCard.getText().toString().trim().isEmpty()) {
            isValid = false;
            nameLayout.setError(getString(R.string.payment_name_on_card) + isRequiredString);
        } else {
            nameLayout.setError(null);
        }

        if (paymentCardNumber.getText().toString().trim().isEmpty()) {
            isValid = false;
            cardLayout.setError(getString(R.string.payment_card_number) + isRequiredString);

        } else if (creditCardUtil != null && !creditCardUtil.isCreditCardNumberValid(paymentCardNumber.getText().toString().trim())) {
            isValid = false;
            cardLayout.setError(getString(R.string.payment_card_number) + " not valid");

        } else if (creditCardUtil != null
                && !AwsManager.getInstance().isAmwellSupportedCardType(creditCardUtil.getCreditCardByNumber(paymentCardNumber.getText().toString().trim()))) {
            isValid = false;
            cardLayout.setError(getString(R.string.payment_card_number) + " not supported");
        } else {
            cardLayout.setError(null);
        }

        if (monthSpinner.getSelectedItemPosition() == 0) {
            isValid = false;
            ((TextView) monthSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.red));
        } else if (Integer.valueOf(monthSpinner.getSelectedItem().toString()) < Calendar.getInstance().get(Calendar.MONTH)) {
            isValid = false;
            ((TextView) monthSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.red));
        } else {
            ((TextView) monthSpinner.getSelectedView()).setTextColor(Color.BLACK);
        }

        if (yearSpinner.getSelectedItemPosition() == 0) {
            isValid = false;
            ((TextView) yearSpinner.getSelectedView()).setTextColor(getResources().getColor(R.color.red));
        } else {
            ((TextView) yearSpinner.getSelectedView()).setTextColor(Color.BLACK);
        }

        if (paymentCvv.getText().toString().trim().isEmpty()) {
            isValid = false;
            cvvLayout.setError(getString(R.string.payment_cvv) + isRequiredString);
        } else if (creditCardUtil != null && !creditCardUtil.isSecurityCodeValid(paymentCardNumber.getText().toString().trim(), paymentCvv.getText().toString().trim())) {
            isValid = false;
            cvvLayout.setError(getString(R.string.payment_cvv) + " not valid");
        } else {
            cvvLayout.setError(null);
        }

        if (paymentZipcode.getText().toString().trim().isEmpty()) {
            isValid = false;
            payZipLayout.setError(getString(R.string.zip) + isRequiredString);
        } else {
            payZipLayout.setError(null);
        }


        if (address1.getText().toString().trim().isEmpty()) {
            isValid = false;
            address1Layout.setError(getString(R.string.address) + isRequiredString);
        } else {
            address1Layout.setError(null);
        }

        //address2 field is optional
        /*if (address2.getText().toString().trim().isEmpty()) {
            isValid = false;
            address2Layout.setError(getString(R.string.address_2) + isRequiredString);
        } else {
            address2Layout.setError(null);
        }*/

        if (city.getText().toString().trim().isEmpty()) {
            isValid = false;
            cityLayout.setError(getString(R.string.city) + isRequiredString);
        } else {
            cityLayout.setError(null);
        }

        if (state.getSelectedItemPosition() == 0) {
            isValid = false;
            ((TextView) state.getSelectedView()).setTextColor(getResources().getColor(R.color.red));
        } else if (!AwsManager.getInstance().isAmwellSupportedState(state.getSelectedItem().toString())) {
            isValid = false;
            ((TextView) state.getSelectedView()).setTextColor(getResources().getColor(R.color.red));
        } else {
            ((TextView) state.getSelectedView()).setTextColor(Color.BLACK);
        }

        if (zip.getText().toString().trim().isEmpty()) {
            isValid = false;
            zipLayout.setError(getString(R.string.zip) + isRequiredString);
        } else {
            zipLayout.setError(null);
        }

        return isValid;
    }

    private void savePaymentMethod() {

        if (!ConnectionUtil.isConnected(getActivity())) {
            CommonUtil.showToast(getActivity(), getString(R.string.no_network_msg));
            return;
        }

        if (!AwsManager.getInstance().isHasInitializedAwsdk()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        CreatePaymentRequest createPaymentRequest = AwsManager.getInstance().getAWSDK().getConsumerPaymentManager().
                getNewCreatePaymentRequest(AwsManager.getInstance().getPatient());

        createPaymentRequest.setNameOnCard(paymentNameOnCard.getText().toString());
        createPaymentRequest.setCreditCardNumber(paymentCardNumber.getText().toString());
        createPaymentRequest.setCreditCardMonth(Integer.valueOf(monthSpinner.getSelectedItem().toString()));
        createPaymentRequest.setCreditCardYear(Integer.valueOf(yearSpinner.getSelectedItem().toString()));
        createPaymentRequest.setCreditCardSecCode(paymentCvv.getText().toString());
        createPaymentRequest.setCreditCardZip(paymentZipcode.getText().toString());

        final Address address = AwsManager.getInstance().getAWSDK().getNewAddress();
        address.setAddress1(address1.getText().toString());
        address.setAddress2(address2.getText().toString());
        address.setCity(city.getText().toString());
        address.setState(AwsManager.getInstance().getState(state.getSelectedItem().toString()));
        address.setCountry(AwsManager.getInstance().getCountry("US"));
        address.setZipCode(zip.getText().toString());

        createPaymentRequest.setAddress(address);

        AwsManager.getInstance().getAWSDK().getConsumerPaymentManager().updatePaymentMethod(
                createPaymentRequest,
                new SDKValidatedCallback<PaymentMethod, SDKError>() {
                    @Override
                    public void onValidationFailure(@NonNull Map<String, String> map) {
                        Timber.e("updatePaymentMethod Validation failed! :/");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(PaymentMethod paymentMethod, SDKError sdkError) {
                        if (sdkError == null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Timber.e("updatePaymentMethod. Something failed! :/");
                            Timber.e("SDK Error: " + sdkError);
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Timber.e("updatePaymentMethod. Something failed! :/");
                        Timber.e("Throwable = " + throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

}
