package com.dangerye.powerful.communicate.http;

import com.dangerye.powerful.builder.CollectionBuilder;
import com.dangerye.powerful.communicate.CallHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public final class HttpInvoker {

    private static final ResponseHandler<String> DEFAULTRESPONSEHANDLER = httpResponse -> {
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    private final HttpSupplier httpSupplier;
    private final Function<Consumer<Exception>, String> httpGetFunction;
    private final HttpThrowFunction httpThrowFunction;

    private HttpInvoker(CloseableHttpClient httpClient, HttpUriRequest httpRequest, ResponseHandler<String> responseHandler, boolean showInfoMsg) {
        this.httpSupplier = client -> {
            ResponseHandler<String> handler = Optional.ofNullable(responseHandler).orElse(DEFAULTRESPONSEHANDLER);
            return CallHandler.init(() -> client.execute(httpRequest, handler),
                    "HTTP_CALLED", CollectionBuilder.<String, Object>mapBuilder(new TreeMap<>())
                            .put("method", Objects.toString(httpRequest.getMethod(), ""))
                            .put("uri", Objects.toString(httpRequest.getURI(), ""))
                            .build(), showInfoMsg)
                    .getOrThrow(e -> e);
        };
        this.httpGetFunction = consumer -> {
            Args.notNull(httpRequest, "HTTP request");
            try (CloseableHttpClient client = Optional.ofNullable(httpClient).orElse(HttpClients.createDefault())) {
                return httpSupplier.get(client);
            } catch (Exception e) {
                if (consumer != null) {
                    consumer.accept(e);
                }
                return null;
            }
        };
        this.httpThrowFunction = new HttpThrowFunction() {
            @Override
            public <E extends Throwable> String apply(Function<Exception, ? extends E> function) throws E {
                Args.notNull(httpRequest, "HTTP request");
                try (CloseableHttpClient client = Optional.ofNullable(httpClient).orElse(HttpClients.createDefault())) {
                    return httpSupplier.get(client);
                } catch (Exception e) {
                    throw function.apply(e);
                }
            }
        };
    }

    public static HttpInvoker execute(HttpUriRequest httpRequest, boolean showInfoMsg) {
        return execute(null, httpRequest, null, showInfoMsg);
    }

    public static HttpInvoker execute(CloseableHttpClient httpClient, HttpUriRequest httpRequest, boolean showInfoMsg) {
        return execute(httpClient, httpRequest, null, showInfoMsg);
    }

    public static HttpInvoker execute(CloseableHttpClient httpClient, HttpUriRequest httpRequest,
                                      ResponseHandler<String> responseHandler, boolean showInfoMsg) {
        return new HttpInvoker(httpClient, httpRequest, responseHandler, showInfoMsg);
    }

    public String get() {
        return get(null);
    }

    public String get(Consumer<Exception> consumer) {
        return httpGetFunction.apply(consumer);
    }

    public <E extends Throwable> String getOrThrow(Function<Exception, ? extends E> function) throws E {
        return httpThrowFunction.apply(function);
    }

    public <E extends Throwable> String getElseThrow(Function<Exception, ? extends E> function) throws E {
        List<Exception> exceptionList = new ArrayList<>();
        return Optional.ofNullable(get(exceptionList::add))
                .orElseThrow(() -> {
                    Exception exception = exceptionList.size() > 0 ? exceptionList.get(0) : null;
                    return function.apply(exception);
                });
    }

    @FunctionalInterface
    private interface HttpSupplier {
        String get(CloseableHttpClient client) throws Exception;
    }

    @FunctionalInterface
    private interface HttpThrowFunction {
        <E extends Throwable> String apply(Function<Exception, ? extends E> function) throws E;
    }
}
