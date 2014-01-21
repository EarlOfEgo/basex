package com.example.basexxmarktests;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity implements PerformInBackgroundListener<String> {

    private Button startButton1;
    private Button startButton2;
    private Button startButton3;
    private Button startButton4;
    private TextView output;
    private Button startTraces;
    private Button startTrace100Mb;
    private CheckBox checkBox;
    private Spinner spinner;
    private int min_max = 0;
    private boolean backwards = false;


    public static File root = android.os.Environment
            .getExternalStorageDirectory();
    private static String externalFilePath = root.getAbsolutePath();
    private static String baseXFileFolder = "BaseXXMark";
    private static String outputFileFolder = "OutputFiles";
    private static String inputFileFolder = "InputFiles";

    public String absoluteInputPath = externalFilePath + File.separator
            + baseXFileFolder + File.separator + inputFileFolder
            + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String appDir = getApplicationInfo().dataDir + "/test_files/";
        absoluteInputPath = appDir;

        fileStuff();

        File file = new File(appDir);
        if (!file.exists()) {
            file.mkdirs();
            Log.e("OnCreate", "NOT THERE" + appDir);
        }
        new CopyFiles().execute(appDir);


        startButton1 = (Button) findViewById(R.id.startButton1);
        startButton2 = (Button) findViewById(R.id.startButton2);
        startButton3 = (Button) findViewById(R.id.startButton3);
        startButton4 = (Button) findViewById(R.id.startButton4);
        output = (TextView) findViewById(R.id.textView1);
        output.setMovementMethod(new ScrollingMovementMethod());

        checkBox = (CheckBox) findViewById(R.id.checkBox);

        startButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("STARTING XMARK BENCHMARKS....");
                enableButtons(false);
                checkBox.setEnabled(false);
                new PerformInBackground(MainActivity.this, getApplicationInfo().dataDir).execute(1, min_max);
            }
        });

        startButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("STARTING XMARK BENCHMARKS....");
                enableButtons(false);
                checkBox.setEnabled(false);
                new PerformInBackground(MainActivity.this, getApplicationInfo().dataDir).execute(2, min_max);
            }
        });

        startButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("STARTING XMARK BENCHMARKS....");
                enableButtons(false);
                checkBox.setEnabled(false);
                new PerformInBackground(MainActivity.this, getApplicationInfo().dataDir).execute(3, min_max);
            }
        });

        startButton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                output.setText("STARTING ALL XMARK BENCHMARKS...." + (backwards ? "Backwards" : ""));
                enableButtons(false);
                checkBox.setEnabled(false);
                new PerformInBackground(MainActivity.this, getApplicationInfo().dataDir).execute(!backwards ? 4 : 5, min_max);
            }
        });

        startTraces = (Button) findViewById(R.id.startTrace);
        startTraces.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("STARTING TRACES");
                enableButtons(false);
                checkBox.setEnabled(false);
                new PerformInBackground(MainActivity.this, getApplicationInfo().dataDir).execute(0);
            }
        });

        startTrace100Mb = (Button) findViewById(R.id.startTrace100Mb);
        startTrace100Mb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                output.setText("STARTING 100 MB!!!111 TRACES...");
                enableButtons(false);
                checkBox.setEnabled(false);
                new PerformInBackground(MainActivity.this, getApplicationInfo().dataDir).execute(6);
            }
        });

        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                backwards = !backwards;
                startButton4.setText("Start all XMark Benchmarks" + (backwards ? " Backwards" : ""));
                enableButtons(!backwards);
                startButton4.setEnabled(true);
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinner_content = ArrayAdapter.createFromResource(this, R.array.min_max, android.R.layout.simple_spinner_item);
        spinner_content.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_content);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                min_max = i +1;
                Log.e("NARF", "" + min_max);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner.setSelection(spinner_content.getPosition("Average"));
    }


    private void fileStuff() {
        if (!directoriesExist(baseXFileFolder)) {
            createDirectories(baseXFileFolder);
        }
        if (!directoriesExist(baseXFileFolder + File.separator
                + outputFileFolder)) {
            createDirectories(baseXFileFolder + File.separator
                    + outputFileFolder);
        }

        File file = new File(root, "traces");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void createDirectories(String baseXFileFolder2) {
        File file = new File(externalFilePath, baseXFileFolder2);
        file.mkdirs();
    }

    private boolean directoriesExist(String directory) {
        File file = new File(externalFilePath, directory);
        return (file.exists() && file.isDirectory());
    }

    private boolean fileExists(String filename) {
        File file = new File(absoluteInputPath, filename);
        return file.exists();
    }


    @Override
    public void statusUpdate(String param) {
        Log.d("CALLBACK", param);
        output.setText(param);
    }

    @Override
    public void finished() {
        enableButtons(true);
        checkBox.setEnabled(true);
    }


    private void enableButtons(boolean active) {
        startTrace100Mb.setEnabled(active);
        startTraces.setEnabled(active);
        startButton1.setEnabled(active);
        startButton2.setEnabled(active);
        startButton3.setEnabled(active);
        startButton4.setEnabled(active);
    }




    private class CopyFiles extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                extractXMLFilesFromApk(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        void extractXMLFilesFromApk(String target) throws IOException {
            AssetManager assetManager = getAssets();
            String[] files = assetManager.list("");

            for (String s : files) {
                if(!s.contains("xml"))
                    continue;

                InputStream in = assetManager.open(s);

                File f = new File(target + s);
                if (!f.exists()) {
                    OutputStream out = new FileOutputStream(target + s);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        }
    }
}
