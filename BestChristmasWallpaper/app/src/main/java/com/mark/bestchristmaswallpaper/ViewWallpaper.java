package com.mark.bestchristmaswallpaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewWallpaper extends AppCompatActivity {
        String title ,image ,imageFileName;
        DisplayMetrics displayMetrics;
        BitmapDrawable bitmapDrawable;
        Bitmap bitmap;
        WallpaperManager wallpaperManager;
        ImageView imageViewFull ,imageViewDownload;
        Button buttonLockScreen,ButtonHomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        title=getIntent().getStringExtra("title");
        image=getIntent().getStringExtra("image");
        imageViewFull =(ImageView)findViewById(R.id.imageFull);
        imageViewDownload=(ImageView)findViewById(R.id.downloadImage);
        ButtonHomeScreen =(Button)findViewById(R.id.setHomeScreen);
        buttonLockScreen =(Button)findViewById(R.id.setLockScreen);
        Glide.with(this).load(image).centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                      buttonLockScreen.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              setWallpaper("Lock");
                          }
                      });
                      ButtonHomeScreen.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              setWallpaper("Home");
                          }
                      });
                      imageViewDownload.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              Dexter.withContext(getApplicationContext())
                                      .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                      .withListener(new PermissionListener() {
                                          @Override
                                          public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                              donloadImageFromImageView();
                                          }

                                          @Override
                                          public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                            Toast.makeText(getApplicationContext(),"Please Give me premission to download this image into your gallery",Toast.LENGTH_LONG).show();
                                          }

                                          @Override
                                          public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                          }
                                      }).check();


                          }
                      });

                        return false;
                    }
                })
                .error(R.drawable.no_image).placeholder(R.drawable.ic_loading).into(imageViewFull);
    }
    private void donloadImageFromImageView(){
        imageFileName =image.substring(image.lastIndexOf("/") + 1);
        FileOutputStream fileOutputStream;
        File file =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"BestChristmasWallpapers");

        if (!file.exists()&&!file.mkdir()){

            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        }else{
            File fileName =new File(file.getAbsolutePath()+"/"+imageFileName);
            int [] size =getScreenSize();
            try {
                fileOutputStream=new FileOutputStream(fileName);

            bitmapDrawable=(BitmapDrawable) imageViewFull.getDrawable();
            bitmap=bitmapDrawable.getBitmap();
            bitmap=Bitmap.createScaledBitmap(bitmap,size[0],size[1],false);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            Toast.makeText(getApplicationContext(),"Image Saved Sucsessfully",Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();

            } catch (IOException e) {
            e.printStackTrace();
        }
            RefreshWallpaper(file);
        }}
        private void RefreshWallpaper(File file){
            Intent intnent1 =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intnent1.setData(Uri.fromFile(file));
            sendBroadcast(intnent1);


}

    private void setWallpaper(String type){
        int[] size =getScreenSize();
        wallpaperManager =WallpaperManager.getInstance(getApplicationContext());
        bitmapDrawable=(BitmapDrawable)imageViewFull.getDrawable();
        bitmap=bitmapDrawable.getBitmap();
        bitmap=Bitmap.createScaledBitmap(bitmap,size[0],size[1],false);
        if (type.equals("Lock")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(bitmap,null ,true,WallpaperManager.FLAG_LOCK);
                    Toast.makeText(getApplicationContext(),"Lock Screen Wallpaper Set",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                 }
            }else {
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(bitmap,null ,true,WallpaperManager.FLAG_SYSTEM);
                    Toast.makeText(getApplicationContext(),"Home Screen Wallpaper Set",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        wallpaperManager.suggestDesiredDimensions(size[0],size[1]);
    }
    private int[] getScreenSize(){
        displayMetrics=new DisplayMetrics() ;
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int[] size =new int[2];
        size [0]=displayMetrics.widthPixels;
        size[1] =displayMetrics.heightPixels;
        return size;
    }


}
