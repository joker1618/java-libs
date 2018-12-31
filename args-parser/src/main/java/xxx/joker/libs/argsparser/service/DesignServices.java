package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classType.InputCommand;
import xxx.joker.libs.argsparser.design.classType.InputOption;
import xxx.joker.libs.argsparser.design.classType.OptionName;
import xxx.joker.libs.core.utils.JkFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static xxx.joker.libs.core.utils.JkConsole.display;

/**
 * Created by f.barbano on 03/09/2017.
 */
public class DesignServices {

	private static final String LAST_KNOWN_SIZE_FILENAME_PREFIX = ".last_known_size";
	private static final DesignServices INSTANCE = new DesignServices();

	private IOptNameService optNameService;
	private IOptService optService;
	private ICmdService cmdService;
	private Path launcherJarPath;


	private DesignServices() {

	}

	public static synchronized void init(Class<? extends OptionName> optNameClass,
										 Class<? extends InputOption> optClass,
										 Class<? extends InputCommand> cmdClass) {
		INSTANCE.launcherJarPath = JkFiles.getLauncherPath(cmdClass);
		INSTANCE.initProviders(optNameClass, optClass, cmdClass);
	}

	private void initProviders(Class<? extends OptionName> optNameClass,
							   Class<? extends InputOption> optClass,
							   Class<? extends InputCommand> cmdClass) {

		boolean checkDesign = isPerformDesignCheck(cmdClass);

		optNameService = new OptNameServiceImpl(optNameClass, checkDesign);
		optService = new OptServiceImpl(optClass, checkDesign, optNameService);
		cmdService = new CmdServiceImpl(cmdClass, checkDesign, optService, optNameService);

		if(checkDesign) {
			registerDesignChecked(cmdClass);
		}
	}

	private boolean isPerformDesignCheck(Class<?> cmdClass) {
		if(!Files.isRegularFile(launcherJarPath)) {
			return true;
		}

		try {
			long actualSize = Files.size(launcherJarPath);
			long lastKnownSize = getLastKnownSize(cmdClass);
			return actualSize != lastKnownSize;

		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private long getLastKnownSize(Class<?> cmdClass) {
		Path sizePath = getLastKnownSizeFilePath(cmdClass);
		long size = 0L;

		if(Files.exists(sizePath)) {
			try {
				String line = Files.readAllLines(sizePath).get(0);
				size = Long.valueOf(line.trim());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return size;
	}

	private void registerDesignChecked(Class<?> cmdClass) {
		if(Files.isRegularFile(launcherJarPath)) {
			try {
				long size = Files.size(launcherJarPath);
				JkFiles.writeFile(getLastKnownSizeFilePath(cmdClass), String.valueOf(size), true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Path getLastKnownSizeFilePath(Class<?> cmdClass) {
		String sizeFileName = String.format("%s_%s_%s", LAST_KNOWN_SIZE_FILENAME_PREFIX, launcherJarPath.getFileName().toString(), cmdClass.getName());
		return launcherJarPath.toAbsolutePath().getParent().resolve(sizeFileName);
	}

	public static IOptNameService getOptNameService() {
		return INSTANCE.optNameService;
	}

	public static IOptService getOptService() {
		return INSTANCE.optService;
	}

	public static ICmdService getCmdService() {
		return INSTANCE.cmdService;
	}
}
