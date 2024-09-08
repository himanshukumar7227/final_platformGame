package audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	
	public static int MENU_1=0;
	public static int LEVEL_1=1;
	public static int LEVEL_2=2;
	
	public static int DIE=0;
	public static int JUMP=1;
	public static int GAMEOVER=2;
	public static int LVL_COMPLETED=3;
	public static int ATTACK_ONE=4;
	public static int ATTACK_TWO=5;
	public static int ATTACK_THREE=6;
	
	private Clip[] songs,effects;
	private int currentSongsId;
	private float volume=0.75f;
	private boolean songMute,effectMute;
	private Random rand=new Random();
	
	public AudioPlayer() {
		loadSongs();
		loadEffects();
		playSong(MENU_1);
	}
	
	private void loadSongs() {
		String[] names={"menu","Level1","Level2"};
		songs=new Clip[names.length];
		for(int i=0;i<songs.length;i++)
			songs[i]=getClip(names[i]);
	}
	
	private void loadEffects() {
		String[] effectNames={"die","jump","gameover","lvlcompleted","attack1","attack2","attack3"};
		effects=new Clip[effectNames.length];
		for(int i=0;i<effects.length;i++)
			effects[i]=getClip(effectNames[i]);
		updateEffectsVolume();
	}
	
//	private Clip getClip(String name) {
//		URL url =getClass().getResource("/audios/"+name+".wav");
//		AudioInputStream audio;
//		
//		try {
//			audio =AudioSystem.getAudioInputStream(url);
//			Clip c=AudioSystem.getClip();
//			c.open(audio);
//			return c;
//		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	private Clip getClip(String name) {
//	    try {
//	        // Load resource using getResourceAsStream instead of getResource
//	        InputStream is = getClass().getResourceAsStream("/audios/" + name + ".wav");
//	        if (is == null) {
//	            System.err.println("Resource not found: " + name);
//	            return null;
//	        }
//	        AudioInputStream audio = AudioSystem.getAudioInputStream(is);
//	        Clip c = AudioSystem.getClip();
//	        c.open(audio);
//	        return c;
//	    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
//	        e.printStackTrace();
//	        return null;
//	    }
//	}

	private Clip getClip(String name) {
	    try {
	        // Load resource using getResourceAsStream instead of getResource
	        InputStream is = getClass().getResourceAsStream("/audios/" + name + ".wav");
	        if (is == null) {
	            System.err.println("Resource not found: " + name);
	            return null;
	        }
	        
	        // Read the entire input stream into memory
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte[] buffer = new byte[4096];
	        int bytesRead;
	        while ((bytesRead = is.read(buffer)) != -1) {
	            baos.write(buffer, 0, bytesRead);
	        }
	        byte[] audioBytes = baos.toByteArray();
	        
	        // Create an AudioInputStream from the byte array
	        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
	        AudioInputStream audio = AudioSystem.getAudioInputStream(bais);
	        
	        // Open a Clip and return it
	        Clip c = AudioSystem.getClip();
	        c.open(audio);
	        return c;
	    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	
	public void setVolume(Float volume) {
		this.volume=volume;
		updateSongVolume();
		updateEffectsVolume();
	}
	
	public void stopSong() {
		if(songs[currentSongsId] != null &&songs[currentSongsId].isActive())
			songs[currentSongsId].stop();
	}
	
	public void setLevelSong(int lvlIndex) {
		if(lvlIndex%2==0)
			playSong(LEVEL_1);
		else
			playSong(LEVEL_2);
	}
	
	public void lvlCompleted() {
		stopSong();
		playEffect(LVL_COMPLETED);
	}
	
	public void playAttackSound() {
		int start=4;
		start+=rand.nextInt(3);
		playEffect(start);
	}
	
	public void playEffect(int effect) {
		effects[effect].setMicrosecondPosition(0);
		effects[effect].start();
	}
	
	public void playSong(int song) {
		stopSong();
		currentSongsId=song;
		updateSongVolume();
		songs[currentSongsId].setMicrosecondPosition(0);
		songs[currentSongsId].loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void toggleSongMute() {
		this.songMute=!songMute;
		for(Clip c:songs) {
			BooleanControl booleanControl=(BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(songMute);
		}
	}
	
	public void toggleEffectMute() {
		this.effectMute=!effectMute;
		for(Clip c:effects) {
			BooleanControl booleanControl=(BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(effectMute);
		}
		if(!effectMute)
			playEffect(JUMP);
	}
	
	private void updateSongVolume() {
		
		FloatControl gainControl=(FloatControl) songs[currentSongsId].getControl(FloatControl.Type.MASTER_GAIN);
		float range =gainControl.getMaximum()-gainControl.getMinimum();
		float gain=(range*volume+gainControl.getMinimum());
		gainControl.setValue(gain);
	}
	
	private void updateEffectsVolume() {
		for(Clip c:effects) {
			FloatControl gainControl=(FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range =gainControl.getMaximum()-gainControl.getMinimum();
			float gain=(range*volume+gainControl.getMinimum());
			gainControl.setValue(gain);
		}
	}
}
