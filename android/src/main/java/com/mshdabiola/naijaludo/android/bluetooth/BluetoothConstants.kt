package com.mshdabiola.naijaludo.bluetooth

enum class BluetoothConstants {
    DEVICE_NAME,
    TOAST,

    REQUEST_ENABLE_BT,

    //Constant indicate the current com.mshdabiola.naijaludo.entity.connection state
    STATE_CONNECTED,
    STATE_CONNECTING,
    STATE_LISTEN,
    STATE_NONE,

    //message types sent from bluetooth service handler

    MESSAGE_STATE_CHANGE,
    MESSAGE_READ,
    MESSAGE_DEVICE_NAME,
    MESSAGE_TOAST

}