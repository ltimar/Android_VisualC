package first.androiduploadfiles;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 1;
   // private static final String SERVER_URL = "C://Content/SaveImage.php ";  //target server
   private static final String SERVER_URL = "http://XXX.XXX.XXX.XXX:8080/FileUpload/UploadFiles";  //target server

    ImageView imageToBeUpload;
    Button btnUploadImage;
    EditText editTextUploadImageName;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageToBeUpload = (ImageView) findViewById(R.id.imageToUpload);
        btnUploadImage = (Button) findViewById(R.id.btnUploadImage);
        editTextUploadImageName = (EditText) findViewById(R.id.editTextImageToUpload);

        imageToBeUpload.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        // this method is called any time the image is clicked
        switch (v.getId()) {
            case R.id.imageToUpload:
                // the pictures from the gallery to be displayed
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.btnUploadImage:
                Bitmap image = ((BitmapDrawable) imageToBeUpload.getDrawable()).getBitmap();
                new UploadImage(image, editTextUploadImageName.getText().toString()).execute();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // method called when the user selects a picture from the gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageURI = data.getData();
            //returns the address (in the phone) of the selected image
            imageToBeUpload.setImageURI(selectedImageURI);
            // put the selected image in the imageFrame on device

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://first.androiduploadfiles/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://first.androiduploadfiles/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class UploadImage extends AsyncTask<Void, Void, Void> {
        Bitmap image;
        String name;

        public UploadImage(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);  //allow inputs
                connection.setDoOutput(true); //allow outputs
                connection.setUseCaches(false);  //don't use a cache copy
                connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "KEEP-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", name);

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file_name\";filename=\""
                        + name + "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.write(byteArrayOutputStream.toByteArray());

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                outputStream.flush();
                outputStream.close();

                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(MainActivity.class.getSimpleName(), "Server response is: " + serverResponseMessage + ":" + serverResponseCode);
                if (serverResponseCode == 200) {
                    Log.i(MainActivity.class.getSimpleName(), "File uploaed completed. Check WebServer");
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.d("Lori Debug", e.getMessage());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("Lori Debug", e.getMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("Lori Debug", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Lori Debug", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
        }
    }

}
