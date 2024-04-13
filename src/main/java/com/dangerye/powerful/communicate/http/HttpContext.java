package com.dangerye.powerful.communicate.http;

import com.dangerye.powerful.concurrent.InvokeVisitContext;
import com.google.common.collect.Maps;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.Args;

import java.util.Map;
import java.util.Objects;

public final class HttpContext implements InvokeVisitContext {
    private final String invokeSign;
    private final CloseableHttpClient httpClient;
    private final ResponseHandler<String> responseHandler;
    private final HttpUriRequest httpRequest;
    private Map<String, Object> paramMap;

    private HttpContext(String invokeSign,
                        CloseableHttpClient httpClient,
                        ResponseHandler<String> responseHandler,
                        HttpUriRequest httpRequest) {
        Args.notNull(httpRequest, "HTTP request");
        this.invokeSign = invokeSign;
        this.httpClient = httpClient;
        this.responseHandler = responseHandler;
        this.httpRequest = httpRequest;
    }

    public static HttpContextBuilder builder() {
        return new HttpContextBuilder();
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public ResponseHandler<String> getResponseHandler() {
        return responseHandler;
    }

    public HttpUriRequest getHttpRequest() {
        return httpRequest;
    }

    @Override
    public String getInvokeSign() {
        return invokeSign;
    }

    @Override
    public Map<String, Object> getParamMap() {
        if (paramMap != null) {
            return paramMap;
        }
        if (httpRequest == null) {
            paramMap = Maps.newHashMap();
            return paramMap;
        }
        paramMap = Maps.newHashMap();
        paramMap.put("method", Objects.toString(httpRequest.getMethod(), ""));
        paramMap.put("uri", Objects.toString(httpRequest.getURI(), ""));
        return paramMap;
    }

    public static class HttpContextBuilder {
        private String invokeSign;
        private CloseableHttpClient httpClient;
        private ResponseHandler<String> responseHandler;
        private HttpUriRequest httpRequest;

        private HttpContextBuilder() {
        }

        public HttpContextBuilder invokeSign(String invokeSign) {
            this.invokeSign = invokeSign;
            return this;
        }

        public HttpContextBuilder httpClient(CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public HttpContextBuilder responseHandler(ResponseHandler<String> responseHandler) {
            this.responseHandler = responseHandler;
            return this;
        }

        public HttpContextBuilder httpRequest(HttpUriRequest httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }

        public HttpContext build() {
            return new HttpContext(invokeSign,
                    httpClient,
                    responseHandler,
                    httpRequest);
        }
    }
}
