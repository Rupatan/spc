package ru.pm52.myapplication.screens;

import static android.app.Activity.RESULT_OK;

import static java.util.Collections.emptyMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ru.pm52.myapplication.FragmentBase;
import ru.pm52.myapplication.R;
import ru.pm52.myapplication.ViewModel.TaskViewModel;
import ru.pm52.myapplication.databinding.AlertDialogChoicePhotoBinding;
import ru.pm52.myapplication.databinding.FragmentTaskBinding;

public class TaskFragment extends FragmentBase implements View.OnClickListener {

    private FragmentTaskBinding binding;
    private TaskViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        binding.button.setOnClickListener(this);

        return binding.getRoot();
    }


    public void requestMultiplePermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean resultCode = true;
                    for (Map.Entry<String, Boolean> i : result.entrySet()) {
                        if (!i.getValue()) {
                            resultCode = false;
                            break;
                        }
                    }

                    if (resultCode)
                        captureImageCameraOrGallery();
                }
            }).launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            });
        }
    }


    public void captureImageCameraOrGallery() {

        final CharSequence[] options = {"Сделать фото...", "Выбрать из галереи...",
                "Закрыть"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                getContext());
//        builder.setView(R.layout.alert_dialog_choice_photo);
        builder.setTitle("Выбор");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 2)
                    dialog.dismiss();

                if (which == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, which);
                } else if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, which);
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent intent) {
        super.onActivityResult(requestcode, resultcode, intent);
        if (resultcode == RESULT_OK) {
            Bitmap photo = (Bitmap) intent.getExtras().get("data");
            @Nullable Uri uriPhoto = null;
            if (requestcode == 0)
                getImageUri(getContext(), photo);

            addImage(photo);
        }
    }

    public void addImage(Bitmap bt) {
        ImageView imageView = new ImageView(getContext());
        imageView.setPadding(2, 2, 2, 2);
        imageView.setImageBitmap(bt);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.linearPhoto.addView(imageView);
    }

    @Nullable
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        @Nullable Uri uribt = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            uribt = Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uribt;
    }

    private void startCropImage(Uri uriPhoto) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(uriPhoto, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 3);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast
                    .makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View v) {
        captureImageCameraOrGallery();
    }
}
