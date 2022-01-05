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

//@file:JsModule("leaflet")
//@file:JsNonModule

package externals.leaflet.map

import externals.leaflet.layer.marker.Icon
import externals.leaflet.layer.marker.Marker
import externals.leaflet.layer.overlay.ImageOverlay
import externals.leaflet.layer.overlay.Popup
import externals.leaflet.layer.overlay.Tooltip
import externals.leaflet.layer.overlay.VideoOverlay
import externals.leaflet.layer.tile.GridLayer
import externals.leaflet.layer.tile.TileLayer
import externals.leaflet.layer.vector.Polygon
import externals.leaflet.layer.vector.Polyline
import org.w3c.dom.HTMLElement

/**
 * See [`https://github.com/Leaflet/Leaflet/blob/v1.7.1/src/map/Map.js#L1137`](https://github.com/Leaflet/Leaflet/blob/v1.7.1/src/map/Map.js#L1137)
 */
external interface DefaultMapPanes {
    /** Pane that contains all other map panes */
    var mapPane: HTMLElement
    /** Pane for [GridLayer]s and [TileLayer]s */
    var tilePane: HTMLElement
    /** Pane for vectors (`Path`s, like [Polyline]s and [Polygon]s), [ImageOverlay]s and [VideoOverlay]s */
    var overlayPane: HTMLElement
    /** Pane for overlay shadows (e.g. [Marker] shadows) */
    var shadowPane: HTMLElement
    /** Pane for [Icon]s of [Marker]s */
    var markerPane: HTMLElement
    /** Pane for [Tooltip]s. */
    var tooltipPane: HTMLElement
    /** Pane for [Popup]s. */
    var popupPane: HTMLElement
}

external interface HTMLElementsObject

inline operator fun HTMLElementsObject.get(name: String): HTMLElement? =
    asDynamic()[name] as HTMLElement?

inline operator fun HTMLElementsObject.set(name: String, value: HTMLElement) {
    asDynamic()[name] = value
}

external interface MapPanes : HTMLElementsObject, DefaultMapPanes
