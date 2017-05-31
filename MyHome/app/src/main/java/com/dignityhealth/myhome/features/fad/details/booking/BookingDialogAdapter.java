package com.dignityhealth.myhome.features.fad.details.booking;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.utils.DateUtil;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingDialogAdapter extends PagerAdapter {
    private Context context;
    private BookingDialogToolbarInterface bookingDialogToolbarInterface;
    private boolean autoPopulateFromProfile;
    private static int NUM_ITEMS = 3;

    private ViewGroup insuranceLayout;
    private ViewGroup personalLayout;
    private ViewGroup reasonForVisitLayout;

    private Profile profile;

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;

        if (autoPopulateFromProfile) {
            profile = ProfileManager.getProfile();
        }
    }

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile, Profile profile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;
        this.profile = profile;
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

            case 2:
                reasonForVisitLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_reason, container, false);
                //setupReasonForVisitView
                container.addView(reasonForVisitLayout, position);
                return reasonForVisitLayout;

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
        TextInputEditText insuranceProvider;
        TextInputEditText memberId;
        TextInputEditText group;

        insuranceProvider = (TextInputEditText) insuranceLayout.findViewById(R.id.provider);
        memberId = (TextInputEditText) insuranceLayout.findViewById(R.id.id);
        group = (TextInputEditText) insuranceLayout.findViewById(R.id.group);

        if (profile.insuranceProvider != null && profile.insuranceProvider.providerName != null) {
            insuranceProvider.setText(profile.insuranceProvider.providerName);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.memberNumber != null) {
            memberId.setText(profile.insuranceProvider.memberNumber);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.groupNumber != null) {
            group.setText(profile.insuranceProvider.groupNumber);
        }
    }

    /**
     * Auto-populates Personal page with values from Profile Singleton
     */
    private void populatePersonalLayout() {
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
        TextView email;

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
        email = (TextView) personalLayout.findViewById(R.id.email);

        if (profile.firstName != null) {
            firstName.setText(profile.firstName);
        }

        if (profile.lastName != null) {
            lastName.setText(profile.lastName);
        }

        if (profile.preferredName != null) {
            preferredName.setText(profile.preferredName);
        }

        if (profile.gender != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (profile.gender.equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

        if (profile.dateOfBirth != null) {
            dateOfBirth.setText(DateUtil.convertUTCtoReadable(profile.dateOfBirth));
        }

        if (profile.address != null && profile.address.line1 != null) {
            address.setText(profile.address.line1);
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
            zip.setText(profile.address.zipCode);
        }

        if (profile.phoneNumber != null) {
            phone.setText(profile.phoneNumber);
        }

        if (profile.email != null) {
            email.setText(profile.email);
        }
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}