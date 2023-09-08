# 利用 MediaPipe 实现运动识别及计数


> 写于 2023.04

相关版本如下：
```
Ubuntu 22.04（虚拟机）
adb 
	Android Debug Bridge version 1.0.41
	Version 33.0.3-8952118
SDK 30.0.3
NDK 21.4.7075529
bazel 5.2.0
Android Studio Electric Eel | 2022.1.1 Patch 1（win10）
```

## 安装 Mediapipe 框架

### 安装依赖环境

```bash
sudo apt-get update && sudo apt-get install -y build-essential git python zip adb openjdk-8-jdk
```

### 安装 bazel 编译环境

下载二进制安装包：[bazel-5.2.0-installer-linux-x86_64.sh](https://mirrors.huaweicloud.com/bazel/5.2.0/)
_有的版本可能会报错，可以根据报错提示下载所要求的版本（修改上方链接的版本号可找到其他版本的 bazel）_

```bash
sudo apt install g++ unzip zip
# chmod +x bazel-<version>-installer-linux-x86_64.sh
chmod +x bazel-5.2.0-installer-linux-x86_64.sh
sudo ./bazel-5.2.0-installer-linux-x86_64.sh
```

### 安装 adb 命令

_要与 windows 安装的 adb 版本一致_

```bash
apt install adb
adb version
```
但是此方法安装的adb版本可能并不与windows下的abd版本一致，可通过如下命令下载指定版本的adb：

```bash
# wget https://dl.google.com/android/repository/platform-tools_r<specific-version>-linux.zip
# 下载最新版adb
wget https://dl.google.com/android/repository/platform-tools-latest-linux.zip
unzip platform-tools-latest-linux.zip
sudo cp platform-tools/adb /usr/bin/adb
```

### 克隆 Mediapipe 源码

```bash
git clone https://github.com/google/mediapipe.git
```

### 安装 OpenCV 环境

```bash
sudo apt install libopencv-core-dev libopencv-highgui-dev \
libopencv-calib3d-dev libopencv-features2d-dev \
libopencv-imgproc-dev libopencv-video-dev
```

## 编译 MediaPipe 的 Android aar 包

### 安装 SDK 环境和 NDK 环境

> 通过 Linux 环境工具 Command-line tools 进行安装

_注意：SDK版本要求 **28.0.3** 版本及以上，NDK版本要求介于 **18** 和 **21** 之间_，版本不符合要求后续编译会报错

在官网的最底下有下载地址 https://developer.android.google.cn/studio
找到 Command line tools only

```bash
wget -P /home/android-sdk/ https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip
unzip commandlinetools-linux-7583922_latest.zip
```
需要执行的相关命令在/home/android-sdk/cmdline-tools/bin下
`cd /home/android-sdk/cmdline-tools/bin`
执行 `./sdkmanager --list --channel=0` 可查看所有版本

```bash
./sdkmanager "build-tools;30.0.3" "platforms;android-30" "ndk;21.4.7075529"
```

### 配置到环境变量

```bash
export ANDROID_HOME=/home/android-sdk/
export ANDROID_NDK_HOME=$PATH:$ANDROID_HOME/ndk/21.4.7075529
```

### 创建 Mediapipe 生成 Android aar 的编译文件

在路径 mediapipe/examples/android/src/java/com/google/mediapipe/apps 下新建文件

```bash
cd mediapipe/examples/android/src/java/com/google/mediapipe/apps/
mkdir buid_aar && cd buid_aar
vim BUILD
```
BUILD 文件内容如下:

```bash
load("//mediapipe/java/com/google/mediapipe:mediapipe_aar.bzl", "mediapipe_aar")
mediapipe_aar(
	name = "mediapipe_pose_tracking",
	calculators = ["//mediapipe/graphs/pose_tracking:pose_tracking_gpu_deps"],
)
```
1、生成安卓aar文件（注意在 mediapipe 根目录下执行命令）

```bash
bazel build -c opt --strip=ALWAYS --host_crosstool_top=@bazel_tools//tools/cpp:toolchain --fat_apk_cpu=arm64-v8a,armeabi-v7a mediapipe/examples/android/src/java/com/google/mediapipe/apps/build_aar:mediapipe_pose_tracking
```
生成的文件在 bazel-bin/mediapipe/examples/android/src/java/com/google/mediapipe/apps/build_aar/ 目录下

2、生成Mediapipe的二进制图

```bash
bazel build -c opt mediapipe/graphs/pose_tracking:pose_tracking_gpu_binary_graph
```
生成文件路径为 bazel-bin/mediapipe/graphs/pose_tracking/pose_tracking_gpu.binarypb


## 构建Android项目

- 在 Windows10 下使用 Android Studio 创建一个空项目
- 将编译生成的 aar 文件到 Android Stdio 工程的 app/libs/ 目录下
- 复制以下文件到Android Stdio工程的app/src/main/assets/目录下
  ```bash
    bazel-bin/mediapipe/graphs/pose_tracking/pose_tracking_gpu.binarypb
    mediapipe/modules/pose_detection/pose_detection.tflite
    mediapipe/modules/pose_landmark/pose_landmark_full.tflite
  ```
    目前 MediaPipe 中没有后两个文件，官方文档也还在更新
    可以到 [https://storage.googleapis.com/mediapipe-assets/](https://storage.googleapis.com/mediapipe-assets/) 查询
  
    [pose_detection.tflite](https://storage.googleapis.com/mediapipe-assets/pose_detection.tflite)
  
    [pose_landmark_full.tflite](https://storage.googleapis.com/mediapipe-assets/pose_landmark_full.tflite)

项目代码可参考 https://gitee.com/luo_zhi_chengMediapipe_pose_Tracking_AAR_example

计数逻辑可参考 https://mc.dfrobot.com.cn/thread-311550-1-1.html?fromuid=827784


## 参考

[mediapipe教程6：在安卓上运行mediapipe的poseTracking](https://blog.csdn.net/luozhichengaichenlei/article/details/117319518)

[Linux安装Android Sdk「建议收藏」](https://cloud.tencent.com/developer/article/2108851)

[Linux 环境下 搭建Android SDK 和Android NDK](https://cloud.tencent.com/developer/article/2188266)

[Ubuntu20.04部署android版mediapipe踩坑记录（持续更新。。。）](https://blog.csdn.net/qq_36577574/article/details/120223281)

