package android.quiniDroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QuiniDroid extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Button b= (Button) findViewById(R.string.btnMiQuiniela);
        //b.setOnClickListener(abrirMiquiniela);
        
       Button b2= (Button) findViewById(R.id.btnHistorico);      
       b2.setOnClickListener(new View.OnClickListener() {

               @Override

               public void onClick(View v) {

                       Intent i = new Intent(QuiniDroid.this, Historico.class);

                       startActivity(i);
                       finish();

               }

       });
    }
}