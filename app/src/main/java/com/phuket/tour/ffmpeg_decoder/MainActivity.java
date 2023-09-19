package com.phuket.tour.ffmpeg_decoder;

import com.phuket.tour.decoder.Mp3Decoder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("songstudio");
    }
    private static String TAG = "MainActivity";

    /** 原始的文件路径 **/
    private static String mp3FilePath = "131.mp3";
    /** 解码后的PCM文件路径 **/
    private static String pcmFilePath = "131.pcm";

    private Button mp3_encoder_btn;

    private boolean recording = false;

    private Handler mHandler;
    private static final int CONVERT_DONE_FLAG = 100000;
    private static final String CONVERT_DURATION = "DURATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CONVERT_DONE_FLAG) {
                    int wasteTimeMills = msg.getData().getInt(CONVERT_DURATION);
                    Toast.makeText(MainActivity.this, "MP3到PCM的转换已经完成，耗时" + wasteTimeMills + "毫秒", Toast.LENGTH_LONG).show();
                    recording = false;
                }
            }
        };
        mp3_encoder_btn = (Button) findViewById(R.id.mp3_encoder_btn);
        mp3_encoder_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    recording = true;
                    Thread convertThread = new Thread(new ConvertThread(), "ConvertThread");
                    convertThread.start();
                } else {
                    Toast.makeText(MainActivity.this, "正在转换中,请稍等", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class ConvertThread implements Runnable {

        @Override
        public void run() {
            long startTimeMills = System.currentTimeMillis();
            Mp3Decoder decoder = new Mp3Decoder();
            CopyAssets(getApplicationContext(), mp3FilePath,
                    getApplicationContext().getFilesDir().getAbsolutePath(), mp3FilePath);
            String lmp3FilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + mp3FilePath;
            String lpcmFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + pcmFilePath;
            int ret = decoder.init(lmp3FilePath, lpcmFilePath);
            if(ret >= 0) {
                decoder.decode();
                decoder.destroy();
            } else {
                Log.i(TAG, "Decoder Initialized Failed...");
            }
            int wasteTimeMills = (int)(System.currentTimeMillis() - startTimeMills);
            Log.i(TAG, "Decode Mp3 Waste TimeMills : " + wasteTimeMills + "ms");
            Message msg = new Message();
            msg.what = CONVERT_DONE_FLAG;
            Bundle bundle = new Bundle();
            bundle.putInt(CONVERT_DURATION, wasteTimeMills);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    /**
 　　*
 　　* @param myContext
 　　* @param ASSETS_NAME 要复制的文件名
 　　* @param savePath 要保存的路径
 　　* @param saveName 复制后的文件名
 　　*/
    public static void CopyAssets(Context myContext, String ASSETS_NAME,
                            String savePath, String saveName) {
        String filename = savePath + "/" + saveName;
        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}