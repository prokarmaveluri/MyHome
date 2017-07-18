package com.prokarma.myhome.crypto;

import android.content.Context;
import android.util.Base64;

import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.AppPreferences;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by cmajji on 7/17/17.
 */

public class CryptoManager {

    private Encryptor encryptor;
    private Decryptor decryptor;
    private Context context;
    private static CryptoManager Instance = null;

    private CryptoManager() {

        encryptor = new Encryptor();
        try {
            decryptor = new Decryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }
    }

    public static CryptoManager getInstance() {
        if (null == Instance) {
            Instance = new CryptoManager();
        }
        return Instance;
    }


    public Encryptor getEncryptor() {
        return encryptor;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public Decryptor getDecryptor() {
        return decryptor;
    }

    public void setDecryptor(Decryptor decryptor) {
        this.decryptor = decryptor;
    }


    public String getToken() {
        String tokenPref = AppPreferences.getInstance().getPreference("auth_token");
        String tokenIvString = AppPreferences.getInstance().getPreference("auth_token_iv");
        if (null != tokenPref && null != tokenIvString) {
            byte[] tokenBytes = Base64.decode(tokenPref, Base64.DEFAULT);
            byte[] tokenIv = Base64.decode(tokenIvString, Base64.DEFAULT);
            if (tokenBytes != null) {
                return decryptText(tokenBytes, tokenIv);
            }
        }
        return null;
    }

    public void saveToken() {
        if (AuthManager.getInstance().getRefreshToken() != null) {
            try {
                encryptText(AuthManager.getInstance().getRefreshToken());
                String tokenPref = Base64.encodeToString(encryptor.getEncryption(), Base64.DEFAULT);
                AppPreferences.getInstance().setPreference("auth_token", tokenPref);

                String tokenIv = Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT);
                AppPreferences.getInstance().setPreference("auth_token_iv", tokenIv);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void encryptText(String text) {
        try {
            encryptor.encryptText(text, context);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private String decryptText(byte[] encryptedData, byte[] iv) {
        try {
            return decryptor.decryptData(encryptedData, iv);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            return null;
        } catch (InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
