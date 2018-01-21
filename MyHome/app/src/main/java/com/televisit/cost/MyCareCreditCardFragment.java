package com.televisit.cost;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.billing.CreatePaymentRequest;
import com.americanwell.sdk.entity.billing.CreditCardType;
import com.americanwell.sdk.entity.billing.PaymentMethod;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;
import com.televisit.AwsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyCareCreditCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCareCreditCardFragment extends BaseFragment {

    public static final String MY_CARE_CREDIT_CARD_TAG = "my_care_credit_card_tag";

    private LinearLayout wholeLayout;
    private ProgressBar progressBar;
    private TextView costInfo;

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

    private LinearLayout addressLayout;
    private TextInputEditText address1;
    private TextInputEditText address2;
    private TextInputEditText city;
    private Spinner state;
    private TextInputEditText zip;

    private TextInputLayout address1Layout;
    private TextInputLayout address2Layout;
    private TextInputLayout cityLayout;
    private TextInputLayout zipLayout;
    private TextInputLayout phoneLayout;

    private List<CreditCardType> types;
    private List<com.americanwell.sdk.entity.State> statesList;

    public MyCareCreditCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCareCreditCardFragment.
     */
    public static MyCareCreditCardFragment newInstance() {
        return new MyCareCreditCardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.payment_method));

        View view = inflater.inflate(R.layout.fragment_my_care_creditcard, container, false);

        wholeLayout = (LinearLayout) view.findViewById(R.id.whole_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        costInfo = (TextView) view.findViewById(R.id.costInfo);

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

        addressLayout = (LinearLayout) view.findViewById(R.id.billing_address_layout);
        address1 = (TextInputEditText) view.findViewById(R.id.address);
        address2 = (TextInputEditText) view.findViewById(R.id.address2);
        city = (TextInputEditText) view.findViewById(R.id.city);
        state = (Spinner) view.findViewById(R.id.state);
        zip = (TextInputEditText) view.findViewById(R.id.zip);

        address1Layout = (TextInputLayout) view.findViewById(R.id.address_layout);
        address2Layout = (TextInputLayout) view.findViewById(R.id.address2_layout);
        cityLayout = (TextInputLayout) view.findViewById(R.id.city_layout);
        zipLayout = (TextInputLayout) view.findViewById(R.id.zip_layout);

        phoneLayout = (TextInputLayout) view.findViewById(R.id.phone_layout);
        phoneLayout.setVisibility(View.GONE);

        ViewGroup.LayoutParams layoutParams = zipLayout.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        zipLayout.setLayoutParams(layoutParams);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        populateCardInfo(AwsManager.getInstance().getPaymentMethod());

        populateProfileAddress(ProfileManager.getProfile());

        getSupportedCardTypes();

        getSupportedStates();
    }

    private String[] getYearsArray() {

        int yearFrom = 2000;
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

    @Override
    public void onStop() {
        super.onStop();
        CommonUtil.hideSoftKeyboard(getActivity());
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_CREDIT_CARD;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.save_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
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
        TealiumUtil.trackView(Constants.MCN_CARD_SCREEN, null);
    }

    private void saveClick() {

        if (isAdded() && AwsManager.getInstance().getPatient() != null) {

            CommonUtil.hideSoftKeyboard(getActivity());

            if (isValidPaymentMethod()) {
                saveNewPaymentMethod();
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
        } else {
            cardLayout.setError(null);
        }

        if (monthSpinner.getSelectedItemPosition() == 0) {
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

        if (address2.getText().toString().trim().isEmpty()) {
            isValid = false;
            address2Layout.setError(getString(R.string.address_2) + isRequiredString);
        } else {
            address2Layout.setError(null);
        }

        if (city.getText().toString().trim().isEmpty()) {
            isValid = false;
            cityLayout.setError(getString(R.string.city) + isRequiredString);
        } else {
            cityLayout.setError(null);
        }

        if (state.getSelectedItemPosition() == 0) {
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

    private void getSupportedCardTypes() {

        types = AwsManager.getInstance().getSupportedCardTypes();
        if (types != null && types.size() > 0) {
            for (CreditCardType type : types) {
                Timber.d("payment. cardtype. " + type.getType() + " " + type.getName());
            }
        } else {
            Timber.d("payment. cardtypes are NULL or size zero ");
        }
    }

    private void getSupportedStates() {

        statesList = AwsManager.getInstance().getSupportedUSStates();
        if (statesList != null && statesList.size() > 0) {
            for (com.americanwell.sdk.entity.State state : statesList) {
                Timber.d("payment. state = " + state.getName());
            }
        } else {
            Timber.d("payment. states are NULL or size zero ");
        }
    }

    private void saveNewPaymentMethod() {

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
                            Timber.e("updatePaymentMethod. saved. ");
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
