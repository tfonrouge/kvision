package test.io.kvision.material.select

import io.kvision.material.ExperimentalMaterialApi
import io.kvision.material.select.MdOutlinedSelect
import io.kvision.material.select.MdSelectOption
import io.kvision.panel.ContainerType
import io.kvision.panel.Root
import io.kvision.test.DomSpec
import kotlinx.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * TODO
 */
@OptIn(ExperimentalMaterialApi::class)
class SelectSpec : DomSpec {

    @Test
    fun dispose() {
        run {
            lateinit var option: MdSelectOption
            var hookFired = 0
            val root = Root("test", containerType = ContainerType.FIXED)
            val select = MdOutlinedSelect {
                option = MdSelectOption("one") {
                    addBeforeDisposeHook {
                        hookFired++
                        assertNotNull(option.parent, "Should dispose the option before detaching it")
                    }
                }
                add(option)
            }
            // Items take the parent of their container, so the select has to be attached for the
            // options to have one at all.
            root.add(select)
            select.dispose()
            assertEquals(1, hookFired, "Should dispose every option")
            assertNull(option.parent, "Should detach every disposed option")
        }
    }

    @Test
    fun remove() {
        run {
            val root = Root("test", containerType = ContainerType.FIXED)
            val keep = MdSelectOption("keep")
            val drop = MdSelectOption("drop")
            val select = MdOutlinedSelect {
                add(keep)
                add(drop)
            }
            root.add(select)
            select.remove(drop)
            val element = document.getElementById("test")
            assertContainsHtml(
                "<md-select-option value=\"keep\"></md-select-option>",
                element?.innerHTML,
                "Should keep the options which were not removed"
            )
            assertEquals(
                false,
                element?.innerHTML?.contains("drop"),
                "Should remove the option instead of adding it again"
            )
            assertNull(drop.parent, "Should detach the removed option")
        }
    }
}
