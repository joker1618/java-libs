package spikes;


import org.junit.Test;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class GetListElemType {

    String str;
    int[] iarr;
    List<String> stringList;
    List<Integer> integerList;
    Map<Integer, Path> mappa;

    @Test
    public void getElemType() throws Exception {
        ParameterizedType strGen = (ParameterizedType) GetListElemType.class.getDeclaredField("str").getGenericType();
        List<? extends Class<?>> cl = JkStreams.map(Arrays.asList(strGen.getActualTypeArguments()), t -> (Class<?>) t);
        display("str  {}\n", cl);
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
        Class<?> mlc1 = (Class<?>) mappapt.getActualTypeArguments()[0];
        Class<?> mlc2 = (Class<?>) mappapt.getActualTypeArguments()[1];
        System.out.println(strf("Map<{}, {}>", mlc1.getSimpleName(), mlc2.getSimpleName()));

    }
}