package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.common.Configs;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsNames;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.oldargsparser.design.classType.InputCommand;
import xxx.joker.libs.oldargsparser.design.classType.InputOption;
import xxx.joker.libs.oldargsparser.design.classType.OptionName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DesignServiceImpl implements DesignService {

    private ParserArgs parserArgs;
    private ParserNames parserNames;
    private ParserCmds parserCmds;
    private Path launcherJarPath;

    public DesignServiceImpl(Class<? extends JkAbstractArgs> argsClass,
                             Class<? extends JkArgsNames> argsNamesClass,
                             Class<? extends JkCommands> cmdsClass)
                             throws DesignError {

        launcherJarPath = JkFiles.getLauncherPath(cmdsClass);
        boolean checkDesign = isPerformDesignCheck(cmdsClass);

        parserNames = new ParserNames(argsNamesClass, checkDesign);
        parserArgs = new ParserArgs(argsClass, parserNames, checkDesign);
        parserCmds = new ParserCmds(cmdsClass, parserArgs, checkDesign);

        if(checkDesign) {
            registerDesignChecked(cmdsClass);
        }
    }


    private boolean isPerformDesignCheck(Class<?> cmdClass) {
        if(!Files.isRegularFile(launcherJarPath)) {
            return true;
        }

        String actualMD5 = JkEncryption.getMD5(launcherJarPath);
        String lastMD5 = getLastKnownMD5(cmdClass);
        return StringUtils.isBlank(lastMD5) || !lastMD5.equals(actualMD5);
    }

    private String getLastKnownMD5(Class<?> cmdClass) {
        Path md5Path = getHashFilePath(cmdClass);
        String md5 = "";
        if(Files.exists(md5Path)) {
            md5 = JkFiles.readLines(md5Path).get(0);
        }
        return md5;
    }

    private void registerDesignChecked(Class<?> cmdClass) {
        if(Files.isRegularFile(launcherJarPath)) {
            String md5 = JkEncryption.getMD5(launcherJarPath);
            JkFiles.writeFile(getHashFilePath(cmdClass), md5, true);
        }
    }

    private Path getHashFilePath(Class<?> cmdClass) {
        String sizeFileName = String.format("%s_%s.md5", launcherJarPath.getFileName().toString(), cmdClass.getName());
        return Configs.TEMP_FOLDER.resolve(sizeFileName);
    }
}
