package com.mshdabiola.naijaludo.entity.connection

interface ConnectInterfaceAnd {
    fun startDiscovery()
    fun startDiscoveryRegistration(records: Map<String, String>)
    fun connect(mac: String)
    fun disconnect()
    fun getGroupInfo(): P2pGroup?
    fun getGroupForm(): P2pInfo?
    fun discoverPeer()
    fun shareApp(str: String, shareImage: Boolean)
    fun log(str: String)

}