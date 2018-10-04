package xxx.joker.libs.javalibs.media.analysis;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;
import xxx.joker.libs.javalibs.exception.JkRuntimeException;
import xxx.joker.libs.javalibs.utils.JkConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

}
