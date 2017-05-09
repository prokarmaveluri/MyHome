package com.dignityhealth.myhome.features.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.utils.Constants;

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

        TextView emailView = (TextView) contactUsView.findViewById(R.id.email);
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });

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
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"hello@dignityhealth.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ask A Question");

        if (ProfileManager.getProfile() != null) {
            Timber.i("Have Profile information. Crafting Support email...");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "Hello, \n\n" +
                            "I was using the MyHome App and I had some questions. Can someone please contact me?\n\n" +
                            "Thank You,\n" +
                            ProfileManager.getProfile().firstName + " " + ProfileManager.getProfile().lastName + "\n" +
                            ProfileManager.getProfile().phoneNumber + "\n");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } else {
            Timber.i("Don't have any Profile information. Showing placeholder...");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "Hello, \n\n" +
                            "I was using the MyHome App and I had some questions. Can someone please contact me?\n\n" +
                            "Thank You,\n" +
                            "Kevin Welsh\n" +
                            "867-5309\n");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }
}