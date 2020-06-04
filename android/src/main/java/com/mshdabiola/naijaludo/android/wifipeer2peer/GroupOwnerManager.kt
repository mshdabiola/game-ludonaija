package com.mshdabiola.naijaludo.wifipeer2peer

import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import com.mshdabiola.naijaludo.android.wifipeer2peer.p2p.createConfig
import com.mshdabiola.naijaludo.wifipeer2peer.common.BaseManager
import com.mshdabiola.naijaludo.wifipeer2peer.p2p.createGroup
import com.mshdabiola.naijaludo.wifipeer2peer.p2p.queryGroupInfo
import com.mshdabiola.naijaludo.wifipeer2peer.p2p.removeGroup


class GroupOwnerManager(
        context: Context, private val callback: Callback,
        groupName: String? = null, groupPassphrase: String? = null
) : BaseManager(context) {

    private val config: WifiP2pConfig? =
            if (groupName.isNullOrBlank() || groupPassphrase.isNullOrBlank()) {
                null
            } else {
                createConfig(groupName, groupPassphrase)
            }

    interface Callback {
        fun onP2pEnabled(enable: Boolean)
        fun onGroupCreated(success: Boolean)
        fun onGroupFormed()
        fun onClientsList(clients: List<WifiP2pDevice>)
    }

    override fun start() {
        super.start()
        if (isWifiP2pEnabled()) {
            startGroup()
        } else {
            throw IllegalStateException("Enable wifi p2p first")
        }

//        if (isWifiP2pEnabled()) {
//            manager.discoverPeers(channel, {
//              Log.e(TAG,"discover peers successful")
//            }, {
//                Log.e(TAG,"discover peers fail")
//            })
//        } else {
//            throw IllegalStateException("Enable wifi p2p first")
//        }

    }


    private fun startGroup() {
        manager.createGroup(channel, config, {
            callback.onGroupCreated(true)
        }, {
            callback.onGroupCreated(false)
        })
    }

    private fun getClients(): List<WifiP2pDevice> {
        return getP2pGroups()?.clientList.orEmpty().toList()
    }

    fun refreshGroup() {
        manager.queryGroupInfo(channel) { group ->
            p2pGroup = group
            callback.onClientsList(getClients())
        }
    }

    private fun stopGroup() {
        manager.removeGroup(channel)
    }

    override fun stop() {
        stopGroup()
        super.stop()
    }

    override fun onWifiP2pEnabled(enable: Boolean) {
        super.onWifiP2pEnabled(enable)
        callback.onP2pEnabled(enable)
    }

    override fun onConnectionInfo(info: WifiP2pInfo?) {
        super.onConnectionInfo(info)
        if (info?.groupFormed == true) {
            callback.onGroupFormed()
        }
    }

    override fun onGroupInfo(info: WifiP2pGroup?) {
        super.onGroupInfo(info)
        callback.onClientsList(getClients())
    }

    companion object {
        private const val TAG = "GroupOwnerManager"
    }
}