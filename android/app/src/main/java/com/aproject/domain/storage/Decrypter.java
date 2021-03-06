/*
 *
 * Copyright (C)  2017 HiteshSahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 *
 */

package com.aproject.domain.storage;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.aproject.utils.PreferenceHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * _____        _____                  _
 * |  __ \      / ____|                | |
 * | |  | | ___| |     _ __ _   _ _ __ | |_ ___  _ __
 * | |  | |/ _ \ |    | '__| | | | '_ \| __/ _ \| '__|
 * | |__| |  __/ |____| |  | |_| | |_) | || (_) | |
 * |_____/ \___|\_____|_|   \__, | .__/ \__\___/|_|
 * __/ | |
 * |___/|_|
 */
public final class Decrypter {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;

    public Decrypter() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    public String decrypt(final String alias, final byte[] encryptedData, Context appContext)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        String IV = PreferenceHelper.getPrefernceHelperInstace().getString(appContext, "IV", "");

        if (null != IV && !IV.isEmpty()) {
            byte[] encryptionIv = Base64.decode(IV, Base64.DEFAULT);
            Log.e("Decrypter", "IV : " + IV + " IV size " + encryptionIv.length);
            final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);
            return new String(cipher.doFinal(encryptedData), "UTF-8");
        } else {
            return "{}";
        }
    }

    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
} 