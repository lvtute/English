package com.pmt.cis.english;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Reading extends AppCompatActivity {

    int socau;
    int intcauhientai = 0;
    int time = 20;
    int auto = 1;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<Result_Model> resultss = new ArrayList<>();
    TextView txtKQ, txtTGCB, textTGConLai, cauhientai, txtCau;
    LinearLayout layoutStart, layoutOverlay;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizerManager mSpeechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        getSupportActionBar().setTitle("Reading");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textTGConLai = findViewById(R.id.textTGConLai);
        cauhientai = findViewById(R.id.cauhientai);
        txtKQ = findViewById(R.id.txtKQ);
        layoutOverlay = findViewById(R.id.layoutOverlay);
        layoutStart = findViewById(R.id.layoutStart);
        txtCau = findViewById(R.id.txtCau);
        txtTGCB = findViewById(R.id.txtTGCB);
        getData();


        layoutStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutOverlay.setVisibility(View.VISIBLE);
                layoutStart.setVisibility(View.GONE);

                new CountDownTimer(4000, 1) {

                    public void onTick(long millisUntilFinished) {
                        txtTGCB.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        layoutOverlay.setVisibility(View.GONE);
                        txtKQ.setVisibility(View.VISIBLE);
                        cauhientai.setText("1/" + String.valueOf(socau));
                        /*while (intcauhientai <= socau){*/
                        txtKQ.setText("");
                        starRecording(time);
                        //}

                    }
                }.start();
            }
        });
    }

    public void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        if (!bundle.getString("time").equals("")) {
            time = Integer.parseInt(bundle.getString("time"));
        }
        auto = bundle.getInt("auto");
        socau = bundle.getInt("socau");
        ArrayList<String> temp = new ArrayList<>();
        temp = bundle.getStringArrayList("data");

        textTGConLai.setText(String.valueOf(time));


        if (temp.size() < socau) {
            socau = temp.size();
        }
        cauhientai.setText("0/" + String.valueOf(socau));
        ArrayList<Integer> tss = new ArrayList<>();
        for (int i = 0; i < socau; i++) {
            Random rd = new Random();
            int number1 = rd.nextInt(temp.size());
            while (tss.contains(number1)) {
                number1 = rd.nextInt(temp.size());
            }

            tss.add(number1);
            data.add(temp.get(number1));
        }

        //int sss = 1;
    }

    public void starRecording(int giay) {
        if (mSpeechManager == null) {
            SetSpeechListener();
        } else if (!mSpeechManager.ismIsListening()) {
            mSpeechManager.destroy();
            SetSpeechListener();
        }
        cauhientai.setText(String.valueOf(intcauhientai + 1) + "/" + String.valueOf(socau));
        txtCau.setText(data.get(intcauhientai));
        new CountDownTimer(giay * 1000 + 1000, 1) {

            public void onTick(long millisUntilFinished) {
                // speechRecognizer.
                textTGConLai.setText(String.valueOf(millisUntilFinished / 1000));
                // myService.mSpeechRecognizer.
            }

            public void onFinish() {
                if (mSpeechManager != null) {

                    Result_Model result_model = new Result_Model();
                    result_model.setStt(intcauhientai + 1);
                    result_model.setNd(data.get(intcauhientai));
                    result_model.setNddoc(txtKQ.getText().toString());
                    if (!result_model.getNd().equals(result_model.getNddoc())) {
                        result_model.setResult(false);
                    } else {
                        result_model.setResult(true);
                    }

                    resultss.add(result_model);
                    intcauhientai = intcauhientai + 1;
                    mSpeechManager.destroy();
                    mSpeechManager = null;
                    next();
                }
            }
        }.start();
    }

    private void SetSpeechListener() {
        mSpeechManager = new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {
                if (results != null && results.size() > 0) {
                    txtKQ.append(results.get(0) + " ");
                } else {
                    //txtKQ.setText("KO KQ");
                }

            }
        });
    }

    public void next() {
        if (intcauhientai < socau) {
            new CountDownTimer(3000, 1) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    txtKQ.setText("");
                    starRecording(time);
                }
            }.start();
        } else {
            new CountDownTimer(3000, 1) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("lstresult", (Serializable) resultss);


                    Intent intent = new Intent(Reading.this, Result.class);
                    intent.putExtra("data",bundle);
                    startActivity(intent);
                }
            }.start();
        }

    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        if (mSpeechManager != null) {
            mSpeechManager.destroy();
            mSpeechManager = null;
        }
        super.onPause();
    }
}
