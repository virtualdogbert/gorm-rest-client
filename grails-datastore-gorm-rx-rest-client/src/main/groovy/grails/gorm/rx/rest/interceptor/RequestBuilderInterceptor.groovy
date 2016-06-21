package grails.gorm.rx.rest.interceptor

import grails.gorm.rx.RxEntity
import grails.http.client.builder.HttpClientRequestBuilder
import io.reactivex.netty.protocol.http.client.HttpClientRequest
import org.grails.datastore.rx.rest.RestEndpointPersistentEntity

/**
 * Allows creating interceptors via builder syntax. Implementors should implement to the "build" method and call "buildRequest" providing the closure
 *
 *
 * @author Graeme Rocher
 * @since 6.0
 */
abstract class RequestBuilderInterceptor implements RequestInterceptor {

    @Override
    final HttpClientRequest intercept(RestEndpointPersistentEntity entity, RxEntity instance, HttpClientRequest request) {
        def builder = new HttpClientRequestBuilder(request, entity.charset)

        def callable = build(entity, instance, request)
        callable.setDelegate(builder)
        callable.call()
        return builder.request
    }

    /**
     * The build method should be implemented by calling the buildRequest method passing in the closure that uses {@link HttpClientRequestBuilder} to alter the request
     *
     * @param entity The entity
     * @param instance The instance (null if a static call)
     * @param request The request
     *
     * @return The closure the performs the building
     */
    abstract Closure build(RestEndpointPersistentEntity entity, RxEntity instance, HttpClientRequest request)

    protected Closure buildRequest(@DelegatesTo(HttpClientRequestBuilder) Closure callable) {
        return callable
    }
}