package com.prokarma.myhome.networking;

import com.prokarma.myhome.utils.CommonUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kwelsh on 11/17/17.
 * Tests for our utility class
 */

public class CommonUtilTest {

    @Test
    public void test1_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword(TestConstants.DEV_PASSWORD));
    }

    @Test
    public void test2_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword(TestConstants.PROD_PASSWORD));
    }

    @Test
    public void test3_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword("pAss123*"));
    }

    @Test
    public void test4_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword("Pass123|a"));
    }

    @Test
    public void test5_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword("Pass123|"));
    }

    @Test
    public void test6_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword("vyN@8yh^wI%g94P|Z^N"));
    }

    @Test
    public void test7_isValidPassword(){
        Assert.assertFalse(CommonUtil.isValidPassword(""));
    }

    @Test
    public void test8_isValidPassword(){
        Assert.assertFalse(CommonUtil.isValidPassword("abc"));
    }

    @Test
    public void test9_isValidPassword(){
        Assert.assertFalse(CommonUtil.isValidPassword("Pass123"));
    }

    @Test
    public void test10_isValidPassword(){
        Assert.assertFalse(CommonUtil.isValidPassword("pass123#"));
    }

    @Test
    public void test11_isValidPassword(){
        Assert.assertTrue(CommonUtil.isValidPassword("Pass123_"));
    }
}
