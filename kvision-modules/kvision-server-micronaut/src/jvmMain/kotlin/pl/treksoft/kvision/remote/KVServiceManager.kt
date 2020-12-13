/*
 * Copyright (c) 2017-present Robert Jaros
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.treksoft.kvision.remote

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.websocket.WebSocketSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Multiplatform service manager for Micronaut.
 */
@Suppress("LargeClass", "TooManyFunctions", "BlockingMethodInNonBlockingContext")
actual open class KVServiceManager<T : Any> actual constructor(val serviceClass: KClass<T>) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(KVServiceManager::class.java.name)
    }

    val getRequests: MutableMap<String, suspend (HttpRequest<*>, ThreadLocal<HttpRequest<*>>, ApplicationContext) -> HttpResponse<String>> =
        mutableMapOf()
    val postRequests: MutableMap<String, suspend (HttpRequest<*>, ThreadLocal<HttpRequest<*>>, ApplicationContext) -> HttpResponse<String>> =
        mutableMapOf()
    val putRequests: MutableMap<String, suspend (HttpRequest<*>, ThreadLocal<HttpRequest<*>>, ApplicationContext) -> HttpResponse<String>> =
        mutableMapOf()
    val deleteRequests: MutableMap<String, suspend (HttpRequest<*>, ThreadLocal<HttpRequest<*>>, ApplicationContext) -> HttpResponse<String>> =
        mutableMapOf()
    val optionsRequests: MutableMap<String, suspend (HttpRequest<*>, ThreadLocal<HttpRequest<*>>, ApplicationContext) -> HttpResponse<String>> =
        mutableMapOf()
    val webSocketsRequests: MutableMap<String, suspend (
        WebSocketSession, ThreadLocal<WebSocketSession>, ApplicationContext, ReceiveChannel<String>, SendChannel<String>
    ) -> Unit> = mutableMapOf()

    val mapper = createDefaultObjectMapper()
    var counter: Int = 0

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified RET> bind(
        noinline function: suspend T.() -> RET,
        method: HttpMethod, route: String?
    ) {
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = if (method == HttpMethod.GET) {
                JsonRpcRequest(req.parameters["id"]?.toInt() ?: 0, "", listOf())
            } else {
                req.getBody(JsonRpcRequest::class.java).get()
            }
            try {
                val result = function.invoke(service)
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            result = mapper.writeValueAsString(result)
                        )
                    )
                )
            } catch (e: Exception) {
                if (e !is ServiceException) LOG.error(e.message, e)
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = e.message ?: "Error",
                            exceptionType = e.javaClass.canonicalName
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified PAR, reified RET> bind(
        noinline function: suspend T.(PAR) -> RET,
        method: HttpMethod, route: String?
    ) {
        if (method == HttpMethod.GET)
            throw UnsupportedOperationException("GET method is only supported for methods without parameters")
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            if (jsonRpcRequest.params.size == 1) {
                val param = getParameter<PAR>(jsonRpcRequest.params[0])
                try {
                    val result = function.invoke(service, param)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified PAR1, reified PAR2, reified RET> bind(
        noinline function: suspend T.(PAR1, PAR2) -> RET,
        method: HttpMethod, route: String?
    ) {
        if (method == HttpMethod.GET)
            throw UnsupportedOperationException("GET method is only supported for methods without parameters")
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            if (jsonRpcRequest.params.size == 2) {
                val param1 = getParameter<PAR1>(jsonRpcRequest.params[0])
                val param2 = getParameter<PAR2>(jsonRpcRequest.params[1])
                try {
                    val result = function.invoke(service, param1, param2)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified PAR1, reified PAR2, reified PAR3, reified RET> bind(
        noinline function: suspend T.(PAR1, PAR2, PAR3) -> RET,
        method: HttpMethod, route: String?
    ) {
        if (method == HttpMethod.GET)
            throw UnsupportedOperationException("GET method is only supported for methods without parameters")
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            @Suppress("MagicNumber")
            if (jsonRpcRequest.params.size == 3) {
                val param1 = getParameter<PAR1>(jsonRpcRequest.params[0])
                val param2 = getParameter<PAR2>(jsonRpcRequest.params[1])
                val param3 = getParameter<PAR3>(jsonRpcRequest.params[2])
                try {
                    val result = function.invoke(service, param1, param2, param3)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified PAR1, reified PAR2, reified PAR3, reified PAR4, reified RET> bind(
        noinline function: suspend T.(PAR1, PAR2, PAR3, PAR4) -> RET,
        method: HttpMethod, route: String?
    ) {
        if (method == HttpMethod.GET)
            throw UnsupportedOperationException("GET method is only supported for methods without parameters")
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            @Suppress("MagicNumber")
            if (jsonRpcRequest.params.size == 4) {
                val param1 = getParameter<PAR1>(jsonRpcRequest.params[0])
                val param2 = getParameter<PAR2>(jsonRpcRequest.params[1])
                val param3 = getParameter<PAR3>(jsonRpcRequest.params[2])
                val param4 = getParameter<PAR4>(jsonRpcRequest.params[3])
                try {
                    val result = function.invoke(service, param1, param2, param3, param4)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified PAR1, reified PAR2, reified PAR3,
            reified PAR4, reified PAR5, reified RET> bind(
        noinline function: suspend T.(PAR1, PAR2, PAR3, PAR4, PAR5) -> RET,
        method: HttpMethod, route: String?
    ) {
        if (method == HttpMethod.GET)
            throw UnsupportedOperationException("GET method is only supported for methods without parameters")
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            @Suppress("MagicNumber")
            if (jsonRpcRequest.params.size == 5) {
                val param1 = getParameter<PAR1>(jsonRpcRequest.params[0])
                val param2 = getParameter<PAR2>(jsonRpcRequest.params[1])
                val param3 = getParameter<PAR3>(jsonRpcRequest.params[2])
                val param4 = getParameter<PAR4>(jsonRpcRequest.params[3])
                val param5 = getParameter<PAR5>(jsonRpcRequest.params[4])
                try {
                    val result = function.invoke(service, param1, param2, param3, param4, param5)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given route with a function of the receiver.
     * @param function a function of the receiver
     * @param method a HTTP method
     * @param route a route
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified PAR1, reified PAR2, reified PAR3,
            reified PAR4, reified PAR5, reified PAR6, reified RET> bind(
        noinline function: suspend T.(PAR1, PAR2, PAR3, PAR4, PAR5, PAR6) -> RET,
        method: HttpMethod, route: String?
    ) {
        if (method == HttpMethod.GET)
            throw UnsupportedOperationException("GET method is only supported for methods without parameters")
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(method, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            @Suppress("MagicNumber")
            if (jsonRpcRequest.params.size == 6) {
                val param1 = getParameter<PAR1>(jsonRpcRequest.params[0])
                val param2 = getParameter<PAR2>(jsonRpcRequest.params[1])
                val param3 = getParameter<PAR3>(jsonRpcRequest.params[2])
                val param4 = getParameter<PAR4>(jsonRpcRequest.params[3])
                val param5 = getParameter<PAR5>(jsonRpcRequest.params[4])
                val param6 = getParameter<PAR6>(jsonRpcRequest.params[5])
                try {
                    val result = function.invoke(service, param1, param2, param3, param4, param5, param6)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * Binds a given web socket connetion with a function of the receiver.
     * @param function a function of the receiver
     * @param route a route
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    protected actual inline fun <reified PAR1 : Any, reified PAR2 : Any> bind(
        noinline function: suspend T.(ReceiveChannel<PAR1>, SendChannel<PAR2>) -> Unit,
        route: String?
    ) {
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        webSocketsRequests[routeDef] = { webSocketSession, tlWsSession, ctx, incoming, outgoing ->
            tlWsSession.set(webSocketSession)
            val service = ctx.getBean(serviceClass.java)
            tlWsSession.remove()
            val requestChannel = Channel<PAR1>()
            val responseChannel = Channel<PAR2>()
            coroutineScope {
                launch {
                    for (p in incoming) {
                        val jsonRpcRequest = getParameter<JsonRpcRequest>(p)
                        if (jsonRpcRequest.params.size == 1) {
                            val par = getParameter<PAR1>(jsonRpcRequest.params[0])
                            requestChannel.send(par)
                        }
                    }
                    requestChannel.close()
                }
                launch {
                    for (p in responseChannel) {
                        val text = mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = 0,
                                result = mapper.writeValueAsString(p)
                            )
                        )
                        outgoing.send(text)
                    }
                    if (!incoming.isClosedForReceive) incoming.cancel()
                }
                launch {
                    function.invoke(service, requestChannel, responseChannel)
                    if (!responseChannel.isClosedForReceive) responseChannel.close()
                }
            }
        }
    }

    /**
     * Binds a given function of the receiver as a tabulator component source
     * @param function a function of the receiver
     */
    @Suppress("TooGenericExceptionCaught")
    protected actual inline fun <reified RET> bindTabulatorRemote(
        noinline function: suspend T.(Int?, Int?, List<RemoteFilter>?, List<RemoteSorter>?, String?) -> RemoteData<RET>,
        route: String?
    ) {
        val routeDef = route ?: "route${this::class.simpleName}${counter++}"
        addRoute(HttpMethod.POST, "/kv/$routeDef") { req, tlReq, ctx ->
            tlReq.set(req)
            val service = ctx.getBean(serviceClass.java)
            tlReq.remove()
            val jsonRpcRequest = req.getBody(JsonRpcRequest::class.java).get()
            @Suppress("MagicNumber")
            if (jsonRpcRequest.params.size == 5) {
                val param1 = getParameter<Int?>(jsonRpcRequest.params[0])
                val param2 = getParameter<Int?>(jsonRpcRequest.params[1])
                val param3 = getParameter<List<RemoteFilter>?>(jsonRpcRequest.params[2])

                @Suppress("MagicNumber")
                val param4 = getParameter<List<RemoteSorter>?>(jsonRpcRequest.params[3])

                @Suppress("MagicNumber")
                val param5 = getParameter<String?>(jsonRpcRequest.params[4])
                try {
                    val result = function.invoke(service, param1, param2, param3, param4, param5)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                result = mapper.writeValueAsString(result)
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (e !is ServiceException) LOG.error(e.message, e)
                    HttpResponse.ok(
                        mapper.writeValueAsString(
                            JsonRpcResponse(
                                id = jsonRpcRequest.id,
                                error = e.message ?: "Error",
                                exceptionType = e.javaClass.canonicalName
                            )
                        )
                    )
                }
            } else {
                HttpResponse.ok(
                    mapper.writeValueAsString(
                        JsonRpcResponse(
                            id = jsonRpcRequest.id,
                            error = "Invalid parameters"
                        )
                    )
                )
            }
        }
    }

    /**
     * @suppress internal function
     */
    fun addRoute(
        method: HttpMethod,
        path: String,
        handler: suspend (HttpRequest<*>, ThreadLocal<HttpRequest<*>>, ApplicationContext) -> HttpResponse<String>
    ) {
        when (method) {
            HttpMethod.GET -> getRequests[path] = handler
            HttpMethod.POST -> postRequests[path] = handler
            HttpMethod.PUT -> putRequests[path] = handler
            HttpMethod.DELETE -> deleteRequests[path] = handler
            HttpMethod.OPTIONS -> optionsRequests[path] = handler
        }
    }

    /**
     * @suppress internal function
     */
    protected inline fun <reified T> getParameter(str: String?): T {
        return str?.let {
            if (T::class == String::class) {
                str as T
            } else {
                mapper.readValue(str)
            }
        } ?: null as T
    }
}
