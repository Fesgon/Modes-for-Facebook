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
package com.dark.frost

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.utils.buildIsLollipopAndUp
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.string

import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.dark.frost.activities.LoginActivity
import com.dark.frost.activities.MainActivity
import com.dark.frost.activities.SelectorActivity
import com.dark.frost.db.CookieDao
import com.dark.frost.db.CookieEntity
import com.dark.frost.db.GenericDao
import com.dark.frost.db.selectAll
import com.dark.frost.facebook.FbCookie
import com.dark.frost.utils.BiometricUtils
import com.dark.frost.utils.EXTRA_COOKIES
import com.dark.frost.utils.L
import com.dark.frost.utils.Prefs
import com.dark.frost.utils.launchNewTask
import com.dark.frost.utils.loadAssets
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.ArrayList

/**
 * Created by Allan Wang on 2017-05-28.
 */
class StartActivity : KauBaseActivity() {

    private val cookieDao: CookieDao by inject()
    private val genericDao: GenericDao by inject()

    private val mAppUnitId: String by lazy {

        getString(R.string.app_id)
    }

    private val mInterstitialAdUnitId: String by lazy {

        getString(R.string.interstitial_ad_id)
    }

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!buildIsLollipopAndUp) { // not supported
            showInvalidSdkView()
            return
        }

        try {
            // TODO add better descriptions
            CookieManager.getInstance()
        } catch (e: Exception) {
            showInvalidWebView()
        }


        launch {
            try {

                val authDefer = BiometricUtils.authenticate(this@StartActivity)
                FbCookie.switchBackUser()
                val cookies = ArrayList(cookieDao.selectAll())
                L.i { "Cookies loaded at time ${System.currentTimeMillis()}" }
                L._d {
                    "Cookies: ${cookies.joinToString(
                        "\t",
                        transform = CookieEntity::toSensitiveString
                    )}"
                }

                loadAssets()
                authDefer.await()


                when {
                    cookies.isEmpty() -> launchNewTask<LoginActivity>()
                    // Has cookies but no selected account
                    Prefs.userId == -1L -> launchNewTask<SelectorActivity>(cookies)

                    else ->
                        // if(mInterstitialAd.isLoaded){mInterstitialAd.show()}
                        startActivity<MainActivity>(intentBuilder = {
                            putParcelableArrayListExtra(EXTRA_COOKIES, cookies)
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                }
            } catch (e: Exception) {
                L._e(e) { "Load start failed" }
                showInvalidWebView()
            }
        }
    }

    private fun showInvalidWebView() =
        showInvalidView(R.string.error_webview)

    private fun showAd() =
        showInvalidView(R.string.error_webview)

    private fun showInvalidSdkView() {
        val text = String.format(string(R.string.error_sdk), Build.VERSION.SDK_INT)
        showInvalidView(text)
    }

    private fun showInvalidView(textRes: Int) =
        showInvalidView(string(textRes))

    private fun showInvalidView(text: String) {
        setContentView(R.layout.activity_invalid)
        findViewById<ImageView>(R.id.invalid_icon)
            .setIcon(GoogleMaterial.Icon.gmd_adb, -1, Color.WHITE)
        findViewById<TextView>(R.id.invalid_text).text = text
    }
}
