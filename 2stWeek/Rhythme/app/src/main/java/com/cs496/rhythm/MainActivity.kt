package com.cs496.rhythm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.cs496.rhythm.calcPoseVector.getPoseVector
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList

lateinit var currentPose:Array<Double>

class MainActivity : AppCompatActivity(), calcPoseVector {

    private lateinit var cameraExecutor: ExecutorService
    var timer = Timer()
    lateinit var videov: VideoView
    lateinit var posePerTime : ArrayList<DoubleArray>
    lateinit var tempImage : ArrayList<Bitmap> //담에지울거
    lateinit var tempViewer : ImageView
    var time = 0
    lateinit var retriever : MediaMetadataRetriever
    lateinit var mediaPlayer : MediaPlayer

    var timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if(posePerTime.size==time){
                timer.cancel()
                endGame()
                return
            }
            Log.d("ddd","${tempImage.size}번째 중 ${time}번째")
            Log.d("현재 카메라 각도","왼쪽 팔꿈치 : ${currentPose[0]}, 왼쪽 어깨 : ${currentPose[1]}, 오른쪽 어깨 : ${currentPose[2]}, 오른쪽 팔꿈치 : ${currentPose[3]}")
            Log.d("현재 동영상 각도","왼쪽 팔꿈치 : ${posePerTime[time][0]}, 왼쪽 어깨 : ${posePerTime[time][1]}, 오른쪽 어깨 : ${posePerTime[time][2]}, 오른쪽 팔꿈치 : ${posePerTime[time][3]}")

            //currentPose, posePerTime[time]비교

            time++
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videov = findViewById<VideoView>(R.id.videoView)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        loadGame()
        startGame()
    }

    fun loadGame() {

        //포즈디텍터 생성
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)

        //동영상 view 셋팅
        val videopath = "android.resource://com.cs496.rhythm/" + R.raw.test
        val uri = Uri.parse(videopath)
        videov.setVideoURI(uri)
        retriever = MediaMetadataRetriever()
        tempImage = ArrayList()
        posePerTime = ArrayList()
        retriever.setDataSource(application, uri)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
        val millisecond = mediaPlayer.duration
        Log.d("영상길이",millisecond.toString())
        for (i in 0 until millisecond / 3000) {
            val time = ((i * 1000000)*3).toLong()
            val temp = retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST)
            val image = InputImage.fromBitmap(temp, 0)
            if (temp != null) {
                Log.d("dddddd","${time/1000000}초 이미지 등록")
                tempImage.add(temp)
            }
            //이미지 처리 실패시(포즈인식 실패) 앞뒤 영상중 되는 영상 확인
            Log.d("Analyzer","스켈레톤 추출 시도")
            poseDetector.process(image)
                .addOnSuccessListener { result ->
                    Log.d("Analyzer","스켈레톤 추출 성공, 관절갯수${result.allPoseLandmarks.size}")
                    if(result.allPoseLandmarks.size>0 && temp!=null){
                        getPoseVector(result)
                        val currentPose = getPoseVector(result)
                        posePerTime.add(currentPose)
                        Log.d("관절각도","왼쪽 팔꿈치 : ${currentPose[0]}, 왼쪽 어깨 : ${currentPose[1]}, 오른쪽 어깨 : ${currentPose[2]}, 오른쪽 팔꿈치 : ${currentPose[3]}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("Analyzer","이미지 분석 실패 ${e.toString()}")
                }
        }
    }

    fun startGame() {
        videov.start()
        timer.schedule(timerTask, 0, 3000)
    }

    //게임종료후 처리 코
    fun endGame() {
        Log.d("게임","끝------------------------")
        videov.pause()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, YourImageAnalyzer {})
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private class YourImageAnalyzer(function: (Any?) -> Unit) : ImageAnalysis.Analyzer {

        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            Log.d("Analyzer","이미지변환")
            if (mediaImage != null) {
                Log.d("Analyzer","이미지 분석 시도")
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                poseDetector.process(image)
                    .addOnSuccessListener { results ->
                        Log.d("Analyzer","스켈레톤 추출 성공, 관절갯수${results.allPoseLandmarks.size}")
                        if(results.allPoseLandmarks.size>0){
                            getPoseVector(results)
                            currentPose = getPoseVector(results) as Array<Double>
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("Analyzer","이미지 분석 실패 ${e.toString()}")
                    }
                    .addOnCompleteListener{
                        imageProxy.close()
                    }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
