package com.dignityhealth.myhome.features.fad.details.booking;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingDialogAdapter extends PagerAdapter {
    private Context context;
    private static int NUM_ITEMS = 3;

    private ViewGroup insuranceLayout;
    private ViewGroup personalLayout;
    private ViewGroup reasonForVisitLayout;


    public BookingDialogAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (position) {
            case 0:
                insuranceLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_insurance, container, false);
                //setupInsuranceView
                container.addView(insuranceLayout, position);
                return insuranceLayout;

            case 1:
                personalLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_personal, container, false);
                //setupPersonalView
                container.addView(personalLayout, position);
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
}
