package xxx.joker.service.commonRepo;

import xxx.joker.libs.repository.JkRepo;
import xxx.joker.service.commonRepo.entities.JkNation;

public interface JkCommonRepo extends JkRepo {

    JkNation getNation(String nationName);


}
