package com.iu.storageroom.utils;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iu.storageroom.model.Product;
import com.iu.storageroom.model.ProductWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {

    private static final String TAG = "RequestHandler";

    // constructor should be never called
    private RequestHandler() {}

    public interface RequestCallback {
        void onCallback(Product product);
    }

    public static ProductWrapper getProduct(String productId) {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Future<ProductWrapper> productResult = executorService.submit(() -> {
                ProductWrapper object;
                try {
                    URL url = new URL("https://world.openfoodfacts.net/api/v3/product/" + productId + "?cc=de&lc=de&tags_lc=de&fields=code%2Cname%2Cbrands%2Cfood_groups%2Ccategories_tags%2Cfood_groups_tags%2Cgeneric_name%2Cgeneric_name_de%2Cimage_front_url%2Cproduct_name%2Cproduct_name_de%2Cproduct_quantity%2Cquantity%2Cstores%2Cstores_tags");
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    InputStream result = httpsURLConnection.getInputStream();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(result, StandardCharsets.UTF_8))) {
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total.append(line);
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        object = objectMapper.readValue(String.valueOf(total), ProductWrapper.class);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return object;
            });
            try {
                return productResult.get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
                Thread.currentThread().interrupt();
            }
        return null;
    }

}
