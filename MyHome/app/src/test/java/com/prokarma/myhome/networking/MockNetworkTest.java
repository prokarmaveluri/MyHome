package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.fad.LocationResponse;
import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetails;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;
import com.prokarma.myhome.features.fad.suggestions.SearchSuggestionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.NetworkBehavior;

/**
 * Created by kwelsh on 1/22/18.
 * Tests for DignityHealth APIs
 */

public class MockNetworkTest {
    private final NetworkBehavior behavior = NetworkBehavior.create();

    @Before
    public void setup() {
        TestUtil.setMockEnvironment(behavior);
    }

    @Test
    public void getProviders_Dev() {
        givenNetworkFailurePercentIs(0);
        getProviderList();
    }

    @Test
    public void getProviderDetails_Dev() {
        List<ProviderDetailsResponse> list = getProviderList();
        getNewProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getSearchSuggestions_Dev() {
        TestUtil.setDevEnvironment();
        getSearchSuggestions();
    }

    @Test
    public void getLocation_Dev() {
        TestUtil.setDevEnvironment();
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

    private void givenNetworkFailurePercentIs(int failurePercent) {
        behavior.setDelay(0, TimeUnit.MILLISECONDS);
        behavior.setVariancePercent(0);
        behavior.setFailurePercent(failurePercent);
    }
}
