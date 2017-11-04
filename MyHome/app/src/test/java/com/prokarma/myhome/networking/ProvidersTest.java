package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetails;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.details.booking.req.scheduling.times.AppointmentTimeSlots;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by kwelsh on 9/29/17.
 * Tests for DignityHealth APIs
 */

public class ProvidersTest {
    public static final String APPOINTMENT_FROM_DATE = "11/01/2017";
    public static final String APPOINTMENT_TO_DATE = "11/30/2017";

    @Before
    public void setup() {

    }

    @Test
    public void getProviders_Dev() {
        TestUtil.setDevEnvironment();
        getProviderList();
    }

    @Test
    public void getProviders_Stage() {
        TestUtil.setStagingEnvironment();
        getProviderList();
    }

    @Test
    public void getProviders_Prod() {
        TestUtil.setProdEnvironment();
        getProviderList();
    }

    @Test
    public void getProviderDetails_Dev() {
        TestUtil.setDevEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getNewProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getProviderDetails_Stage() {
        TestUtil.setStagingEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getNewProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getProviderDetails_Prod() {
        TestUtil.setProdEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getNewProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getProviderAppointments_Dev() {
        TestUtil.setDevEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getProviderAppointments(TestUtil.getOnlineProvider(list), APPOINTMENT_FROM_DATE, APPOINTMENT_TO_DATE, null);
    }

    @Test
    public void getProviderAppointments_Stage() {
        TestUtil.setStagingEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getProviderAppointments(TestUtil.getOnlineProvider(list), APPOINTMENT_FROM_DATE, APPOINTMENT_TO_DATE, null);
    }

    @Test
    public void getProviderAppointments_Prod() {
        TestUtil.setProdEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getProviderAppointments(TestUtil.getOnlineProvider(list), APPOINTMENT_FROM_DATE, APPOINTMENT_TO_DATE, null);
    }

    @Test
    public void getSearchSuggestions_Dev() {
        TestUtil.setStagingEnvironment();
        getSearchSuggestions();
    }

    @Test
    public void getSearchSuggestions_Stage() {
        TestUtil.setStagingEnvironment();
        getSearchSuggestions();
    }

    @Test
    public void getSearchSuggestions_Prod() {
        TestUtil.setProdEnvironment();
        getSearchSuggestions();
    }

    @Test
    public void getLocation_Dev() {
        TestUtil.setStagingEnvironment();
        getLocation();
    }

    @Test
    public void getLocation_Stage() {
        TestUtil.setStagingEnvironment();
        getLocation();
    }

    @Test
    public void getLocation_Prod() {
        TestUtil.setProdEnvironment();
        getLocation();
    }

    public LocationResponse getLocation() {
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

    public List<SearchSuggestionResponse> getSearchSuggestions() {
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

    public static ProviderDetailsResponse getProviderDetails(String npi) {
        Call<ProviderDetailsResponse> call = NetworkManager.getInstance().getProviderDetails(npi);

        try {
            Response<ProviderDetailsResponse> response = call.execute();

            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccessful());
            Assert.assertNotNull(response.body());
            Assert.assertNotNull(response.body().Npi);
            Assert.assertFalse(response.body().Npi.isEmpty());
            Assert.assertNotNull(response.body().LastName);
            Assert.assertFalse(response.body().LastName.isEmpty());

            return response.body();
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
