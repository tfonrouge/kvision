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
package io.kvision.state

import io.github.fenrur.signal.MutableSignal
import io.github.fenrur.signal.Signal

/**
 * Extension property returning an ObservableState<S> for a Signal<S>.
 */
inline val <S>Signal<S>.observableState: ObservableState<S>
    get() = ObservableValue(this.value).apply {
        this@observableState.subscribe {
            it.onSuccess {
                this.value = it
            }
        }
    }

/**
 * Extension property returning a MutableState<S> for a MutableSignal<S>.
 */
inline val <S>MutableSignal<S>.mutableState: MutableState<S>
    get() = object : ObservableValue<S>(this.value) {
        init {
            this@mutableState.subscribe {
                it.onSuccess {
                    this.value = it
                }
            }
        }

        override fun setState(state: S) {
            super.setState(state)
            this@mutableState.value = state
        }
    }
