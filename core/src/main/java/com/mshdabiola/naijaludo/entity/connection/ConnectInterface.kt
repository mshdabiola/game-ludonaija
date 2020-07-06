package com.mshdabiola.naijaludo.entity.connection

import java.net.InetAddress

interface ConnectInterface {
    fun onP2pEnabled(enable: Boolean)
    fun onP2pError()
    fun onDeviceBusy()
    fun onConnectResult(success: Boolean)
    fun onServiceFound(name: String, registrationType: String, device: WifiDevice)
    fun onServiceInfo(fullName: String, records: Map<String, String>, device: WifiDevice)
    fun onGroupFormed(form: P2pInfo?)
    fun onGroupInfo(info: P2pGroup?)
    fun onDiscovery(start: Boolean)
    fun onPeerDevicesChanged(peerDevices: List<Pair<String, String>>)


}

data class WifiDevice(val name: String, val address: String, val isOwner: Boolean)
data class P2pInfo(val groupOwnerAddress: InetAddress, val isGroupOwner: Boolean, val isGroupForm: Boolean)
data class P2pGroup(val password: String?, val networkName: String, val isGroupOwner: Boolean)


