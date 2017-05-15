package com.dignityhealth.myhome.networking;

import com.dignityhealth.myhome.features.fad.ProvidersResponse;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by cmajji on 5/12/17.
 */

public class ProvidersAPITest {

    @Before
    public void setup(){
        NetworkManager.getInstance().initService();
    }


    @Test
    public void TestProvidersAPI(){
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if(response.isSuccessful()){
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });

        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void Test2ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test3ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void Test4ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test5ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test6ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test7ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test8ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test9ProvidersAPI() {
        final CountDownLatch lock = new CountDownLatch(1);
        final long prevtime = System.currentTimeMillis();
        NetworkManager.getInstance().getProviders("Gy", "", "", "CA", "").enqueue(new Callback<ProvidersResponse>() {
            @Override
            public void onResponse(Call<ProvidersResponse> call, Response<ProvidersResponse> response) {
                if (response.isSuccessful()) {
                    Timber.i("End "+(System.currentTimeMillis() - prevtime)/1000);
                    System.out.println("time ** "+ (System.currentTimeMillis() - prevtime)/1000);
                    lock.countDown();
                }
            }

            @Override
            public void onFailure(Call<ProvidersResponse> call, Throwable t) {

            }
        });
        try {
            lock.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
