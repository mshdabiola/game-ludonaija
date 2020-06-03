package com.mshdabiola.naijaludo.wifipeer2peer.p2p

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

private const val TAG = "P2pConnection"

/**
 * connect by MAC address(hardware address)
 */
fun WifiP2pManager.connect(
        channel: WifiP2pManager.Channel,
        deviceAddress: String,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    val config = createConfig(deviceAddress)
    connect(channel, config, object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "connect device success")
            success?.invoke()
        }

        override fun onFailure(reason: Int) {
            Log.d(TAG, "connect device fail: $reason")
            failure?.invoke(reason)
        }
    })
}

/**
 * invoke this method to connect a p2p device
 */
fun WifiP2pManager.connect(
        channel: WifiP2pManager.Channel,
        device: WifiP2pDevice,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    connect(channel, device.deviceAddress, success, failure)
}


fun WifiP2pManager.cancelConnect(
        channel: WifiP2pManager.Channel,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    cancelConnect(channel, object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "connect cancel success")
            success?.invoke()
        }

        override fun onFailure(reason: Int) {
            Log.d(TAG, "connect cancel fail: $reason")
            failure?.invoke(reason)
        }
    })
}

fun WifiP2pManager.queryConnectionInfo(
        channel: WifiP2pManager.Channel,
        infoListener: ((WifiP2pInfo) -> Unit)? = null
) {
    requestConnectionInfo(channel, infoListener)
}