package it.spaghettisource.tigersupply.engine.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
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
	 * @throws Exception
	 */
	public void play(byte[] data, boolean loop) throws Exception{
		
		AudioInputStream audioInputStream = getAudioInputStream(new ByteArrayInputStream(data));
		AudioFormat	audioFormat = audioInputStream.getFormat();

		DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);

		SourceDataLine	line = null;
		line = (SourceDataLine) AudioSystem.getLine(info);

		line.open(audioFormat);
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
