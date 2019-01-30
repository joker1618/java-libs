package misc;

import org.junit.Test;
import xxx.joker.apps.agenda.console.args.AgendaArgs;
import xxx.joker.apps.agenda.model.entities.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Misc {

    @Test
    public void c() throws NoSuchFieldException {
        Event ev = new Event();
        Field[] declFields = ev.getClass().getDeclaredFields();
        Arrays.stream(declFields).forEach(f -> display("{} {}", Modifier.toString(f.getModifiers()), f.getName()));
        Field[] superFields = ev.getClass().getSuperclass().getDeclaredFields();
        Arrays.stream(superFields).forEach(f -> {
            try {
                display("{} {} {}", Modifier.toString(f.getModifiers()), f.getName(), f.get(ev));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        display("\nmmm");
        Arrays.stream(ev.getClass().getMethods()).forEach(f -> display("{} {}", Modifier.toString(f.getModifiers()), f.getName()));
        display("\nmmm2");
        Arrays.stream(ev.getClass().getDeclaredMethods()).forEach(f -> display("{} {}", Modifier.toString(f.getModifiers()), f.getName()));

    }
}
