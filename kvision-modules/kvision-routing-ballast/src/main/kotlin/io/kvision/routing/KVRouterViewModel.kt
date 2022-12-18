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

package io.kvision.routing

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.ExperimentalBallastApi
import com.copperleaf.ballast.build
import com.copperleaf.ballast.eventHandler
import com.copperleaf.ballast.navigation.browser.withBrowserHashRouter
import com.copperleaf.ballast.navigation.browser.withBrowserHistoryRouter
import com.copperleaf.ballast.navigation.vm.BasicRouter
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalBallastApi::class)
class KVRouterViewModel(
    routingTable: KVRoutingTable,
    initialRoute: KVRoute,
    basePath: String? = null,
    useHash: Boolean = true,
    coroutineScope: CoroutineScope
) : BasicRouter<KVRoute>(
    config = BallastViewModelConfiguration.Builder().apply {
        if (useHash) {
            withBrowserHashRouter(
                routingTable,
                initialRoute
            )
        } else {
            withBrowserHistoryRouter(
                routingTable,
                basePath,
                initialRoute
            )
        }
    }.build(),
    eventHandler = eventHandler { },
    coroutineScope = coroutineScope,
)
