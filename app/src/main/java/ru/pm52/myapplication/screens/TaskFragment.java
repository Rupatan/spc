package ru.pm52.myapplication.screens;

import static android.app.Activity.RESULT_OK;

import static java.util.Collections.emptyMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import ru.pm52.myapplication.INotify;
import ru.pm52.myapplication.MainActivity;
import ru.pm52.myapplication.Model.AuthRepository;
import ru.pm52.myapplication.Model.ModelContext;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.Model.TypeWork;
import ru.pm52.myapplication.R;
import ru.pm52.myapplication.ResponseHTTPResult;
import ru.pm52.myapplication.ResponseResult;
import ru.pm52.myapplication.ViewModel.Factory;
import ru.pm52.myapplication.ViewModel.TaskViewModel;
import ru.pm52.myapplication.ViewModel.ViewModelBase;
import ru.pm52.myapplication.databinding.AlertDialogChoicePhotoBinding;
import ru.pm52.myapplication.databinding.FragmentTaskBinding;
import ru.pm52.myapplication.databinding.LadfadBinding;
import ru.pm52.myapplication.databinding.SpinnerItemBinding;

import java.nio.file.*;
import java.io.*;

public class TaskFragment extends FragmentBase {

    public class TypeWorkArrayAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;

        public TypeWorkArrayAdapter(@NonNull Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return ModelContext.typeWorkList.size();
        }

