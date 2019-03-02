package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.argsparser.common.Configs;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class DesignServiceImpl implements DesignService {

    private static final Logger logger = LoggerFactory.getLogger(DesignServiceImpl.class);

    private ParserArgs parserArgs;
    private ParserTypes parserTypes;
    private ParserCmds parserCmds;
    private Path launcherJarPath;


    public DesignServiceImpl(Class<? extends JkAbstractArgs> argsClass,
                             Class<? extends JkArgsTypes> argsNamesClass,
                             Class<? extends JkCommands> cmdsClass,
                             boolean ignoreCaseArgs)
                             throws DesignError {

        launcherJarPath = JkFiles.getLauncherPath(cmdsClass);
        logger.debug("Launcher path: {}", launcherJarPath);

        boolean checkDesign = doDesignCheck(cmdsClass);
        logger.debug("Check design: {} (ignore case: {})", checkDesign, ignoreCaseArgs);

        parserTypes = new ParserTypes(argsNamesClass, checkDesign);
        parserArgs = new ParserArgs(argsClass, parserTypes, checkDesign, ignoreCaseArgs);
        parserCmds = new ParserCmds(cmdsClass, parserTypes, parserArgs, checkDesign);

        if(checkDesign) {
            boolean res = registerDesignChecked(cmdsClass);
            if(res)     logger.debug("Saved MD5 of JAR to '{}'", launcherJarPath);
        }
    }

    @Override
    public ArgWrapper getArgByNameAlias(String nameOrAlias) {
        return parserArgs.getArgWrapper(nameOrAlias);
    }

    @Override
    public CmdWrapper retrieveCommand(Collection<ArgWrapper> inputArgs) {
        return parserCmds.retrieveCommand(inputArgs);
    }

    private boolean doDesignCheck(Class<?> cmdClass) {
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
    private boolean registerDesignChecked(Class<?> cmdClass) {
        if(Files.isRegularFile(launcherJarPath)) {
            String md5 = JkEncryption.getMD5(launcherJarPath);
            JkFiles.writeFile(getHashFilePath(cmdClass), md5, true);
            return true;
        }
        return false;
    }
    private Path getHashFilePath(Class<?> cmdClass) {
        String sizeFileName = String.format("%s_%s.md5", launcherJarPath.getFileName().toString(), cmdClass.getName());
        return Configs.APPS_FOLDER.resolve(sizeFileName);
    }


}
