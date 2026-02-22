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

package io.kvision.jquery

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> JQueryPromiseCallback<T>.invoke(value: T? = null, vararg args: Any) {
    asDynamic()(value, args)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T, U> JQueryPromiseOperator<T, U>.invoke(
    callback1: JQueryPromiseCallback<T>,
    vararg callbacksN: JQueryPromiseCallback<Any>
): JQueryPromise<U> {
    return asDynamic()(callback1, callbacksN)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T, U> JQueryPromiseOperator<T, U>.invoke(
    callback1: JQueryPromiseCallback<T>,
    vararg callbacksN: Array<JQueryPromiseCallback<Any>>
): JQueryPromise<U> {
    return asDynamic()(callback1, callbacksN)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T, U> JQueryPromiseOperator<T, U>.invoke(
    callback1: Array<JQueryPromiseCallback<T>>,
    vararg callbacksN: JQueryPromiseCallback<Any>
): JQueryPromise<U> {
    return asDynamic()(callback1, callbacksN)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T, U> JQueryPromiseOperator<T, U>.invoke(
    callback1: Array<JQueryPromiseCallback<T>>,
    vararg callbacksN: Array<JQueryPromiseCallback<Any>>
): JQueryPromise<U> {
    return asDynamic()(callback1, callbacksN)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryParam.invoke(obj: Any): String {
    return asDynamic()(obj)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryParam.invoke(obj: Any, traditional: Boolean): String {
    return asDynamic()(obj, traditional)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryEventConstructor.invoke(name: String, eventProperties: Any? = null): JQueryEventObject {
    return asDynamic()(name, eventProperties)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryEasingFunction.invoke(percent: Number): Number {
    return asDynamic()(percent)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryEasingFunctions.get(name: String): JQueryEasingFunction? = asDynamic()[name]

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryEasingFunctions.set(name: String, value: JQueryEasingFunction) {
    asDynamic()[name] = value
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(selector: String, context: Element): JQuery {
    return asDynamic()(selector, context)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(selector: String, context: JQuery): JQuery {
    return asDynamic()(selector, context)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(element: Element): JQuery {
    return asDynamic()(element)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(elementArray: Array<Element>): JQuery {
    return asDynamic()(elementArray)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(noinline callback: (jQueryAlias: JQueryStatic? /*= null*/) -> Any?): JQuery {
    return asDynamic()(callback)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(`object`: Any?): JQuery {
    return asDynamic()(`object`)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(`object`: JQuery): JQuery {
    return asDynamic()(`object`)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(): JQuery {
    return asDynamic()()
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(html: String, ownerDocument: Document): JQuery {
    return asDynamic()(html, ownerDocument)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(html: String, attributes: Any): JQuery {
    return asDynamic()(html, attributes)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQueryStatic.invoke(selector: String): JQuery {
    return asDynamic()(selector)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQuery.get(index: String): Any? = asDynamic()[index]

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQuery.set(index: String, value: Any) {
    asDynamic()[index] = value
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQuery.get(index: Number): HTMLElement? = asDynamic()[index]

@Suppress("NOTHING_TO_INLINE")
inline operator fun JQuery.set(index: Number, value: HTMLElement) {
    asDynamic()[index] = value
}

object Factory {
    fun getInstance(): JQueryStatic = jQuery
}
