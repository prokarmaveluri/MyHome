package com.prokarma.myhome.features.contact;

import com.prokarma.myhome.app.BasePresenter;

/**
 * Created by kwelsh on 2/12/18.
 */

public class ContactUsContract {
    interface View {

    }

    interface Interactor {

    }

    interface Presentor extends BasePresenter {
        void onEmailClicked();
        void onPhoneClicked();
    }

    interface Router {
        void goToEmail();
        void goToDialer();
    }
}
