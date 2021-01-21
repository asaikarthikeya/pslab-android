package io.pslab.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;

import io.pslab.R;
import io.pslab.others.CustomSnackBar;
import io.pslab.others.ScienceLabCommon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ESPFragment extends DialogFragment {
    private String espIPAddress = "";
    private ProgressBar espConnectProgressBar;
    private Button espConnectBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_esp, container, false);
        EditText espIPEditText = rootView.findViewById(R.id.esp_ip_edit_text);
        espConnectBtn = rootView.findViewById(R.id.esp_connect_btn);
        espConnectProgressBar = rootView.findViewById(R.id.esp_connect_progressbar);
        espConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                espIPAddress = espIPEditText.getText().toString();
                if (espIPAddress.length() == 0) {
                    espIPEditText.setError( getString(R.string.incorrect_IP_address_message));
                } else {
                    new ESPTask().execute();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private class ESPTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            espConnectBtn.setVisibility(View.GONE);
            espConnectProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://" + espIPAddress)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    ScienceLabCommon.setIsWifiConnected(true);
                    ScienceLabCommon.setEspBaseIP(espIPAddress);
                }
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            espConnectProgressBar.setVisibility(View.GONE);
            espConnectBtn.setVisibility(View.VISIBLE);
            TextInputLayout errormsg = (TextInputLayout) getView().findViewById(R.id.input_layout_password);
            if (result.length() == 0) {
                errormsg.setError( getString(R.string.incorrect_IP_address_message));
            } else {
                errormsg.setError(null);
                Log.v("Response", result);
            }
        }
    }
}