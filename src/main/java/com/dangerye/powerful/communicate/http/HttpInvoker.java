package com.dangerye.powerful.communicate.http;

import com.dangerye.powerful.communicate.AbstractInvoker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpInvoker extends AbstractInvoker<String, Exception, HttpInvoker.HttpContext> {}
//public final class HttpInvoker extends CallableUtils<String, Exception> {
//
//    private HttpInvoker(final HttpContext httpContext) {
//        super();
//        Args.notNull(httpContext.getHttpRequest(), "HTTP request");
//        final Callable<String> callable = () -> {
//            try (CloseableHttpClient client = Optional.ofNullable(httpContext.getHttpClient()).orElseGet(HTTP_CLIENT_SUPPLIER)) {
//                ResponseHandler<String> handler = Optional.ofNullable(httpContext.getResponseHandler()).orElse(DEFAULTRESPONSEHANDLER);
//                return client.execute(httpContext.getHttpRequest(), handler);
//            }
//        };
//        final Callable<String> proxy = ProxyUtils.getCallable(callable, Lists.newArrayList(HTTP_PROLOG_INTERCEPTOR), httpContext);
//        init(proxy, Function.identity());
//    }
//
//    private static final Supplier<CloseableHttpClient> HTTP_CLIENT_SUPPLIER = () -> {
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectionRequestTimeout(1000)
//                .setSocketTimeout(5000)
//                .setConnectTimeout(5000)
//                .build();
//        return HttpClientBuilder.create()
//                .setDefaultRequestConfig(requestConfig)
//                .build();
//    };
//
//    private static final ResponseHandler<String> DEFAULTRESPONSEHANDLER = httpResponse -> {
//        int status = httpResponse.getStatusLine().getStatusCode();
//        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
//            HttpEntity entity = httpResponse.getEntity();
//            return entity != null ? EntityUtils.toString(entity) : null;
//        } else {
//            throw new ClientProtocolException("Unexpected response status: " + status);
//        }
//    };
//
//    private static final ProxyUtils.Interceptor HTTP_PROLOG_INTERCEPTOR = new ProxyUtils.Interceptor() {
//        @Override
//        protected <R> R intercept(ProxyUtils.Invocation<R> invocation) throws Exception {
//            try {
//                return invocation.proceed();
//            } catch (Exception e) {
//                LogUtils.warn(log, "httpCallFail",
//                        Objects.toString(invocation.getContext().getSupplier(), "") + " param:{}",
//                        JSON.toJSONString(invocation.getContext().getParamMap(), SerializerFeature.DisableCircularReferenceDetect), e);
//                throw e;
//            }
//        }
//    };
//
//    public static HttpInvoker execute(final HttpContext httpContext) {
//        return new HttpInvoker(httpContext);
//    }
//
//    @Data
//    @Builder
//    public static class HttpContext implements ProxyUtils.Context {
//        private CloseableHttpClient httpClient;
//        private HttpUriRequest httpRequest;
//        private ResponseHandler<String> responseHandler;
//        private String supplier;
//        private Map<String, Object> paramMap;
//    }
//}
