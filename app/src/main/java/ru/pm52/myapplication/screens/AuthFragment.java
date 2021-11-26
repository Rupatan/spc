package ru.pm52.myapplication.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

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

        viewModel = new ViewModelProvider(this, new Factory()).get(AuthViewModel.class);

        viewModel.ListTasks.observe(getViewLifecycleOwner(), new Observer<List<TaskModel>>() {
            @Override
            public void onChanged(List<TaskModel> listTask) {
                binding.progressEnter.setVisibility(View.GONE);
                binding.buttonEnter.setVisibility(View.VISIBLE);

                FragmentManager f = getParentFragmentManager();
                Fragment fragment = f.getFragments().get(0);
                f.beginTransaction()
                        .remove(fragment)
                        .add(R.id.fragmentContainer, new TaskListFragment(listTask))
                        .commit();

            }
        });

        binding.buttonEnter.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        b.setVisibility(View.GONE);
        binding.progressEnter.setVisibility(View.VISIBLE);

        viewModel.login(binding.login.getText().toString(),
                binding.password.getText().toString(), "login");
    }


}
