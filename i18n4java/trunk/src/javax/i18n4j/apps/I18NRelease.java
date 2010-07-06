/***************************************************************************
 *
 * Copyright 2009-2010 PureSol Technologies 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 ***************************************************************************/

package javax.i18n4j.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private final File i18nDirectory;
    private final List<File> inputFiles = new ArrayList<File>();

    public I18NRelease(File i18nDirectory) {
	this.i18nDirectory = i18nDirectory;
    }

    public I18NRelease(String args[]) {
	if ((args.length == 0) || (args.length > 1)) {
	    i18nDirectory = null;
	    showUsage();
	    return;
	}
	this.i18nDirectory = new File(args[0]);
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
	inputFiles.addAll(FileSearch.find(i18nDirectory, "**/*.i18n"));
    }

    private void processFiles() {
	for (File file : inputFiles) {
	    processFile(new File(i18nDirectory, file.toString()));
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
