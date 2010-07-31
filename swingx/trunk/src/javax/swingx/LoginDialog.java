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

package javax.swingx;

import java.io.IOException;

import javax.i18n4java.Translator;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swingx.connect.Slot;

/**
 * PasswordDialog is an standard object to present a password dialog. It is used
 * for all authentication processes with databases and within the user
 * administration.
 * 
 * @author Rick-Rainer Ludwig
 */
public final class LoginDialog extends Dialog {

	private static final long serialVersionUID = 1L;

	private static final Translator translator = Translator
			.getTranslator(LoginDialog.class);

	/**
	 * This JTextField holds the user name.
	 */
	private JTextField username;

	/**
	 * This JPasswordField holds the password. It is not shown and only
	 * represented as stars.
	 */
	private JPasswordField password;

	/**
	 * Ok is for starting the login process.
	 */
	private Button ok;

	/**
	 * Cancel is for interrupting the login process.
	 */
	private Button cancel;

	private boolean finishedByOk = false;

	/**
	 * This is the constructor for PasswordDialog.
	 * 
	 * @param owner
	 *            is the calling parent window. If the password dialog is to be
	 *            used during startup without a parent window, that null should
	 *            be used.
	 */
	public LoginDialog() {
		super(translator.i18n("User Confirmation"), true);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new Label(translator.i18n("Username")));
		panel.add(username = new JTextField());
		panel.add(new Label(translator.i18n("Password")));
		panel.add(password = new JPasswordField());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(ok = new Button(translator.i18n("OK")));
		buttonPanel.add(cancel = new Button(translator.i18n("Cancel")));

		ok.connect("start", this, "quit");
		cancel.connect("start", this, "abort");

		panel.add(buttonPanel);
		setContentPane(panel);

		pack();
		getRootPane().setDefaultButton(ok);
	}

	/**
	 * This method returns the set user name after the dialog was closed.
	 * 
	 * @return A string with the user name is returned.
	 * @throws IOException
	 *             is thrown in a case of IO error.
	 */
	public String getUsername() {
		if (!finishedByOk) {
			return "";
		}
		return username.getText();
	}

	/**
	 * This method returns the set password after the dialog was closed.
	 * 
	 * @return A string with the password is returned.
	 * @throws IOException
	 *             is thrown in a case of IO error.
	 */
	public String getPassword() {
		if (!finishedByOk) {
			return "";
		}
		return String.valueOf(password.getPassword());
	}

	public boolean run() {
		setVisible(true);
		return finishedByOk;
	}

	@Slot
	public void abort() {
		super.abort();
	}

	@Slot
	public void quit() {
		finishedByOk = true;
		super.quit();
	}
}
