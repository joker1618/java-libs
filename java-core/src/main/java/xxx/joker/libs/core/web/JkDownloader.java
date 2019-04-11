package xxx.joker.libs.core.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(JkDownloader.class);

    private final Path folder;

    public JkDownloader(Path folder) {
        this.folder = folder;
    }

    public String getHtml(String url) {
        List<String> lines = getHtmlLines(url);
        return JkStreams.join(lines, StringUtils.LF);
    }

    public List<String> getHtmlLines(String url) {
        try {
            String fname = url.replace("/", "_").replace(":", "").replaceAll(" +", "");
            fname += ".html";

            Path htmlPath = folder.resolve(fname);
            List<String> lines;
            if(!Files.exists(htmlPath)) {
                LOG.info("Downloading html from: {}", url);
                lines = JkWeb.downloadHtmlLines(url);
                JkFiles.writeFile(htmlPath, lines);
            } else {
                lines = JkFiles.readLines(htmlPath);
            }
            return lines;

        } catch(Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }

    public boolean downloadResource(String outFileName, String url) {
        Path outPath = folder.resolve(outFileName);
        if(!Files.exists(outPath)) {
            JkWeb.downloadResource(url, outPath);
            return true;
        }
        return false;
    }

}
