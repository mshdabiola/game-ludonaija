package com.mshdabiola.naijaludo.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.files.FileHandle
import com.google.android.gms.ads.*
import com.google.android.gms.appinvite.AppInviteInvitation
import com.mshdabiola.naijaludo.R
import com.mshdabiola.naijaludo.android.wifipeer2peer.P2pServiceFinder
import com.mshdabiola.naijaludo.entity.connection.*
import com.mshdabiola.naijaludo.screen.NaijaLudo
import kotlinx.coroutines.*
import java.util.*
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

class AndroidLauncher : AndroidApplication(), CoroutineScope by CoroutineScope(Dispatchers.Default) {


    private var naijaludo: NaijaLudo by Delegates.notNull()
    private var p2pServiceFinder by Delegates.notNull<P2pServiceFinder>()


    var coordinatorLayout by Delegates.notNull<Deferred<CoordinatorLayout>>()
//            async { CoordinatorLayout(this@AndroidLauncher) }


    var layout by Delegates.notNull<Deferred<ConstraintLayout>>()
//            async { ConstraintLayout(this@AndroidLauncher) }

    fun log(str: String) {
        Log.e(this::class.java.name, "android log $str")
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    val connectInterfaceAnd = object : ConnectInterfaceAnd {
        override fun startDiscovery() {
            p2pServiceFinder.startDiscovery()
//            groupOwnerManager.start()
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
//            log("discover peer")
//            p2pServiceFinder.discoverPeer()
        }

        override fun shareApp(str: String, shareImage: Boolean) {

            shareGame(str, shareImage)
        }

        override fun log(str: String) {
            this@AndroidLauncher.log("From Core: $str")
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
            log("clients list ${peers.size}")
            val peerss = peers.map { Pair(it.deviceName, it.deviceAddress) }
            connectInterface.onPeerDevicesChanged(peerss)

        }

        override fun onGroupInfo(group: WifiP2pGroup?) {
            log("on group info")
            group?.let {
                log("groupName: ${it.networkName}")
                log("password: ${it.passphrase}")
                log("isOwner: ${it.isGroupOwner}")
                log("ownerName: ${it.owner.deviceName}")
                log("owner Address: ${it.owner.deviceAddress}")
                log("owner primary Device: ${it.owner.primaryDeviceType}")
                log("owner secondary Device: ${it.owner.secondaryDeviceType}")
                log("owner status: ${it.owner.status}")
                log("interface: ${it.`interface`}")
                log("clientList: ${it.clientList}")

            }
            group?.let {

                connectInterface.onGroupInfo(P2pGroup(it.passphrase, it.networkName, it.isGroupOwner))
            }
        }

        override fun onDiscoverStateChanged(start: Boolean) {
            log("on discovery state changed start:$start")
            connectInterface.onDiscovery(start)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val time = measureTimeMillis {
            coordinatorLayout = async { CoordinatorLayout(this@AndroidLauncher) }
            layout = async { ConstraintLayout(this@AndroidLauncher) }

            coordinatorLayout.start()
            layout.start()


            val config = AndroidApplicationConfiguration()
            config.useWakelock = true
            config.useImmersiveMode = true

            val timeToInitNaijaLudo = measureTimeMillis {
                naijaludo = NaijaLudo()
            }
            log("time to init Naijaludo is $timeToInitNaijaLudo")

//        initialize(naijaludo, config)
            val timeSetupUi = measureTimeMillis {
                setUpLayout(initializeForView(naijaludo, config).apply { id = R.id.ludo })
            }
            log("time to setup ui is $timeSetupUi")

            launch { setConnection() }
            launch { setUpAd() }

        }

        log("create total time is $time")
    }


    override fun onPause() {
        super.onPause()
//        p2pServiceFinder.unRegister()

        log("on pause")
    }

    override fun onResume() {
        super.onResume()
//        p2pServiceFinder.register()

        log("on resume")


    }

    override fun onDestroy() {
        super.onDestroy()
//        groupOwnerManager.stop()
        p2pServiceFinder.unRegister()
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

    private fun setUpLayout(naijaLudoView: View) = runBlocking {


        val set = ConstraintSet()



        coordinatorLayout.await().addView(layout.await(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        layout.await().addView(naijaLudoView)
        setContentView(coordinatorLayout.await())
        set.constrainHeight(R.id.ludo, ConstraintSet.WRAP_CONTENT)
        set.constrainWidth(R.id.ludo, ConstraintSet.MATCH_CONSTRAINT)

        set.connect(R.id.ludo, ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)

        set.connect(R.id.ludo, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        set.connect(R.id.ludo, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        set.connect(R.id.ludo, ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)


        set.applyTo(layout.await())
    }

    private suspend fun setUpAd() {
        val testDevice = Arrays.asList("E5E6F4890A489D152144C2B40672C646")
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder().setTestDeviceIds(testDevice).build())


        val testBanner = "ca-app-pub-3940256099942544/6300978111"
        val banner = AdView(this)
        banner.id = R.id.banner
        banner.adSize = AdSize.BANNER

        banner.adUnitId = testBanner
        val adRequest = AdRequest.Builder().build()
        val isTest = adRequest.isTestDevice(this)
        log("is this device test $isTest")

        val paras = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        paras.gravity = Gravity.BOTTOM




        withContext(Dispatchers.Main) {
            coordinatorLayout.await().addView(banner, paras)
            banner.loadAd(adRequest)
        }


    }

    private fun setConnection() {
        connectInterface = naijaludo.connectInterface

        naijaludo.connectInterfaceAnd = connectInterfaceAnd

        p2pServiceFinder = P2pServiceFinder(this, discoveryCallback)
        p2pServiceFinder.register()
    }
}