package com.prokarma.myhome.features.contact;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.utils.CommonUtil;

/**
 * Created by kwelsh on 2/12/18.
 */

public class ContactUsView {

    public ContactUsView(final Context context, final View masterView, final ContactUsPresenter presenter) {
        TextView emailView = (TextView) masterView.findViewById(R.id.email);
        String emailViewContentDescription = String.format(context.getString(R.string.email_content_description), context.getString(R.string.contact_us_email));
        emailView.setContentDescription(emailViewContentDescription);
        TextView phoneView = (TextView) masterView.findViewById(R.id.phone);

        String phoneViewContentDescription = CommonUtil.stringToSpacesString(CommonUtil.constructPhoneNumberDots(context.getString(R.string.contact_us_phone)));
        phoneView.setContentDescription(context.getString(R.string.phone_number_des) + phoneViewContentDescription + context.getString(R.string.phone_number_open_dialer));

        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEmailClicked();
            }
        });
        phoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPhoneClicked();
            }
        });

        phoneView.setText(CommonUtil.constructPhoneNumberDots(context.getString(R.string.contact_us_phone)));
    }
}
