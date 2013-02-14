package android.quiniDroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class QuinielaActual extends Activity {

	private static String URL_BASE;
	private static String URL_RSS;
	Context context;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actual);

    }

}
