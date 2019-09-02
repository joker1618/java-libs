package unitTests;

import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;

import java.nio.file.Path;

public class TmcArgs extends JkAbstractArgs<TmcCmd> {

	@JkArg
	private boolean b1;
	@JkArg(aliases = {"-b2"})
	private boolean b2;
	@JkArg(name = "bool3")
	private boolean b3;
	@JkArg(name = "bool4", aliases = {"bb4"})
	private boolean b4;

	@JkArg
	private String str1;
	@JkArg
	private String altro;





}