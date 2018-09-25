package tika;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import xxx.joker.libs.javalibs.media.analysis.JkMediaAnalyzer;
import xxx.joker.libs.javalibs.media.analysis.JkVideoInfo;

import java.io.File;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class SpikesTika {

    @Test
    public void tt() throws Exception {
        File source = new File("C:\\Users\\feder\\IdeaProjects\\APPS\\video-persistence\\videos\\Amateur_African_Girl_Fucked_and_Covered_in_Cum-240-XHamster.1.mp4");

        //detecting the file type
//        try (FileInputStream inputstream = new FileInputStream(source)) {
//            BodyContentHandler handler = new BodyContentHandler();
//            Metadata metadata = new Metadata();
//            ParseContext pcontext = new ParseContext();
//
//            //Html parser
//            MP4Parser MP4Parser = new MP4Parser();
//            MP4Parser.parse(inputstream, handler, metadata, pcontext);
//            System.out.println("Contents of the document:  :" + handler.toString());
//            System.out.println("Metadata of the document:");
//            String[] metadataNames = metadata.names();
//
//            for (String name : metadataNames) {
//                System.out.println(name + ": " + metadata.get(name));
//            }
//        }

        JkVideoInfo vinfo = JkMediaAnalyzer.analyzeVideo(source.toPath());
        display("%s", ToStringBuilder.reflectionToString(vinfo, ToStringStyle.MULTI_LINE_STYLE));
    }
}
