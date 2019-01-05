package xxx.joker.libs.language;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 19/01/2018.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class JkLanguageDetector {

	private static final Map<JkLanguage, List<String>> languages;

	static {
		languages = new HashMap<>();
		// Load dictionaries statically
		for (JkLanguage lan : JkLanguage.values()) {
			List<String> words = getWords(lan);
			if (words != null) {
				languages.put(lan, words);
			}
		}
	}

	private static List<String> getWords(JkLanguage language) {
		String dictPath = String.format("/dictionaries/lan_%s.txt", language.getLabel());
		InputStream is = JkLanguageDetector.class.getResourceAsStream(dictPath);
		if (is == null) {
			return null;
		}

		List<String> words = new ArrayList<>();
		String line;
		try (InputStreamReader bis = new InputStreamReader(is);
			 BufferedReader br = new BufferedReader(bis)) {
			while ((line = br.readLine()) != null) {
				words.add(line.toLowerCase());
			}
			is.close();
			return words;

		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	public static JkLanguage detectLanguage(String text) {
		if(StringUtils.isBlank(text))	return null;

		Map<JkLanguage, Integer> countMap = new HashMap<>();

		List<String> allWords = Arrays.stream(text.split("\\s")).filter(StringUtils::isNotBlank).sorted().distinct().collect(Collectors.toList());

		for (String word : allWords) {
			List<JkLanguage> wordLangs = getWordLanguages(word);
			if (wordLangs.size() == 1) {
				// if 'wordLangs' empty or multiple, ignore word
				JkLanguage wordLang = wordLangs.get(0);
				Integer val = countMap.get(wordLang);
				Integer newVal = val == null ? 1 : val + 1;
				countMap.put(wordLang, newVal);
			}
		}

		JkLanguage detectedLang = null;

		OptionalInt max = countMap.values().stream().mapToInt(i -> i).max();
		if (max.isPresent()) {
			int maxOccur = max.getAsInt();
			List<JkLanguage> mapKeys = JkStreams.getMapKeys(countMap, i -> i == maxOccur);
			if (mapKeys.size() == 1) {
				detectedLang = mapKeys.get(0);
			}
		}

		return detectedLang;
	}

	private static List<JkLanguage> getWordLanguages(String word) {
		List<JkLanguage> langs = new ArrayList<>();
		for (JkLanguage lan : languages.keySet()) {
			if (languages.get(lan).contains(word.toLowerCase())) {
				langs.add(lan);
			}
		}
		return langs;
	}

}