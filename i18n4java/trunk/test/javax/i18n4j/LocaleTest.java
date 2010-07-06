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

package javax.i18n4j;

import java.util.Locale;
import java.util.Vector;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;

public class LocaleTest extends TestCase {

	@Test
	public void testAvailableLocales() {
		Locale locales[] = Locale.getAvailableLocales();
		Vector<String> localeShortCuts = new Vector<String>();
		for (int index = 0; index < locales.length; index++) {
			Locale locale = locales[index];
			localeShortCuts.add(locale.toString());
			System.out.println(locale.toString() + "\t"
					+ locale.getDisplayName() + ":\t"
					+ locale.getDisplayLanguage() + "\t"
					+ locale.getDisplayCountry() + "\t"
					+ locale.getDisplayVariant());
		}
		Assert.assertTrue(localeShortCuts.contains("de_DE"));
		Assert.assertTrue(localeShortCuts.contains("en_GB"));
		Assert.assertTrue(localeShortCuts.contains("en_US"));
		Assert.assertTrue(localeShortCuts.contains("vi_VN"));
		Assert.assertTrue(localeShortCuts.contains("zh_TW"));
	}

}
