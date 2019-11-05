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
package pl.treksoft.kvision.form.select

import pl.treksoft.kvision.core.Component
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.StringBoolPair
import pl.treksoft.kvision.core.Widget
import pl.treksoft.kvision.form.FieldLabel
import pl.treksoft.kvision.form.InvalidFeedback
import pl.treksoft.kvision.form.StringFormControl
import pl.treksoft.kvision.panel.SimplePanel
import pl.treksoft.kvision.remote.KVServiceManager
import pl.treksoft.kvision.remote.RemoteOption
import pl.treksoft.kvision.utils.SnOn

/**
 * The form field component for SelectRemote control.
 *
 * @constructor
 * @param value selected value
 * @param serviceManager multiplatform service manager
 * @param function multiplatform service method returning the list of options
 * @param stateFunction a function to generate the state object passed with the remote request
 * @param name the name attribute of the generated HTML input element
 * @param multiple allows multiple value selection (multiple values are comma delimited)
 * @param ajaxOptions additional options for remote data source
 * @param preload preload all options from remote data source
 * @param label label text bound to the input element
 * @param rich determines if [label] can contain HTML code
 */
@Suppress("TooManyFunctions")
open class SelectRemote<T : Any>(
    value: String? = null,
    serviceManager: KVServiceManager<T>,
    function: suspend T.(String?, String?, String?) -> List<RemoteOption>,
    stateFunction: (() -> String)? = null,
    name: String? = null,
    multiple: Boolean = false,
    ajaxOptions: AjaxOptions? = null,
    preload: Boolean = false,
    label: String? = null,
    rich: Boolean = false
) : SimplePanel(setOf("form-group")), StringFormControl {
    /**
     * A value of the selected option.
     */
    override var value
        get() = input.value
        set(value) {
            input.value = value
        }
    /**
     * Determines if multiple value selection is allowed.
     */
    var multiple
        get() = input.multiple
        set(value) {
            input.multiple = value
        }
    /**
     * Maximal number of selected options.
     */
    var maxOptions
        get() = input.maxOptions
        set(value) {
            input.maxOptions = value
        }
    /**
     * The placeholder for the select control.
     */
    var placeholder
        get() = input.placeholder
        set(value) {
            input.placeholder = value
        }
    /**
     * The style of the select control.
     */
    var style
        get() = input.style
        set(value) {
            input.style = value
        }
    /**
     * The width of the select control.
     */
    var selectWidth
        get() = input.selectWidth
        set(value) {
            input.selectWidth = value
        }
    /**
     * The width type of the select control.
     */
    var selectWidthType
        get() = input.selectWidthType
        set(value) {
            input.selectWidthType = value
        }
    /**
     * The dropdown align of the select control.
     */
    var dropdownAlign
        get() = input.dropdownAlign
        set(value) {
            input.dropdownAlign = value
        }
    /**
     * Determines if an empty option is automatically generated.
     */
    var emptyOption
        get() = input.emptyOption
        set(value) {
            input.emptyOption = value
        }
    /**
     * Determines if the select is automatically focused.
     */
    var autofocus
        get() = input.autofocus
        set(value) {
            input.autofocus = value
        }
    /**
     * The label text bound to the select element.
     */
    var label
        get() = flabel.content
        set(value) {
            flabel.content = value
        }
    /**
     * Determines if [label] can contain HTML code.
     */
    var rich
        get() = flabel.rich
        set(value) {
            flabel.rich = value
        }

    private val idc = "kv_form_SelectRemote_$counter"
    final override val input: SelectRemoteInput<T> = SelectRemoteInput(
        value, serviceManager, function, stateFunction, multiple, ajaxOptions, preload,
        setOf("form-control")
    ).apply {
        this.id = idc
        this.name = name
    }
    final override val flabel: FieldLabel = FieldLabel(idc, label, rich)
    final override val invalidFeedback: InvalidFeedback = InvalidFeedback().apply { visible = false }

    init {
        @Suppress("LeakingThis")
        input.eventTarget = this
        this.addInternal(flabel)
        this.addInternal(input)
        this.addInternal(invalidFeedback)
        counter++
    }

    override fun getSnClass(): List<StringBoolPair> {
        val cl = super.getSnClass().toMutableList()
        if (validatorError != null) {
            cl.add("text-danger" to true)
        }
        return cl
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Widget> setEventListener(block: SnOn<T>.() -> Unit): Widget {
        input.setEventListener(block)
        return this
    }

    override fun setEventListener(block: SnOn<Widget>.() -> Unit): Widget {
        input.setEventListener(block)
        return this
    }

    override fun removeEventListeners(): Widget {
        input.removeEventListeners()
        return this
    }

    override fun add(child: Component): SimplePanel {
        input.add(child)
        return this
    }

    override fun addAll(children: List<Component>): SimplePanel {
        input.addAll(children)
        return this
    }

    override fun remove(child: Component): SimplePanel {
        input.remove(child)
        return this
    }

    override fun removeAll(): SimplePanel {
        input.removeAll()
        return this
    }

    override fun getChildren(): List<Component> {
        return input.getChildren()
    }

    /**
     * Opens dropdown with options.
     */
    open fun showOptions() {
        input.showOptions()
    }

    /**
     * Hides dropdown with options.
     */
    open fun hideOptions() {
        input.hideOptions()
    }

    /**
     * Toggles visibility of dropdown with options.
     */
    open fun toggleOptions() {
        input.toggleOptions()
    }

    override fun focus() {
        input.focus()
    }

    override fun blur() {
        input.blur()
    }

    companion object {
        internal var counter = 0
    }
}

/**
 * DSL builder extension function.
 *
 * It takes the same parameters as the constructor of the built component.
 */
fun <T : Any> Container.selectRemote(
    value: String? = null,
    serviceManager: KVServiceManager<T>,
    function: suspend T.(String?, String?, String?) -> List<RemoteOption>, stateFunction: (() -> String)? = null,
    name: String? = null, multiple: Boolean = false, ajaxOptions: AjaxOptions? = null, preload: Boolean = false,
    label: String? = null, rich: Boolean = false, init: (SelectRemote<T>.() -> Unit)? = null
): SelectRemote<T> {
    val selectRemote =
        SelectRemote(
            value,
            serviceManager,
            function,
            stateFunction,
            name,
            multiple,
            ajaxOptions,
            preload,
            label,
            rich
        ).apply { init?.invoke(this) }
    this.add(selectRemote)
    return selectRemote
}
