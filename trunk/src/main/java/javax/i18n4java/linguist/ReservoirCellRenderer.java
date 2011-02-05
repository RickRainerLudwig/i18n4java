/****************************************************************************
 *
 *   ReservoirCellRenderer.java
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
 
package javax.i18n4java.linguist;

import java.awt.Component;
import java.util.Locale;

import javax.i18n4java.data.LanguageSet;
import javax.i18n4java.data.MultiLanguageTranslations;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ReservoirCellRenderer implements ListCellRenderer {

	private MultiLanguageTranslations translationsHash;
	private Locale selectedLocale = Locale.getDefault();

	public ReservoirCellRenderer() {
		super();
	}

	public ReservoirCellRenderer(MultiLanguageTranslations translationsHash) {
		super();
		this.translationsHash = translationsHash;
	}

	/**
	 * @return the translationsHash
	 */
	public MultiLanguageTranslations getTranslationsHash() {
		return translationsHash;
	}

	/**
	 * @param translationsHash
	 *            the translationsHash to set
	 */
	public void setTranslationsHash(MultiLanguageTranslations translationsHash) {
		this.translationsHash = translationsHash;
	}

	/**
	 * @return the locale
	 */
	public Locale getSelectedLocale() {
		return selectedLocale;
	}

	public void setSelectedLocale(Locale locale) {
		this.selectedLocale = locale;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		String source = (String) value;

		boolean finished = false;
		LanguageSet languageSet = translationsHash.getTranslations(source);
		if (languageSet == null) {
			return new StatusComponent(source, isSelected, cellHasFocus, false);
		}
		String translation = languageSet.get(selectedLocale);
		if ((translation != null) && (!translation.isEmpty())) {
			finished = true;
		}
		return new StatusComponent(source, isSelected, cellHasFocus, finished);
	}
}
