/***************************************************************************
 *
 *   I18NRelease.java
 *   -------------------
 *   copyright            : (c) 2009 by Rick-Rainer Ludwig
 *   author               : Rick-Rainer Ludwig
 *   email                : rl719236@sourceforge.net
 *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package javax.i18n4j.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.i18n4j.FileSearch;
import javax.i18n4j.I18NFile;
import javax.i18n4j.LanguageSet;
import javax.i18n4j.MultiLanguageTranslations;
import javax.i18n4j.SingleLanguageTranslations;
import javax.i18n4j.TRFile;
import javax.i18n4j.Translator;

import org.apache.log4j.Logger;

/**
 * This application converts all i18n files in the the specified directory and
 * converts them into tr files for usage in internationalized applications.
 * 
 * @author Rick-Rainer Ludwig
 */
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
			showUsage();
			return;
		}
		this.i18nDirectory = args[0];
	}

	private void showUsage() {
		System.out.println("===========");
		System.out.println("I18NRelease");
		System.out.println("===========");
		System.out.println();
		System.out.println(translator.i18n("usage:  I18NRelease <directory>"));
		System.out.println();
		System.out
				.println(translator
						.i18n("This application converts all i18n files in the\n"
								+ "the specified directory and converts them into\n"
								+ "tr files for usage in internationalized applications."));
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
			logger.info(translator.i18n("Process file {0}...", file.getPath()));
			MultiLanguageTranslations mlTranslations = I18NFile.read(file);
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
		logger.info(translator.i18n("Release language {0}  to file {1}",
				language, file.getPath()));
		SingleLanguageTranslations translations = new SingleLanguageTranslations();
		Set<String> sources = mlTranslations.getSources();
		for (String source : sources) {
			LanguageSet set = mlTranslations.get(source);
			if (set.containsLanguage(language)) {
				translations.set(source, set.get(language));
			}
		}
		TRFile.write(file, translations);
	}

	public static void main(String[] args) {
		new I18NRelease(args).release();
	}
}
