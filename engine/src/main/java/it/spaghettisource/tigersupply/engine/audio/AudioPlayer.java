package it.spaghettisource.tigersupply.engine.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;



/**
 * music player, it can also support the loop
 * this player block the esecution of the code, os an external observer (thread must manage it). 
 * It si possible to stop it calling {@linkplain AudioPlayer#forceStop()} from an external thread
 * 
 * 
 * @author Alessandro D'Ottavio
 *
 */
public class AudioPlayer {

	private boolean forceStop;
	
	/**
	 * build this player
	 * 
	 * @param audioType logica type of this player, it has not effect inside its code
	 */
	public AudioPlayer(){
		this.forceStop = false;
	}
	
	public void forceStop(){
		forceStop = true;
	}
	
	
	/**
	 * this method is used to play the music passed as argument.
	 * it is a blocking method.
	 * if it called without the loop flag set to true, the mothod finish when the buffer is finish to play
	 * 
	 * @param data it is the byte of data of a music file
	 * @param loop
	 * @param volume linear playback volume in {@code [0,1]} (0 = silent, 1 = full); applied to the
	 *               mixer line when a gain control is available, otherwise ignored
	 * @throws Exception
	 */
	public void play(byte[] data, boolean loop, float volume) throws Exception{
		
		AudioInputStream audioInputStream = getAudioInputStream(new ByteArrayInputStream(data));
		AudioFormat	audioFormat = audioInputStream.getFormat();

		DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);

		SourceDataLine	line = null;
		line = (SourceDataLine) AudioSystem.getLine(info);

		line.open(audioFormat);
		applyVolume(line, volume);
		line.start();

		if(loop){

			/*
			 * rewind by recreating the AudioInputStream from the source bytes at each iteration:
			 * this works identically for every supported encoding (WAV, MP3) because it does not
			 * rely on mark()/reset(), which is unsupported on SPI-decoded MP3 streams.
			 */
			while(!forceStop){	//continue until request to stop
				sendStreamToMixer(audioInputStream, line);
				audioInputStream.close();
				audioInputStream = getAudioInputStream(new ByteArrayInputStream(data));
			}

		}else{
			sendStreamToMixer(audioInputStream, line);	
		}

		audioInputStream.close();
		
		line.drain();
		line.close();

	}

	
	
	/**
	 * apply a linear playback volume to the given line using the mixer's own gain control.
	 *
	 * <p>The value is clamped to {@code [0,1]} and mapped to {@link FloatControl.Type#MASTER_GAIN}
	 * (in decibels) when supported, falling back to {@link FloatControl.Type#VOLUME}. When neither
	 * control is available the line is left at its default level (best-effort, no error).
	 *
	 * @param line   the opened output line
	 * @param volume linear volume in {@code [0,1]}
	 */
	private void applyVolume(SourceDataLine line, float volume) {

		float v = Math.max(0.0f, Math.min(1.0f, volume));

		if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
			float decibels = (v <= 0.0f) ? gain.getMinimum() : (float) (20.0 * Math.log10(v));
			decibels = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), decibels));
			gain.setValue(decibels);
		} else if (line.isControlSupported(FloatControl.Type.VOLUME)) {
			FloatControl control = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
			float value = control.getMinimum() + (control.getMaximum() - control.getMinimum()) * v;
			control.setValue(value);
		}
	}

	private void sendStreamToMixer(AudioInputStream audioInputStream,SourceDataLine line)throws IOException {

		byte[]	abData = new byte[128000];
		int	nBytesRead = 0;

		while (nBytesRead != -1 && !forceStop){	//continue until buffer is finish or request to stop
			nBytesRead = audioInputStream.read(abData, 0, abData.length);	//read from the buffer 

			if (nBytesRead >= 0){
				line.write(abData, 0, nBytesRead);		//write in the mixer and play
			}
		}
	}

	/**
	 * convert the InputStream to a signed PCM format the mixer can open.
	 *
	 * <p>Streams that are already {@link AudioFormat.Encoding#PCM_SIGNED} (typically WAV) are
	 * returned unchanged. Every other encoding — MPEG for MP3 (decoded by the registered MP3SPI
	 * provider), as well as ALAW/ULAW — is converted to signed 16-bit PCM derived from the source
	 * sample rate and channel count, because a {@link SourceDataLine} cannot be opened on a
	 * compressed/companded format.
	 *
	 * @param in the raw audio stream
	 * @return an {@link AudioInputStream} in signed PCM, ready to be written to a mixer line
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	private AudioInputStream getAudioInputStream(InputStream in) throws UnsupportedAudioFileException, IOException {

		AudioInputStream audio =  AudioSystem.getAudioInputStream(in);
		AudioFormat baseFormat = audio.getFormat();

		//already PCM (e.g. WAV): can be opened on a mixer line directly
		if (baseFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
			return audio;
		}

		/*
		 * every other encoding (MPEG for MP3, ALAW/ULAW, ...) must be decoded to signed PCM before it
		 * can be opened on a SourceDataLine. Derive a 16-bit PCM target from the source sample rate
		 * and channels and let the registered SPI providers perform the conversion.
		 */
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(),
				false);

		return AudioSystem.getAudioInputStream(decodedFormat, audio);
	}



}
