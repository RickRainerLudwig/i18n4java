package javax.i18n4j.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.i18n4j.FileSearch;
import javax.i18n4j.I18NFile;
import javax.i18n4j.LanguageSet;
import javax.i18n4j.MultiLanguageTranslations;
import javax.i18n4j.SingleLanguageTranslations;
import javax.i18n4j.Translator;

import org.apache.log4j.Logger;

public class I18NRelease {

	private static final Logger logger = Logger.getLogger(I18NRelease.class);
	private static final Translator translator = Translator
			.getTranslator(I18NRelease.class);

	private String i18nDirectory = "";
	private Vector<File> inputFiles = new Vector<File>();

	public I18NRelease(String i18nDirectory) {
		this.i18nDirectory = i18nDirectory;
	}

	public I18NRelease(String args[]) {
		if ((args.length == 0) || (args.length > 1)) {
			System.out.println(translator
					.i18n("usage:  I18NRelease <directory>"));
			return;
		}
		this.i18nDirectory = args[0];
	}

	public void release() {
		findAllInputFiles();
		processFiles();
	}

	private void findAllInputFiles() {
		inputFiles.addAll(FileSearch.find(i18nDirectory + "/**/*.i18n"));
	}

	private void processFiles() {
		Iterator<File> i = inputFiles.iterator();
		while (i.hasNext()) {
			File file = i.next();
			processFile(file);
		}
	}

	private void processFile(File file) {
		try {
			logger.info("Process file " + file.getPath() + "...");
			MultiLanguageTranslations mlTranslations = I18NFile
					.readMultiLanguageFile(file);
			Set<String> languages = mlTranslations.getAvailableLanguages();
			for (String language : languages) {
				release(mlTranslations, language, new File(file.getPath()
						.replaceAll("\\.i18n", "." + language + ".tr")));
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void release(MultiLanguageTranslations mlTranslations,
			String language, File file) {
		logger.info("Release language " + language + " to file "
				+ file.getPath());
		SingleLanguageTranslations translations = new SingleLanguageTranslations();
		Set<String> sources = mlTranslations.getSources();
		for (String source : sources) {
			LanguageSet set = mlTranslations.get(source);
			if (set.containsLanguage(language)) {
				translations.set(source, set.get(language));
			}
		}
		I18NFile.writeSingleLanguageFile(file, translations);
	}

	public static void main(String[] args) {
		System.out.println("Set translator...");
		Translator.setDefault(new Locale("de", "DE"));
		System.out.println("set.");
		new I18NRelease(args).release();
	}
}
