package xxx.joker.libs.media;

import xxx.joker.libs.core.ToAnalyze;

import java.nio.file.Path;

/**
 * Created by f.barbano on 12/07/2017.
 */

@ToAnalyze
@Deprecated
public class JkVideoInfo {

	private Path videoPath;
	private long duration;	// in millis
	private String contentType;
	private int width;
	private int height;
	private int samplingRate;
	
	
	protected JkVideoInfo() {
	    
	}

    public Path getVideoPath() {
        return videoPath;
    }

    public long getDuration() {
        return duration;
    }

    public String getContentType() {
        return contentType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    protected void setVideoPath(Path videoPath) {
        this.videoPath = videoPath;
    }

    protected void setDuration(long duration) {
        this.duration = duration;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    protected void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }
}
