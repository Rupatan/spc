package ru.pm52.myapplication.Model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.pm52.myapplication.INotify;

public class RepositoryBase implements INotify {

    protected HashMap<INotify, List<String>> notifyMap = new HashMap<>();

    public void addListener(INotify objectNotify) {
        if (!notifyMap.containsKey(objectNotify))
            notifyMap.put(objectNotify, new ArrayList<>());
    }

    public void addListener(INotify objectNotify, String eventName) {
        addListener(objectNotify);

        notifyMap.get(objectNotify).add(eventName);
    }

    public void removeListener(INotify objectNotify) {
        if (notifyMap.containsKey(objectNotify))
            notifyMap.remove(objectNotify);
    }

    public void removeListener(INotify objectNotify, String eventName) {
        if (notifyMap.containsKey(objectNotify))
            notifyMap.get(objectNotify).remove(eventName);
    }

    public void notifyObjects() {
//        for (INotify iNotify : notifyList)
//            iNotify.NotifyResponse();
    }

    @SuppressLint("NewApi")
    @Override
    public void NotifyResponse(String eventString, Object... params) {
        for (Map.Entry<INotify, List<String>> i : notifyMap.entrySet()) {
            if (i.getValue().contains(eventString))
                i.getKey().NotifyResponse(eventString, params);
        }
    }
}