        @Override
        public TypeWork getItem(int i) {
            return ModelContext.typeWorkList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TypeWork typeWork = (TypeWork) getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spinner_item, parent, false);
            }
            TextView txt = (TextView) convertView.findViewById(R.id.name);
            txt.setText(typeWork.Name);
            txt.setTag(typeWork.Ref);

            return convertView;
        }

    }

    @Nullable
    private INotify iNotify;
    private LadfadBinding binding;
    private TaskViewModel viewModel;
    @Nullable
    private TaskModel taskModel;
    private boolean sendRequest = false;

    public TaskFragment(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    public TaskFragment() {
        this(null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).addListenerCallbackPress(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, new Factory(getActivity())).get(TaskViewModel.class);
        viewModel.IsDone.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDone) {
                if (isDone) {

                }
            }
        });
        viewModel.IsSend.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSend) {
                if (isSend) {
                    binding.progressBarTask.setVisibility(View.VISIBLE);
                    binding.mtaskLayout.setVisibility(View.GONE);
//                }else{
//                    binding.progressBarTask.setVisibility(View.GONE);
//                    binding.mtaskLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        viewModel.Info.observe(getViewLifecycleOwner(), new Observer<ResponseHTTPResult>() {
            @Override
            public void onChanged(ResponseHTTPResult responseHTTPResult) {
                String msg = "???????????? ???????????????????? ????????????";
                boolean isDone = false;
                isDone = responseHTTPResult.Status == 1;
                if (isDone) {
                    msg = "???????????? ??????????????????";
                } else {
                    msg = responseHTTPResult.Info;
                    isDone = false;
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                if (isDone) {
                    viewModel.deleteImages();

                    try {
                        getParentFragmentManager().popBackStackImmediate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    binding.progressBarTask.setVisibility(View.GONE);
                    binding.mtaskLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        if (savedInstanceState != null) {
            taskModel = viewModel.getTaskModel();

            for (Map.Entry<Integer, Uri> i : viewModel.getAddedImages().entrySet()) {
//                addImage();
            }

            sendRequest = savedInstanceState.getBoolean("sendRequest");
            if (sendRequest) {
                binding.progressBarTask.setVisibility(View.GONE);
                binding.mtaskLayout.setVisibility(View.VISIBLE);
            }
        }

        binding = LadfadBinding.inflate(inflater, container, false);

        binding.addImage.setOnClickListener(this::onClick);

        binding.Name.setText(taskModel.Name);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        binding.txtDate.setText(formatter.format(taskModel.DateTime));
        binding.txtNumber.setText(taskModel.Number);
        binding.contact.setText(taskModel.Contact);
        binding.cotragent.setText(taskModel.Contragent);
        binding.Description.setText(taskModel.Descritpion);
        binding.ServicePoint.setText(taskModel.ServicePointAddress);
        TypeWorkArrayAdapter adapter = new TypeWorkArrayAdapter(getContext());
        binding.typeWork.setAdapter(adapter);
        //binding.button2.setOnClickListener(this::onClickSendComplete);

        //viewModel.setTask(taskModel);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("sendRequest", sendRequest);

//        String stringJson = new GsonBuilder().setPrettyPrinting().create().toJson(taskModel);
//        outState.putString("taskModel", stringJson);
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

    }

    private Uri uriPhoto;
    private Integer counterImages = 0;
    private HashMap<Integer, Uri> addedImages = new HashMap<>();

    public void captureImageCameraOrGallery() {

        final CharSequence[] options = {"?????????????? ????????...", "?????????????? ???? ??????????????...",
                "??????????????"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                getContext());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 2)
                    dialog.dismiss();

                if (which == 0) {
                    try {

                        String prefix = String.valueOf(new Random().nextInt()).replaceAll("-", "");
                        String imageFileName = String.format("Image_%1$s.jpeg", prefix);

                        File imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File newFile = new File(imagePath, imageFileName);

//                        newFile.deleteOnExit();

                        uriPhoto = FileProvider.getUriForFile(getContext(),
                                "ru.pm52.myapplication.MainActivity.provider",
                                newFile);
//                        File f = FileProvider.getPathStrategy(getContext(), ViewModelBase.AUTHORITY).getFileForUri(uriPhoto);
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);


                        startActivityForResult(takePhotoIntent, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
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
            @Nullable Uri uri = null;
            if (requestcode == 0)
                uri = uriPhoto;
            else
                uri = intent.getData();

            if (uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    float dip = 60f;
                    Resources r = getResources();
                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());

                    Bitmap newBitmap = getResizedBitmap(bitmap, px);

                    addImage(newBitmap, uri);

                    uriPhoto = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "???????????? ?????? ???????????????????? ????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addImage(Object bt, Uri name) {
        ImageView imageView = new ImageView(getContext());
        imageView.setPadding(2, 2, 2, 2);

        if (bt instanceof Bitmap)
            imageView.setImageBitmap((Bitmap) bt);
        else if (bt instanceof Uri)
            imageView.setImageURI((Uri) bt);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setId(++counterImages);
        imageView.setTag(name);

        viewModel.addImage(counterImages, name);

//        addedImages.put(counterImages, name);
        binding.linearPhoto.addView(imageView);
    }

    public Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public Bitmap getResizedBitmap(Bitmap bm, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float newWidth = width * ((float) newHeight / height);

        return getResizedBitmap(bm, newWidth, newHeight);
    }

    @Override
    public void NotifyResponse(String eventString, Object... params) {
        String msg = "???????????? ???????????????????? ????????????";
        boolean isDone = false;
        if (eventString.equals("update")) {
            int code = (int) params[1];
            isDone = code == 200;
            if (isDone) {
                String stringParams = params[0].toString();
                try {
                    JSONObject obj = new JSONObject(stringParams);
                    if (obj.getInt("status") == 1) {
                        msg = "???????????? ??????????????????";
                    } else {
                        msg = obj.getString("info");
                        isDone = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    isDone = false;
                }
            }
        }

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

        if (isDone) {
            viewModel.deleteImages();

            try {
                getParentFragmentManager().popBackStackImmediate();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            binding.progressBarTask.setVisibility(View.GONE);
            binding.mtaskLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View v) {
//        captureImageCameraOrGallery();
        requestMultiplePermissions();
    }

    public void onClickSendComplete(View view) {
        sendComplete();
    }

    public String getFileName(Uri uri, ContentResolver contentResolver) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    protected void sendComplete() {
        binding.progressBarTask.setVisibility(View.VISIBLE);
        binding.mtaskLayout.setVisibility(View.GONE);

        String stringLeadTime = String.valueOf(binding.leadTime.getText());

        try {
            if (stringLeadTime.isEmpty()) {
                Toast.makeText(getActivity(), "???? ?????????????????? ???????? \"????????\"", Toast.LENGTH_LONG).show();
                binding.leadTime.setClickable(true);
                binding.progressBarTask.setVisibility(View.GONE);
                binding.mtaskLayout.setVisibility(View.VISIBLE);
                return;
            }
            taskModel.LeadTime = stringLeadTime.isEmpty() ? 0.0 : Double.parseDouble(stringLeadTime);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        taskModel.Comment = String.valueOf(binding.Comment.getText());
        taskModel.TextSubtask = String.valueOf(binding.textSubtask.getText());
        taskModel.HaveTask = !taskModel.TextSubtask.trim().isEmpty();
        taskModel.TypeWork = (TypeWork) binding.typeWork.getSelectedItem();

        viewModel.sendComplete(taskModel);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemComplete: {

                sendComplete();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void CallBackPress() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.deleteImages();

        ((MainActivity) getActivity()).removeListenerCallbackPress(this);
    }
}
