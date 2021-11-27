package ru.pm52.myapplication.screens;

import static android.app.Activity.RESULT_OK;

import static java.util.Collections.emptyMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import ru.pm52.myapplication.BuildConfig;
import ru.pm52.myapplication.FragmentBase;
import ru.pm52.myapplication.HTTPClient;
import ru.pm52.myapplication.HttpFile;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.R;
import ru.pm52.myapplication.ViewModel.TaskViewModel;
import ru.pm52.myapplication.databinding.AlertDialogChoicePhotoBinding;
import ru.pm52.myapplication.databinding.FragmentTaskBinding;

public class TaskFragment extends FragmentBase implements View.OnClickListener {

    private FragmentTaskBinding binding;
    private TaskViewModel viewModel;
    @Nullable
    private TaskModel taskModel;

    public TaskFragment(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    public TaskFragment() {
        this(null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        if (savedInstanceState != null) {
            taskModel = viewModel.getTaskModel();
        }

        binding = FragmentTaskBinding.inflate(inflater, container, false);

        binding.button.setOnClickListener(this);

        viewModel.Task.observe(getViewLifecycleOwner(), new Observer<TaskModel>() {
            @Override
            public void onChanged(TaskModel taskModel) {
                binding.Name.setText(taskModel.Name);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                binding.txtDate.setText(formatter.format(taskModel.DateTime));
                binding.txtNumber.setText(taskModel.Number);
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString().replace('-', '_');
                AuthRepository authRepository = AuthRepository.getInstance();

                HTTPClient.Builder client = new HTTPClient.Builder(ModelContext.URLBase)
                        .addHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT")
                        .addHeader("Cache-Control", "no-store, no-cache, must-revalidate")
                        .addHeader("Cache-Control", "post-check=0, pre-check=0")
                        .addHeader("Pragma", "no-cache")
                        .authentication(authRepository.getUsername(), authRepository.getPassword())
                        .pathURL("mobile/tasks/upload?uid=" + uuidAsString)
                        .method(HTTPClient.METHOD_SEND.POST)
                        .callback(this);

                for (int i = 0; i <= binding.linearPhoto.getChildCount() - 1; i++) {
                    if (binding.linearPhoto.getChildAt(i) instanceof ImageView) {
                        ImageView imageView = (ImageView) binding.linearPhoto.getChildAt(i);
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        HttpFile httpFile = new HttpFile();
                        httpFile.FileName = i + ".jpeg";
                        httpFile.Name = i + "file";
                        httpFile.Data = bitmap;
                        httpFile.ContentType = "Content-Type: image/jpeg";
                        client.addFile(httpFile);
                    }
                }

                HttpFile objectJson = new HttpFile();
                objectJson.ContentType = "Content-Type: application/json; charset=UTF-8";
                objectJson.Data = "{\"data\": 123}";
                client.addFile(objectJson);

                client.build().setNameEvent("getlist").sendAsync();
            }
        });

        viewModel.setTask(taskModel);

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String stringJson = new GsonBuilder().setPrettyPrinting().create().toJson(taskModel);
        outState.putString("taskModel", stringJson);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int result = 0;
        for (int i : grantResults) {
            result += i;
        }

        if (PackageManager.PERMISSION_GRANTED == result) {
            captureImageCameraOrGallery();
        }
    }

    public void requestMultiplePermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)


                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, 998);
        } else {
            captureImageCameraOrGallery();
        }


//            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
//                @Override
//                public void onActivityResult(Map<String, Boolean> result) {
//                    boolean resultCode = true;
//                    for (Map.Entry<String, Boolean> i : result.entrySet()) {
//                        if (!i.getValue()) {
//                            resultCode = false;
//                            break;
//                        }
//                    }
//
//                    if (resultCode)
//                        captureImageCameraOrGallery();
//                }
//            }).launch(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA
//            });
//        }
    }

    private Uri uriPhoto;

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

                    String path = getActivity().getFilesDir().getAbsolutePath();
//
                    String imageFile = "/Image" + new Random().nextInt() + ".jpeg";
                    File file = new File(path, imageFile);
                    uriPhoto = Uri.fromFile(file);

//                    uriPhoto = FileProvider.getUriForFile(
//                            getCa,
//                            TaskFragment.class.getSimpleName() + ".provider",
//                            file);

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
//                    intent.putExtra("return-data", true);
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
//            if (requestcode == 0)
//                getImageUri(getContext(), photo);
            addImage(photo);
        }
    }

    public void addImage(Bitmap bt) {
        ImageView imageView = new ImageView(getContext());
        imageView.setPadding(2, 2, 2, 2);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bt.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] bitmapdata = bytes.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.linearPhoto.addView(imageView);
    }

    @Nullable
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        @Nullable Uri uribt = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage = Bitmap.createScaledBitmap(inImage, inImage.getWidth(), inImage.getHeight(), true);

            inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Image"
                    + new Random().nextInt() + ".jpeg", null);
            uribt = Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//        uribt = bitmapToUriConverter(inImage);
        return uribt;
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(getActivity().getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = getActivity().openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
//        captureImageCameraOrGallery();
        requestMultiplePermissions();
    }
}
