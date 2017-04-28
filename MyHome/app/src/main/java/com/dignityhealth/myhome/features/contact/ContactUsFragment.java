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
import com.dignityhealth.myhome.utils.Constants;

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
        Intent emailintent = new Intent(android.content.Intent.ACTION_SEND);
        emailintent.setType("plain/text");
        emailintent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"hello@dignityhealth.com"});
        emailintent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Customer Support");
        emailintent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Kevin Welsh\n" +
                        "kwelsh@prokarma.com\n" +
                        "Male, 30\n" +
                        "Android Developer & overall awesome guy\n\n" +
                        "How May We Help You?\n");
        startActivity(Intent.createChooser(emailintent, "Send mail..."));
    }
}