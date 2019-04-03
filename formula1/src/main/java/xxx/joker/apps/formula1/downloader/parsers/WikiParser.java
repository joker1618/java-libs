package xxx.joker.apps.formula1.downloader.parsers;

import static xxx.joker.apps.formula1.common.F1Const.*;

import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.web.JkDownloader;

import java.util.List;

abstract class WikiParser {

    public static final String PREFIX_URL = "https://en.wikipedia.org";

    private JkDownloader downloader;
    private int year;

    protected WikiParser(int year) {
        this.year = year;
        downloader = new JkDownloader(HTML_FOLDER.resolve(""+year));
    }

    protected abstract List<F1Entrant> parseEntrants();


}
