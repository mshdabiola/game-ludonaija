package com.mshdabiola.naijaludo.wifipeer2peer.common

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.p2p.*
import android.os.Looper
import android.util.Log
import androidx.annotation.CallSuper
import com.mshdabiola.naijaludo.wifipeer2peer.WifiP2pStateMonitor
import com.mshdabiola.naijaludo.wifipeer2peer.p2p.*

import java.net.InetAddress
import kotlin.properties.Delegates

abstract class BaseManager(protected open val context: Context) {

    private val stateMonitor = WifiP2pStateMonitor.get(context)

    protected var manager: WifiP2pManager by Delegates.notNull()
    protected var channel: WifiP2pManager.Channel by Delegates.notNull()
    protected var wManager: WifiManager by Delegates.notNull()


    private var wifiP2pEnabled = false

    protected var localDevice: WifiP2pDevice? = null
    protected var p2pConnection: WifiP2pInfo? = null
    protected var p2pGroup: WifiP2pGroup? = null
    protected var p2pDeviceList: WifiP2pDeviceList? = null

    init {
        check(WifiUtils.checkWifiSupport(context))
        WifiUtils.askPermissions(context)
        manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(context, Looper.getMainLooper()) {
            onChannelDisconnected()
        }
        wManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager


    }

    private val stateCallback = object : WifiP2pStateMonitor.StateCallback {
        override fun onWifiP2pState(enable: Boolean) {

            wifiP2pEnabled = enable
            onWifiP2pEnabled(enable)
        }

        override fun onDiscoveryStateChanged(start: Boolean) {
            onDiscoverStateChanged(start)
            if (start) {
                log("discovery start")
            } else {
                log("discovery stop")
            }
        }

        override fun onConnectionChanged(connected: Boolean) {
            if (connected) {
                manager.queryConnectionInfo(channel) { info ->
                    p2pConnection = info
                    onConnectionInfo(p2pConnection)
                }
                manager.queryGroupInfo(channel) { group ->
                    p2pGroup = group
                    onGroupInfo(p2pGroup)
                }
            }
        }

        override fun onDeviceInfoChanged(device: WifiP2pDevice?) {
            localDevice = device
        }

        override fun onPeersChanged() {
            manager.queryPeers(channel) { deviceList ->
                p2pDeviceList = deviceList
                onPeersList(p2pDeviceList)
            }
        }
    }

    @CallSuper
    open fun start() {


    }

    fun enable() {
        if (!wManager.isWifiEnabled) {
            wManager.isWifiEnabled = true
        }
    }

    fun disable() {
        if (wManager.isWifiEnabled) {
            wManager.isWifiEnabled = false
        }
    }

    //    @CallSuper
    open fun stop() {
        disable()

    }

    fun startServiceDiscovery() {

    }

    fun register() {
        stateMonitor.addCallback(stateCallback)
        stateMonitor.start()
    }

    fun unRegister() {
        stateMonitor.stop()
        stateMonitor.removeCallback(stateCallback)
    }

    fun isWifiP2pEnabled(): Boolean {
        return wifiP2pEnabled
    }

    fun isGroupOwner(): Boolean {
        return p2pConnection?.isGroupOwner ?: false
    }

    fun isGroupFormed(): Boolean {
        return p2pConnection?.groupFormed ?: false
    }

    fun discoverPeer() {
        enable()
        manager.createGroup(channel, localDevice?.deviceAddress?.let { createConfig(it) }, success = { log("create group successful") }, failure = { log("fail to create group") })
//        manager.discoverPeers(channel,{
//            log("discover successful")
//        },{
//            log("discover error code : $it")
//        })
    }

    fun groupOwnerAddress(): InetAddress? {
        return p2pConnection?.groupOwnerAddress
    }

    fun getP2pGroups(): WifiP2pGroup? {
        return p2pGroup
    }

    fun getP2pInfo(): WifiP2pInfo? {
        return p2pConnection
    }

    fun getPeersList(): List<WifiP2pDevice> {
        return p2pDeviceList?.deviceList.orEmpty().toList()
    }

    open fun onChannelDisconnected() {}
    open fun onWifiP2pEnabled(enable: Boolean) {}
    open fun onConnectionInfo(info: WifiP2pInfo?) {}
    open fun onGroupInfo(info: WifiP2pGroup?) {}
    open fun onPeersList(peers: WifiP2pDeviceList?) {}
    open fun onDiscoverStateChanged(start: Boolean) {

    }

    open fun log(string: String) {
        Log.e(this::class.java.name, string)
    }


}