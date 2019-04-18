package util;

import org.junit.Test;
import xxx.joker.apps.formula1.dataCreator.model.F1Model;
import xxx.joker.apps.formula1.dataCreator.model.F1ModelImpl;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.utils.JkConsole;

import static xxx.joker.libs.core.utils.JkConsole.display;

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
