package dji.sampleV5.aircraft.models

import android.util.Log
import android.view.SurfaceView
import com.dji.wpmzsdk.common.utils.kml.model.Location2D
import dji.sampleV5.aircraft.util.ToastUtils
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.RemoteControllerKey
import dji.sdk.keyvalue.key.OcuSyncKey
import dji.sdk.keyvalue.key.AirLinkKey
import dji.sdk.keyvalue.key.BatteryKey
import dji.sdk.keyvalue.key.CameraKey
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.common.Velocity3D
import dji.sdk.keyvalue.value.flightcontroller.FlightMode
import dji.sdk.keyvalue.key.GimbalKey
import dji.sdk.keyvalue.key.co_v.KeyRotateBySpeed
import dji.sdk.keyvalue.value.camera.CameraMode
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.sdk.keyvalue.value.common.LocationCoordinate2D
import dji.sdk.keyvalue.value.common.LocationCoordinate3D
import dji.sdk.keyvalue.value.flightcontroller.DroneType
import dji.sdk.keyvalue.value.flightcontroller.FlyToOperationType
import dji.sdk.keyvalue.value.flightcontroller.FlyToPointInfo
import dji.sdk.keyvalue.value.flightcontroller.LEDsSettings
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotation
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotationMode
import dji.sdk.keyvalue.value.gimbal.GimbalMode
import dji.sdk.keyvalue.value.gimbal.GimbalSpeedRotation
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.et.action
import dji.v5.et.create
import dji.v5.et.get
import dji.v5.et.set
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.interfaces.ICameraStreamManager
import dji.v5.utils.common.ContextUtil
import dji.v5.utils.common.DiskUtil
import dji.v5.utils.common.LogUtils
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dji.sampleV5.aircraft.models.BasicAircraftControlVM.Companion.latestFrame
import dji.sdk.keyvalue.value.camera.CameraStorageLocation
import dji.v5.manager.datacenter.camera.StreamInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class BasicAircraftControlVM : DJIViewModel() {
    companion object {
        var latestFrame: ByteArray = ByteArray(0)
        var filePath=""
    }

    fun rgbaToJpeg(rawData: ByteArray, width: Int, height: Int, quality: Int): ByteArray {
        // Convert the raw RGBA byte array to a Bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Assuming rawData is in the correct format, if not you might need to adjust the way rawData is used
        bitmap.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(rawData))
        val stream = ByteArrayOutputStream()
        // Compress and convert the Bitmap to JPEG format, then to a byte array
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        // Get the compressed JPEG as a byte array
        val jpegData = stream.toByteArray()
        // Return the JPEG byte array
        return jpegData
    }

