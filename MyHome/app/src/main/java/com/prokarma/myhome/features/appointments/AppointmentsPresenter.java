package com.prokarma.myhome.features.appointments;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.entities.MyAppointmentsResponse;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.ConnectionUtil;

import retrofit2.Response;

/**
 * Created by kwelsh on 2/19/18.
 */

public class AppointmentsPresenter implements AppointmentsContract.Presentor, AppointmentsContract.InteractorOutput {
    private Context context;
    private AppointmentsView view;
    private AppointmentsInteractor interactor;
    private AppointmentsRouter router;

    public AppointmentsPresenter(final Context context, BaseFragment fragment, View masterView, FragmentManager fragmentManager) {
        this.context = context;
        this.view = new AppointmentsView(context, masterView, this, fragmentManager);
        this.interactor = new AppointmentsInteractor(this);
        this.router = new AppointmentsRouter();
    }

    @Override
    public void onCreate() {
        if (ConnectionUtil.isConnected(context)) {
            interactor.getAppointments();
        } else {
            CommonUtil.showToast(context, context.getString(R.string.no_network_msg));
        }
    }

    @Override
    public void onDestroy() {
        context = null;
        view = null;
        interactor = null;
        router = null;
    }

    @Override
    public void receivedAppointmentsSuccess(Response<MyAppointmentsResponse> response) {
        view.onAppointmentsSuccess(response);
    }

    @Override
    public void receivedAppointmentsFailed(Response<MyAppointmentsResponse> response) {
        view.onAppointmentsFailed(response);
    }

    @Override
    public void receivedAppointmentsFailed(Throwable throwable) {
        view.onAppointmentsFailed(throwable);
    }
}
