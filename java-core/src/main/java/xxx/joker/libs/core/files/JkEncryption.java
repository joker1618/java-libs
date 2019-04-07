package xxx.joker.libs.core.files;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.utils.JkBytes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

public class JkEncryption {

    private static final byte[] salt = new byte[]{(byte) 67, (byte) 118, (byte) -107, (byte) -57, (byte) 91, (byte) -41, (byte) 69, (byte) 23};

    private JkEncryption() {
    }

    /* FILE ENCRYPTION */
    public static byte[] encryptBytes(byte[] source, String password) {
        try {
            Cipher cipher = makeCipher(password, true);

            byte blockSize = 8;
            int paddedCount = blockSize - source.length % blockSize;
            byte[] bytes = JkBytes.mergeArrays(source, new byte[paddedCount]);

            return cipher.doFinal(bytes);

        } catch (GeneralSecurityException ex) {
            throw new JkRuntimeException(ex);
        }
    }
    public static byte[] decryptBytes(byte[] source, String password) {
        try {
            Cipher cipher = makeCipher(password, false);

            byte[] decData = cipher.doFinal(source);
            byte padCount = decData[decData.length - 1];
            if (padCount >= 1 && padCount <= 8) {
                decData = Arrays.copyOfRange(decData, 0, decData.length - padCount);
            }

            return decData;

        } catch (GeneralSecurityException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    public static void encryptFile(Path inputPath, Path outputPath, String password) {
        encryptFile(inputPath, outputPath, password, true);
    }
    public static void encryptFile(Path inputPath, Path outputPath, String password, boolean overwrite) {
        try {
            if (!overwrite && Files.exists(outputPath)) {
                throw new IOException("File [" + outputPath + "] already exists!");
            }

            File inputFile = inputPath.toFile();
            byte[] inputBytes;
            try (FileInputStream inStream = new FileInputStream(inputFile)) {
                inputBytes = new byte[(int) inputFile.length()];
                inStream.read(inputBytes);
            }

            byte[] encData = encryptBytes(inputBytes, password);

            JkFiles.writeFile(outputPath, encData, overwrite);

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }
    public static void decryptFile(Path inputPath, Path outputPath, String password) {
        decryptFile(inputPath, outputPath, password, true);
    }
    public static void decryptFile(Path inputPath, Path outputPath, String password, boolean overwrite) {
        try {
            if (!overwrite && Files.exists(outputPath)) {
                throw new IOException("File [" + outputPath + "] already exists!");
            }

            File inputFile = inputPath.toFile();
            byte[] encData;
            try (FileInputStream inStream = new FileInputStream(inputFile)) {
                encData = new byte[(int) inputFile.length()];
                inStream.read(encData);
            }

            byte[] decrBytes = decryptBytes(encData, password);

            JkFiles.writeFile(outputPath, decrBytes, overwrite);

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }


    /* HASHING */
    public static String getMD5(Path inputPath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            try (FileInputStream fis = new FileInputStream(inputPath.toFile())) {
                byte[] dataBytes = new byte[1024];
                int nread;
                while ((nread = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }
            }

            return computeMD5(md);

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }
    public static String getMD5(String s) {
        return getMD5(s.getBytes());
    }
    public static String getMD5(String s, Charset encoding) {
        return getMD5(s.getBytes(encoding));
    }
    public static String getMD5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            return computeMD5(md);

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }


    private static Cipher makeCipher(String password, boolean decryptMode) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            if (decryptMode) {
                cipher.init(1, key, pbeParamSpec);
            } else {
                cipher.init(2, key, pbeParamSpec);
            }

            return cipher;

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private static String computeMD5(MessageDigest md) {
        try {
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }
}
