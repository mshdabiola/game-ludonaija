package com.mshdabiola.naijaludo.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.widget.Toast

class BluetoothService(val activity: Activity, val handler: Handler) {

    var mState = BluetoothConstants.STATE_NONE
        set(value) {
            field = value
            handler.obtainMessage(BluetoothConstants.MESSAGE_STATE_CHANGE.ordinal, value.ordinal, -1)
        }
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter().takeIf {
        Toast.makeText(activity, "Bluetooth is not enable", Toast.LENGTH_SHORT).show()
        it != null
    }
    var isHost = false

    init {

    }


}