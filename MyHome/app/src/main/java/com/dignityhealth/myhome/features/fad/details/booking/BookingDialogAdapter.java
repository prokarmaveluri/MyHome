package com.dignityhealth.myhome.features.fad.details.booking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.PagerAdapter;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.DateUtil;

import java.util.Calendar;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingDialogAdapter extends PagerAdapter {
    private Context context;
    private BookingDialogToolbarInterface bookingDialogToolbarInterface;
    private boolean autoPopulateFromProfile;
    private static int NUM_ITEMS = 2;

    private ViewGroup insuranceLayout;
    private ViewGroup personalLayout;

    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText preferredName;
    Spinner gender;
    TextInputEditText dateOfBirth;
    TextInputEditText address;
    TextInputEditText address2;
    TextInputEditText city;
    Spinner state;
    TextInputEditText zip;
    TextInputEditText phone;
    TextInputEditText email;
    EditText reasonForVisit;

    TextInputEditText insuranceProvider;
    TextInputEditText memberId;
    TextInputEditText group;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dateOfBirth.setText(DateUtil.convertDateToReadable(myCalendar.getTime()));
        }
    };

    private Profile formsProfile;

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;

        if (autoPopulateFromProfile) {
            formsProfile = Profile.copy(ProfileManager.getProfile());
        }
    }

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile, Profile profile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;
        formsProfile = Profile.copy(profile);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (position) {
            case 0:
                insuranceLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_insurance, container, false);
                //setupInsuranceView
                container.addView(insuranceLayout, position);

                if (autoPopulateFromProfile) {
                    populateInsuranceLayout();
                }

                return insuranceLayout;

            case 1:
                personalLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_personal, container, false);
                //setupPersonalView
                container.addView(personalLayout, position);

                if (autoPopulateFromProfile) {
                    populatePersonalLayout();
                }

                return personalLayout;

            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeViewAt(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (bookingDialogToolbarInterface != null) {
            bookingDialogToolbarInterface.setToolbarMenu(position);
        }
    }

    /**
     * Auto-populates Insurance page with values from Profile Singleton
     */
    private void populateInsuranceLayout() {
        insuranceProvider = (TextInputEditText) insuranceLayout.findViewById(R.id.provider);
        memberId = (TextInputEditText) insuranceLayout.findViewById(R.id.id);
        group = (TextInputEditText) insuranceLayout.findViewById(R.id.group);

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.providerName != null) {
            insuranceProvider.setText(formsProfile.insuranceProvider.providerName);
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.memberNumber != null) {
            memberId.setText(formsProfile.insuranceProvider.memberNumber);
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.groupNumber != null) {
            group.setText(formsProfile.insuranceProvider.groupNumber);
        }
    }

    /**
     * Auto-populates Personal page with values from Profile Singleton
     */
    private void populatePersonalLayout() {
        firstName = (TextInputEditText) personalLayout.findViewById(R.id.first_name);
        lastName = (TextInputEditText) personalLayout.findViewById(R.id.last_name);
        preferredName = (TextInputEditText) personalLayout.findViewById(R.id.preferred_name);
        gender = (Spinner) personalLayout.findViewById(R.id.gender);
        dateOfBirth = (TextInputEditText) personalLayout.findViewById(R.id.dob);
        address = (TextInputEditText) personalLayout.findViewById(R.id.address);
        address2 = (TextInputEditText) personalLayout.findViewById(R.id.address2);
        city = (TextInputEditText) personalLayout.findViewById(R.id.city);
        state = (Spinner) personalLayout.findViewById(R.id.state);
        zip = (TextInputEditText) personalLayout.findViewById(R.id.zip);
        phone = (TextInputEditText) personalLayout.findViewById(R.id.phone);
        email = (TextInputEditText) personalLayout.findViewById(R.id.email);
        reasonForVisit = (EditText) personalLayout.findViewById(R.id.booking_reason);

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.hideSoftKeyboard(context, dateOfBirth);
                new DatePickerDialog(context, dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(context, gender);
                return false;
            }
        });

        state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(context, state);
                return false;
            }
        });

        if (formsProfile.firstName != null) {
            firstName.setText(formsProfile.firstName);
        }

        if (formsProfile.lastName != null) {
            lastName.setText(formsProfile.lastName);
        }

        if (formsProfile.preferredName != null) {
            preferredName.setText(formsProfile.preferredName);
        }

        if (formsProfile.gender != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (formsProfile.gender.equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

        if (formsProfile.dateOfBirth != null) {
            dateOfBirth.setText(DateUtil.convertUTCtoReadable(formsProfile.dateOfBirth));
        }

        if (formsProfile.address != null && formsProfile.address.line1 != null) {
            address.setText(formsProfile.address.line1);
        }

        if (formsProfile.address != null && formsProfile.address.line2 != null) {
            address2.setText(formsProfile.address.line2);
        }

        if (formsProfile.address != null && formsProfile.address.city != null) {
            city.setText(formsProfile.address.city);
        }

        if (formsProfile.address != null && formsProfile.address.stateOrProvince != null) {

            //Loop through states until we find a match, then set state spinner selection
            for (int i = 0; i < state.getAdapter().getCount(); i++) {
                if (formsProfile.address.stateOrProvince.equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                    state.setSelection(i);
                    break;
                }
            }
        } else {
            state.setSelection(0);  //Placeholder is the first item in the array
        }

        if (formsProfile.address != null && formsProfile.address.zipCode != null) {
            zip.setText(formsProfile.address.zipCode);
        }

        if (formsProfile.phoneNumber != null) {
            phone.setText(formsProfile.phoneNumber);
        }

        if (formsProfile.email != null) {
            email.setText(formsProfile.email);
        }
    }

    /**
     * Gets the Profile constructed from the values in the forms
     *
     * @return a Profile that accurately represents the forms filled out
     */
    public Profile getProfile() {
//        if (formsProfile.address == null) {
//            formsProfile.address = new Address();
//        }
//
//        if (formsProfile.insuranceProvider == null) {
//            formsProfile.insuranceProvider = new InsuranceProvider();
//        }

        if (firstName.getText() != null && !firstName.getText().toString().isEmpty()) {
            formsProfile.firstName = firstName.getText().toString().trim();
        }

        if (lastName.getText() != null && !lastName.getText().toString().isEmpty()) {
            formsProfile.lastName = lastName.getText().toString().trim();
        }

        if (preferredName.getText() != null && !preferredName.getText().toString().isEmpty()) {
            formsProfile.preferredName = preferredName.getText().toString().trim();
        }

        if (!gender.getSelectedItem().toString().isEmpty()) {
            formsProfile.gender = gender.getSelectedItem().toString().trim();
        }

        if (dateOfBirth.getText() != null && !dateOfBirth.getText().toString().isEmpty()) {
            formsProfile.dateOfBirth = DateUtil.convertReadableToUTC(dateOfBirth.getText().toString().trim());
        }

        if (address.getText() != null && !address.getText().toString().isEmpty()) {
            formsProfile.address.line1 = address.getText().toString().trim();
        }

        if (address2.getText() != null && !address2.getText().toString().isEmpty()) {
            formsProfile.address.line2 = address2.getText().toString().trim();
        }

        if (city.getText() != null && !city.getText().toString().isEmpty()) {
            formsProfile.address.city = city.getText().toString().trim();
        }

        if (!state.getSelectedItem().toString().isEmpty() && state.getSelectedItemPosition() != 0) {
            formsProfile.address.stateOrProvince = state.getSelectedItem().toString().trim();
        }

        if (zip.getText() != null && !zip.getText().toString().isEmpty()) {
            formsProfile.address.zipCode = zip.getText().toString().trim();
        }

        //Make sure to strip phone number of any non-digits
        if (phone.getText() != null && !phone.getText().toString().isEmpty()) {
            formsProfile.phoneNumber = CommonUtil.stripPhoneNumber(phone.getText().toString().trim());
        }

        if (insuranceProvider.getText() != null && !insuranceProvider.getText().toString().isEmpty()) {
            formsProfile.insuranceProvider.providerName = insuranceProvider.getText().toString().trim();
        }

        if (memberId.getText() != null && !memberId.getText().toString().isEmpty()) {
            formsProfile.insuranceProvider.memberNumber = memberId.getText().toString().trim();
        }

        if (group.getText() != null && !group.getText().toString().isEmpty()) {
            formsProfile.insuranceProvider.groupNumber = group.getText().toString().trim();
        }

        return formsProfile;
    }
}