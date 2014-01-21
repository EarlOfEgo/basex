package org.basex.basexandroidexample;

import android.app.Activity;
import android.os.Bundle;

import org.basex.android.BaseXDatabase;

public class MainActivity extends Activity {

    private BaseXDatabase baseXDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseXDatabase = BaseXDatabase.getBaseXDatabaseInstance(getApplicationInfo().dataDir);


    }

}
