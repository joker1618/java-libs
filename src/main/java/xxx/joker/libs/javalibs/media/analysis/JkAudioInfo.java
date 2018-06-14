package xxx.joker.libs.javalibs.media.analysis;

import it.sauronsoftware.jave.AudioInfo;
import it.sauronsoftware.jave.MultimediaInfo;
import xxx.joker.libs.javalibs.format.JkColumnFmtBuilder;
import xxx.joker.libs.javalibs.utils.JkStrings;

import java.nio.file.Path;

/**
 * Created by f.barbano on 10/07/2017.
 */
public class JkAudioInfo {

	private Path audioPath;
	// Multimedia info
	private long duration;	// in millis
	private String format;
	// Audio specific info
	private int samplingRate = -1;
	private int channels = -1;
	private int bitRate = -1;
	private String decoder;


	JkAudioInfo(Path audioPath, MultimediaInfo mInfo) {
		this.audioPath = audioPath;

		this.duration = mInfo.getDuration();
		this.format = mInfo.getFormat();

		AudioInfo audio = mInfo.getAudio();
		this.samplingRate = audio.getSamplingRate();
		this.channels = audio.getChannels();
		this.bitRate = audio.getBitRate();
		this.decoder = audio.getDecoder();
	}


	@Override
	public String toString() {
		StringBuilder sbfields = new StringBuilder();
		sbfields.append("Path:|").append(audioPath.toAbsolutePath()).append("\n");
		sbfields.append("Duration:|").append(duration).append("\n");
		sbfields.append("Format:|").append(format).append("\n");
		sbfields.append("Sampling rate:|").append(samplingRate).append("\n");
		sbfields.append("Bit rate:|").append(bitRate).append("\n");
		sbfields.append("Channels:|").append(channels).append("\n");
		sbfields.append("Decoder:|").append(decoder).append("\n");
		String fieldStr = new JkColumnFmtBuilder().addLines(sbfields.toString()).toString("|", 2);
		fieldStr = JkStrings.leftPadLines(fieldStr, " ", 3);


		StringBuilder sb = new StringBuilder();
		sb.append("Audio file \"").append(audioPath.getFileName()).append("\" ");
		sb.append("{\n");
		sb.append(fieldStr);
		sb.append("}");

		return sb.toString();
	}


	public long getDuration() {
		return duration;
	}
	public String getFormat() {
		return format;
	}
	public int getSamplingRate() {
		return samplingRate;
	}
	public int getChannels() {
		return channels;
	}
	public int getBitRate() {
		return bitRate;
	}
	public String getDecoder() {
		return decoder;
	}
}
