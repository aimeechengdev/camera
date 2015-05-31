package com.example.bc.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button camerButton = (Button)findViewById(R.id.button);
        Button fileButton = (Button)findViewById(R.id.button2);
        flowerName= (TextView)findViewById(R.id.textView);
        upLoadServerUri = "https://flower-aimeechengdev.c9.io/flower1";
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
        String urlString = "https://flower-aimeechengdev.c9.io/testpost";
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
            int statusCode = response.getStatusLine().getStatusCode();

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
// Response Body
            String responseBody = responseHandler.handleResponse(response);
  //          Toast.makeText(this, Integer.toString(statusCode), Toast.LENGTH_LONG).show();
  //          Toast.makeText(this, responseBody, Toast.LENGTH_LONG).show();
         //   flowerName.setText(responseBody);
        }
        catch (Exception ex){
          //  Toast.makeText(this, "error in post", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, "before dopost", Toast.LENGTH_LONG).show();
                     //   new Connection().execute();
                    }catch(Exception e){

                    }

                }else{
                    Toast.makeText(this, "error when saving", Toast.LENGTH_LONG).show();
                }

    }

    public void openFile(View view){
        Toast.makeText(this, "openFile", Toast.LENGTH_SHORT).show();
    }
    public void uploadImage(View view){
        Toast.makeText(this, "uploadImage", Toast.LENGTH_SHORT).show();
        dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                uploadFile(imageFile.getAbsolutePath());
            }
        }).start();
    }
    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" +imageFile.getAbsolutePath());
            return 0;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="+ fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(MainActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(MainActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
}
