package xxx.joker.libs.javalibs.media.analysis;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by f.barbano on 10/07/2017.
 */
public class JkMediaAnalyzer {

	public static JkVideoInfo analyzeVideo(Path videoPath) throws EncoderException, FileNotFoundException {
		if(!Files.exists(videoPath))	throw new FileNotFoundException(videoPath + " not found");
		Encoder encoder = new Encoder();
		MultimediaInfo info = encoder.getInfo(videoPath.toFile());
		return new JkVideoInfo(videoPath, info);
	}

	public static JkAudioInfo analyzeAudio(Path audioPath) throws EncoderException, FileNotFoundException {
		if(!Files.exists(audioPath))	throw new FileNotFoundException(audioPath + " not found");
		Encoder encoder = new Encoder();
		MultimediaInfo info = encoder.getInfo(audioPath.toFile());
		return new JkAudioInfo(audioPath, info);
	}
}
