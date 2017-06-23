package com.prokarma.myhome.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by cmajji on 5/2/17.
 */

public class CommonUtilTest {

    //Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character !@#$%^&*

    // positive test cases

    @Test
    public void testP1IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123*"));
    }

    @Test
    public void testP2IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123!"));
    }

    @Test
    public void testP3IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123@"));
    }

    @Test
    public void testP4IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123#"));
    }

    @Test
    public void testP5IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123$"));
    }

    @Test
    public void testP6IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123%"));
    }
    @Test
    public void testP7IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123^"));
    }
    @Test
    public void testP8IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Password123&"));
    }

    @Test
    public void testP9IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("Passwo1&"));
    }

    @Test
    public void testP10IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("1&Passwo"));
    }

    @Test
    public void testP11IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("1&Passwo"));
    }
    @Test
    public void testP12IsValidPassword() throws Exception{
        assertTrue(CommonUtil.isValidPassword("1&aasswO"));
    }

    // negative test cases

    @Test
    public void testN1IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Password123"));
    }

    @Test
    public void testN2IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Password*"));
    }

    @Test
    public void testN3IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("password123*"));
    }

    @Test
    public void testN4IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("passwo1*"));
    }
    @Test
    public void testN5IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1?"));
    }
    @Test
    public void testN6IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1~"));
    }
    @Test
    public void testN7IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1"));
    }
    @Test
    public void testN8IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1)"));
    }
    @Test
    public void testN9IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1;"));
    }
    @Test
    public void testN10IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1\""));
    }
    @Test
    public void testN11IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1}"));
    }
    @Test
    public void testN12IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1+"));
    }
    @Test
    public void testN13IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1_"));
    }
    @Test
    public void testN14IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1-"));
    }
    @Test
    public void testN15IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Passwo1:"));
    }
    @Test
    public void testN16IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("P123456*"));
    }
    @Test
    public void testN17IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("p123456*"));
    }
    @Test
    public void testN18IsValidPassword() throws Exception{
        assertFalse(CommonUtil.isValidPassword("Pa123 456*"));
    }
}
