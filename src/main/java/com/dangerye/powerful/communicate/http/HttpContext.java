package com.dangerye.powerful.communicate.http;

import com.dangerye.powerful.communicate.Invoker;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Data;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public final class HttpContext implements Invoker.Context {
    private String supplier;
    private CloseableHttpClient httpClient;
    private ResponseHandler<String> responseHandler;
    private HttpUriRequest httpRequest;

    @Override
    public String getSupplier() {
        return supplier;
    }

    @Override
    public Map<String, Object> getParamMap() {
        if (httpRequest != null) {
            return ImmutableMap.<String, Object>builder()
                    .put("method", Objects.toString(httpRequest.getMethod(), ""))
                    .put("uri", Objects.toString(httpRequest.getURI(), ""))
                    .build();
        }
        return Collections.emptyMap();
    }
}
