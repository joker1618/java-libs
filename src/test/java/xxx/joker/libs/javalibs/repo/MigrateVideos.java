package xxx.joker.libs.javalibs.repo;

import org.junit.Test;
import xxx.joker.libs.javalibs.dao.csv.JkCsvDao;
import xxx.joker.libs.javalibs.repository.JkRepository;
import xxx.joker.libs.javalibs.utils.JkStreams;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MigrateVideos {

    Path FOLDER = Paths.get("src\\test\\resources\\migration");


    @Test
    public void readSaveCsvDao() throws Exception {
        JkCsvDao<Category> categoryDao = new JkCsvDao<>(FOLDER.resolve("categories.csv"), Category.class);
        List<Category> categories = categoryDao.readAll();
        categoryDao.persist(categories);

        JkCsvDao<Video> videoDao = new JkCsvDao<>(FOLDER.resolve("videos.csv"), Video.class);
        List<Video> videos = videoDao.readAll();
        videoDao.persist(videos);
    }

    @Test
    public void migrateCategories() throws Exception {
        JkCsvDao<Category> categoryDao = new JkCsvDao<>(FOLDER.resolve("categories.csv"), Category.class);
        List<Category> categories = categoryDao.readAll();

        List<NewCategory> newCats = JkStreams.map(categories, NewCategory::new);
        JkRepository.save(FOLDER.resolve("categoriesNEW.csv"), newCats);

        List<NewCategory> newCats2 = JkRepository.load(FOLDER.resolve("categoriesNEW.csv"));
        JkRepository.save(FOLDER.resolve("categoriesNEW_2.csv"), newCats2);
    }

    @Test
    public void migrateVideos() throws Exception {
        JkCsvDao<Video> videoDao = new JkCsvDao<>(FOLDER.resolve("videos.csv"), Video.class);
        List<Video> video = videoDao.readAll();

        List<NewVideo> newVideos = JkStreams.map(video, NewVideo::new);
        JkRepository.save(FOLDER.resolve("videosNEW.csv"), newVideos);

        List<NewVideo> newVideos2 = JkRepository.load(FOLDER.resolve("videosNEW.csv"));
        JkRepository.save(FOLDER.resolve("videosNEW_2.csv"), newVideos2);
    }
}
