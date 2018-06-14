package xxx.joker.libs.javalibs.media.analysis;

import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import xxx.joker.libs.javalibs.format.JkColumnFmtBuilder;
import xxx.joker.libs.javalibs.utils.JkStrings;

import java.nio.file.Path;

/**
 * Created by f.barbano on 10/07/2017.
 */
public class JkVideoInfo {

	private Path videoPath;
	// Multimedia info
	private long duration;	// in millis
	private String format;
	// Video specific info
	private int width;
	private int height;
	private int bitRate;
	private float frameRate;
	private String decoder;
	
	
	JkVideoInfo(Path videoPath, MultimediaInfo mInfo) {
		this.videoPath = videoPath;

		this.duration = mInfo.getDuration();
		this.format = mInfo.getFormat();

		VideoInfo video = mInfo.getVideo();
		this.width = video.getSize().getWidth();
		this.height = video.getSize().getHeight();
		this.bitRate = video.getBitRate();
		this.frameRate = video.getFrameRate();
		this.decoder = video.getDecoder();
	}


	@Override
	public String toString() {
		StringBuilder sbfields = new StringBuilder();
		sbfields.append("Path:|").append(videoPath.toAbsolutePath()).append("\n");
		sbfields.append("Resolution:|").append(width).append(" x ").append(height).append("\n");
		sbfields.append("Duration:|").append(duration).append("\n");
		sbfields.append("Format:|").append(format).append("\n");
		sbfields.append("Bit rate:|").append(bitRate).append("\n");
		sbfields.append("Frame rate:|").append(frameRate).append("\n");
		sbfields.append("Decoder:|").append(decoder).append("\n");
		String fieldStr = new JkColumnFmtBuilder().addLines(sbfields.toString()).toString("|", 2);
		fieldStr = JkStrings.leftPadLines(fieldStr, " ", 3);


		StringBuilder sb = new StringBuilder();
		sb.append("Video file \"").append(videoPath.getFileName()).append("\" ");
		sb.append("{\n");
		sb.append(fieldStr);
		sb.append("}");

		return sb.toString();
	}


	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public long getDuration() {
		return duration;
	}
	public String getFormat() {
		return format;
	}
	public int getBitRate() {
		return bitRate;
	}
	public float getFrameRate() {
		return frameRate;
	}
	public String getDecoder() {
		return decoder;
	}
}
