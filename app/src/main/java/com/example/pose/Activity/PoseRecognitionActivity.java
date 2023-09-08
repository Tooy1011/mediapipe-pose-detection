/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.pose.Activity;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pose.Judgement.Judgement;
import com.example.pose.Judgement.JudgementFactory;
import com.example.pose.Judgement.JumpOnAndOffJudgement;
import com.example.pose.Judgement.RopeSkippingJudgement;
import com.example.pose.Judgement.SitUpJudgement;
import com.example.pose.Model.Data;
import com.example.pose.Model.PoseLandMark;
import com.example.pose.R;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;
import com.google.protobuf.InvalidProtocolBufferException;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Date;

import com.example.pose.Model.PoseLandMark;


/**
 * PoseRecognition activity of MediaPipe example apps.
 */
public class PoseRecognitionActivity extends AppCompatActivity {
    private static final String TAG = "PoseRecognitionActivity";
    private static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";
    private static final int NUM_HANDS = 2;
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.BACK;
    // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
    // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
    // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
    // corner, whereas MediaPipe in general assumes the image origin is at top-left.
    private static final boolean FLIP_FRAMES_VERTICALLY = true;

    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    // where the camera-preview frames can be accessed.
    private SurfaceTexture previewFrameTexture;
    // that displays the camera-preview frames processed by a MediaPipe graph.
    private SurfaceView previewDisplayView;
    // Creates and manages an EGLContext.
    private EglManager eglManager;
    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a Surface.
    private FrameProcessor processor;
    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by FrameProcessor and the underlying MediaPipe graph.
    private ExternalTextureConverter converter;
    // ApplicationInfo for retrieving metadata defined in the manifest.
    private ApplicationInfo applicationInfo;
    // Handles camera access via the CameraX Jetpack support library.
    private CameraXPreviewHelper cameraHelper;
    // control
    private TextView text_num;

    private float count;
    private boolean isStart = false;
    private Button bt_ctl;
    private ImageButton bt_exit;

