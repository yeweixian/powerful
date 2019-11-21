package com.dangerye.powerful.communicate.http;

import com.dangerye.powerful.common.ExceptionHandler;
import com.dangerye.powerful.communicate.ThreadContext;
import com.dangerye.powerful.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public final class HttpInvoker {

    private static final ResponseHandler<String> defaultResponseHandler = httpResponse -> {
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    /**
     * http
     *
     * @param httpClient       use org.apache.http.impl.client.HttpClients to build httpClient. nullAble
     * @param httpRequest      use org.apache.http.client.methods.RequestBuilder to build request. notNull
     * @param exceptionHandler nullAble
     * @return http response body
     */
    public static String execute(CloseableHttpClient httpClient, HttpUriRequest httpRequest, ExceptionHandler exceptionHandler) {
        return execute(httpClient, httpRequest, null, exceptionHandler);
    }

    /**
     * http
     *
     * @param httpClient       use org.apache.http.impl.client.HttpClients to build httpClient. nullAble
     * @param httpRequest      use org.apache.http.client.methods.RequestBuilder to build request. notNull
     * @param responseHandler  nullAble
     * @param exceptionHandler nullAble
     * @return http response body
     */
    public static String execute(CloseableHttpClient httpClient, HttpUriRequest httpRequest,
                                 ResponseHandler<String> responseHandler, ExceptionHandler exceptionHandler) {
        Args.notNull(httpRequest, "HTTP request");
        ResponseHandler<String> handler = Optional.ofNullable(responseHandler).orElse(defaultResponseHandler);
        try (CloseableHttpClient client = Optional.ofNullable(httpClient).orElse(HttpClients.createDefault())) {
            String responseBody = client.execute(httpRequest, handler);
            if (StringUtils.isNotBlank(ThreadContext.getTraceId())) {
                LogUtils.info(log, "HttpInvoker execute event",
                        "URI:{}, method:{}, responseBody:{}",
                        Objects.toString(httpRequest.getURI(), ""),
                        Objects.toString(httpRequest.getMethod(), ""),
                        responseBody);
            }
            return responseBody;
        } catch (Exception e) {
            LogUtils.error(log, "HttpInvoker execute fail",
                    "URI:{}, method:{}, errorMsg:{}",
                    Objects.toString(httpRequest.getURI(), ""),
                    Objects.toString(httpRequest.getMethod(), ""),
                    e.getMessage(), e);
            if (exceptionHandler != null) {
                exceptionHandler.handleException(e);
            }
            return null;
        }
    }
}
