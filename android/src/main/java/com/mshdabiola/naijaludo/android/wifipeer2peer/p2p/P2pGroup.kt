package com.mshdabiola.naijaludo.android.wifipeer2peer.p2p

import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

private const val TAG = "P2pGroup"

/**
 * request the group info
 */
fun WifiP2pManager.queryGroupInfo(
        channel: WifiP2pManager.Channel,
        infoListener: ((WifiP2pGroup) -> Unit)? = null
) {
    requestGroupInfo(channel, infoListener)
}

/**
 * create a group
 */
fun WifiP2pManager.createGroup(
        channel: WifiP2pManager.Channel,
        config: WifiP2pConfig? = null,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    val listener = object : WifiP2pManager.ActionListener {
        override fun onFailure(reason: Int) {
            Log.d(TAG, "create group fail: $reason")
            failure?.invoke(reason)
        }

        override fun onSuccess() {
            Log.d(TAG, "create group success")
            success?.invoke()
        }
    }
    if (config == null) {
        createGroup(channel, listener)
    } else {
        createGroup(channel, config, listener)
    }
}

/**
 * remove a group
 */
fun WifiP2pManager.removeGroup(
        channel: WifiP2pManager.Channel,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    removeGroup(channel, object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "remove group success")
            success?.invoke()
        }

        override fun onFailure(reason: Int) {
            Log.d(TAG, "remove group success: $reason")
            failure?.invoke(reason)
        }
    })
}