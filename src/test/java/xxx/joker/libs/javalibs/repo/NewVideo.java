package xxx.joker.libs.javalibs.repo;

import xxx.joker.libs.javalibs.datetime.JkTime;
import xxx.joker.libs.javalibs.media.analysis.JkMediaAnalyzer;
import xxx.joker.libs.javalibs.media.analysis.JkVideoInfo;
import xxx.joker.libs.javalibs.repository.JkRepoField;
import xxx.joker.libs.javalibs.repository.JkDefaultRepoTable;
import xxx.joker.libs.javalibs.utils.JkBytes;
import xxx.joker.libs.javalibs.utils.JkEncryption;
import xxx.joker.libs.javalibs.utils.JkFiles;
import xxx.joker.libs.javalibs.utils.JkStreams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class NewVideo extends JkDefaultRepoTable {

	@JkRepoField(index = 0)
	private Path path;
	@JkRepoField(index = 1)
	private String md5;
	@JkRepoField(index = 2)
	private long size;
	@JkRepoField(index = 3)
	private int width;
	@JkRepoField(index = 4)
	private int height;
	@JkRepoField(index = 5)
	private long duration;
	@JkRepoField(index = 6, collectionType = NewCategory.class)
	private Set<NewCategory> categories;
	@JkRepoField(index = 7)
	private int playTimes;
	@JkRepoField(index = 8)
	private boolean toBeSplit;
	@JkRepoField(index = 9)
	private boolean cataloged;


	public NewVideo() {
		this.categories = new TreeSet<>();
	}
	public NewVideo(Video v) {
		this.path = v.getPath();
		this.md5 = v.getMd5();
		this.size = v.getSize();
		this.width = v.getWidth();
		this.height = v.getHeight();
		this.duration = v.getDuration().getTotalMillis();
		this.categories = new TreeSet<>(JkStreams.map(v.getCategories(), NewCategory::new));
		this.playTimes = v.getPlayTimes();
		this.toBeSplit = v.isToBeSplit();
		this.cataloged = v.isCataloged();
	}

	public static NewVideo createFromPath(Path path) throws Exception {
		NewVideo video = new NewVideo();
		video.md5 = JkEncryption.getMD5(JkBytes.getBytes(path));
		video.path = path;
		video.size = Files.size(path);
		JkVideoInfo vinfo = JkMediaAnalyzer.analyzeVideo(path);
		video.width = vinfo.getWidth();
		video.height = vinfo.getHeight();
		video.duration = vinfo.getDuration();
		return video;
	}


    public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getURL() {
		return JkFiles.toUrlString(path);
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isCataloged() {
		return cataloged;
	}

	public void setCataloged(boolean cataloged) {
		this.cataloged = cataloged;
	}

	public void setToBeSplit(boolean toBeSplit) {
		this.toBeSplit = toBeSplit;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getFormat() {
		return (double) width / height;
	}

	public JkTime getDuration() {
		return JkTime.of(duration);
	}

	public Set<NewCategory> getCategories() {
		return categories;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public boolean isToBeSplit() {
		return toBeSplit;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setDuration(JkTime duration) {
		this.duration = duration.getTotalMillis();
	}

	public void addCategories(Collection<NewCategory> categories) {
		this.categories.addAll(categories);
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
	}

	public void incrementPlayTimes() {
		this.playTimes++;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

    @Override
    public String getPrimaryKey() {
        return getMd5();
    }

}
