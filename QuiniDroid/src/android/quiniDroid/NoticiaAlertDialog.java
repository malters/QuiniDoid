package android.quiniDroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class NoticiaAlertDialog extends AlertDialog.Builder {
	private Noticia n;
	private Context c;
	private Speech s;

	protected NoticiaAlertDialog(Context context, Speech speech, Noticia noticia) {
		super(context);
		n = noticia;
		c = context;
		s = speech;
		this.setTitle(n.getTitulo());
		this.setIcon(context.getResources().getDrawable(R.drawable.logo));
		this.setMessage(n.getDescripcion());
		this.setNegativeButton(context.getString(R.string.atras), null);
		this.setPositiveButton(context.getString(R.string.leemelo), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				s.speakAfterCheckingForTTS(n.getTitulo()+". "+n.getDescripcion());
			}
		});
		this.setNeutralButton(context.getString(R.string.web), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(n.getLink()));
				c.startActivity( intent );
			}
		});
	}
}
