package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetails;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;
import com.prokarma.myhome.utils.EnviHandler;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 9/29/17.
 * Tests for DignityHealth APIs
 */

@RunWith(Parameterized.class)
public class ProvidersTest {
    public static final String APPOINTMENT_FROM_DATE = "11/01/2017";
    public static final String APPOINTMENT_TO_DATE = "11/30/2017";

    @Parameterized.Parameter
    public EnviHandler.EnvType environment;

    @Parameterized.Parameters
    public static Object[] environments() {
        return new Object[]{
                EnviHandler.EnvType.DEV,
                EnviHandler.EnvType.STAGE,
                EnviHandler.EnvType.PROD,
        };
    }

    @Rule
    public Timeout glocalTimeout = Timeout.seconds(20);

    @Test
    public void getProviders() {
        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                break;
        }

        getProviderList();
    }

    @Test
    public void getProviderDetails() {
        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                break;
        }

        List<ProviderDetailsResponse> list = getProviderList();
        getNewProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getProviderAppointments() {
        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                break;
        }

        List<ProviderDetailsResponse> list = getProviderList();
        getProviderAppointments(TestUtil.getOnlineProvider(list), APPOINTMENT_FROM_DATE, APPOINTMENT_TO_DATE, null);
    }

    @Test
    public void getSearchSuggestions() {
        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                break;
        }

        getSearchSuggestionList();
    }

    @Test
    public void getLocation() {
        switch (environment) {
            case DEV:
                TestUtil.setDevEnvironment();
                break;
            case STAGE:
                TestUtil.setStagingEnvironment();
                break;
            case PROD:
                TestUtil.setProdEnvironment();
                break;
        }

        getLocationList();
    }

    public LocationResponse getLocationList() {
        Call<LocationResponse> call = NetworkManager.getInstance().getLocation();

        try {
            Response<LocationResponse> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertFalse(response.body().getState().isEmpty());

            return response.body();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }

    public List<SearchSuggestionResponse> getSearchSuggestionList() {
        Call<List<SearchSuggestionResponse>> call = NetworkManager.getInstance().getSearchSuggestions(
                "Primary Care",
                "40.587509",
                "-122.392929",
                "",
                "");

        try {
            Response<List<SearchSuggestionResponse>> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertFalse(response.body().isEmpty());

            return response.body();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }

    public static List<ProviderDetailsResponse> getProviderList() {
        Call<ProvidersResponse> call = NetworkManager.getInstance().getProviders(
                "Primary Care",
                "40.587509",
                "-122.392929",
                "Redding, CA",
                "",
                "1",
                "25",
                "100",
                "",
                "",
                "",
                "",
                "",
                "",
                "");

        try {
            Response<ProvidersResponse> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertNotNull(response.body().getProviders());
            Assert.assertFalse(response.body().getProviders().isEmpty());

            return response.body().getProviders();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }

    public static ProviderDetails getNewProviderDetails(String npi) {
        Call<ProviderDetails> call = NetworkManager.getInstance().getNewProviderDetails(npi);

        try {
            Response<ProviderDetails> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertNotNull(response.body().getResult());
            Assert.assertFalse(response.body().getResult().isEmpty());
            Assert.assertNotNull(response.body().getResult().get(0).getNpi());
            Assert.assertNotNull(response.body().getResult().get(0).getLastName());

            return response.body();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }

    public static AppointmentTimeSlots getProviderAppointments(String npi, String fromDate, String toDate, String addressHash) {
        Call<AppointmentTimeSlots> call;

        if (addressHash != null && !addressHash.isEmpty()) {
            call = NetworkManager.getInstance().getProviderAppointments(npi, fromDate, toDate, addressHash);
        } else {
            call = NetworkManager.getInstance().getProviderAppointments(npi, fromDate, toDate);
        }

        try {
            Response<AppointmentTimeSlots> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertNotNull(response.body().getData());
            Assert.assertFalse(response.body().getData().isEmpty());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getProvider());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getService());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getFacility());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getLocation());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getAvailableTimes());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getAppointmentTypes());
            Assert.assertNotNull(response.body().getData().get(0).getAttributes().getNextAvailableTimes());

            return response.body();
        } catch (IOException e) {
            Assert.fail(e.toString());
            return null;
        }
    }
}
