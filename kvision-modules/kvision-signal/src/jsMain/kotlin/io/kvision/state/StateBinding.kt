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
import io.github.fenrur.signal.operators.map
import io.github.petertrr.diffutils.algorithm.myers.MyersDiff
import io.github.petertrr.diffutils.diff
import io.github.petertrr.diffutils.patch.ChangeDelta
import io.github.petertrr.diffutils.patch.DeleteDelta
import io.github.petertrr.diffutils.patch.EqualDelta
import io.github.petertrr.diffutils.patch.InsertDelta
import io.kvision.core.Component
import io.kvision.core.Container
import io.kvision.core.Display
import io.kvision.form.GenericFormComponent
import io.kvision.panel.SimplePanel
import io.kvision.panel.simplePanel
import kotlin.js.Date

/**
 * An extension function which binds the widget to the given signal.
 *
 * @param S the state type
 * @param W the widget type
 * @param signal the Signal instance
 * @param removeChildren remove all children of the component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, W : Component> W.bind(
    signal: Signal<S>,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: (W.(S) -> Unit)
): W {
    var skip = !runImmediately
    this.addBeforeDisposeHook(signal.subscribe {
        it.onSuccess {
            if (!skip) {
                this.singleRenderAsync {
                    if (removeChildren) (this as? Container)?.disposeAll()
                    factory(it)
                }
            } else {
                skip = false
            }
        }
    })
    return this
}

/**
 * An extension function which binds the widget to the given signal using the substate extractor.
 *
 * @param S the state type
 * @param T the substate type
 * @param W the widget type
 * @param signal the Signal instance
 * @param sub an extractor function for substate
 * @param removeChildren remove all children of the component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, T, W : Component> W.bind(
    signal: Signal<S>,
    sub: (S) -> T,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: (W.(T) -> Unit)
): W {
    return this.bind(
        signal.map(sub),
        removeChildren,
        runImmediately,
        factory
    )
}

/**
 * An extension function which inserts child component and binds it to the observable state
 * when the given condition is true.
 *
 * @param S the state type
 * @param W the container type
 * @param signal the Signal instance
 * @param removeChildren remove all children of the child component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, W : SimplePanel> W.insertWhen(
    signal: Signal<S>,
    condition: (S) -> Boolean,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: SimplePanel.(S) -> Unit
): SimplePanel {
    return simplePanel {
        display = Display.CONTENTS
    }.bind(signal, removeChildren, runImmediately) { state ->
        if (condition(state)) {
            factory(state)
            this.show()
        } else {
            this.hide()
        }
    }
}

/**
 * An extension function which inserts child component and binds it to the given signal using the substate extractor
 * when the given condition is true.
 *
 * @param S the state type
 * @param T the substate type
 * @param W the container type
 * @param signal the Signal instance
 * @param sub an extractor function for substate
 * @param removeChildren remove all children of the child component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, T, W : SimplePanel> W.insertWhen(
    signal: Signal<S>,
    sub: (S) -> T,
    condition: (T) -> Boolean,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: SimplePanel.(T) -> Unit
): SimplePanel {
    val simplePanel = simplePanel {
        display = Display.CONTENTS
    }
    simplePanel.bind(signal.map(sub), removeChildren, runImmediately) { state ->
        if (condition(state)) {
            factory(state)
            this.show()
        } else {
            this.hide()
        }
    }
    return simplePanel
}

/**
 * An extension function which inserts child component and binds it to the given signal
 * when the state value is not null.
 *
 * @param S the state type
 * @param W the container type
 * @param signal the Signal instance
 * @param removeChildren remove all children of the child component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, W : SimplePanel> W.insertNotNull(
    signal: Signal<S?>,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: SimplePanel.(S) -> Unit
): SimplePanel {
    return insertWhen(signal, { it != null }, removeChildren, runImmediately) {
        factory(it!!)
    }
}

/**
 * An extension function which inserts child component and binds it to the given signal using the substate extractor
 * when the state value is not null.
 *
 * @param S the state type
 * @param T the substate type
 * @param W the container type
 * @param signal the Signal instance
 * @param sub an extractor function for substate
 * @param removeChildren remove all children of the child component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, T, W : SimplePanel> W.insertNotNull(
    signal: Signal<S>,
    sub: (S) -> T?,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: SimplePanel.(T) -> Unit
): SimplePanel {
    return insertWhen(signal, sub, { it != null }, removeChildren, runImmediately) {
        factory(it!!)
    }
}

/**
 * An extension function which inserts child component and binds it to the given signal.
 *
 * @param S the state type
 * @param W the container type
 * @param signal the Signal instance
 * @param removeChildren remove all children of the child component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, W : SimplePanel> W.insert(
    signal: Signal<S>,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: SimplePanel.(S) -> Unit
): SimplePanel {
    return insertWhen(signal, { true }, removeChildren, runImmediately, factory)
}

/**
 * An extension function which inserts child component and binds it to the given signal using the substate extractor.
 *
 * @param S the state type
 * @param T the substate type
 * @param W the container type
 * @param signal the Signal instance
 * @param sub an extractor function for substate
 * @param removeChildren remove all children of the child component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, T, W : SimplePanel> W.insert(
    signal: Signal<S>,
    sub: (S) -> T,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: SimplePanel.(T) -> Unit
): SimplePanel {
    return insertWhen(signal, sub, { true }, removeChildren, runImmediately, factory)
}

/**
 * An extension function which binds the widget to the given signal synchronously.
 * It's less efficient than [bind], but fully compatible with KVision 4 state bindings.
 *
 * @param S the state type
 * @param W the widget type
 * @param signal the Signal instance
 * @param removeChildren remove all children of the component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, W : Component> W.bindSync(
    signal: Signal<S>,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: (W.(S) -> Unit)
): W {
    var skip = !runImmediately
    this.addBeforeDisposeHook(signal.subscribe {
        it.onSuccess {
            if (!skip) {
                this.singleRender {
                    if (removeChildren) (this as? Container)?.disposeAll()
                    factory(it)
                }
            } else {
                skip = false
            }
        }
    })
    return this
}

/**
 * An extension function which binds the widget to the given signal synchronously using the substate extractor.
 * It's less efficient than [bind], but fully compatible with KVision 4 state bindings.
 *
 * @param S the state type
 * @param T the substate type
 * @param W the widget type
 * @param signal the Signal instance
 * @param sub an extractor function for substate
 * @param removeChildren remove all children of the component
 * @param runImmediately whether to run factory function immediately with the current state
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, T, W : Component> W.bindSync(
    signal: Signal<S>,
    sub: (S) -> T,
    removeChildren: Boolean = true,
    runImmediately: Boolean = true,
    factory: (W.(T) -> Unit)
): W {
    return this.bindSync(
        signal.map(sub),
        removeChildren,
        runImmediately,
        factory
    )
}

/**
 * An extension function which binds the container to the given signal with a list of items.
 *
 * @param S the state type
 * @param W the container type
 * @param signal the Signal instance
 * @param equalizer optional custom equalizer function
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, W : SimplePanel> W.bindEach(
    signal: Signal<List<S>>,
    equalizer: ((S, S) -> Boolean)? = null,
    factory: (W.(S) -> Unit)
): W {
    fun addSingleComponent(state: S) {
        val previousChildrenSize = this.getChildren().size
        factory(state)
        val newChildrenSize = this.getChildren().size
        val newChildren = this.getChildren()
        if (newChildrenSize != previousChildrenSize + 1) {
            simplePanel {
                display = Display.CONTENTS
                for (i in newChildrenSize - 1 downTo previousChildrenSize) {
                    val child = newChildren[i]
                    this@bindEach.removeAt(i)
                    add(0, child)
                }
            }
        }
    }

    fun getSingleComponent(state: S): Component {
        val previousChildrenSize = this.getChildren().size
        factory(state)
        val newChildrenSize = this.getChildren().size
        val newChildren = this.getChildren()
        return if (newChildrenSize == previousChildrenSize + 1) {
            val child = newChildren.last()
            removeAt(newChildrenSize - 1)
            child
        } else {
            SimplePanel {
                display = Display.CONTENTS
                for (i in newChildrenSize - 1 downTo previousChildrenSize) {
                    val child = newChildren[i]
                    this@bindEach.removeAt(i)
                    add(0, child)
                }
            }
        }
    }

    this._archivedState = null
    val unsubscribe = signal.subscribe {
        it.onSuccess {
            this.singleRender {
                val previousState = _archivedState?.unsafeCast<List<S>>() ?: emptyList()
                val myersDiff = if (equalizer != null) {
                    MyersDiff(equalizer)
                } else MyersDiff()
                val patch = diff(previousState, it, myersDiff)
                val deltas = patch.deltas
                val iterator = deltas.listIterator(deltas.size)
                while (iterator.hasPrevious()) {
                    when (val delta = iterator.previous()) {
                        is ChangeDelta -> {
                            val position: Int = delta.source.position
                            val size: Int = delta.source.size()
                            for (i in 0 until size) {
                                val component = this.getChildren()[position]
                                this.removeAt(position)
                                component.dispose()
                            }
                            delta.target.lines.forEachIndexed { i, line ->
                                if (position + i == this.getChildren().size) {
                                    addSingleComponent(line)
                                } else {
                                    this.add(position + i, getSingleComponent(line))
                                }
                            }
                        }

                        is DeleteDelta -> {
                            val position = delta.source.position
                            for (i in 0 until delta.source.size()) {
                                val component = this.getChildren()[position]
                                this.removeAt(position)
                                component.dispose()
                            }
                        }

                        is InsertDelta -> {
                            val position = delta.source.position
                            delta.target.lines.forEachIndexed { i, line ->
                                if (position + i == this.getChildren().size) {
                                    addSingleComponent(line)
                                } else {
                                    this.add(position + i, getSingleComponent(line))
                                }
                            }
                        }

                        is EqualDelta -> {
                        }
                    }
                }
                _archivedState = it.toList()
            }
        }
    }
    this.addBeforeDisposeHook {
        this._archivedState = null
        unsubscribe()
    }
    return this
}

/**
 * An extension function which binds the container to the given signal with a list of items using the substate extractor.
 *
 * @param S the state type
 * @param T the substate type
 * @param W the container type
 * @param signal the Signal instance
 * @param sub an extractor function for substate
 * @param equalizer optional custom equalizer function
 * @param factory a function which re-creates the view based on the given state
 */
