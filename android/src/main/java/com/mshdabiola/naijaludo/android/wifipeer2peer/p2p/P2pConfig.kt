package com.mshdabiola.naijaludo.android.wifipeer2peer.p2p

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig

fun createConfig(
        groupName: String,
        groupPin: String,
        band: Int = WifiP2pConfig.GROUP_OWNER_BAND_AUTO,
        wpsSetup: Int = WpsInfo.PBC,
        persistent: Boolean = false
): WifiP2pConfig {
    return WifiP2pConfig.Builder().apply {
        setNetworkName(groupName)
        setPassphrase(groupPin)
        setGroupOperatingBand(band)
        enablePersistentMode(persistent)
    }.build().also {
        it.wps.setup = wpsSetup
    }
}

fun createConfig(address: String): WifiP2pConfig {
    return WifiP2pConfig().apply {
        deviceAddress = address
        wps.setup = WpsInfo.PBC
    }
}