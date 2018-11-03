package com.justclack.blurry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView button;
    PhotoEditorView mPhotoEditorView;
    PhotoEditor mPhotoEditor;
    File myDir, file;
    Button textButton, emoji, save;
    private EmojiBSFragment mEmojiBSFragment;
    TextEditorDialogFragment textEditorDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mPhotoEditor.setFilterEffect(PhotoFilter.BRIGHTNESS);

        clickListeners();

    }

    private void clickListeners() {
        emoji.setOnClickListener(this);
        textButton.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    private void findViewByIds() {
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        textButton = findViewById(R.id.text);
        emoji = findViewById(R.id.emoji);
        save = findViewById(R.id.save);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        if (v == textButton) {
            textEditorDialogFragment = TextEditorDialogFragment.show(MainActivity.this);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    mPhotoEditor.addText(inputText, colorCode);
                }
            });
        }
        if (v == emoji) {
            mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
            mEmojiBSFragment.setEmojiListener(new EmojiBSFragment.EmojiListener() {
                @Override
                public void onEmojiClick(String emojiUnicode) {
                    mPhotoEditor.addEmoji(emojiUnicode);
                }
            });
        }
        if (v == save) {
            myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
            myDir.mkdirs();
            file = new File(myDir, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build();
           /* mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                }

                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });*/
            mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        // Use the compress method on the BitMap object to write image to the OutputStream
                        saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        Toast.makeText(MainActivity.this, "Image Saved Successfully.", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

}