package xxx.joker.libs.javalibs.media.analysis;

import java.nio.file.Path;

/**
 * Created by f.barbano on 12/07/2017.
 */
public class JkAudioInfo {

	private Path audioPath;
	private long duration;	// in millis
	private String contentType;
	private String versionLabel;
	private int sampleRate;
	
	
	protected JkAudioInfo() {
	    
	}

    public Path getAudioPath() {
        return audioPath;
    }

    public long getDuration() {
        return duration;
    }

    public String getContentType() {
        return contentType;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    protected void setAudioPath(Path audioPath) {
        this.audioPath = audioPath;
    }

    protected void setDuration(long duration) {
        this.duration = duration;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }
}
