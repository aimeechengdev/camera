package com.example.bc.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


public class MainActivity extends ActionBarActivity {
    TextView flowerName;
    private File imageFile;
    Uri imgUri;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;
    int serverResponseCode = 0;
    String imagePath;
    Button uploadButton;
    String ba1;
    String responseStr;
    Boolean responseFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button camerButton = (Button)findViewById(R.id.button);
        Button fileButton = (Button)findViewById(R.id.button2);
        uploadButton = (Button)findViewById(R.id.uploadImage);
        uploadButton.setEnabled(false);
        flowerName= (TextView)findViewById(R.id.textView);
        upLoadServerUri = "https://flower-aimeechengdev.c9.io/flower1";
        responseFlag = false;
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
    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            doPostRequest();
            return null;
        }
    }

    private void doPostRequest(){
        String urlString = "https://flower-aimeechengdev.c9.io/flowerPhone";
        try
        {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("image",ba1));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            responseStr = EntityUtils.toString(response.getEntity());
            responseFlag = true;
        }
        catch (Exception ex){
            Log.e("Debug", "error1: " + ex.getMessage(), ex);
        }
    }
    public void openCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "flower.jpg");
        imgUri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri;
        if(requestCode==0) {
            selectedImageUri = imgUri;
        }else {
            selectedImageUri = data.getData();
        }
        imagePath = selectedImageUri.getPath();
        getContentResolver().notifyChange(selectedImageUri, null);
        ImageView imageView = (ImageView) findViewById(R.id.photo);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);
            imageView.setImageBitmap(bitmap);
            uploadButton.setEnabled(true);
            Toast.makeText(this, "Image ready to upload", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    public void openFile(View view){
        Toast.makeText(this, "openFile", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 1);
    }

    public void uploadImage(View view){
        Toast.makeText(this, "uploadImage", Toast.LENGTH_SHORT).show();
        dialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Uploading file...", true);
        dialog.setCancelable(true);
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        int arrayLength = ba.length;
        ba1 = Base64.encodeToString(ba, 0, arrayLength, Base64.DEFAULT);
        new Connection().execute();
        for (int i = 0; i < 1000; i++) {
             android.os.SystemClock.sleep(1000);
            if(responseFlag){
                flowerName.setText(responseStr);
                break;
            }
        }
        dialog.dismiss();
        Toast.makeText(this, "uploadImage finished", Toast.LENGTH_SHORT).show();
    }
}
