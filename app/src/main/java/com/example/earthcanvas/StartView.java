package com.example.earthcanvas;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class StartView extends AppCompatActivity {

    Intent intent1;
    String theme;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);
        Typeface rondeB = Typeface.createFromAsset(getAssets(), "gamaamli_regular.ttf");
        TextView text = findViewById(R.id.appname);
        text.setTypeface(rondeB);

        //スタート処理
        findViewById(R.id.startButton).setOnClickListener(
                view->{
                    if(theme==null){
                        TextView textView=findViewById(R.id.themeview);
                        textView.setText("お題ボタンを押してね！");
                    }else{
                        intent1 = new Intent(StartView.this, MapsActivity.class);
                        intent1.putExtra("SEND_DATA",textView.getText().toString());
                        Intent[] intent={intent1};
                        startActivities(intent);
                    }
                }
        );

        //テーマボタン
        findViewById(R.id.themebutton).setOnClickListener(
                view -> {
                    int rdm=(int)(Math.random()*5);
                    String[] themeArray={"とり", "ねこ", "さかな", "たいよう", "き"};
                    theme= themeArray[rdm];
                    textView=findViewById(R.id.themeview);
                    textView.setText(theme);
                }
        );
    }
}
