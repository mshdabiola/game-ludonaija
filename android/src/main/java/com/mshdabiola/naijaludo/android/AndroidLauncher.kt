package com.mshdabiola.naijaludo.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.files.FileHandle
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.appinvite.AppInviteInvitation
import com.mshdabiola.naijaludo.entity.connection.*
import com.mshdabiola.naijaludo.screen.NaijaLudo
import com.mshdabiola.naijaludo.wifipeer2peer.GroupOwnerManager
import com.mshdabiola.naijaludo.wifipeer2peer.P2pServiceFinder
import kotlin.properties.Delegates

class AndroidLauncher : AndroidApplication() {


    private var naijaludo: NaijaLudo by Delegates.notNull()
    private var p2pServiceFinder by Delegates.notNull<P2pServiceFinder>()
    private var groupOwnerManager by Delegates.notNull<GroupOwnerManager>()

    fun log(str: String) {
        Log.e(this::class.java.name, "android log $str")
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    val connectInterfaceAnd = object : ConnectInterfaceAnd {
        override fun startDiscovery() {
//            p2pServiceFinder.startDiscovery()
            groupOwnerManager.start()
            log("start discovery")
        }

        override fun startDiscoveryRegistration(records: Map<String, String>) {
            p2pServiceFinder.startRegistration(records)
            log("register discovery")
        }

        override fun connect(mac: String) {
            log("connect $mac")
            p2pServiceFinder.connect(mac)
        }


        override fun disconnect() {
            log("disconnect")
            p2pServiceFinder.stop()

        }

        override fun getGroupInfo(): P2pGroup? {

            return p2pServiceFinder.getP2pGroups()?.run {
                P2pGroup(passphrase, networkName, isGroupOwner)
            }
        }

        override fun getGroupForm(): P2pInfo? {
            return p2pServiceFinder.getP2pInfo()?.run {

                P2pInfo(groupOwnerAddress, isGroupOwner, groupFormed)
            }
        }

        override fun discoverPeer() {
            log("discover peer")
            p2pServiceFinder.discoverPeer()
        }

        override fun shareApp(str: String, shareImage: Boolean) {

            shareGame(str, shareImage)
        }


    }
    var connectInterface by Delegates.notNull<ConnectInterface>()
    val discoveryCallback = object : P2pServiceFinder.Callback {
        override fun onP2pEnabled(enable: Boolean) {
            log("enable $enable")
            connectInterface.onP2pEnabled(enable)
        }

        override fun onP2pError() {
            log("error occur")
            connectInterface.onP2pError()
        }

        override fun onDeviceBusy() {
            log("device busy")
            connectInterface.onDeviceBusy()
        }

        override fun onConnectResult(success: Boolean) {
            log("connected result is $success")
            connectInterface.onConnectResult(success)
        }

        override fun onServiceFound(name: String, registrationType: String, device: WifiP2pDevice) {
            log("service found")
            connectInterface.onServiceFound(name, registrationType, WifiDevice(device.deviceName, device.deviceAddress, device.isGroupOwner))
        }

        override fun onServiceInfo(fullName: String, records: Map<String, String>, device: WifiP2pDevice) {
            log("service information")
            connectInterface.onServiceInfo(fullName, records, WifiDevice(device.deviceName, device.deviceAddress, device.isGroupOwner))
        }

        override fun onGroupFormed(info: WifiP2pInfo?) {
            log("on group form")
            info?.let {
                connectInterface.onGroupFormed(P2pInfo(it.groupOwnerAddress, it.isGroupOwner, it.groupFormed))
            }

        }

        override fun onPeersList(peers: List<WifiP2pDevice>) {
            log("on peer list")

        }

        override fun onGroupInfo(group: WifiP2pGroup?) {
            log("on group info")
            group?.let {

                connectInterface.onGroupInfo(P2pGroup(it.passphrase, it.networkName, it.isGroupOwner))
            }
        }

        override fun onDiscoverStateChanged(start: Boolean) {
            log("on discovery state changed start:$start")
            connectInterface.onDiscovery(start)
        }
    }
    val ownerCallback = object : GroupOwnerManager.Callback {
        override fun onP2pEnabled(enable: Boolean) {
            if (enable) {
                log("p2p enabled")
            } else {
                log("p2p not enabled")
            }
            connectInterface.onP2pEnabled(enable)
        }

        override fun onGroupCreated(success: Boolean) {
            if (success) {
                log("group created successful")
            } else {
                log("group not created successful")
            }
            groupOwnerManager.getP2pGroups()?.run {
                connectInterface.onGroupInfo(P2pGroup(passphrase, networkName, isGroupOwner))
            }
        }

        override fun onGroupFormed() {
            log("on group formed")
            groupOwnerManager.getP2pInfo()?.run {
                connectInterface.onGroupFormed(P2pInfo(groupOwnerAddress, isGroupOwner, groupFormed))
            }


        }

        override fun onClientsList(clients: List<WifiP2pDevice>) {
            log("clients list ${clients.size}")


        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.useWakelock = true
        config.useImmersiveMode = true

        MobileAds.initialize(this)

        naijaludo = NaijaLudo()
        initialize(naijaludo, config)

        connectInterface = naijaludo.connectInterface

        naijaludo.connectInterfaceAnd = connectInterfaceAnd

        p2pServiceFinder = P2pServiceFinder(this, discoveryCallback)
        groupOwnerManager = GroupOwnerManager(this, ownerCallback)


        log("finished on create")
    }


    override fun onPause() {
        super.onPause()
//        p2pServiceFinder.unRegister()
        groupOwnerManager.unRegister()
        log("on pause")
    }

    override fun onResume() {
        super.onResume()
//        p2pServiceFinder.register()
        groupOwnerManager.register()
        log("on resume")
    }

    override fun onDestroy() {
        super.onDestroy()
        groupOwnerManager.stop()
        p2pServiceFinder.stop()
        log("on destroy")
    }

    fun shareGame(str: String, shareImage: Boolean) {
        val iconFile = FileHandle("icon.png")


        val i = AppInviteInvitation.IntentBuilder("Install best game LUDONAIJA")
                .setCallToActionText("INSTALL")
                .setMessage("Install best Ludo game on Play store ")

        if (iconFile.exists()) {
            log("iconfile exists")
            i.setCustomImage(Uri.parse(iconFile.path()))
        }
        startActivityForResult(i.build(), 1000)

//        startActivity(i.build())

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, str)
//        intent.data=
//        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            log("send invitation")
        }

    }
}