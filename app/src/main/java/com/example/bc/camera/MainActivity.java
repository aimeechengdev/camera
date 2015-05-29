package com.example.bc.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends Activity {
    TextView flowerName;
    private File imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button camerButton = (Button)findViewById(R.id.button);
        Button fileButton = (Button)findViewById(R.id.button2);
        flowerName= (TextView)findViewById(R.id.textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openCamera(View view){
        Intent intent = new Intent( );
        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "flower.jpg");
        Uri tmpuri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpuri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, 0);
        flowerName.setText("rose");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Activity.RESULT_OK:
                if(imageFile.exists()){
                    Toast.makeText(this, imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "error when saving", Toast.LENGTH_LONG).show();
                }
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(this, "canceled", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    public void openFile(View view){
        Toast.makeText(this, " orchid", Toast.LENGTH_SHORT).show();
    }
}
