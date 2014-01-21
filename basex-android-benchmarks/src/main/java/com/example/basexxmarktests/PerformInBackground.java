package com.example.basexxmarktests;

import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import org.basex.android.BaseXDatabase;
import org.basex.core.BaseXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stephan on 1/15/14.
 */

interface PerformInBackgroundListener<T> {
    public void statusUpdate(T param);

    public void finished();
}

public class PerformInBackground extends AsyncTask<Integer, Void, Void> {

    private PerformInBackgroundListener<String> listener;
    private BaseXDatabase baseXDatabase;
    private String csv = "";

    private static String[] dbNames = new String[14];

    public static File root = android.os.Environment
            .getExternalStorageDirectory();
    private static String externalFilePath = root.getAbsolutePath();
    private static String baseXFileFolder = "BaseXXMark";
    private static String outputFileFolder = "OutputFiles";
    private static String inputFileFolder = "InputFiles";

    private static String absoluteOutputPath = externalFilePath
            + File.separator + baseXFileFolder + File.separator
            + outputFileFolder + File.separator;
    private String absoluteInputPath;
    private String output = "";


    private String tracefile_target = baseXFileFolder + File.separator + outputFileFolder + File.separator;
    private int runs = 50;
    private long breakup_time;
    private int min_max;


    PerformInBackground(PerformInBackgroundListener listener, String dataDir) {

        this.listener = listener;
        this.baseXDatabase = BaseXDatabase.getBaseXDatabaseInstance(dataDir);

        absoluteInputPath = dataDir + "/test_files/";
        breakup_time = 5000; //in millisecs

        for (int i = 1; i <= 14; i++) {
            dbNames[i - 1] = "" + i;
        }
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        int operation = integers[0];
        if(integers.length > 1) {
            min_max = integers[1];
        }
        output = "";
        try {
            switch (operation) {
                case 0:
                    createTraces("1000kb", "10.xml");
                    break;
                case 1:
                    executeTests(0, 7);
                    break;
                case 2:
                    executeTests(7, 12);
                    break;
                case 3:
                    executeTests(12, 20);
                    break;
                case 4:
                    executeTests(0, 20);
                    break;
                case 5:
                    executeTestsBackwards();
                    break;
                case 6:
                    createTraces("100MB", "100.xml");
                    break;
                default:
                    break;
            }
        } catch (BaseXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void executeTestsBackwards() throws IOException {

        initCsvBackwards();

        String list = baseXDatabase.listDBs();

        for (String i : dbNames) {
            if (!list.contains(i + "00kb")) {
                Log.d("DB_OP", "CREATING: " + i + "00kb DB");
                baseXDatabase.createDatabase(i + "00kb");
                baseXDatabase.openDatabase(i + "00kb");
                String file_name = absoluteInputPath + i + ".xml";
                baseXDatabase.addSource(null, file_name);
                Log.d("DB_OP", "ADDING: " + file_name);
                baseXDatabase.closeDatabase();
            }
        }
        for (String db : dbNames) {
            executeAllQueriesOnDBBackwards(db + "00kb");
        }

        writeCsv(20, 1);
    }

    private void executeAllQueriesOnDBBackwards(String db_name) throws IOException {

        baseXDatabase.openDatabase(db_name);
        output = db_name + " Database\n\n";

        ArrayList<String> queries = initQueries(db_name);
        csv += db_name;
        for (int i = 19; i >= 0; i--) {
            output += "Executing Q" + i + ":\n";
            publishProgress(null);
            String query = queries.get(i);
            String time = getMeasuredTime(query);
            csv += ";" + time.replace(" (avg)", "");
            output += "Took: " + time + "n***\n";
            publishProgress(null);
        }
        csv += "\n";
        baseXDatabase.closeDatabase();
        output = "FINISHED!";
        publishProgress(null);
        writeCsv(20, 1);
    }

    private String getMeasuredTime(String query) throws IOException {
        int j = 0;
        long time_start, time_end, the_time = min_max == 1 ? 100000 : 0;
        Performance runPerformance = new Performance();
        for (; j < runs; j++) {
            time_start = runPerformance.getTimeInMilli();
            baseXDatabase.executeXQuery(query);
            time_end = runPerformance.getTimeInMilli();
            if(min_max == 1) {
                if((time_end - time_start) < the_time)
                    the_time = time_end - time_start;
            }
            if(min_max == 3) {
                if((time_end - time_start) > the_time)
                    the_time = time_end - time_start;
            }
            if(runPerformance.getTimeInMilli() > breakup_time) {
                break;
            }
        }
        String ret = min_max == 2 ? runPerformance.getTime(j + 1) : ("" + the_time + "ms (" + ( min_max == 1 ? "min)" : "max)"));

        return ret;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.finished();
    }

    private void createTraces(String db_name, String file_name_xml) throws IOException {


        String list = baseXDatabase.listDBs();
        if (!list.contains(db_name)) {
            baseXDatabase.createDatabase(db_name);
            baseXDatabase.openDatabase(db_name);
            String file_name = absoluteInputPath + file_name_xml;
            baseXDatabase.addSource(null, file_name);
            Log.d("DB_OP", "ADDING: " + file_name);
            baseXDatabase.closeDatabase();
        }

        baseXDatabase.openDatabase(db_name);

        ArrayList<String> queries = initQueries(db_name);
        int i = 1;
        for (String query : queries) {
            output += "Tracing Query " + i + "\n";
            publishProgress(null);
            Log.d("TRACES", "Query " + i);
            Debug.startMethodTracing(tracefile_target + "q" + i++ + "-trace-" + db_name);
            baseXDatabase.executeXQuery(query);
            Debug.stopMethodTracing();
            output += "End of tracing Query " + (i - 1) + "\n\n";
            publishProgress(null);
        }

        baseXDatabase.closeDatabase();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        listener.statusUpdate(output);
    }

    private ArrayList<String> initQueries(String db_name) {
        ArrayList<String> queries = new ArrayList<String>();
        queries.add(Queries.getQ1(db_name));
        queries.add(Queries.getQ2(db_name));
        queries.add(Queries.getQ3(db_name));
        queries.add(Queries.getQ4(db_name));
        queries.add(Queries.getQ5(db_name));
        queries.add(Queries.getQ6(db_name));
        queries.add(Queries.getQ7(db_name));
        queries.add(Queries.getQ8(db_name));
        queries.add(Queries.getQ9(db_name));
        queries.add(Queries.getQ10(db_name));
        queries.add(Queries.getQ11(db_name));
        queries.add(Queries.getQ12(db_name));
        queries.add(Queries.getQ13(db_name));
        queries.add(Queries.getQ14(db_name));
        queries.add(Queries.getQ15(db_name));
        queries.add(Queries.getQ16(db_name));
        queries.add(Queries.getQ17(db_name));
        queries.add(Queries.getQ18(db_name));
        queries.add(Queries.getQ19(db_name));
        queries.add(Queries.getQ20(db_name));
        return queries;
    }

    private void executeTests(int from, int to) throws IOException {

        initCsv(from, to);

        String list = baseXDatabase.listDBs();

        for (String i : dbNames) {
            if (!list.contains(i + "00kb")) {
                Log.d("DB_OP", "CREATING: " + i + "00kb DB");
                baseXDatabase.createDatabase(i + "00kb");
                baseXDatabase.openDatabase(i + "00kb");
                String file_name = absoluteInputPath + i + ".xml";
                baseXDatabase.addSource(null, file_name);
                Log.d("DB_OP", "ADDING: " + file_name);
                baseXDatabase.closeDatabase();
            }
        }
        for (String db : dbNames) {
            executeAllQueriesOnDB(db + "00kb", from, to);
        }

        writeCsv(from, to);
    }

    private void writeCsv(int from, int to) {
        String filename = "basex_XMarkTests_Q" + from + "-Q" + to + "-"
                + new SimpleDateFormat("yyyy-MM-dd_kk-mm").format(new Date()
                .getTime()) +
                (min_max == 1 ? "-MIN" : (min_max == 2 ? "-AVG" : "-MAX")) + ".csv";
        File file = new File(absoluteOutputPath, filename);
        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(csv);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCsv(int from, int to) {
        csv = "";
        for (int i = from +1; i <= to +1; i++) {
            csv += ";Query " + i;
        }
        csv += "\n";
    }

    private void initCsvBackwards() {
        csv = "";
        for (int i = 20; i > 0; i--) {
            csv += ";Query " + i;
        }
        csv += "\n";
    }

    private void executeAllQueriesOnDB(String db_name, int from, int to) throws IOException {

        String ret = "Opening " + db_name + "\n";
        baseXDatabase.openDatabase(db_name);
        output = db_name + " Database\n\n";

        ArrayList<String> queries = initQueries(db_name);
        System.out.println("At " + db_name + "\n");
        csv += db_name;
        for (int i = from; i < to; i++) {
            output += "Executing Q" + (i +1) + ":\n";
            publishProgress(null);
            String query = queries.get(i);
            String time = getMeasuredTime(query);
            csv += ";" + time.replace(" (avg)", "");
            output += "Took: " + time + "\n***\n";
            publishProgress(null);
        }
        csv += "\n";
        baseXDatabase.closeDatabase();
        output = "FINISHED!";
        publishProgress(null);
        writeCsv(from, to);
    }
}
