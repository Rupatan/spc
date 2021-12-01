package ru.pm52.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public abstract class FragmentBase extends Fragment implements INotify, MainActivity.ICallBackPress {

    @Override
    public void NotifyResponse(String eventString, Object... params) throws InterruptedException {

    }

    @Override
    public void CallBackPress(){

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return onOptionsItemSelected(item);
    }
}
