package spikes;


import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class GetListElemType {

    List<String> stringList = new ArrayList<String>();
    List<Integer> integerList = new ArrayList<Integer>();

    @Test
    public void getElemType() throws Exception {
        Field stringListField = GetListElemType.class.getDeclaredField("stringList");
        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
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

    }
}