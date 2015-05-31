package com.example.bc.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;


public class MainActivity extends ActionBarActivity {
    TextView flowerName;
    private File imageFile;
    Uri imgUri;
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
    private void doPostRequest(){

        String urlString = "http://flower-aimeechengdev.c9.io/test";
        try
        {
            HttpClient client = new DefaultHttpClient();
            //HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(urlString);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email1", "email2"));
            nameValuePairs.add(new BasicNameValuePair("email1", "slgjlskjgsg"));
            nameValuePairs.add(new BasicNameValuePair("email2", "xkjfhgkdjfhgkdjfg"));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);
            String response_str = response.toString();
            flowerName.setText(response_str);
        }
        catch (Exception ex){
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
    }
    public void openCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "flower.jpg");
        imgUri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, 0);
        flowerName.setText("rose");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        flowerName.setText("requestCode " + requestCode +" --- " + resultCode + " --- "+imageFile.getAbsolutePath());

                if(imageFile.exists()){
                    Toast.makeText(this, imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    getContentResolver().notifyChange(imgUri, null);
                    ImageView imageView =  (ImageView)findViewById(R.id.photo);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(cr, imgUri);
                        imageView.setImageBitmap(bitmap);
                        doPostRequest();
                    }catch(Exception e){

                    }

                }else{
                    Toast.makeText(this, "error when saving", Toast.LENGTH_LONG).show();
                }

    }

    public void openFile(View view){
        Toast.makeText(this, " orchid", Toast.LENGTH_SHORT).show();
    }
}
