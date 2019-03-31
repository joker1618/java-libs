package xxx.joker.libs.core.web;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JkDownloader {

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

}
