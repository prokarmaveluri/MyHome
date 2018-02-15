package com.prokarma.myhome.features.contact;

import android.content.Intent;
import android.net.Uri;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;

import timber.log.Timber;

/**
 * Created by kwelsh on 2/12/18.
 */

public class ContactUsRouter implements ContactUsContract.Router {
    BaseFragment fragment;

    public ContactUsRouter(BaseFragment fragment) {
        this.fragment = fragment;
    }


    @Override
    public void goToEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Constants.SUPPORT_EMAIL});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, fragment.getString(R.string.ask_a_question));

        if (ProfileManager.getProfile() != null) {
            Timber.i("Have Profile information. Crafting Support email...");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, fragment.getString(R.string.contact_us_email_body) + ",\n" +
                    ProfileManager.getProfile().firstName + " " + ProfileManager.getProfile().lastName + "\n" +
                    (ProfileManager.getProfile().phoneNumber != null ? CommonUtil.constructPhoneNumberDots(ProfileManager.getProfile().phoneNumber) : "") + "\n");
            fragment.startActivity(Intent.createChooser(emailIntent, fragment.getString(R.string.contact_us_email_intent_header)));
        } else {
            Timber.i("Don't have any Profile information. Showing placeholder...");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, fragment.getString(R.string.contact_us_email_body));
            fragment.startActivity(Intent.createChooser(emailIntent, fragment.getString(R.string.contact_us_email_intent_header)));
        }
    }

    @Override
    public void goToDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(Constants.TEL + CommonUtil.constructPhoneNumberDots(fragment.getString(R.string.contact_us_phone))));
        fragment.getActivity().startActivity(intent);
    }
}
