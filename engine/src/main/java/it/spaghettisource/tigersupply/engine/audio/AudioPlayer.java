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

			int streamLengthInBytes = (int)(audioInputStream.getFrameLength() * audioFormat.getFrameSize());
			
			if (streamLengthInBytes > Integer.MAX_VALUE){
				Exception ex = new Exception("length of AudioInputStream exceeds 2^31, cannot properly reset stream!");
				throw ex;
			}

			audioInputStream.mark(streamLengthInBytes);
			
			while(!forceStop){	//continue until request to stop
				sendStreamToMixer(audioInputStream, line);
				audioInputStream.reset();	
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
	 * convert the InputStream to supported format
	 * 
	 * @param in
	 * @return
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	private AudioInputStream getAudioInputStream(InputStream in) throws UnsupportedAudioFileException, IOException {

		AudioInputStream audio =  AudioSystem.getAudioInputStream(in);
		AudioFormat format = audio.getFormat();		

		/**
		 * we can't yet open the device for ALAW/ULAW playback,
		 * convert ALAW/ULAW to PCM
		 */
		if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||(format.getEncoding() == AudioFormat.Encoding.ALAW)) {
			format = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, 
					format.getSampleRate(),
					format.getSampleSizeInBits() * 2,
					format.getChannels(),
					format.getFrameSize() * 2,
					format.getFrameRate(),
					true);
		}

		return AudioSystem.getAudioInputStream(format, audio);
	}



}
