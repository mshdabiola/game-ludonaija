package com.mshdabiola.naijaludo.wifipeer2peer.p2p

import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

private const val TAG = "P2pDiscover"

/**
 * discover available peer list
 */
fun WifiP2pManager.discoverPeers(
        channel: WifiP2pManager.Channel,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    discoverPeers(channel, object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "discover Peers success")
            success?.invoke()
        }

        override fun onFailure(reason: Int) {
            Log.d(TAG, "discover Peers fail: $reason")
            failure?.invoke(reason)
        }
    })
}

/**
 * request the peer list available
 */
fun WifiP2pManager.queryPeers(
        channel: WifiP2pManager.Channel,
        infoListener: ((WifiP2pDeviceList) -> Unit)? = null
) {
    requestPeers(channel, infoListener)
}

/**
 * stop peers discovery
 */
fun WifiP2pManager.stopPeerDiscovery(
        channel: WifiP2pManager.Channel,
        success: (() -> Unit)? = null, failure: ((Int) -> Unit)? = null
) {
    stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Log.d(TAG, "stop discover Peers success")
            success?.invoke()
        }

        override fun onFailure(reason: Int) {
            Log.d(TAG, "stop discover Peers fail: $reason")
            failure?.invoke(reason)
        }
    })
}