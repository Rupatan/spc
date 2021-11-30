package ru.pm52.myapplication;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HTTPClient implements ICallbackResponse {

    @Override
    public void CallbackResponse(String content, int responseCode) throws Exception {
        if (callback != null)
            callback.CallbackResponse(content, responseCode);

        if (notify != null)
            notify.NotifyResponse(nameEvent, content, responseCode);
    }

    public static enum METHOD_SEND {
        POST("POST"),
        GET("GET");

        private String method;

        METHOD_SEND(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return method;
        }
    }

    public HTTPClient setNameEvent(@Nullable String nameEvent) {
        this.nameEvent = nameEvent;
        return this;
    }

    @Nullable
    private String urlString;
    @Nullable
    private ICallbackResponse callback;
    @Nullable
    private String path;
    @Nullable
    private String user;
    @Nullable
    private String password;
    @Nullable
    private String bodyRequest;
    @Nullable
    private METHOD_SEND methodRequest;
    @Nullable
    private HashMap<String, String> header;

    @Nullable
    private INotify notify;
    @Nullable
    private String nameEvent;
    @Nullable
    private List<HttpFile> files;

    private HTTPClient(Builder builder) {
        this.urlString = builder.urlString;
        this.path = builder.path;
        this.callback = builder.callback;
        this.methodRequest = builder.methodRequest;
        this.password = builder.password;
        this.bodyRequest = builder.bodyRequest;
        this.user = builder.user;
        this.header = builder.header;
        this.notify = builder.notify;

        this.nameEvent = builder.nameEvent;
        this.files = builder.files;
    }

    public void send(String pathString) {
        this.path = pathString;
        send();
    }

    public void send() {
        try {
            HTTPClient.HTTPProcess.getResponseHTTP(
                    this,
                    urlString,
                    path,
                    user,
                    password,
                    bodyRequest,
                    methodRequest,
                    header,
                    5000,
                    files
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SendRequesAsync extends AsyncTask<HTTPClient, Void, Boolean> {

        @Override
        protected Boolean doInBackground(HTTPClient... httpClients) {
            try {
//                Thread.sleep(2000);
                HTTPClient client = httpClients[0];
                client.send();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

    public void sendAsync() {
        new SendRequesAsync().execute(this);
    }

    public void sendAsync(String pathString) {
        this.path = pathString;
        sendAsync();
    }

    public static class Builder {

        @Nullable
        private String urlString;
        @Nullable
        private ICallbackResponse callback;
        @Nullable
        private String path;
        @Nullable
        private String user;
        @Nullable
        private String password;
        @Nullable
        private String bodyRequest;
        @Nullable
        private METHOD_SEND methodRequest;
        @Nullable
        private HashMap<String, String> header;
        @Nullable
        private INotify notify;
        @Nullable
        private String nameEvent;
        @Nullable
        private List<HttpFile> files;

        public HTTPClient build() {
            return new HTTPClient(this);
        }

        public Builder addFile(HttpFile file) {
            if (files == null)
                files = new ArrayList<>();

            files.add(file);
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (header == null)
                header = new HashMap<>();

            header.put(key, value);
            return this;
        }

        public Builder(String urlString) {
            this.urlString = urlString;
        }

        public Builder pathURL(String path) {
            this.path = path;
            return this;
        }

        public Builder nameEvent(String nameEvent) {
            this.nameEvent = nameEvent;
            return this;
        }

        public Builder authentication(String username, String password) {
            this.user = username;
            this.password = password;
            return this;
        }

        public Builder callback(Object object) {
//            this.callback = (ICallbackResponse) object;

            if (object instanceof ICallbackResponse)
                this.callback = (ICallbackResponse) object;
            else if (object instanceof INotify)
                this.notify = (INotify) object;

            return this;
        }

        public Builder callbackNotify(View.OnClickListener object) {
            this.notify = (INotify) object;

//            if (object instanceof ICallbackResponse
//                    || (cl != null && cl.isAssignableFrom(ICallbackResponse.class)))
//                this.callback = (ICallbackResponse) object;
//            else if (object instanceof INotify
//                    || (cl != null && cl.isAssignableFrom(INotify.class)))
//                this.notify = (INotify) object;

            return this;
        }

//        public Builder callback(INotify object) {
//            this.notify = object;
//            return this;
//        }

        public Builder body(String body) {
            this.bodyRequest = body;
            return this;
        }

        public Builder method(METHOD_SEND methodRequest) {
            this.methodRequest = methodRequest;
            return this;
        }

    }

    public static class HTTPProcess {

        public static HttpURLConnection getHTTPURLConnection(@Nullable String URLBase, @Nullable String path) throws IOException {
            String urlString = URLBase;
            if (path != null) {
                urlString = URLBase.endsWith("/") ? "%1$s%2$s" : "%1$s/%2$s";
                urlString = String.format(urlString, URLBase, path);
            }

            return getURLConnection(urlString);
        }

        public static HttpURLConnection getHTTPURLConnection(String URLBase) throws IOException {
            return getURLConnection(URLBase);
        }

        public static HttpURLConnection getURLConnection(String urlString) throws IOException {
            URL url = new URL(urlString);
            return (HttpURLConnection) url.openConnection();
        }

        public static ResponseResult getResponseHTTP(@Nullable ICallbackResponse callback,
                                                     @Nullable String URLBase,
                                                     @Nullable String path,
                                                     @Nullable String user,
                                                     @Nullable String password,
                                                     @Nullable String body,
                                                     @Nullable METHOD_SEND method,
                                                     @Nullable HashMap<String, String> headers,
                                                     @Nullable Integer connectionTimeOut,
                                                     @Nullable List<HttpFile> files) throws Exception {
            int responseCode = -1;
            String responseBody = "";

            HttpURLConnection con = getHTTPURLConnection(URLBase, path);

            if (user != null && password != null)
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                });

            String methodString = METHOD_SEND.GET.toString();
            if (method != null)
                methodString = method.toString();
            con.setRequestMethod(methodString);

            @Nullable String boundary = null;
            if (files != null) {
                UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString().replaceAll("-", "");
                boundary = "--" + uuidAsString;

                if (headers == null)
                    headers = new HashMap<>();

                String val = "";
                if (headers.containsKey("Content-Type")) {
                    val = headers.get("Content-Type").trim();
                    if (!val.endsWith(";"))
                        val += ";";

                    val += boundary;
                } else {
                    val = "multipart/form-data; boundary=" + boundary;
                }
                headers.put("Content-Type", val);
            }

            try {
                for (Map.Entry<String, String> i : headers.entrySet()) {
                    con.setRequestProperty(i.getKey(), i.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            int conTimeOut = 5000;
            if (connectionTimeOut != null)
                conTimeOut = connectionTimeOut;

            con.setConnectTimeout(conTimeOut);
            boolean doOutput = body != null || files != null;
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setDoOutput(doOutput);

            @Nullable DataOutputStream bufferedOutputStream = null;
            @Nullable ByteArrayOutputStream baos = null;

            if (body != null) {
                baos = new ByteArrayOutputStream();
                bufferedOutputStream = new DataOutputStream(baos);
                bufferedOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
            }

            boolean headersIsSet = false;
            boolean isfiles = files != null && !files.isEmpty();
            if (isfiles) {
                String crlf = "\r\n";
                if (bufferedOutputStream == null) {
                    baos = new ByteArrayOutputStream();
                    bufferedOutputStream = new DataOutputStream(baos);
                }
                byte[] crlfb = crlf.getBytes(StandardCharsets.UTF_8);
                for (HttpFile file : files) {
                    bufferedOutputStream.write(("--" + boundary + crlf).getBytes(StandardCharsets.UTF_8));
                    String stringContentDisposition = null;
                    if (file.ContentDesposition != null) {
                        stringContentDisposition = file.ContentDesposition.toString();
                    } else
                        stringContentDisposition = "Content-Disposition: form-data;";
                    if (file.Name != null)
                        stringContentDisposition += "name=\"" + file.Name + "\";";

                    if (file.FileName != null)
                        stringContentDisposition += "filename=\"" + file.FileName + "\"";

                    stringContentDisposition += crlf;

                    bufferedOutputStream.write(stringContentDisposition.getBytes(StandardCharsets.UTF_8));

                    if (file.ContentType != null) {
                        bufferedOutputStream.writeBytes(file.ContentType);
                        bufferedOutputStream.write(crlfb);
                    }

                    bufferedOutputStream.write(crlfb);
                    if (file.Data instanceof Bitmap) {
                        Bitmap bitmap = (Bitmap) file.Data;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        byte[] encode = Base64.encode(byteArray, Base64.DEFAULT);

                        bufferedOutputStream.write(encode);

                        stream.flush();
                        stream.close();
                    } else if (file.Data instanceof String) {
                        bufferedOutputStream.writeBytes((String) file.Data);
                    } else if (file.Data instanceof byte[]) {
                        bufferedOutputStream.write((byte[]) file.Data);
                    }

                    bufferedOutputStream.write(crlfb);

                }
                bufferedOutputStream.write(("--" + boundary + "--" + crlf).getBytes(StandardCharsets.UTF_8));
            }

            if (bufferedOutputStream != null) {
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
                byte[] bodyByte = baos.toByteArray();
//                if (isfiles) {
//                    byte[] encode = Base64.encode(bodyByte, Base64.DEFAULT);
//                    outputStream.write(encode);
//                } else
                outputStream.write(bodyByte);
                outputStream.flush();
                outputStream.close();
            }

            boolean error = false;
            try {
                responseCode = con.getResponseCode();
                responseBody = readStream(con.getInputStream());
            } catch (
                    Exception e) {
                responseBody = e.getMessage();
                error = true;
                e.printStackTrace();
            }

            if (callback != null)
                callback.CallbackResponse(responseBody, responseCode);

            return new ResponseResult(responseBody, responseCode);
        }

        @NonNull
        static String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }
}
