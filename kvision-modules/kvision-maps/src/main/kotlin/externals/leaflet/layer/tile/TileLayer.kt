@file:JsModule("leaflet")
@file:JsNonModule

package externals.leaflet.layer.tile

import externals.leaflet.DoneCallback
import externals.leaflet.geo.Coords
import externals.leaflet.layer.tile.TileLayer.TileLayerOptions
import org.w3c.dom.HTMLElement

/**
 * Used to load and display tile layers on the map. Note that most tile servers require
 * attribution, which you can set under [TileLayerOptions.attribution].
 *
 *
 * @param[urlTemplate] A string of the following form:
 * ```
 * http://{s}.somedomain.com/blabla/{z}/{x}/{y}{r}.png
 * ```
 * * `{s}` means one of the available subdomains (used sequentially to help with browser parallel
 *   requests per domain limitation; subdomain values are specified in options; `a`, `b` or `c` by
 *   default, can be omitted),
 * * `{z}` — zoom level,
 * * `{x}` and `{y}` — tile coordinates.
 * * `{r}` can be used to add `"@2x"` to the URL to load retina tiles.
 *
 *  You can use custom keys in the template, which will be evaluated from [TileLayer] options,
 *  like this:
 *
 *  ```kotlin
 *  LeafletObjectFactory.tileLayer("http://{s}.somedomain.com/{foo}/{z}/{x}/{y}.png") {
 *    asDynamic().foo = "bar"
 *  }
 *  ```
 */
open external class TileLayer<T : TileLayerOptions>(
    urlTemplate: String,
    options: T = definedExternally
) : GridLayer<T> {

    /** @return this */
    open fun setUrl(url: String, noRedraw: Boolean = definedExternally): TileLayer<*>

    open fun getTileUrl(coords: Coords): String

    open fun _tileOnLoad(done: DoneCallback, tile: HTMLElement)
    open fun _tileOnError(done: DoneCallback, tile: HTMLElement, e: Error)
    open fun _abortLoading()
    open fun _getZoomForUrl(): Number

    interface TileLayerOptions : GridLayerOptions {
        var id: String?
        var accessToken: String?
        /**
         * Subdomains of the tile service. Can be passed in the form of one string (where each
         * letter is a subdomain name) or an array of strings.
         */
        var subdomains: dynamic /* String? | Array<String>? */
        var errorTileUrl: String?
        var zoomOffset: Number?
        /**
         * If true, inverses Y axis numbering for tiles (turn this on for
         * [TMS](https://en.wikipedia.org/wiki/Tile_Map_Service) services)
         */
        var tms: Boolean?
        var zoomReverse: Boolean?
        var detectRetina: Boolean?
        var crossOrigin: dynamic /* Boolean? | String? */
    }

}