    private Judgement judgement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutResId());
        Bundle bundle = this.getIntent().getExtras();
        String sportsName = bundle.getString("sports");
        judgement = JudgementFactory.getJudgementFactory(sportsName);


        text_num = findViewById(R.id.text_num);
        bt_ctl = findViewById(R.id.bt_ctl);
        bt_ctl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStart) {
                    Date date = new Date();
                    long timestamp = date.getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //设置格式
                    String timeText = format.format(timestamp);
                    Data data = new Data(sportsName, (int) count, timeText);
                    data.assignBaseObjId(0);
                    data.save();
                    Toast.makeText(getApplicationContext(), "运动结束，数据已记录保存", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    isStart = true;
                    bt_ctl.setText("结束");
                    Toast.makeText(getApplicationContext(), "运动开始", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_exit = findViewById(R.id.bt_exit);
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();


        try {
            applicationInfo =
                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Cannot find application info: " + e);
        }
        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(this);
        eglManager = new EglManager(null);
        processor =
                new FrameProcessor(
                        this,
                        eglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        processor
                .getVideoSurfaceOutput()
                .setFlipY(FLIP_FRAMES_VERTICALLY);

        // 获取肢体关键点
        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,
                (packet) -> {
                    Log.v(TAG, "Received Pose landmarks packet.");
                    try {
                        byte[] landmarksRaw = PacketGetter.getProtoBytes(packet);
                        NormalizedLandmarkList poseLandmarks = NormalizedLandmarkList.parseFrom(landmarksRaw);
//                        Log.v(TAG, "[TS:" + packet.getTimestamp() + "] " + getPoseLandmarksDebugString(poseLandmarks));
//                        SurfaceHolder srh = previewDisplayView.getHolder();


                        if (isStart) {
                            //getPoseLandmarksDebugString(poseLandmarks);
                            ArrayList<PoseLandMark> poseMarkers = new ArrayList<PoseLandMark>();
                            for (NormalizedLandmark landmark : poseLandmarks.getLandmarkList()) {
                                PoseLandMark marker = new PoseLandMark(landmark.getX(), landmark.getY(), landmark.getVisibility());
                                poseMarkers.add(marker);
                            }

                            judgement.init(poseMarkers, cameraHelper.isCameraRotated());
                            count = judgement.countTheNum();

                            PoseRecognitionActivity.this.runOnUiThread(() -> text_num.setText(String.valueOf((int) count)));
                        }

                    } catch (InvalidProtocolBufferException exception) {
                        Log.e(TAG, "failed to get proto.", exception);
                    }

                }
        );

        PermissionHelper.checkAndRequestCameraPermissions(this);    // 检查权限
    }


    // Used to obtain the content view for this application. If you are extending this class, and
    // have a custom layout, override this method and return the custom layout.
    protected int getContentViewLayoutResId() {
        return R.layout.activity_pose_recognition;
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter =
                new ExternalTextureConverter(
                        eglManager.getContext(), 2);
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        converter.close();

        // Hide preview display until we re-open the camera again.
        previewDisplayView.setVisibility(View.GONE);
    }

    // 权限 处理用户响应
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        previewFrameTexture = surfaceTexture;

        // Make the display view visible to start showing the preview. This triggers the
        // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
        previewDisplayView.setVisibility(View.VISIBLE);
    }

    protected Size cameraTargetResolution() {
        return null; // No preference and let the camera (helper) decide.
    }

    public void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    onCameraStarted(surfaceTexture);
                });
        CameraHelper.CameraFacing cameraFacing = CameraHelper.CameraFacing.BACK;
        cameraHelper.startCamera(
                this, cameraFacing, /*unusedSurfaceTexture=*/ null, cameraTargetResolution());
    }

    protected Size computeViewSize(int width, int height) {
        return new Size(width, height);
    }

    protected void onPreviewDisplaySurfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        // (Re-)Compute the ideal size of the camera-preview display (the area that the
        // camera-preview frames get rendered onto, potentially with scaling and rotation)
        // based on the size of the SurfaceView that contains the display.
        Size viewSize = computeViewSize(width, height);
        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
        boolean isCameraRotated = cameraHelper.isCameraRotated();

        // Connect the converter to the camera-preview frames as its input (via
        // previewFrameTexture), and configure the output width and height as the computed
        // display size.
        converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
    }

    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
        viewGroup.addView(previewDisplayView);

        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                                Log.d("Surface", "Surface Created");

                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
                                Log.d("Surface", "Surface Changed");
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                                Log.d("Surface", "Surface destroy");
                            }

                        });

    }

    // 提取 landmark 的坐标。
    private static String getPoseLandmarksDebugString(NormalizedLandmarkList poseLandmarks) {
        String poseLandmarkStr = "Pose landmarks: " + poseLandmarks.getLandmarkCount() + "\n";
        ArrayList<PoseLandMark> poseMarkers = new ArrayList<PoseLandMark>();
        for (NormalizedLandmark landmark : poseLandmarks.getLandmarkList()) {
            PoseLandMark marker = new PoseLandMark(landmark.getX(), landmark.getY(), landmark.getVisibility());
            //System.out.println("[" + landmark.getX() + "," + landmark.getY() + "," + landmark.getZ() + "],");
            poseMarkers.add(marker);
        }

        PoseLandMark right_hit = poseMarkers.get(24);
        PoseLandMark right_knee = poseMarkers.get(26);
        PoseLandMark right_foot = poseMarkers.get(28);
        PoseLandMark right_shoulder = poseMarkers.get(12);
        PoseLandMark right_elbow = poseMarkers.get(14);
        PoseLandMark right_hand = poseMarkers.get(16);

        double right_elbow_angle = getAngle(right_shoulder, right_elbow, right_hand);
        double right_hit_angle = getAngle(right_shoulder, right_hit, right_knee);
        double right_knee_angle = getAngle(right_hit, right_knee, right_foot);

        System.out.println("=================================");
        System.out.print("right_elbow_angle: " +right_elbow_angle + "\t");
        System.out.print("right_hit_angle: " + right_hit_angle + "\t");
        System.out.println("right_knee_angle: " + right_knee_angle);
        System.out.println("=================================");


        return poseLandmarkStr;
        /*
            16 右手腕      14 右肘   12 右肩 -> 右臂角度
            15 左手腕      13 左肘   11 左肩 -> 左臂角度
            24 右骨盆      26 右膝   28 右脚踝 -> 右膝角度
            23 左骨盆      25 左膝   27 左脚踝 -> 左膝角度
            14 右肘       12 右肩   24 右骨盆 -> 右下臂角度
            13 左肘       11 左肩   23 左骨盆 -> 左下臂角度
        */
    }

    // 根据三个点计算关节角度
    static double getAngle(PoseLandMark firstPoint, PoseLandMark midPoint, PoseLandMark lastPoint) {
        double result =
                Math.toDegrees(
                        Math.atan2(lastPoint.getY() - midPoint.getY(), lastPoint.getX() - midPoint.getX())
                                - Math.atan2(firstPoint.getY() - midPoint.getY(), firstPoint.getX() - midPoint.getX()));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    static PoseLandMark getMid(PoseLandMark left_hip, PoseLandMark right_hip) {
        float x = (left_hip.getX() + right_hip.getX()) / 2;
        float y = (left_hip.getY() + right_hip.getY()) / 2;
        float visible = (left_hip.getVisible() + right_hip.getVisible()) / 2;
        PoseLandMark mid_hip = new PoseLandMark(x, y, visible);
        return mid_hip;
    }
}