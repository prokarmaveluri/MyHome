package com.prokarma.myhome.features.contact;

import android.content.Context;
import android.view.View;

/**
 * Created by kwelsh on 2/12/18.
 */

public class ContactUsPresenter implements ContactUsContract.Presentor {
    Context context;
    ContactUsView view;
    ContactUsInteractor interactor;
    ContactUsRouter router;

    public ContactUsPresenter(Context context, ContactUsFragment fragment, View masterView) {
        this.context = context;
        this.view = new ContactUsView(context, masterView, this);
        this.interactor = new ContactUsInteractor();
        this.router = new ContactUsRouter(fragment);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void onEmailClicked() {
        router.goToEmail();
    }

    @Override
    public void onPhoneClicked() {
        router.goToDialer();
    }
}
