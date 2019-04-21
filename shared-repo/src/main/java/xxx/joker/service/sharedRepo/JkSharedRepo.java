package xxx.joker.service.sharedRepo;

import xxx.joker.libs.repository.JkRepo;
import xxx.joker.service.sharedRepo.entities.JkNation;

public interface JkSharedRepo extends JkRepo {

    JkNation getNation(String nationName);


}
