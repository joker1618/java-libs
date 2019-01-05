package xxx.joker.libs.core.utils;

import xxx.joker.libs.core.exception.JkRuntimeException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 23/01/2018.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class JkBytes {

	public static boolean isBitSet(byte b, int bit) {
		int expected = 0x01 << bit;
		return (b & (0x01 << bit)) == expected;
	}

	public static byte setBit(int num) {
		return (byte)(0x01 << num);
	}
	public static byte setBits(int... nums) {
		byte b = 0x00;
		for(int num : nums) {
			b |= setBit(num);
		}
		return b;
	}
	public static byte setBits(List<Integer> bitsNum) {
		byte b = 0x00;
		for(int num : bitsNum) {
			b |= setBit(num);
		}
		return b;
	}

	public static boolean isEquals(byte b, int num) {
		return b == (byte)num;
	}

	public static byte[] mergeArrays(byte[]... arrays) {
		return mergeArrays(Arrays.asList(arrays));
	}
	public static byte[] mergeArrays(List<byte[]> arrayList) {
		int len = 0;
		for(byte[] arr : arrayList)	len += arr.length;

		int idx = 0;
		byte[] toRet = new byte[len];
		for(int i = 0; i < arrayList.size(); i++) {
			byte[] bi = arrayList.get(i);
			for(int j = 0; j < bi.length; j++, idx++) {
				toRet[idx] = bi[j];
			}
		}

		return toRet;
	}

	public static byte[] getBytes(Path path) throws JkRuntimeException {
	    try {
            int size = (int) Files.size(path);
            return getBytes(path, 0, size);
        } catch (IOException ex) {
	        throw new JkRuntimeException(ex);
        }
	}
	public static byte[] getBytes(Path path, int start, int length) throws JkRuntimeException {
		try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
			return getBytes(raf, start, length);
        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}
	public static byte[] getBytes(RandomAccessFile raf, int start, int length) throws JkRuntimeException {
	    try {
            byte[] toRet = new byte[length];
            raf.seek(start);
            int counter = raf.read(toRet);
            if (counter == length) {
                return toRet;
            }

            toRet = getBytes(toRet, 0, counter);
            while (counter < length) {
                int rem = length - counter;
                byte[] arr = new byte[rem];
                int read = raf.read(arr);
                toRet = mergeArrays(toRet, getBytes(arr, 0, read));
                counter += read;
            }

            return toRet;

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	public static byte[] getBytes(byte[] byteArr, int start, int length) {
		return Arrays.copyOfRange(byteArr, start, start + length);
	}

	public static byte[] toByteArray(List<Byte> list) {
		byte[] arr = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}
	public static List<Byte> toByteList(byte[] arr) {
		List<Byte> toRet = new ArrayList<>();
		for (byte b : arr) {
			toRet.add(b);
		}
		return toRet;
	}

	public static boolean areEquals(byte[] arr1, byte[] arr2) {
		if(arr1 == null && arr2 == null)	return true;
		if(arr1 == null || arr2 == null)	return false;
		if(arr1.length != arr2.length) 		return false;

		for (int i = 0; i < arr1.length; i++) {
			if (arr1[i] != arr2[i]) {
				return false;
			}
		}

		return true;
	}
}
