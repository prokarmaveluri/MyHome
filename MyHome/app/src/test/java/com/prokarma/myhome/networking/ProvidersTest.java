package com.prokarma.myhome.networking;

import com.prokarma.myhome.features.fad.ProvidersResponse;
import com.prokarma.myhome.features.fad.details.ProviderDetailsResponse;

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

    @Before
    public void setup() {

    }

    @Test
    public void getProviders_Dev(){
        TestUtil.setDevEnvironment();
        getProviderList();
    }

    @Test
    public void getProviders_Stage(){
        TestUtil.setStagingEnvironment();
        getProviderList();
    }

    @Test
    public void getProviders_Prod(){
        TestUtil.setProdEnvironment();
        getProviderList();
    }

    @Test
    public void getProviderDetails_Dev(){
        TestUtil.setDevEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getProviderDetails_Stage(){
        TestUtil.setStagingEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getProviderDetails(list.get(0).getNpi());
    }

    @Test
    public void getProviderDetails_Prod(){
        TestUtil.setProdEnvironment();
        List<ProviderDetailsResponse> list = getProviderList();
        getProviderDetails(list.get(0).getNpi());
    }

    public List<ProviderDetailsResponse> getProviderList() {
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

    public ProviderDetailsResponse getProviderDetails(String npi){
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
        } catch (IOException e){
            Assert.fail(e.toString());
            return null;
        }
    }
}
