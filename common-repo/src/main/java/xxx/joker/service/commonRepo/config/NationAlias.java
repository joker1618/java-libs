package xxx.joker.service.commonRepo.config;


import java.util.HashMap;
import java.util.Map;

public class NationAlias {


    private static Map<String, String> aliasMap;
    static {
        aliasMap = new HashMap<>();
        aliasMap.put("Republic of Ireland", "Ireland");
    }
}
