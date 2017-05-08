package com.dignityhealth.myhome.features.contact;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseActivity;
import com.dignityhealth.myhome.databinding.ActivityContactUsBinding;


/*
 * Activity to login.
 *
 * Created by cmajji on 05/08/17.
 */
public class ContactUsActivity extends BaseActivity {


    private ActivityContactUsBinding binding;

    /*
     * Get an intent for contact us activity.
     */
    public static Intent getContactUsIntent(Context context) {

        return new Intent(context, ContactUsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);

        ContactUsFragment fragment = ContactUsFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(binding.contactUsFrame.getId(), fragment).commit();

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbarWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}