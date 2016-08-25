package ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.showbox.showbox.R;

/**
 * Created by Vlade Ilievski on 8/15/2016.
 */
public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);


                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
