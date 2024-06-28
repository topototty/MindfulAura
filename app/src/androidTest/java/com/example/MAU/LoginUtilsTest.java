package com.example.MAU;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginUtilsTest {

    @Test
    public void testAreFieldsValid_withValidFields() {
        assertTrue(LoginUtils.areFieldsValid("test@example.com", "password"));
    }

    @Test
    public void testAreFieldsValid_withEmptyEmail() {
        assertFalse(LoginUtils.areFieldsValid("", "password"));
    }

    @Test
    public void testAreFieldsValid_withEmptyPassword() {
        assertFalse(LoginUtils.areFieldsValid("test@example.com", ""));
    }

    @Test
    public void testAreFieldsValid_withSpacesOnlyEmail() {
        assertFalse(LoginUtils.areFieldsValid("    ", "password"));
    }

    @Test
    public void testAreFieldsValid_withSpacesOnlyPassword() {
        assertFalse(LoginUtils.areFieldsValid("test@example.com", "    "));
    }

    @Test
    public void testAreFieldsValid_withNullEmail() {
        assertFalse(LoginUtils.areFieldsValid(null, "password"));
    }

    @Test
    public void testAreFieldsValid_withNullPassword() {
        assertFalse(LoginUtils.areFieldsValid("test@example.com", null));
    }
}
