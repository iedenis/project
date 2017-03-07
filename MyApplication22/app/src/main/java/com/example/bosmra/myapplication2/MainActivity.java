package com.example.bosmra.myapplication2;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final String host = "192.168.1.33";
    int port = 55555;
    ImageView buckysImageView;
    String filename;
    String filePath;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buckyButton = (Button) findViewById(R.id.buckysButton);
        Button SendButton = (Button) findViewById(R.id.sendButton);
        buckysImageView = (ImageView) findViewById(R.id.buckysImageView);

        if (!hasCamera())
            buckyButton.setEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

    }

    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }


    public void SendPhoto(View view) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Socket socket = null;
                FileInputStream in = null;
                OutputStream outputStream = null;
                try {
                    socket = new Socket(host, port);
                    outputStream = socket.getOutputStream();
                    in = new FileInputStream(filePath);
                    byte[] buffer = new byte[1024]; // 1KB buffer size
                    int length = 0;
                    while ((length = in.read(buffer, 0, buffer.length)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (socket != null) try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                File myFile = new File(selectedImage.toString());
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_change_ip) {

            FirstFragment firstFragment = new FirstFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, firstFragment,
                            firstFragment.getTag())
                    .commit();

        } else if (id == R.id.nav_user_details) {
            SecondFragment secondFragment = new SecondFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, secondFragment,
                            secondFragment.getTag())
                    .commit();

        } else {
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //selectedImage = null;
            //from here

            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            buckysImageView.setImageBitmap(photo);
            selectedImage = data.getData();
            if (selectedImage != null) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                //file path of captured image
                filePath = cursor.getString(columnIndex);
                //file path of captured image
                File f = new File(filePath);
                filename = f.getName();


                Toast.makeText(getApplicationContext(), "Your Filename:" + filename, Toast.LENGTH_LONG).show();
                cursor.close();

                //Savefile(filename, filePath);

            } else if (selectedImage == null) {
                selectedImage = getImageUri(getApplicationContext(), photo);
                //   Toast.makeText(getApplicationContext(), "The URI is:" + selectedImage, Toast.LENGTH_LONG).show();

                File f = new File(getRealPathFromURI(selectedImage));
                filename = f.getName();
                //   Toast.makeText(getApplicationContext(), "Your Filename:" + filename, Toast.LENGTH_LONG).show();
                filePath=f.getPath();
            }
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "User cancelled image capture", Toast.LENGTH_SHORT)
                    .show();

        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void Savefile(String name, String path) {
        File direct = new File(Environment.getExternalStorageDirectory() + "C:\\Users\\Bosmra\\AndroidStudioProjects\\MyApplication\\app");
        File file = new File(Environment.getExternalStorageDirectory() + "C:\\Users\\Bosmra\\AndroidStudioProjects\\MyApplication\\app\\image" + name + ".png");

        if (!direct.exists()) {
            direct.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileChannel src = new FileInputStream(path).getChannel();
                FileChannel dst = new FileOutputStream(file).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}