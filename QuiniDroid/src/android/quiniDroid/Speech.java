package android.quiniDroid;

import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Speech {
	private Activity activity;
	private TextToSpeech tts;
	private static final int TTS_DATA_CHECK = 1;
	private String text;
	private boolean isTTSinstalled = false;
	private boolean isTTSinitialized = false;
	
	/**
	 * Gets the reference to the Activity for being able to call Intents.
	 * @param activity
	 */
	public Speech(Activity activity){
		this.activity = activity;
	}
	
	/**
	 * Does not assume that TextToSpeech is installed but starts an intent
	 * for checking whether it is or not. The Activity which calls this
	 * function should implement: 
	 *  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *   speech.installOrSpeak(requestCode,resultCode);
     *  }
	 * @param txt Text to speak
	 */
	public void speakAfterCheckingForTTS(String txt){
		this.text = txt;
		if(isTTSinitialized){
			tts.speak(txt, TextToSpeech.QUEUE_ADD, null);
		}else{
			Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			activity.startActivityForResult(intent, TTS_DATA_CHECK);
		}
		
	}
	
	/**
	 * Assumes that TextToSpeech is installed
	 * @param txt Text to speak
	 */
	public void speakWithoutCheckingForTTS(String txt){
		tts = new TextToSpeech(activity, new OnInitListener() {
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					Locale loc = new Locale("es","","");
					if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE)
						tts.setLanguage(loc);
					tts.setPitch(0.8f);
					tts.setSpeechRate(1.1f);
					isTTSinitialized = true;
					//Speak this:
					tts.speak(text, TextToSpeech.QUEUE_ADD, null);
				}
			}
		});			
	}

    /**
     * Called after checking for the installation of TTS
     * @param requestCode
     * @param resultCode
     */
    public void installOrSpeak(int requestCode, int resultCode){
    	if(requestCode == TTS_DATA_CHECK){
			if (resultCode == Engine.CHECK_VOICE_DATA_PASS) {
				isTTSinstalled = true;
				speakWithoutCheckingForTTS(text);
			} else {
				Intent installVoice = new Intent(Engine.ACTION_INSTALL_TTS_DATA);
				activity.startActivity(installVoice);
			}
    	}    	
    }
	
	/**
	 * Should be called in the onPause() or onStop() method of the Activity.
	 */
    public void stop(){
		if (tts != null) {
			tts.stop();
			tts.shutdown();
			isTTSinitialized = false;
		}
    }


	public boolean isTTSinstalled() {
		return isTTSinstalled;
	}
       

}