fun <S, T, W : SimplePanel> W.bindEach(
    signal: Signal<S>,
    sub: (S) -> List<T>,
    equalizer: ((T, T) -> Boolean)? = null,
    factory: (SimplePanel.(T) -> Unit)
): W {
    return bindEach(signal.map(sub), equalizer, factory)
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <S, T : GenericFormComponent<S>> T.bindTo(state: MutableSignal<S>): T {
    bindSync(state, false) {
        if (value != it) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it
    })
    return this
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <T : GenericFormComponent<String?>> T.bindTo(state: MutableSignal<String>): T {
    bindSync(state, false) {
        if (value != it && !(value == null && it == "")) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it ?: ""
    })
    return this
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <T : GenericFormComponent<Number?>> T.bindTo(state: MutableSignal<Int?>): T {
    bindSync(state, false) {
        if (value != it) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it?.toInt()
    })
    return this
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <T : GenericFormComponent<Number?>> T.bindTo(state: MutableSignal<Int>): T {
    bindSync(state, false) {
        if (value != it) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it?.toInt() ?: 0
    })
    return this
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <T : GenericFormComponent<Number?>> T.bindTo(state: MutableSignal<Double?>): T {
    bindSync(state, false) {
        if (value != it) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it?.toDouble()
    })
    return this
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <T : GenericFormComponent<Number?>> T.bindTo(state: MutableSignal<Double>): T {
    bindSync(state, false) {
        if (value != it) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it?.toDouble() ?: 0.0
    })
    return this
}

/**
 * Bidirectional data binding to the MutableSignal instance.
 * @param state the MutableSignal instance
 * @return current component
 */
fun <T : GenericFormComponent<Date?>> T.bindTo(state: MutableSignal<Date>): T {
    bindSync(state, false) {
        if (value != it) value = it
    }
    addBeforeDisposeHook(subscribe {
        state.value = it ?: Date()
    })
    return this
}
