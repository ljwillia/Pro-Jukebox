package com.example.pro.jukebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;





//comment Below was all here before

public class ProJb extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_jb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pro_jb, menu);
        return true;
    }
}
