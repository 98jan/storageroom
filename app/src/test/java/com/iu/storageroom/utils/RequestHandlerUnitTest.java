package com.iu.storageroom.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.os.Build;

import com.iu.storageroom.model.ProductWrapper;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * JUnit Test, which tests the RequestHandler
 */
public class RequestHandlerUnitTest {
    @Test
    public void productFound() {
        ProductWrapper productWrapper = RequestHandler.getProduct("4337256505338");
        assertNotNull(productWrapper);
        assertNotNull(productWrapper.getProduct());
        assertEquals("REWE Beste Wahl", productWrapper.getProduct().getBrand());
    }

    @Test
    public void productNotFound() {
        ProductWrapper productWrapper = RequestHandler.getProduct("11833");
        assertNull(productWrapper);
    }

}