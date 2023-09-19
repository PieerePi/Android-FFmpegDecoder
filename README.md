# 项目练习 - Android将MP3转换为PCM（使用FFmpeg）

《音视频开发进展指南-基于Android与iOS平台的实践》书中的[Android-FFmpegDecoder](https://github.com/zhanxiaokai/Android-FFmpegDecoder)项目，使用Android Studio Dolphin和MSYS2环境。

有一些修改，具体如下，

- 增加了fdk-aac/ffmpeg/lame/opus/x264/几个库的[构造脚本](./app/src/main/jni/3rdparty/buildscripts/)，支持armeabi-v7a和arm64-v8a两种ABI
  - ffmpeg不要指定--ld之类的，只要指定--cc和--cxx
  - ffmpeg要指定--disable-asm，否则arm64-v8a链接时会有问题

- gradle中配置使用cmake自动编译jni

- 修改了过时的FFmpeg API使用，avcodec_decode_audio4暂时没有修改为avcodec_send_packet和avcodec_receive_frame样式

- 131.mp3文件打包在APK中，使用时拷贝到应用专属存储空间，暂时没有让jni直接读资源文件；转换后的PCM文件131.pcm在应用专属存储空间/data/data/com.phuket.tour.ffmpeg_decoder/files目录下，需要使用Device File Explorer来导出
  - ffplay 131.pcm -f s16le -ac 2 -ar 44100

- 在后台线程进行转换操作，防止ANR
