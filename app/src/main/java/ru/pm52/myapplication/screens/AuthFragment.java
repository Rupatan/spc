package ru.pm52.myapplication.screens;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import ru.pm52.myapplication.DBHelper;
import ru.pm52.myapplication.FragmentBase;
import ru.pm52.myapplication.ICallbackResponse;
import ru.pm52.myapplication.Model.AuthModel;
import ru.pm52.myapplication.Model.TaskModel;
import ru.pm52.myapplication.R;
import ru.pm52.myapplication.ViewModel.AuthViewModel;
import ru.pm52.myapplication.ViewModel.Factory;
import ru.pm52.myapplication.databinding.FragmentAuthBinding;

public class AuthFragment extends FragmentBase implements View.OnClickListener {

    private FragmentAuthBinding binding;

    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater, container, false);

//        DBHelper dbHelper = new DBHelper();
//        SQLiteDatabase db = dbHelper.getWritableDB();
//        db.execSQL("DELETE FROM SETTINGS");

        viewModel = new ViewModelProvider(this, new Factory()).get(AuthViewModel.class);

        String dbString = viewModel.getDatabase();
        if (dbString.isEmpty())
            dbString = "Torg83";
        binding.database.setText(dbString);

        String serverString = viewModel.getServer();
        if (serverString.isEmpty())
            serverString = "95.79.48.85:8008";
        binding.server.setText(serverString);

        binding.login.setText(viewModel.getLogin());

        binding.login.setText("Алехин Денис");

        viewModel.ListTasks.observe(getViewLifecycleOwner(), new Observer<List<TaskModel>>() {
            @Override
            public void onChanged(List<TaskModel> listTask) {
                if (!viewModel.isLogin.getValue()) {

                    binding.progressEnter.setVisibility(View.GONE);
                    binding.buttonEnter.setVisibility(View.VISIBLE);

                    return;
                }
                FragmentManager f = getParentFragmentManager();
                Fragment fragment = f.getFragments().get(0);
                f.beginTransaction()
                        .remove(fragment)
                        .add(R.id.fragmentContainer, new TaskListFragment(listTask))
                        .commit();

            }
        });

        viewModel.isLogin.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLogin) {
                if (!isLogin) {
                    setVisibilityLogin(false);
                    Toast.makeText(getContext(), viewModel.MessageLogin.getValue(), Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.buttonEnter.setOnClickListener(this);

        return binding.getRoot();
    }

    private void setVisibilityLogin(boolean isLogin) {
        if (!isLogin) {
            binding.progressEnter.setVisibility(View.GONE);
            binding.buttonEnter.setVisibility(View.VISIBLE);
        } else {
            binding.progressEnter.setVisibility(View.VISIBLE);
            binding.buttonEnter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        b.setVisibility(View.GONE);
        binding.progressEnter.setVisibility(View.VISIBLE);

        try {
            viewModel.login(binding.login.getText().toString(),
                    binding.password.getText().toString(),
                    binding.database.getText().toString(),
                    binding.server.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
