package android.quiniDroid;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Historico extends Activity {
	
	private static String URL_BASE;
	private static String URL_RSS;

	Context context;
	ListView listView;
	ArrayList<Noticia> noticias;
	NoticiasAdapter noticiasAdapter;
	TareaDescarga tarea;
	Speech speech;

	
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historico);
        context = getApplicationContext();

        URL_BASE = getString(R.string.url_base);
        URL_RSS = getString(R.string.url_rss);

        final ArrayList<Noticia> data = (ArrayList<Noticia>) getLastNonConfigurationInstance();
        if (data == null) {
            noticias = new ArrayList<Noticia>();
            noticiasAdapter = new NoticiasAdapter(context, R.layout.fila, noticias);
            lanzaDescargaDeNoticias();
        }else{
        	noticias = data;
            noticiasAdapter = new NoticiasAdapter(context, R.layout.fila, noticias);
        }

        speech = new Speech(this);
        
        listView = (ListView)findViewById(R.id.ListView01);
        listView.setAdapter(noticiasAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Noticia n = noticias.get((int)id);
				NoticiaAlertDialog nad = new NoticiaAlertDialog(Historico.this, speech, n);
				nad.show();
			}
		});
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        final ArrayList<Noticia> data = noticias;
        return data;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	speech.installOrSpeak(requestCode,resultCode);
    	
    }
    
    @Override
    public void onStop() {
    	speech.stop();
    	super.onStop();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu m) {
		getMenuInflater().inflate(R.menu.menu, m);
		return true;
	}
    
 	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.item01:
			speech.stop();
			break;
		case R.id.item02:
			lanzaDescargaDeNoticias();
			break;
		case R.id.item03:
			AlertDialog.Builder ab=new AlertDialog.Builder(Historico.this);
			ab.setIcon(getResources().getDrawable(R.drawable.logo));
			ab.setPositiveButton(R.string.aceptar,null);
			ab.show();
			break;
		}
		return true;
	}
    
    
    void lanzaDescargaDeNoticias(){
		try {
			tarea = new TareaDescarga();
			tarea.execute(new URL(URL_RSS));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
   }
   
   
   private class TareaDescarga extends AsyncTask<URL, String, List<Noticia>>{
	    ArrayList<Noticia> noticiasDescargadas;
		ProgressDialog progressDialog;
		boolean error=false;

		@Override
		protected List<Noticia> doInBackground(URL... params) {
			try {
				URL url = params[0];
				noticiasDescargadas = new ArrayList<Noticia>();
				
				XmlPullParserFactory parserCreator = XmlPullParserFactory
						.newInstance();
				XmlPullParser parser = parserCreator.newPullParser();
				parser.setInput(url.openStream(), null);
				int parserEvent = parser.getEventType();
				int nItems = 0;
				while (parserEvent != XmlPullParser.END_DOCUMENT) {
					switch (parserEvent) {
					case XmlPullParser.START_TAG:
						String tag = parser.getName();
						if (tag.equalsIgnoreCase("item")) {
							publishProgress(getString(R.string.descargandonoticia)+" "+(++nItems));
							Noticia noticia = new Noticia();
							parserEvent = parser.next();
							boolean itemClosed = false;
							while (parserEvent != XmlPullParser.END_DOCUMENT && !itemClosed) {
								switch (parserEvent) {
								case XmlPullParser.START_TAG:
									tag = parser.getName();
									if (tag.equalsIgnoreCase("title")) {
										noticia.setTitulo(parser.nextText());
									}
									if (tag.toLowerCase().contains("pubDate")) {
										noticia.setFecha(parser.nextText());
									}
									if (tag.equalsIgnoreCase("link")) {
										noticia.setLink(URL_BASE+parser.nextText());
									}
									if (tag.equalsIgnoreCase("description")) {
										String textoHtml = parser.nextText();
										Spanned texto = Html.fromHtml(textoHtml, new ImageGetter(), null);
										
										noticia.setDescripcion(texto);
	
										String linkImagen = URL_BASE+imageSource;
										noticia.setLinkImagen(linkImagen);
									}
									break;
								case XmlPullParser.END_TAG:
									tag = parser.getName();
									if(tag.equalsIgnoreCase("item")){
										itemClosed = true;
										noticiasDescargadas.add(noticia);
									}
									break;
								}
								parserEvent = parser.next();
							}
						}
						break;
					}
					parserEvent = parser.next();
	
				}
			} catch (Exception e) {
				Log.e("Net", "Error in network call", e);
				error = true;
			}
			return noticiasDescargadas;
		}
		
		String imageSource=null;
	    class ImageGetter implements Html.ImageGetter {
	        public Drawable getDrawable(String source) {
	        	imageSource = source;
	        	return new BitmapDrawable();
	        }
	    };
	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			noticiasAdapter.clear();
			progressDialog = ProgressDialog.show(Historico.this, getString(R.string.espere), getString(R.string.descargandonoticias),true,true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					tarea.cancel(true);
				}
			});
		}
	
		@Override
		protected void onCancelled() {
			super.onCancelled();
			noticiasAdapter.clear();
			noticiasDescargadas = new ArrayList<Noticia>();
		}
	
		@Override
		protected void onProgressUpdate(String... progreso) {
			super.onProgressUpdate(progreso);
			progressDialog.setMessage(progreso[0]);
			noticiasAdapter.notifyDataSetChanged();
		}
	
		@Override
		protected void onPostExecute(List<Noticia> result) {
			super.onPostExecute(result);
			for(Noticia n:noticiasDescargadas){
				noticiasAdapter.add(n);
			}
			noticiasAdapter.notifyDataSetChanged();
			progressDialog.dismiss();
			if(error){
				Toast.makeText(context, R.string.errordered, Toast.LENGTH_LONG).show();
			}
			lanzaDescargaDeImagenes();
		}
	   
   }

   @SuppressWarnings("unchecked")
   void lanzaDescargaDeImagenes(){
		TareaDescargaImagen tdi = new TareaDescargaImagen();
		tdi.execute(noticias);
   }

   
   private class TareaDescargaImagen extends AsyncTask<List<Noticia>, String, Drawable>{

		@Override
		protected Drawable doInBackground(List<Noticia>... arg0) {
			List<Noticia> noticias = arg0[0];
			int imagenesSinCargar = noticias.size();
			while(imagenesSinCargar > 0){
				imagenesSinCargar = noticias.size();
				for(Noticia n:noticias){
					if(n.getImagen()==null || n.getImagen().getIntrinsicHeight() <= 0){ // Reintento necesario?
						try{
							n.loadImagen(n.getLinkImagen());
							publishProgress("");
						}catch(Exception e){
							n.setImagen(getResources().getDrawable(R.drawable.logo));
						}
					}else{
						imagenesSinCargar --;
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			Toast.makeText(context, getString(R.string.imagenes_descargadas), Toast.LENGTH_SHORT).show();
			noticiasAdapter.notifyDataSetChanged();
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			noticiasAdapter.notifyDataSetChanged();
		}

	  
   }
   
   
}