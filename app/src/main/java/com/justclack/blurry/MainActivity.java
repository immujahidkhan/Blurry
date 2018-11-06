package com.justclack.blurry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;

public class MainActivity extends AppCompatActivity {
    PhotoEditorView mPhotoEditorView;
    PhotoEditor mPhotoEditor;
    File myDir, file;
    private EmojiBSFragment mEmojiBSFragment;
    TextEditorDialogFragment textEditorDialogFragment;
    BottomNavigationView bottomNavigationView;
    Uri imageURI = null;
    Uri imageBGURI = null;
    int RC_IMG_CHOOSE = 1212;
    int RC_BG_CHOOSE = 1112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_emoji);
        getSupportActionBar().setDisplayShowTitleEnabled(true); //optional
        setContentView(R.layout.activity_main);
        findViewByIds();
        mEmojiBSFragment = new EmojiBSFragment();
        mPhotoEditorView.getSource().setImageResource(R.drawable.srk);
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.spectrashell);
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();
        PhotoEditor.getEmojis(MainActivity.this);
        mPhotoEditor.setBrushDrawingMode(true);
        mPhotoEditor.setBrushSize(2);
        mPhotoEditor.setOpacity(2);
        mPhotoEditor.brushEraser();
        //mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);
        clickListeners();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("MissingPermission")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addText:
                        textEditorDialogFragment = TextEditorDialogFragment.show(MainActivity.this);
                        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                            @Override
                            public void onDone(String inputText, int colorCode) {
                                mPhotoEditor.addText(inputText, colorCode);
                            }
                        });
                        return true;
                    case R.id.addEmoji:
                        mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                        mEmojiBSFragment.setEmojiListener(new EmojiBSFragment.EmojiListener() {
                            @Override
                            public void onEmojiClick(String emojiUnicode) {
                                mPhotoEditor.addEmoji(emojiUnicode);
                            }
                        });
                        return true;
                    case R.id.addPhoto:
                        Intent choose = new Intent(Intent.ACTION_GET_CONTENT);
                        choose.setType("image/*");
                        startActivityForResult(choose, RC_IMG_CHOOSE);
                        return true;
                    case R.id.background:
                        Intent chooseBg = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseBg.setType("image/*");
                        startActivityForResult(chooseBg, RC_BG_CHOOSE);
                        return true;
                    case R.id.save:
                        myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
                        myDir.mkdirs();
                        file = new File(myDir, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
                        SaveSettings saveSettings = new SaveSettings.Builder()
                                .setClearViewsEnabled(true)
                                .setTransparencyEnabled(true)
                                .build();
                        mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                            @Override
                            public void onSuccess(@NonNull String imagePath) {
                                mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                            }

                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                        return true;
                }
                return false;
            }
        });

    }

    private void clickListeners() {
        /*emoji.setOnClickListener(this);
        textButton.setOnClickListener(this);
        save.setOnClickListener(this);
        undo.setOnClickListener(this);
        redo.setOnClickListener(this);
        chooseImg.setOnClickListener(this);*/
    }

    private void findViewByIds() {
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        /*textButton = findViewById(R.id.text);
        emoji = findViewById(R.id.emoji);
        save = findViewById(R.id.save);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        chooseImg = findViewById(R.id.chooseImg);*/
        bottomNavigationView = findViewById(R.id.bottom_navigation);
       /* bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_main);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMG_CHOOSE) {
            if (resultCode == RESULT_OK) {
                imageURI = data.getData();
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity(imageURI)
                        //.setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageURI = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                    mPhotoEditor.addImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == RC_BG_CHOOSE) {
            if (resultCode == RESULT_OK) {
                imageBGURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageBGURI);
                    mPhotoEditorView.getSource().setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                mPhotoEditor.undo();
                return true;
            case R.id.redo:
                mPhotoEditor.redo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}