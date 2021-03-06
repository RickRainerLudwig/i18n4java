/****************************************************************************
 *
 *   I18NProjectConfiguration.java
 *   -------------------
 *   copyright            : (c) 2009-2011 by PureSol-Technologies
 *   author               : Rick-Rainer Ludwig
 *   email                : ludwig@puresol-technologies.com
 *
 ****************************************************************************/

/****************************************************************************
 *
 * Copyright 2009-2011 PureSol-Technologies
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
 ****************************************************************************/

package com.puresoltechnologies.i18n4java.proc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is used to handle the configuration of project's i18n framework.
 * This class takes the project directory as constructor parameter and reads the
 * file i18n4java.properties in the top directory which holds all information
 * about the directories where all files can be found.
 * 
 * Getter methods provide an easy access to information about the configuration.
 * 
 * @author Rick-Rainer Ludwig
 * 
 */
public class I18NProjectConfiguration {

	public static final String CONFIGURATION_FILENAME = "i18n4java.properties";

	private static final String SOURCE_DIRECTORY_KEY = "i18n4java.project.directories.source";
	private static final String I18N_DIRECTORY_KEY = "i18n4java.project.directories.i18n";
	private static final String DESTINATION_DIRECTORY_KEY = "i18n4java.project.directories.destination";

	private final File projectDirectory;
	private final String relativeSourceDirectory;
	private final String relativeI18nDirectory;
	private final String relativeDestinationDirectory;

	/**
	 * Is the constructor to construct a new instance.
	 * 
	 * @param fileOrDirectory
	 *            is the file or directory to look up the configuration.
	 * @throws IOException
	 *             is thrown in case of IO issues like reading issues or the
	 *             configuration files was not found.
	 */
	public I18NProjectConfiguration(File fileOrDirectory) throws IOException {
		super();
		if (!fileOrDirectory.exists()) {
			throw new FileNotFoundException("File or directory '"
					+ fileOrDirectory + "' was not found!");
		}
		File fileLocation;
		Properties props = new Properties();
		if (fileOrDirectory.isDirectory()) {
			File file = new File(fileOrDirectory, CONFIGURATION_FILENAME);
			if (!file.exists()) {
				throw new FileNotFoundException("File '" + file
						+ "' was not found!");
			}
			FileInputStream inStream = new FileInputStream(file);
			try {
				props.load(inStream);
				fileLocation = fileOrDirectory;
			} finally {
				inStream.close();
			}
		} else {
			FileInputStream inStream = new FileInputStream(fileOrDirectory);
			try {
				props.load(inStream);
				fileLocation = fileOrDirectory.getParentFile();
			} finally {
				inStream.close();
			}
		}
		relativeSourceDirectory = props.containsKey(SOURCE_DIRECTORY_KEY) ? props
				.getProperty(SOURCE_DIRECTORY_KEY) : "src/main/java";
		relativeI18nDirectory = props.containsKey(I18N_DIRECTORY_KEY) ? props
				.getProperty(I18N_DIRECTORY_KEY) : "i18n";
		relativeDestinationDirectory = props
				.containsKey(DESTINATION_DIRECTORY_KEY) ? props
				.getProperty(DESTINATION_DIRECTORY_KEY) : "res";
		projectDirectory = fileLocation;
	}

	/**
	 * Returns the relative source directory.
	 * 
	 * @return The relative source directory is returned.
	 */
	public String getRelativeSourceDirectory() {
		return relativeSourceDirectory;
	}

	/**
	 * Provides the relative I18n directory containing the translations.
	 * 
	 * @return The relative I18n directory is returned.
	 */
	public String getRelativeI18nDirectory() {
		return relativeI18nDirectory;
	}

	/**
	 * Provides the destination directory.
	 * 
	 * @return The relative destination directory is returned.
	 */
	public String getRelativeDestinationDirectory() {
		return relativeDestinationDirectory;
	}

	public File getProjectDirectory() {
		return projectDirectory;
	}

	public File getSourceDirectory() {
		return new File(projectDirectory, relativeSourceDirectory);
	}

	public File getI18nDirectory() {
		return new File(projectDirectory, relativeI18nDirectory);
	}

	public File getDestinationDirectory() {
		return new File(projectDirectory, relativeDestinationDirectory);
	}
}
