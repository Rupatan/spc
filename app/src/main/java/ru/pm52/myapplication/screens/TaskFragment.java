package ru.pm52.myapplication.screens;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import ru.pm52.myapplication.FragmentBase;
import ru.pm52.myapplication.ViewModel.TaskViewModel;
import ru.pm52.myapplication.databinding.FragmentTaskBinding;

public class TaskFragment extends FragmentBase {

    private FragmentTaskBinding binding;
    private TaskViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImageCameraOrGallery();
            }
        });

        return binding.getRoot();
    }

    public void captureImageCameraOrGallery() {


        final CharSequence[] options = {"Сделать фото...", "Выбрать из галереи...",
                "Закрыть"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                getContext());

        builder.setTitle("Выбор");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Сделать фото...")) {
                    try {
                        Intent cameraIntent = new Intent(
                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 1);
                    } catch (ActivityNotFoundException ex) {
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                    }


                } else if (options[which].equals("Выбрать из галереи...")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[which].equals("Закрыть")) {
                    dialog.dismiss();
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
            if (requestcode == 1) {
                Uri picUri = intent.getData();
                startCropImage(picUri);
            }
//            } else if (requestcode == 2) {
//                Bitmap photo = (Bitmap) intent.getExtras().get("data");
//                Drawable drawable = new BitmapDrawable(photo);
//            } else if (requestcode == ACTIVITY_SELECT_IMAGE) {
//                Uri selectedImage = intent.getData();
//                String[] filePath = { MediaStore.Images.Media.DATA };
//                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
//                        null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                Drawable drawable = new BitmapDrawable(thumbnail);
////                backGroundImageLinearLayout.setBackgroundDrawable(drawable);
//
//            }
        }
    }



    private void startCropImage(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
//            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast
                    .makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
