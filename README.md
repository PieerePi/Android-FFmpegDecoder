# 项目练习 - Android将MP3转换为PCM（使用FFmpeg）

《音视频开发进展指南-基于Android与iOS平台的实践》书中的[Android-FFmpegDecoder](https://github.com/zhanxiaokai/Android-FFmpegDecoder)项目，使用Android Studio Dolphin和MSYS2环境。

有一些修改，具体如下，

- 增加了fdk-aac/ffmpeg/lame/opus/x264/几个库的[构造脚本](./app/src/main/jni/3rdparty/buildscripts/)，支持armeabi-v7a和arm64-v8a两种ABI

- gradle中配置使用cmake自动编译jni

- 修改了过时的FFmpeg API使用，avcodec_decode_audio4暂时没有修改为avcodec_send_packet和avcodec_receive_frame样式
