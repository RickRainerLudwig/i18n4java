package javax.swingx.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * ConfigFile is an object to handle ASCII configuration files with sections and
 * keys. <br/>
 * <br/>
 * 
 * A configuration file is an ASCII file with either Windows or Unix ending
 * characters. The file is organized in sections which start with a section
 * title. The section title is the name of the section in rectangular brackets,
 * e.g.: [SECTIONTITLE] <br/>
 * <br/>
 * 
 * In the section the values are organized as keys. Each key name is followed by
 * an equal sign '=' and the value for the key, e.g.: key=value <br/>
 * <br/>
 * A special section is a section with a vertical data table in it. For details
 * on that have a look to VerticalDataFile. In a section it is quite the same.
 * The table starts with a line of column titles and the rest are lines with
 * data. <br/>
 * <br/>
 * 
 * Comments are started with a sharp sign '#'.
 * 
 * @author Rick-Rainer Ludwig
 */
public class ConfigFile extends RandomAccessFile {

	/**
	 * This is a File object keeping the path to the opened configuration file.
	 */
	protected File file;

	/**
	 * This string keeps the open mode for later usage.
	 */
	protected String mode;

	/**
	 * This is the constructor with a file object only. The file will be opened
	 * read-only.
	 * 
	 * @param file
	 *            is the file which is to open by the constructor.
	 */
	public ConfigFile(File file) throws IOException {
		super(file, "r");
		this.file = file;
		this.mode = "r";
	}

	/**
	 * This is the constructor with file object for the file to be opened and a
	 * mode string defining the open mode.
	 * 
	 * @param file
	 *            is the file which is to open by the constructor.
	 * @param mode
	 *            is the open mode string. It contains 'r' and/or 'w'.
	 */
	public ConfigFile(File file, String mode) throws IOException {
		super(file, mode);
		this.file = file;
		this.mode = mode;
	}

	/**
	 * This method reads a single line readLine(), but trims the the leading and
	 * trailing white spaces.
	 * 
	 * @return A String is returned representing the line read from the file.
	 * @throws IOException
	 *             is thrown in case of an IO error.
	 */
	public String readLineTrimmed() throws IOException {
		String line = readLine();
		if (line == null) {
			return null;
		}
		return line.trim();
	}

