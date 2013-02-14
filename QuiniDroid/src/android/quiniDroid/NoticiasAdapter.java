package android.quiniDroid;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticiasAdapter extends ArrayAdapter<Noticia> {
	ArrayList<Noticia> noticias;
	Context context;

	public NoticiasAdapter(Context context, int textViewResourceId,
			ArrayList<Noticia> noticias) {
		super(context, textViewResourceId, noticias);
		this.noticias = noticias;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.fila, null);
		}
		Noticia noticia = noticias.get(position);
		if (noticia != null) {
			TextView tv1 = (TextView) convertView.findViewById(R.id.FilaTexto1);
			TextView tv2 = (TextView) convertView.findViewById(R.id.FilaTexto2);
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.FilaImagen);
			if (tv1 != null) {
				tv1.setText(noticia.getTitulo());
			}
			if (tv2 != null) {
				tv2.setText(noticia.getFecha());
			}
			if (iv != null) {
				iv.setImageDrawable(noticia.getImagen());
			}
		}
		return convertView;
	}
}
