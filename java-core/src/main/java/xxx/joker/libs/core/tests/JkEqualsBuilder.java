package xxx.joker.libs.core.tests;

import xxx.joker.libs.core.ToAnalyze;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes tested:
 * - boolean, Boolean
 * - int, long, float, double, W...
 * - File
 * - Path
 * - LocalDate
 * - LocalTime
 * - LocalDateTime
 * - [], lists
 * - all others classes will be tested using equals()
 */
@ToAnalyze
@Deprecated

public class JkEqualsBuilder {

    private List<ObjRel> objList;

    public JkEqualsBuilder() {
        this.objList = new ArrayList<>();
    }

    public JkEqualsBuilder add(Object o1, Object o2) {
        objList.add(new ObjRel(o1, o2));
        return this;
    }

//    public boolean areEquals() {
//        return areEquals(false);
//    }
//    public boolean areEquals(boolean ignoreStringCase) {
//        for(ObjRel rel : objList) {
//
//        }
//    }

//    private boolean objsEquals(Object o1, Object o2, boolean ignoreStringCase) {
//        if(o1 == null && o2 == null)    return true;
//        if(o1 == null || o2 == null)    return false;
//
//        if(o1.getClass() != o2.getClass())  return false;
//
//        Class<?> clazz = o1.getClass();
//
//        if(clazz == boolean.class) {
//            return ((boolean)o1) == ((boolean)o2);
//        } else if(clazz == boolean[].class) {
//            boolean[] arr1 = (boolean[]) o1;
//            boolean[] arr2 = (boolean[]) o2;
//            if(arr1.length != arr2.length)  return false;
//            boolean res = true;
//            for(int i = 0; res && i < arr1.length; i++) {
//                res &= objsEquals(arr1[i], arr2[i], ignoreStringCase);
//            }
//            return res;
//        } else if(clazz == Boolean.class) {
//            return ((Boolean)o1).booleanValue() == ((Boolean)o2).booleanValue();
//        } else if(clazz == Boolean[].class) {
//            return arraysEquals((Boolean[]) o1, (Boolean[]) o2, ignoreStringCase);
//
//        } else if(clazz == int.class) {
//            return ((int)o1) == ((int)o2);
//        } else if(clazz == long.class) {
//            return ((long)o1) == ((long)o2);
//        } else if(clazz == float.class) {
//            return ((float)o1) == ((float)o2);
//        } else if(clazz == double.class) {
//            return ((double)o1) == ((double)o2);
//
//        } else if(clazz == int[].class) {
//            int[] arr1 = (int[]) o1;
//            int[] arr2 = (int[]) o2;
//            if(arr1.length != arr2.length)  return false;
//            boolean res = true;
//            for(int i = 0; res && i < arr1.length; i++) {
//                res &= objsEquals(arr1[i], arr2[i], ignoreStringCase);
//            }
//            return res;
//        } else if(clazz == long[].class) {
//            long[] arr1 = (long[]) o1;
//            long[] arr2 = (long[]) o2;
//            if(arr1.length != arr2.length)  return false;
//            boolean res = true;
//            for(int i = 0; res && i < arr1.length; i++) {
//                res &= objsEquals(arr1[i], arr2[i], ignoreStringCase);
//            }
//            return res;
//        } else if(clazz == float[].class) {
//            float[] arr1 = (float[]) o1;
//            float[] arr2 = (float[]) o2;
//            if(arr1.length != arr2.length)  return false;
//            boolean res = true;
//            for(int i = 0; res && i < arr1.length; i++) {
//                res &= objsEquals(arr1[i], arr2[i], ignoreStringCase);
//            }
//            return res;
//        } else if(clazz == double[].class) {
//            double[] arr1 = (double[]) o1;
//            double[] arr2 = (double[]) o2;
//            if(arr1.length != arr2.length)  return false;
//            boolean res = true;
//            for(int i = 0; res && i < arr1.length; i++) {
//                res &= objsEquals(arr1[i], arr2[i], ignoreStringCase);
//            }
//            return res;
//
//        } else if(clazz == Integer.class) {
//            return ((Integer)o1).intValue() == ((Integer)o2).intValue();
//
//        } else if(clazz == Boolean[].class) {
//            return arraysEquals((Boolean[]) o1, (Boolean[]) o2, ignoreStringCase);
//
//        }
//    }

//    private boolean arraysEquals(Object[] arr1, Object[] arr2, boolean ignoreStringCase) {
//        if(arr1.length != arr2.length)  return false;
//        boolean res = true;
//        for(int i = 0; res && i < arr1.length; i++) {
//            res &= objsEquals(arr1[i], arr2[i], ignoreStringCase);
//        }
//        return res;
//    }


    private class ObjRel {
        private Object o1;
        private Object o2;

        ObjRel(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }
    }
}
