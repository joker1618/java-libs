package xxx.joker.libs.javalibs.media.analysis;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;
import xxx.joker.libs.javalibs.datetime.JkTime;
import xxx.joker.libs.javalibs.exception.JkRuntimeException;
import xxx.joker.libs.javalibs.utils.JkConverter;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

/**
 * Created by f.barbano on 10/07/2017.
 */
public class JkMediaAnalyzer {

    public static JkVideoInfo analyzeVideo(Path videoPath) throws JkRuntimeException {
        try (FileInputStream inputstream = new FileInputStream(videoPath.toFile())) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            MP4Parser MP4Parser = new MP4Parser();
            MP4Parser.parse(inputstream, handler, metadata, pcontext);

            JkVideoInfo jkVideoInfo = new JkVideoInfo();
            jkVideoInfo.setVideoPath(videoPath);
            jkVideoInfo.setContentType(metadata.get("Content-Type"));

            long dur = Math.round(JkConverter.stringToDouble(metadata.get("xmpDM:duration"))*1000d);
            jkVideoInfo.setDuration(dur);

            jkVideoInfo.setWidth(JkConverter.stringToInteger(metadata.get("tiff:ImageWidth")));
            jkVideoInfo.setHeight(JkConverter.stringToInteger(metadata.get("tiff:ImageLength")));
            jkVideoInfo.setSamplingRate(JkConverter.stringToInteger(metadata.get("xmpDM:audioSampleRate")));

            return jkVideoInfo;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    public static JkPictureInfo analyzePicture(Path picturePath) throws JkRuntimeException {
        try (FileInputStream inputstream = new FileInputStream(picturePath.toFile())) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            //Jpeg Parse
            JpegParser JpegParser = new JpegParser();
            JpegParser.parse(inputstream, handler, metadata,pcontext);

            JkPictureInfo imageInfo = new JkPictureInfo();
            imageInfo.setImagePath(picturePath);
            imageInfo.setWidth(JkConverter.stringToInteger(metadata.get("tiff:ImageWidth")));
            imageInfo.setHeight(JkConverter.stringToInteger(metadata.get("tiff:ImageLength")));

            return imageInfo;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    public static JkAudioInfo analyzeMP3(Path mp3Path) throws JkRuntimeException {
        try (FileInputStream inputstream = new FileInputStream(mp3Path.toFile())) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            //Jpeg Parse
            Mp3Parser parser = new Mp3Parser();
            parser.parse(inputstream, handler, metadata,pcontext);
            Arrays.stream(metadata.names()).forEach(n -> display("%-20s:\t%s", n, metadata.get(n)));

            JkAudioInfo audioInfo = new JkAudioInfo();
            audioInfo.setAudioPath(mp3Path);
            audioInfo.setSampleRate(JkConverter.stringToInteger(metadata.get("xmpDM:audioSampleRate"), -1));
            audioInfo.setContentType(metadata.get("Content-Type"));
            audioInfo.setVersionLabel(metadata.get("version"));
            audioInfo.setDuration(JkConverter.stringToLong(metadata.get("xmpDM:duration").replaceAll("\\..*", ""), -1L));

            return audioInfo;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    public static void main(String[] args) {
        JkMediaAnalyzer.analyzeMP3(Paths.get("C:\\Users\\feder\\Desktop\\Vivere.mp3"));
    }

}
