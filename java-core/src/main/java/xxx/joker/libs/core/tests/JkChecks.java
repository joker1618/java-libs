package xxx.joker.libs.core.tests;

import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class JkChecks {

    /* NUMBERS */
    public static boolean isBoolean(String source) {
        return source.toLowerCase().equals("true") || source.toLowerCase().equals("false");
    }
    public static boolean isBooleanArray(String[] source) {
        return isBooleanList(Arrays.asList(source));
    }
    public static boolean isBooleanList(List<String> source) {
        for(String elem : source) {
            if(!isBoolean(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInt(String str) {
        try {
            new Integer(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean areInts(String[] source) {
        return areInts(Arrays.asList(source));
    }
    public static boolean areInts(List<String> source) {
        for(String elem : source) {
            if(!isInt(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLong(String str) {
        try {
            new Long(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean areLongs(String[] source) {
        return areLongs(Arrays.asList(source));
    }
    public static boolean areLongs(List<String> source) {
        for(String elem : source) {
            if(!isLong(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFloat(String str) {
        try {
            new Float(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean areFloats(String[] source) {
        return areFloats(Arrays.asList(source));
    }
    public static boolean areFloats(List<String> source) {
        for(String elem : source) {
            if(!isFloat(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDouble(String str) {
        try {
            new Double(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean areDoubles(String[] source) {
        return areDoubles(Arrays.asList(source));
    }
    public static boolean areDoubles(List<String> source) {
        for(String elem : source) {
            if(!isDouble(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumber(String str) {
        return isInt(str) || isLong(str) || isFloat(str) || isDouble(str);
    }
    public static boolean areNumbers(String[] source) {
        return areNumbers(Arrays.asList(source));
    }
    public static boolean areNumbers(List<String> source) {
        for(String str : source) {
            if(!isNumber(str)) {
                return false;
            }
        }
        return true;
    }


    /* MISCELLANEA */
    public static <T> boolean duplicatesPresents(List<T> sourceList) {
        for(int i = 0; i < sourceList.size(); i++) {
            for(int j = i+1; j < sourceList.size(); j++) {
                T elemI = sourceList.get(i);
                T elemJ = sourceList.get(j);

                if(elemI instanceof Path) {
                    if(elemJ instanceof Path) {
                        if(JkFiles.areEquals((Path)elemI, (Path)elemJ)) {
                            return true;
                        }
                    }

                } else if(elemI instanceof LocalDate) {
                    if(elemJ instanceof LocalDate) {
                        if(((LocalDate)elemI).isEqual((LocalDate)elemJ)) {
                            return true;
                        }
                    }

                } else if(elemI instanceof LocalDateTime) {
                    if(elemJ instanceof LocalDateTime) {
                        if(((LocalDateTime)elemI).isEqual((LocalDateTime)elemJ)) {
                            return true;
                        }
                    }

                } else if(elemI instanceof LocalTime) {
                    if(elemJ instanceof LocalTime) {
                        if(((LocalTime)elemI).compareTo((LocalTime)elemJ) == 0) {
                            return true;
                        }
                    }

                } else {
                    if(elemI.equals(elemJ)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public static <T> boolean duplicatesPresents(T[] sourceArray) {
        return duplicatesPresents(Arrays.asList(sourceArray));
    }


}