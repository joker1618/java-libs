package xxx.joker.libs.media;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.utils.JkConvert;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by f.barbano on 10/07/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
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

            long dur = Math.round(JkConvert.toDouble(metadata.get("xmpDM:duration"))*1000d);
            jkVideoInfo.setDuration(dur);

            jkVideoInfo.setWidth(JkConvert.toInt(metadata.get("tiff:ImageWidth")));
            jkVideoInfo.setHeight(JkConvert.toInt(metadata.get("tiff:ImageLength")));
            jkVideoInfo.setSamplingRate(JkConvert.toInt(metadata.get("xmpDM:audioSampleRate")));

            return jkVideoInfo;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    public static JkPictureInfo analyzePicture(byte[] bytes) throws JkRuntimeException {
        try(InputStream is = new ByteArrayInputStream(bytes)) {
            return analyzePicture(is);
        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }
    public static JkPictureInfo analyzePicture(Path picturePath) throws JkRuntimeException {
        try(InputStream is = new FileInputStream(picturePath.toFile())) {
            JkPictureInfo pic = analyzePicture(is);
            pic.setImagePath(picturePath);
            return pic;
        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }
    private static JkPictureInfo analyzePicture(InputStream inputstream) throws JkRuntimeException {
        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            //Jpeg Parse
            JpegParser jpegParser = new JpegParser();
            jpegParser.parse(inputstream, handler, metadata, pcontext);

            JkPictureInfo imageInfo = new JkPictureInfo();
            imageInfo.setWidth(JkConvert.toInt(metadata.get("tiff:ImageWidth")));
            imageInfo.setHeight(JkConvert.toInt(metadata.get("tiff:ImageLength")));

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

            JkAudioInfo audioInfo = new JkAudioInfo();
            audioInfo.setAudioPath(mp3Path);
            audioInfo.setSampleRate(JkConvert.toInt(metadata.get("xmpDM:audioSampleRate"), -1));
            audioInfo.setContentType(metadata.get("Content-Type"));
            audioInfo.setVersionLabel(metadata.get("version"));
            audioInfo.setDuration(JkConvert.toLong(metadata.get("xmpDM:duration").replaceAll("\\..*", ""), -1L));

            return audioInfo;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

}
