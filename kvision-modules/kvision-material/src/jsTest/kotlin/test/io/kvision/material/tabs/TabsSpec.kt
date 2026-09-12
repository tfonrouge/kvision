package test.io.kvision.material.tabs

import io.kvision.material.ExperimentalMaterialApi
import io.kvision.material.tabs.MdSecondaryTab
import io.kvision.material.tabs.MdTabs
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
class TabsSpec : DomSpec {

    @Test
    fun dispose() {
        run {
            val root = Root("test", containerType = ContainerType.FIXED)
            lateinit var tab: MdSecondaryTab
            var hookFired = 0
            val tabs = MdTabs {
                tab = MdSecondaryTab("One") {
                    addBeforeDisposeHook {
                        hookFired++
                        assertNotNull(tab.parent, "Should dispose the tab before detaching it")
                    }
                }
                add(tab)
            }
            // Items take the parent of their container, so the panel has to be attached for the
            // tabs to have one at all.
            root.add(tabs)
            tabs.dispose()
            assertEquals(1, hookFired, "Should dispose every tab")
            assertNull(tab.parent, "Should detach every disposed tab")
        }
    }

    @Test
    fun disposeTabContent() {
        run {
            var contentHooks = 0
            val tabs = MdTabs {
                add(MdSecondaryTab("One") {
                    addBeforeDisposeHook { contentHooks++ }
                })
                add(MdSecondaryTab("Two") {
                    addBeforeDisposeHook { contentHooks++ }
                })
            }
            tabs.dispose()
            assertEquals(2, contentHooks, "Should dispose the tabs of every position")
        }
    }
}
