package com.example.testapp;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private EditText inputText;
    private Button confirmButton;

    private String content;


    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView()
    {
        inputText = findViewById(R.id.inputText);
        confirmButton = findViewById(R.id.confirmButton);
    }

    private void initListener()
    {
        confirmButton.setOnClickListener(this);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status != TextToSpeech.SUCCESS)
                {
                    Toast.makeText(MainActivity.this, "语音初始化失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmButton:
                speek();
                break;

            default:
                break;
        }
    }

    private void speek()
    {
        content = inputText.getText().toString().trim();
        if (TextUtils.isEmpty(content))
        {
            Toast.makeText(this, "请输入要播放的内容", Toast.LENGTH_LONG).show();
            return;
        }

        //判断是否支持下面两种语言
        int result1 = textToSpeech.setLanguage(Locale.US);
        int result2 = textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
        boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
        boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);
        if (a || b)
        {
            Toast.makeText(MainActivity.this, "数据丢失或不支持", Toast.LENGTH_LONG).show();
            return;
        }

        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        textToSpeech.setPitch(1.0f);
        // 设置语速
        textToSpeech.setSpeechRate(1.0f);
        // queueMode用于指定发音队列模式，两种模式选择
        //（1）TextToSpeech.QUEUE_FLUSH：该模式下在有新任务时候会清除当前语音任务，执行新的语音任务
        //（2）TextToSpeech.QUEUE_ADD：该模式下会把新的语音任务放到语音任务之后，等前面的语音任务执行完了才会执行新的语音任务
        textToSpeech.speak(content, TextToSpeech.QUEUE_ADD, null);
    }

    private void close()
    {
        if (textToSpeech.isSpeaking())
        {
            return;
        }
        textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
        textToSpeech.shutdown(); // 关闭，释放资源
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        close();
    }
}
