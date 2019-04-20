package runner;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.web.JkDownloader;
import xxx.joker.service.commonRepo.JkCommonRepo;
import xxx.joker.service.commonRepo.JkCommonRepoImpl;

import java.nio.file.Path;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.service.commonRepo.config.Configs.HTML_FOLDER;
import static xxx.joker.service.commonRepo.config.Configs.TMP_FOLDER;

abstract class AbstractRunner {

    protected JkCommonRepo model = JkCommonRepoImpl.getInstance();

    private JkDownloader dhtml = new JkDownloader(HTML_FOLDER);
    private JkDownloader dwTemp = new JkDownloader(TMP_FOLDER);

    protected String getHtml(String resUrl) {
        return dhtml.getHtml(resUrl);
    }

    protected Pair<Boolean, Path> downloadResource(String resUrl) {
        String fname = JkEncryption.getMD5(resUrl) + "." + JkFiles.getExtension(resUrl);
        return dwTemp.downloadResource(fname, resUrl);
    }

    protected String fixResourceName(String fn, String url) {
        String finalFname = fn;
        String ext = JkFiles.getExtension(url);
        if(!ext.isEmpty() && !finalFname.endsWith("." + ext)) {
            finalFname += "." + ext;
        }
        return finalFname;
    }

    protected String createWikiUrl(String wikiSubPath) {
        return strf("https://en.wikipedia.org/{}", wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createWikiUrl(JkTag aTag) {
        return createWikiUrl(aTag.getAttribute("href"));
    }

    protected String createImageUrl(JkTag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }
}
