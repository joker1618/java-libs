package util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.apps.formula1.parsers.WikiParser;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConsole;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.*;
import java.util.stream.Collectors;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ChecksVari {

    F1Model model = F1ModelImpl.getInstance();

    @Test
    public void var() throws Exception {
        display("Race times weird");
        model.getGranPrixs().stream()
                .flatMap(gp -> gp.getRaces().stream())
                .filter(r -> r.getTime() != null)
                .filter(r -> r.getTime().toMillis() < (1000*60*10))
                .distinct()
                .forEach(r -> JkConsole.display("   {}", r.strFull()));

        display("\n##################################\n");

        display("Fast lap times weird");
        model.getGranPrixs().stream()
                .filter(gp -> gp.getFastLap().getLapTime().toMillis() < (1000*60))
                .filter(gp -> gp.getFastLap().getLapTime().toMillis() > (1000*60*3))
                .forEach(gp -> JkConsole.display("   {}", gp));

        display("\n##################################\n");

        display("Qualify times weird");
        model.getGranPrixs().stream()
                .flatMap(gp -> gp.getQualifies().stream())
                .forEach(q -> {
                    for(JkDuration time : q.getTimes()) {
                        if(time != null) {
                            if(time.toMillis() < (1000 * 60) || time.toMillis() > (1000 * 60 * 3)) {
                                display("   {}", q.strFull());
                            }
                        }
                    }
                });
    }
}
