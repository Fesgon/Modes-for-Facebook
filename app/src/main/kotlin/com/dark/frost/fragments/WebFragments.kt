/*
 * Copyright 2018 Allan Wang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dark.frost.fragments

import android.webkit.WebView
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.dark.frost.R
import com.dark.frost.contracts.MainFabContract
import com.dark.frost.facebook.FbItem
import com.dark.frost.injectors.JsActions
import com.dark.frost.utils.L
import com.dark.frost.utils.Prefs
import com.dark.frost.views.FrostWebView
import com.dark.frost.web.FrostWebViewClient
import com.dark.frost.web.FrostWebViewClientMenu

/**
 * Created by Allan Wang on 27/12/17.
 *
 * Basic webfragment
 * Do not extend as this is always a fallback
 */
class WebFragment : BaseFragment() {

    override val layoutRes: Int = R.layout.view_content_web

    /**
     * Given a webview, output a client
     */
    fun client(web: FrostWebView) = when (baseEnum) {
        FbItem.MENU -> FrostWebViewClientMenu(web)
        else -> FrostWebViewClient(web)
    }

    override fun updateFab(contract: MainFabContract) {
        val web = core as? WebView
        if (web == null) {
            L.e { "Webview not found in fragment $baseEnum" }
            return super.updateFab(contract)
        }
        if (baseEnum.isFeed && Prefs.showCreateFab) {
            contract.showFab(GoogleMaterial.Icon.gmd_edit) {
                JsActions.CREATE_POST.inject(web)
            }
            return
        }
        super.updateFab(contract)
    }
}
