package com.cs496.rhythm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.cs496.rhythm.calcPoseVector.getPoseVector
import com.cs496.rhythm.calcPoseVector.grade
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf

lateinit var currentPose:DoubleArray

class MainActivity : AppCompatActivity(), calcPoseVector {

    private lateinit var cameraExecutor: ExecutorService
    var timer = Timer()
    var Loader = Timer()
    lateinit var videov: VideoView
    lateinit var posePerTime : ArrayList<DoubleArray>
    lateinit var tempImage : ArrayList<Bitmap> //담에지울거
    lateinit var scoreboard : TextView
    var time = 0
    lateinit var retriever : MediaMetadataRetriever
    lateinit var mediaPlayer : MediaPlayer
    var CheckSet : Int = 0
    var finalScore = 0
    //var selectedVideo = intent.getIntExtra("video", 0) //intent R.raw file

    var LoadCheck: TimerTask = object : TimerTask() {
        override fun run() {
            Log.d("로딩중","${posePerTime.size}/${CheckSet}")
            if(posePerTime.size == CheckSet){
                Loader.cancel()
                startGame()
                return
            }
        }
    }

    var ScoreCheck: TimerTask = object : TimerTask() {
        override fun run() {
            if(posePerTime.size==time){
                timer.cancel()
                endGame()
                return
            }
            Log.d("게임 진행도","${tempImage.size}번째 중 ${time}번째")
            Log.d("현재 카메라 각도","왼쪽 팔꿈치 : ${currentPose[0]}, 왼쪽 어깨 : ${currentPose[1]}, 오른쪽 어깨 : ${currentPose[2]}, 오른쪽 팔꿈치 : ${currentPose[3]}")
            Log.d("현재 동영상 각도","왼쪽 팔꿈치 : ${posePerTime[time][0]}, 왼쪽 어깨 : ${posePerTime[time][1]}, 오른쪽 어깨 : ${posePerTime[time][2]}, 오른쪽 팔꿈치 : ${posePerTime[time][3]}")

            var score:Int = grade(currentPose,posePerTime[time]);
            Log.d("점수",score.toString())
            scoreboard.text = score.toString()
            finalScore+=score;

            time++
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videov = findViewById<VideoView>(R.id.videoView)
        scoreboard = findViewById<TextView>(R.id.matchScore)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        loadGame()
    }
    fun loadGame() {

        //Log.d("확--------------------------")
        //포즈디텍터 생성
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(options)

        //동영상 view 셋팅
        //${selectedVideo.toString()}
//        val tmp = selectedVideo?.toInt()

/*        val afd : AssetFileDescriptor = getAssets().openFd("test.mp4");
        videov.setVideoPath(afd)
        retriever = MediaMetadataRetriever()
        tempImage = ArrayList()
        posePerTime = ArrayList()
        retriever.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength())
        mediaPlayer = MediaPlayer,  */
        val videopath = "android.resource://com.cs496.rhythm/" + R.raw.testrasputin40s
        val uri = Uri.parse(videopath)
        videov.setVideoURI(uri)
        //videov.setVideoPath("content://com.cs496.rhythm/test.mp4")
        retriever = MediaMetadataRetriever()
        tempImage = ArrayList()
        posePerTime = ArrayList()
        retriever.setDataSource(application, uri)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
        val millisecond = mediaPlayer.duration
        CheckSet=millisecond / 3000
        Log.d("영상길이",millisecond.toString())
        for (i in 0 until CheckSet) {
            val time = ((i * 1000000)*3).toLong()
            val temp = retriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST)
            val image = InputImage.fromBitmap(temp, 0)
            if (temp != null) {
                Log.d("동영상 분석 전처리","${time/1000000}초 이미지 등록")
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
                        Log.d("관절각도","왼쪽 팔꿈치 : ${currentPose[0]}, 왼쪽 어깨 : ${currentPose[1]}, 오른쪽 어깨 : ${currentPose[2]},   팔꿈치 : ${currentPose[3]}")
                    }
                    else {
                        posePerTime.add(DoubleArray(8))
                    }

                }
                .addOnFailureListener { e ->
                    Log.d("Analyzer","이미지 분석 실패 ${e.toString()}")
                }
        }
        Loader.schedule(LoadCheck, 0, 100)
    }

    fun startGame() {
        videov.start()
        timer.schedule(ScoreCheck, 5000, 3000)
        //로딩 끝
    }

    //게임종료후 처리 코
    fun endGame() {
        //최종점수
        finalScore/=CheckSet
        scoreboard.text = finalScore.toString()

        Log.d("게임","끝------------------------")
        videov.pause()
    }

    private fun startCamera() {
        Log.d("카메라","시작")
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
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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
            if (mediaImage != null) {
                Log.d("Analyzer","이미지 분석 시도")
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                poseDetector.process(image)
                    .addOnSuccessListener { results ->
//                        Log.d("Analyzer","스켈레톤 추출 성공, 관절갯수${results.allPoseLandmarks.size}")
                        if(results.allPoseLandmarks.size>0){
                            getPoseVector(results)
                            currentPose = getPoseVector(results)
                        }
                        else {
                            currentPose = DoubleArray(8)
                        }
                    }
                    .addOnFailureListener { e ->
//                        Log.d("Analyzer","이미지 분석 실패 ${e.toString()}")
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