	/**
	 * This methods starts at the beginning of the file and searches for a
	 * section given as argument. If the section is found the file pointer
	 * remains there.
	 * 
	 * @param name
	 *            is the name of the section to look for.
	 * @return True is returned in case the section was successfully found.
	 *         Otherwise false is returned.
	 */
	public boolean gotoSection(String name) {
		try {
			seek(0);
			String line = "";
			while (!line.equals("[" + name + "]")) {
				line = readLineTrimmed();
				if (line == null) {
					return false;
				}
			}
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}

	/**
	 * This method searches for a section and counts the included lines of a
	 * table without the column header line.
	 * 
	 * @param section
	 *            is the name of the section read.
	 * @return The number of data lines in the table is returned without
	 *         counting the line with the column headers.
	 */
	public int getTableLength(String section) {
		if (!gotoSection(section)) {
			return 0;
		}
		int length = 0;
		try {
			String line;
			while ((line = readLineTrimmed()) != null) {
				if (line.startsWith("[")) {
					break;
				}
				if (line.equals("")) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				length++;
			}
		} catch (IOException ioe) {
			return 0;
		}
		return length - 1; // -1 because of headerline
	}

	/**
	 * This method reads the complete section into a string.
	 * 
	 * @param section
	 * @return
	 * @throws IOException
	 */
	public String read(String section) throws IOException {
		if (!gotoSection(section)) {
			return "";
		}
		String result = "";
		String line = readLine();
		while ((line != null) && (!line.startsWith("["))) {
			result += line;
			line = readLine();
		}
		return result;
	}

	/**
	 * read() is the method to extract an entry from a configuration file.
	 * 
	 * @throws IOException
	 *             is thrown in case of an IO error.
	 * @param section
	 *            is the name of the section where the entry must be found.
	 * @param entry
	 *            is the name of the entry which is to be found.
	 * @return A string with the keys content is returned. If the key was not
	 *         found, null is returned.
	 */
	public String read(String section, String entry) throws IOException {
		if (!gotoSection(section)) {
			return null;
		}
		String line;
		String result = null;
		while ((line = readLineTrimmed()) != null) {
			if (line.startsWith("[")) {
				break;
			} else if (line.startsWith(entry + "=")) {
				result = line;
				break;
			}
		}
		if (line == null) {
			return null;
		}
		if (!line.startsWith(entry + "=")) {
			return null;
		}
		return result.substring(entry.length() + 1);
	}

	/**
	 * This method reads in a single(!) file the entry for a configuration key.
	 * 
	 * @param file
	 *            is the file to be read. This is the complete file path to be
	 *            read.
	 * @param section
	 *            is the name of the section where the entry must be found.
	 * @param entry
	 *            is the name of the entry which is to be found.
	 * @return A string with the keys content is returned. If the key was not
	 *         found, null is returned.
	 */
	static private String readEntry(File file, String section, String entry) {
		if (!file.exists()) {
			return "";
		}
		try {
			ConfigFile configFile = new ConfigFile(file);
			String line = configFile.read(section, entry);
			configFile.close();
			if (line == null) {
				return "";
			}
			return line;
		} catch (FileNotFoundException fnfe) {
			return "";
		} catch (IOException ioe) {
			return "";
		}
	}

	static ArrayList<String> getPotentialConfigFiles(String filename) {
		ArrayList<String> files = new ArrayList<String>();
		files.add("/etc/" + filename);
		files.add(System.getProperty("user.home")
				+ System.getProperty("file.separator") + filename);
		files.add(System.getProperty("user.home")
				+ System.getProperty("file.separator") + "." + filename);
		files.add(System.getProperty("user.dir")
				+ System.getProperty("file.separator") + filename);
		files.add(System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "." + filename);
		return files;
	}

	static ArrayList<String> getAvailableConfigFiles(String filename) {
		ArrayList<String> files = getPotentialConfigFiles(filename);
		ArrayList<String> availableFiles = new ArrayList<String>();
		for (String file : files) {
			if (new File(file).exists()) {
				availableFiles.add(file);
			}
		}
		return availableFiles;
	}

	/**
	 * readEntry is a static method to read configuration file entries in a very
	 * easy (but not so efficient) way.
	 * 
	 * @param filename
	 *            is the short path to the file to be read. Short path means you
	 *            only have to specify the configuration directory and the
	 *            filename. Suffixes "/etc/", "~/.", "./." and "./" are put
	 *            before automatically.
	 * @param section
	 *            is the name of the section where the entry must be found.
	 * @param entry
	 *            is the name of the entry which is to be found.
	 * @return A string with the keys content is returned. If the key was not
	 *         found, null is returned.
	 */
	static public String readEntry(String filename, String section, String entry) {
		URL url = ConfigFile.class.getResource(filename);
		if (url != null) {
			String file = url.getFile();
			return readEntry(new File(file), section, entry);
		}
		String result = "";
		String line;
		ArrayList<String> files = getAvailableConfigFiles(filename);
		for (String file : files) {
			line = readEntry(new File(file), section, entry);
			if (!line.isEmpty()) {
				result = line;
			}
		}
		return result;
	}

	static public String readSection(File file, String section) {
		try {
			ConfigFile configFile = new ConfigFile(file);
			String result = configFile.read(section);
			configFile.close();
			return result;
		} catch (IOException e) {
			return "";
		}
	}

	static public String readSection(String filename, String section) {
		URL url = ConfigFile.class.getClassLoader().getResource(filename);
		if (url != null) {
			String file = url.getFile();
			return readSection(new File(file), section);
		}
		ArrayList<String> files = getAvailableConfigFiles(filename);
		if (files.size() == 0) {
			return "";
		}
		return readSection(new File(files.get(files.size() - 1)), section);
	}

	/**
	 * This method writes a single section header to the already opened file.
	 * The method only add rectangular brackets to the name and writes the line
	 * into the file.
	 * 
	 * @param section
	 *            is the name of the section the header is to be written.
	 * @throws IOException
	 *             is thrown in case of an IO error.
	 */
	public void writeSection(String section) throws IOException {
		writeBytes("[" + section + "]\n");
	}

	/**
	 * This mothod write a single key value pair into the file. The key name and
	 * the key's value is specified. There is only an equal sign '=' added in
	 * between and the string is written afterwards as a line into the file. To
	 * be compatible a section header must be written before the key to have a
	 * valid section and key assignment.
	 * 
	 * @param key
	 *            is the name of the key to be written.
	 * @param value
	 *            is the key's value.
	 * @throws IOException
	 *             is thrown in case of an IO error.
	 */
	public void writeEntry(String key, String value) throws IOException {
		writeBytes(key + "=" + value + "\n");
	}

	/**
	 * This method just returns the current file's File object.
	 * 
	 * @return A file object is returned pointing the the currently opened file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * This method removes comments by truncating the line after the sharp (#)
	 * and removing all trailing and leading white spaces.
	 * 
	 * @param line
	 *            is a String to be removed from comment and white spaces.
	 * @return The cleaned string is returned.
	 */
	private String removeComment(String line) {

		String result = line;
		if (result.contains("#")) {
			result = result.substring(0, result.indexOf("#"));
		}
		return result.trim();
	}

	/**
	 * This method reads the content of the file completely into a ConfigHash.
	 * 
	 * @return A ConfigHash is returned containing all information in the config
	 *         file.
	 */
	public ConfigHash readToHash() throws IOException {
		ConfigHash hash = new ConfigHash();
		seek(0);
		String line;
		String section = "";
		do {
			line = readLineTrimmed();
			if (line == null) {
				continue;
			}
			line = removeComment(line);
			if (line.equals("")) {
				continue;
			}
			if (line.startsWith("[") && line.endsWith("]")) {
				section = line.substring(1, line.length() - 1);
				continue;
			}
			if (section.equals("")) {
				continue;
			}
			if (!line.contains("=")) {
				continue;
			}
			String key = line.substring(0, line.indexOf("="));
			if (key.equals("")) {
				continue;
			}
			String value = line.substring(line.indexOf("=") + 1);
			if (value.equals("")) {
				continue;
			}
			if (!hash.containsKey(section)) {
				hash.put(section, new Hashtable<String, String>());
			}
			hash.get(section).put(key, value);
		} while (line != null);
		return hash;
	}

	/**
	 * This is the static main method for starting this object. It is used for
	 * testing purposes only.
	 * 
	 * @param args
	 *            is the array of string for the command line parameters.
	 * @throws IOException
	 *             is thrown in case of an IO error.
	 */
	public static void main(String args[]) throws IOException {
		ConfigFile file = new ConfigFile(new File("/etc/pcmanalyse/config"));
		String str;
		do {
			str = file.readLine();
			if (str != null)
				System.out.println(str);
		} while (str != null);
		System.out.println("Read:");
		System.out.println("=====");
		System.out.println(file.read("GENERAL", "server"));
		System.out.println(ConfigFile.readEntry("pcmanalyse/config", "GENERAL",
				"server"));
		ConfigHash configHash = file.readToHash();
		configHash.println();
		file.close();
	}
}
