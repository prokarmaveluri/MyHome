package com.prokarma.myhome.features.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ContactUsFragment extends BaseFragment {
    public static final String CONTACT_TAG = "contact_tag";
    View contactUsView;

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        contactUsView = inflater.inflate(R.layout.contact_us, container, false);
        getActivity().setTitle(getString(R.string.contact_support));
        TextView emailView = (TextView) contactUsView.findViewById(R.id.email);
        String emailViewContentDescription = String.format(getString(R.string.email_content_description), getString(R.string.contact_us_email));
        emailView.setContentDescription(emailViewContentDescription);
        TextView phoneView = (TextView) contactUsView.findViewById(R.id.phone);
        String phoneViewContentDescription =  String.format(getString(R.string.phone_description), CommonUtil.constructPhoneNumber(getString(R.string.contact_us_phone)));
        phoneView.setContentDescription(phoneViewContentDescription);
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });
        phoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailPhone();
            }
        });

        phoneView.setText(CommonUtil.constructPhoneNumber(getString(R.string.contact_us_phone)));
        return contactUsView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.CONTACT_US;
    }


    /**
     * Create a support email
     */
    private void composeEmail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Constants.SUPPORT_EMAIL});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.ask_a_question));

        if (ProfileManager.getProfile() != null) {
            Timber.i("Have Profile information. Crafting Support email...");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.contact_us_email_body) + ",\n" +
                    ProfileManager.getProfile().firstName + " " + ProfileManager.getProfile().lastName + "\n" +
                    (ProfileManager.getProfile().phoneNumber != null ? CommonUtil.constructPhoneNumber(ProfileManager.getProfile().phoneNumber) : "") + "\n");
            startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_us_email_intent_header)));
        } else {
            Timber.i("Don't have any Profile information. Showing placeholder...");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    getString(R.string.contact_us_email_body));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_us_email_intent_header)));
        }
    }

    /**
     * Create a support phone dial
     */
    private void dailPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(Constants.TEL + CommonUtil.constructPhoneNumber(getString(R.string.contact_us_phone))));
        getActivity().startActivity(intent);
    }
}