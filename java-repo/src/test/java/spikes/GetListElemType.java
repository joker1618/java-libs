package spikes;


import org.junit.Test;
import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.lambda.JkStreams;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkStrings.strf;

public class GetListElemType {

    String str;
    int[] iarr;
    List<String> stringList;
    List<Integer> integerList;
    Map<Integer, Path> mappa;
    Map<JkDateTime, List<File>> mappa2;

    @Test
    public void getElemType() throws Exception {
        Field stringListField = GetListElemType.class.getDeclaredField("stringList");
        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
        Class<?>[] arr = JkStreams.map(Arrays.asList(stringListType.getActualTypeArguments()), t -> (Class<?>) t).toArray(new Class<?>[0]);
        Class<?> stringListClass = arr[0];
//        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        System.out.println("toString: "+stringListClass); // class java.lang.String.
        System.out.println("name: "+stringListClass.getName()); // class java.lang.String.
        System.out.println("simple name: "+stringListClass.getSimpleName()); // class java.lang.String.
        System.out.println("canonical name: "+stringListClass.getCanonicalName()); // class java.lang.String.
        System.out.println();

        Field integerListField = GetListElemType.class.getDeclaredField("integerList");
        ParameterizedType integerListType = (ParameterizedType) integerListField.getGenericType();
        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
        System.out.println("toString: "+integerListClass);
        System.out.println("name: "+integerListClass.getName());
        System.out.println("simple name: "+integerListClass.getSimpleName());
        System.out.println("canonical name: "+integerListClass.getCanonicalName());
        System.out.println();

        Field mapfielf = GetListElemType.class.getDeclaredField("mappa");
        ParameterizedType mappapt = (ParameterizedType) mapfielf.getGenericType();
        Class<?> mlc1_1 = (Class<?>) mappapt.getActualTypeArguments()[0];
        Class<?> mlc1_2 = (Class<?>) mappapt.getActualTypeArguments()[1];
        System.out.println(strf("Map<{}, {}>", mlc1_1.getSimpleName(), mlc1_2.getSimpleName()));

        Field mapfielf2 = GetListElemType.class.getDeclaredField("mappa2");
        ParameterizedType mappapt2 = (ParameterizedType) mapfielf2.getGenericType();
        Class<?> mlc2_1 = (Class<?>) mappapt2.getActualTypeArguments()[0];
        Class<?> mlc2_2 = (Class<?>) ((ParameterizedType) mappapt2.getActualTypeArguments()[1]).getActualTypeArguments()[0];
        System.out.println(strf("Map<{}, List<{}>>", mlc2_1.getSimpleName(), mlc2_2.getSimpleName()));

    }
}