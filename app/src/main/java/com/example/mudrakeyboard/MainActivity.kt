package com.example.mudrakeyboard

import MudraAndroidSDK.GestureType
import MudraAndroidSDK.Mudra
import MudraAndroidSDK.Mudra.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


data class Key(val name: String = "", val button: Button? = null)

class Keyboard constructor(keylist: MutableList<MutableList<Key>>, focusIndex: MutableList<Int>, onPress: (key: Key)->Unit){
    private var keylist: MutableList<MutableList<Key>>
    private var focusIndex = mutableListOf(0, 0)
    private var onPress: (key: Key)->Unit

    init {
        this.keylist = keylist
        this.focusIndex = focusIndex
        this.onPress = onPress
        changeFocusColor()
    }
    fun press(){
        //this.focusIndex = mutableListOf(0,1)
        //this.keylist[focusIndex[0]][focusIndex[1]].press()
        val key: Key = this.keylist[focusIndex[0]][focusIndex[1]]
        this.onPress(key)
    }
    fun right(){
        if(this.focusIndex[1] == this.keylist[0].size-1) {
            this.focusIndex = mutableListOf(focusIndex[0], 0)
        }else{
            this.focusIndex = mutableListOf(focusIndex[0], focusIndex[1]+1)
        }
        changeFocusColor()
    }
    fun left(){
        if(this.focusIndex[1] == 0) {
            this.focusIndex = mutableListOf(focusIndex[0], this.keylist[0].size-1)
        }else{
            this.focusIndex = mutableListOf(focusIndex[0], focusIndex[1]-1)
        }
        changeFocusColor()
    }
    fun up(){
        if(this.focusIndex[0] == 0) {
            this.focusIndex = mutableListOf(this.keylist.size-1, focusIndex[1])
        }else{
            this.focusIndex = mutableListOf(focusIndex[0]-1, focusIndex[1])
        }
        changeFocusColor()
    }
    fun down(){
        if(this.focusIndex[0] == this.keylist.size-1) {
            this.focusIndex = mutableListOf(0, focusIndex[1])
        }else{
            this.focusIndex = mutableListOf(focusIndex[0]+1, focusIndex[1])
        }
        changeFocusColor()
    }
    fun changeFocusColor(){
        for((i, keyline) in this.keylist.withIndex()){
            for((j,key) in keyline.withIndex()){
                Log.d("MainActivity", "focus: i: ${i} j: ${j}, index: ${this.focusIndex}")
                if(this.focusIndex[0] == i && this.focusIndex[1] == j){
                    key.button?.setBackgroundColor(Color.parseColor("#FF00C8C8"))
                    Log.d("MainActivity", "focus correct: i: ${i} j: ${j}, index: ${this.focusIndex}")
                }else{
                    key.button?.setBackgroundColor(0)
                }
            }
        }
    }
}


class MainActivity : AppCompatActivity() {
    private var mMudra: Mudra? = null
    private var mAirMousePosX = 0f
    private var mAirMousePosY = 0f
    private val mScreenWidth = 0
    private  var mScreenHeight = 0
    private  var focusKey = mutableListOf(0, 0) // focus
    private var labelText: TextView? = null
    private var inputText: TextView? = null
    private var resetButton: Button? = null
    private var keyboard: Keyboard? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init Mudra
        requestPermissions()
        initializeMudraConnection()
        keyboard = Keyboard(mutableListOf(
            mutableListOf(
                Key("あ",findViewById<Button>(R.id.button1) as Button),
                Key("か",findViewById<Button>(R.id.button2) as Button),
                Key("さ",findViewById<Button>(R.id.button3) as Button)
            ),
            mutableListOf(
                Key("た",findViewById<Button>(R.id.button4) as Button),
                Key("な",findViewById<Button>(R.id.button5) as Button),
                Key("は",findViewById<Button>(R.id.button6) as Button)
            ),
            mutableListOf(
                Key("ま",findViewById<Button>(R.id.button7) as Button),
                Key("や",findViewById<Button>(R.id.button8) as Button),
                Key("ら",findViewById<Button>(R.id.button9) as Button)
            ),
            mutableListOf(
                Key("-",findViewById<Button>(R.id.button10) as Button),
                Key("わ",findViewById<Button>(R.id.button11) as Button),
                Key("-",findViewById<Button>(R.id.button12) as Button)
            )
        ), mutableListOf(0,0), ::handlePressKey)

        // TextView の設定
        inputText = findViewById<TextView>(R.id.text) as TextView
        labelText = findViewById<TextView>(R.id.label) as TextView
        resetButton = findViewById<Button>(R.id.button_reset) as Button


        resetButton?.setOnClickListener({
            inputText?.setText("")
        })
    }


    private fun handlePressKey(key: Key) {
        Log.d("MainActivity", "Press Key: ${key.name}")
        inputText?.setText("${inputText?.text}${key.name}")
    }

    private fun requestPermissions() {
        Mudra.requestAccessLocationPermissions(this)
        // Required permission for Mudra - note we do not access any of your files/locationj!
        // Location is required for bluetooth,
        // Storage is required for reading gesture calibration file saved during callibration
        requestPermissions(
            arrayOf(
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ),
            1
        )
    }

    private fun initializeMudraConnection() {
        mMudra = Mudra.autoConnectPaired(this)
        if (mMudra != null) {
            mMudra?.setOnGestureReady(onGestureReady)
            mMudra?.setOnFingertipPressureReady(onFingertipPressureReady)
            mMudra?.setOnAirMousePositionChanged(OnAirMousePositionChanged)
            mMudra?.setOnImuQuaternionReady(onImuQuaternionReady)
            mMudra?.setOnDeviceStatusChanged(onDeviceStatusChanged)
            mMudra?.setOnBatteryLevelChanged(onBatteryLevelChanged)
        }
    }

    //Mudra callbacks

    //Mudra callbacks
    var onImuQuaternionReady =
        OnImuQuaternionReady { l, floats ->

        }

    var onGestureReady = OnGestureReady { gestureType ->
        val context = applicationContext
        when (gestureType) {
            GestureType.Tap -> {
                Log.d("MainActivity", "Gesture Type: Tap (Enter)")
                labelText?.setText("Gesture Type: タップ")
                keyboard?.press()
            }
            GestureType.Index -> {
                Log.d("MainActivity", "Gesture Type: Index (Right)")
                labelText?.setText("Gesture Type: 人差し指")
                //focusKey = mutableListOf(0,1)
                //focus()
                keyboard?.right()
            }
            GestureType.Thumb -> {
                Log.d("MainActivity", "Gesture Type: Thumb (Down)")
                labelText?.setText("Gesture Type: 親指")
                keyboard?.down()
            }
        }
    }

    var onFingertipPressureReady =
        OnFingertipPressureReady { v: Float ->
            runOnUiThread {
                Log.d("MainActivity", "Pressure: ${(v * 1000).toInt()} ")
            }
        }

    var OnAirMousePositionChanged =
        OnAirMousePositionChanged { floats ->
            Log.d("MainActivity", "onAirMousePositionChanged: X: ${floats[0]} Y: ${floats[1]} ")

        }


    var onDeviceStatusChanged =
        OnDeviceStatusChanged { b ->
            if (b) {
                runOnUiThread(Thread(Runnable {
                    Log.d("MainActivity", "BLE Address: ${mMudra!!.bluetoothDevice.address} %")
                }))
            }
        }

    var onBatteryLevelChanged = OnBatteryLevelChanged {
        runOnUiThread(Thread(Runnable {
            Log.d("MainActivity", "バッテリー値: ${mMudra?.getBatteryLevel()} %")
        }))
    }

}
