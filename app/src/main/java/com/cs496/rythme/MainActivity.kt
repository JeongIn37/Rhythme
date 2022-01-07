package com.cs496.rythme

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() ,AngleCalculator{

    //카메라 및 프리뷰
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var previewView : androidx.camera.view.PreviewView

    private lateinit var poseDetector : PoseDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.viewFinder)

        // 권한 체크
        permissionCheck()

        //카메라 스레드
        cameraExecutor = Executors.newSingleThreadExecutor()

        poseDetector = getDetector()
    }

    //권한
    private fun permissionCheck() {

        var permissionList =
            listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!PermissionUtil.checkPermission(this, permissionList)) {
            PermissionUtil.requestPermission(this, permissionList)
        } else {
            startCamera()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "승인")
            startCamera()
        } else {
            Log.d(TAG, "승인 거부")
            onBackPressed()
        }
    }

    //포즈디텍션 셋팅
    private fun getDetector() : PoseDetector
    {
        // Base pose detector with streaming frames, when depending on the pose-detection sdk
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build();

        return PoseDetection.getClient(options)
    }

    //카메라 스타트
   @SuppressLint("UnsafeOptInUsageError")
   private fun startCamera() {
        Log.d(TAG,"카마라 바인딩")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //카메라 프리뷰, Analyzer 바인
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //프리뷰 바인
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            //Analyzer 바인더
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                val mediaImage = imageProxy.image
                Log.d("이미지 분석기", "시도중")
                try {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    val result = poseDetector.process(image)
                    Log.d("이미지 분석기", "성")

                } catch (e: MlKitException) {
                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                }

                imageProxy.close()
            })

            //카메라 뱡항
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

            } catch (e: Exception) {
                Log.d(TAG, "바인딩 실패 $e")
            }
        }, ContextCompat.getMainExecutor(this))
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

}