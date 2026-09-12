package test.io.kvision.material.select

import io.kvision.material.ExperimentalMaterialApi
import io.kvision.material.select.MdOutlinedSelect
import io.kvision.material.select.MdSelectOption
import io.kvision.panel.ContainerType
import io.kvision.panel.Root
import io.kvision.test.DomSpec
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
}
