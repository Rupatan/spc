package ru.pm52.myapplication.Model;

import android.media.VolumeShaper;

import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ru.pm52.myapplication.ICallbackResponse;


public class ModelContext {
    public final static String URLBase = "http://95.79.48.85:8008/Torg83_debug_Alehin/hs/pmSPC";
}
