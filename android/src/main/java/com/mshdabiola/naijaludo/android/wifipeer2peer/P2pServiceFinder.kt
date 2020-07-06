package com.mshdabiola.naijaludo.android.wifipeer2peer

import android.content.Context
import android.net.wifi.p2p.*
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo
import android.util.Log
import com.mshdabiola.naijaludo.android.wifipeer2peer.p2p.cancelConnect
import com.mshdabiola.naijaludo.android.wifipeer2peer.p2p.connect
import com.mshdabiola.naijaludo.wifipeer2peer.*
import com.mshdabiola.naijaludo.wifipeer2peer.common.BaseManager
import kotlin.properties.Delegates

class P2pServiceFinder(
        context: Context,
        private val callback: Callback,
        serviceType: Int = WifiP2pServiceInfo.SERVICE_TYPE_ALL
) : BaseManager(context) {


    private val serviceRequest by lazy { createServiceRequest(serviceType) }
    var serviceInfo by Delegates.notNull<WifiP2pServiceInfo>()

    interface Callback {
        fun onP2pEnabled(enable: Boolean)
        fun onP2pError()
        fun onDeviceBusy()
        fun onConnectResult(success: Boolean)
        fun onServiceFound(name: String, registrationType: String, device: WifiP2pDevice)
        fun onServiceInfo(fullName: String, records: Map<String, String>, device: WifiP2pDevice)
        fun onGroupFormed(info: WifiP2pInfo?)
        fun onPeersList(peers: List<WifiP2pDevice>)
        fun onGroupInfo(group: WifiP2pGroup?)
        fun onDiscoverStateChanged(start: Boolean)

    }


    fun startDiscovery() {
        enable()
        addListener()
        addRequest()
        discovery()

    }

    private fun addListener() {
        manager.listenDnsSdResponse(
                channel,
                { instanceName, registrationType, srcDevice ->
                    callback.onServiceFound(instanceName, registrationType, srcDevice)
                },
                { fullDomainName, txtRecordMap, srcDevice ->
                    callback.onServiceInfo(fullDomainName, txtRecordMap, srcDevice)
                }
        )
    }

    private fun addRequest() {
        manager.addServiceRequest(channel, serviceRequest, { log("addServiceRequest Successfully") }, {
            handleError(it)
        })
    }

    private fun removeRequest() {
        manager.removeServiceRequest(channel, serviceRequest)
    }

    private fun discovery() {
        manager.discoverServices(channel, { log("DiscoverServices Successfully") }, {
            handleError(it)
        })
    }

    private fun handleError(reason: Int) {
        when (reason) {
            WifiP2pManager.P2P_UNSUPPORTED -> {
                log("Handle error: P2p Unsupported ")
            }
            WifiP2pManager.ERROR -> {
                callback.onP2pError()
            }
            WifiP2pManager.BUSY -> {
                callback.onDeviceBusy()
            }
        }
        removeRequest()
    }

    fun startRegistration(records: Map<String, String>) {
        enable()
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("LudoNaija2", "_presence._tcp", records)
        manager.addService(channel, serviceInfo, { log("AddService Successfully") }, { int ->
            handleError(int)
        })

    }


    fun connect(mac: String) {

        manager.connect(channel, mac, {
            callback.onConnectResult(true)
        }, {
            callback.onConnectResult(false)
        })

    }

    fun cancelConnect() {
        manager.cancelConnect(channel, { log("CancelConnect successfully") }, { log("CancelConnect failure") })

    }

    override fun log(string: String) {
        Log.e(this::class.java.name, string)
    }


    override fun stop() {
        cancelConnect()
        manager.removeLocalService(channel, serviceInfo, null)
        manager.removeGroup(channel, null)
        manager.removeService(channel, serviceInfo, {}, {})
        manager.clearServiceRequests(channel)
        cancelConnect()
        super.stop()
    }

    override fun onWifiP2pEnabled(enable: Boolean) {
        super.onWifiP2pEnabled(enable)
        callback.onP2pEnabled(enable)
    }

    override fun onPeersList(peers: WifiP2pDeviceList?) {
        super.onPeersList(peers)
        callback.onPeersList(getPeersList())
    }

    override fun onConnectionInfo(info: WifiP2pInfo?) {
        super.onConnectionInfo(info)

//        info.groupOwnerAddress.hostAddress
        if (info?.groupFormed == true) {
            callback.onGroupFormed(info)
        }
    }

    override fun onGroupInfo(info: WifiP2pGroup?) {
        super.onGroupInfo(info)

        callback.onGroupInfo(getP2pGroups())
    }

    override fun onDiscoverStateChanged(start: Boolean) {
        super.onDiscoverStateChanged(start)
        callback.onDiscoverStateChanged(start)
    }
}