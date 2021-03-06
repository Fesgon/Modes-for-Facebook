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
package com.dark.frost.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import com.dark.frost.R
import com.dark.frost.activities.SettingsActivity
import com.dark.frost.utils.Prefs

/**
 * Created by Allan Wang on 2017-08-08.
 */
fun SettingsActivity.getNetworkPrefs(): KPrefAdapterBuilder.() -> Unit = {

    checkbox(
        R.string.network_media_on_metered,
        { !Prefs.loadMediaOnMeteredNetwork },
        { Prefs.loadMediaOnMeteredNetwork = !it }) {
        descRes = R.string.network_media_on_metered_desc
    }
}
