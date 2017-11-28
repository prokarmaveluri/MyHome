/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.visit.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.prokarma.myhome.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View used to display {@link Pharmacy} data.
 */
public class PharmacyView extends LinearLayout {
    @BindView(R.id.pharmacy_name)
    TextView nameView;
    @BindView(R.id.pharmacy_address1)
    TextView address1View;
    @BindView(R.id.pharmacy_address2)
    TextView address2View;
    @BindView(R.id.pharmacy_city_state_zip)
    TextView regionView;
    @BindView(R.id.pharmacy_phone)
    TextView phoneView;
    @BindView(R.id.pharmacy_fax)
    TextView faxView;
    @BindView(R.id.pharmacy_phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.pharmacy_fax_layout)
    LinearLayout faxLayout;

    public PharmacyView(Context context) {
        super(context, null);
        init(context);
    }

    public PharmacyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_pharmacy, this, true);

        ButterKnife.bind(this);
    }

    public void setPharmacy(Pharmacy pharmacy) {
        setName(pharmacy.getName());
        Address address = pharmacy.getAddress();
        setAddress1(address.getAddress1());
        setAddress2(address.getAddress2());
        setRegion(address.getCity(), address.getState().getName(), address.getZipCode());
        setPhone(pharmacy.getPhone());
        setFax(pharmacy.getFax());
    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void setAddress1(String address1) {
        address1View.setText(address1);
    }

    public void setAddress2(String address2) {
        address2View.setText(address2);
        address2View.setVisibility(TextUtils.isEmpty(address2) ? GONE : VISIBLE);
    }

    public void setRegion(String city, String state, String zip) {
        String text = String.format(getResources().getString(R.string.visit_reports_pharmacy_region),
                city, state, zip);
        regionView.setText(text);
    }

    public void setPhone(String phone) {
        phoneView.setText(phone);
        phoneLayout.setVisibility(TextUtils.isEmpty(phone) ? GONE : VISIBLE);
    }

    public void setFax(String fax) {
        faxView.setText(fax);
        faxLayout.setVisibility(TextUtils.isEmpty(fax) ? GONE : VISIBLE);
    }
}