// ffplay -f h264 -fflags nobuffer -flags low_delay -framedrop tcp://192.168.68.101:12345
// vlc --network-caching=155  --demux h264 udp://@:12345
    fun startStreamingUdp(ip:String, port:Int) {
        val cameraIndex = ComponentIndexType.LEFT_OR_MAIN
        clientAddresses.add(0, InetSocketAddress(ip, port))
        // Start the UDP broadcaster in a coroutine to avoid blocking the main thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = DatagramSocket() // Create a DatagramSocket to send data
                println("UDP Server started")

                // Register the stream listener
                MediaDataCenter.getInstance().cameraStreamManager.addReceiveStreamListener(cameraIndex,
                    ICameraStreamManager.ReceiveStreamListener { bytes: ByteArray, offset: Int, length: Int, streamInfo: StreamInfo ->
                        // Forward the received bytes to the registered clients
                        forwardStreamToClients(socket, bytes, offset, length)
                    })

            } catch (e: Exception) {
                println("Error starting UDP server: ${e.message}")
            }
        }
    }

    // Keep a list of client addresses
    private val clientAddresses: MutableList<InetSocketAddress> = mutableListOf(
    )

    // Function to forward the camera stream to all registered clients
    private fun forwardStreamToClients(socket: DatagramSocket, bytes: ByteArray, offset: Int, length: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            clientAddresses.forEach { address ->
                try {
                    val packet = DatagramPacket(bytes, offset, length, address)
                    socket.send(packet)
                } catch (e: Exception) {
                    println("Error sending stream to client: ${e.message}")
                }
            }
        }
    }        fun startStreamingFpv() {
            /*
            MediaDataCenter.getInstance().cameraStreamManager.addFrameListener(ComponentIndexType.LEFT_OR_MAIN, ICameraStreamManager.FrameFormat.RGBA_8888,
                object : ICameraStreamManager.CameraFrameListener {
                    override fun onFrame(frameData: ByteArray, offset: Int, length: Int, width: Int, height: Int, format: ICameraStreamManager.FrameFormat) {
                        println("aa:" + length)
                    }})
    */
        MediaDataCenter.getInstance().cameraStreamManager.addFrameListener(
            ComponentIndexType.LEFT_OR_MAIN,
            ICameraStreamManager.FrameFormat.RGBA_8888,
            object : ICameraStreamManager.CameraFrameListener {
                override fun onFrame(frameData: ByteArray, offset: Int, length: Int, width: Int, height: Int, format: ICameraStreamManager.FrameFormat) {
                    val dirs = File(DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), "CameraStreamImageDir"))
                    if (!dirs.exists()) {
                        dirs.mkdirs()
                    }
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    try {
                        byteArrayOutputStream.write(frameData, offset, length)
                       latestFrame = rgbaToJpeg(byteArrayOutputStream.toByteArray(), width,  height, 20);
                        filePath = File(dirs.absolutePath, "fpv2.jpg").toString()
                        val fileOutputStream = FileOutputStream(filePath)
                        fileOutputStream.write(latestFrame)
                        fileOutputStream.close()
                        byteArrayOutputStream.close()
                    } finally {
                        // Make sure to close the ByteArrayOutputStream
                    }

                    // Because only one frame needs to be saved, you need to call removeOnFrameListener here
                    // If you need to read frame data for a long time, you can choose to actually call remove OnFrameListener according to your needs
                }
            })
    }

    fun getFpvFrameByteArray(): ByteArray {
        return latestFrame
    }
    fun getFpvFrameFilePath(): String {
        return filePath
    }

    fun startTakeOff(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        dji.v5.utils.common.LocationUtil.getLastLocation()
        FlightControllerKey.KeyStartTakeoff.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
        Log.d("DroneType", DroneType.DJI_MINI_2.value().toString());
    }

    fun startLanding(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartAutoLanding.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
    }
    fun setHomepoint(loc: Location2D, callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyHomeLocationUsingCurrentRemoteControllerLocation.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
    }

    fun startMotor(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyTurnOnTheMotor.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
    }


    fun setGimbalMode(free: Boolean) {
        if (free)
            GimbalKey.KeyGimbalMode.create().set(GimbalMode.FREE, {}, {})
        else
            GimbalKey.KeyGimbalMode.create().set(GimbalMode.YAW_FOLLOW, {}, {})
    }

    fun setGimbalAttitude(pitch: Double, yaw: Double, roll: Double,
                           pitchIgnored: Boolean, yawIgnored: Boolean, rollIgnored: Boolean,
                           duration: Double,
                           absoluteAngle:Boolean
                           ) {
        val rotation = GimbalAngleRotation();
        if (absoluteAngle)
            rotation.mode = GimbalAngleRotationMode.ABSOLUTE_ANGLE
        else
            rotation.mode = GimbalAngleRotationMode.RELATIVE_ANGLE
        rotation.pitch = pitch
        rotation.yaw = yaw
        rotation.roll = roll
        rotation.pitchIgnored = pitchIgnored
        rotation.yawIgnored = yawIgnored
        rotation.rollIgnored = rollIgnored
        rotation.duration = duration
        GimbalKey.KeyRotateByAngle.create().action(rotation, {}, { e: IDJIError -> })
    }
    
    fun getAircraftLocation3D() = FlightControllerKey.KeyAircraftLocation3D.create().get(LocationCoordinate3D(-99.0,-99.0,-99.0));

    fun getAltitude() = FlightControllerKey.KeyAltitude.create().get();
    fun getUltrasonicHeight() = FlightControllerKey.KeyUltrasonicHeight.create().get();
    fun getStickLeftHorizontal() = RemoteControllerKey.KeyStickLeftHorizontal.create().get();
    fun getStickLeftVertical() = RemoteControllerKey.KeyStickLeftVertical.create().get();
    fun getStickRightHorizontal() = RemoteControllerKey.KeyStickRightHorizontal.create().get();
    fun getStickRightVertical() = RemoteControllerKey.KeyStickRightVertical.create().get();


    fun getAircraftSpeed() = FlightControllerKey.KeyAircraftVelocity.create().get( Velocity3D(-99.0, -99.0, -99.0))
    fun getHomeLocation() = FlightControllerKey.KeyHomeLocation.create().get();
    fun getTakeoffLocationAltitude() = FlightControllerKey.KeyTakeoffLocationAltitude.create().get(0.0);

    fun getIsMotorOn() = FlightControllerKey.KeyAreMotorsOn.create().get(false);
    fun getIsFlying() = FlightControllerKey.KeyIsFlying.create().get(false);
    fun getFlightMode() = FlightControllerKey.KeyFlightMode.create().get();
    fun getDroneType(): String {
        return FlightControllerKey.KeyDroneType.create().get().toString()
    }
    fun setCoordinatedTurnEnabled(enable: Boolean) = FlightControllerKey.KeyCoordinatedTurnEnabled.create().set(false,{}, { e: IDJIError -> Log.e("QQ", e.description())})
    fun getGroundStationModeEnabled(): Boolean? {
        return FlightControllerKey.KeyGroundStationModeEnabled.create().get()
    }
    fun getVirtualStickControlModeEnabled(): Boolean? {
        return FlightControllerKey.KeyVirtualStickControlModeEnabled.create().get()
    }
    fun setKeyLEDsSettings( frontLEDsOn:Boolean, statusIndicatorLEDsOn:Boolean, rearLEDsOn:Boolean, navigationLEDsOn:Boolean) = FlightControllerKey.KeyLEDsSettings.create().
    set(LEDsSettings(frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn))
    fun setHomeLocation(lat :Double, lon: Double) {
        FlightControllerKey.KeyHomeLocation.create().set(LocationCoordinate2D(lat, lon))
    }

    fun startRecord() {
        CameraKey.KeyCameraMode.create().set(CameraMode.VIDEO_NORMAL)
        return CameraKey.KeyStartRecord.create().action()
    }
    fun stopRecord() {
        return CameraKey.KeyStopRecord.create().action()
    }
    fun startShootPhoto() {
        CameraKey.KeyCameraMode.create().set(CameraMode.PHOTO_NORMAL)
        return CameraKey.KeyStartShootPhoto.create().action()
    }
    fun stopShootPhoto() {
        return CameraKey.KeyStartShootPhoto.create().action()
    }

    fun getAircraftName(): String? {
        return FlightControllerKey.KeyAircraftName.create().get()
    }
    fun setAircraftName(name: String): String? {
        FlightControllerKey.KeyAircraftName.create().set(name)
        return "OK"
    }
    fun getChargeRemainingInPercent(): Int? {
        return BatteryKey.KeyChargeRemainingInPercent.create().get()
    }
    fun startGoHome(): String? {
        FlightControllerKey.KeyStartGoHome.create().action()
        return "OK"
    }

    fun formatStorageSD(): String {
        CameraKey.KeyFormatStorage.create().set(CameraStorageLocation.SDCARD)
        return "OK"
    }


    //djiSdkModel.performActionWithOutResult(KeyTools.createKey(CameraKey.KeyStartRecord, cameraIndex));
    //fun getProto4() = FlightControllerKey.FlyToPointInfo.create().set(FlyToPointInfo().point)
//KeyCoordinatedTurnEnabled
    //KeyTiltInAttiNormal
    //FlyToPointInfo
    // KeyFlightControlAdvancedParameterRange
}