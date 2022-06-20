package org.eclipse.ui.internal.console;

import static org.eclipse.ui.console.IAnsiConsoleConstants.P_CUSTOM_COLORS;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_COLOR_PALETTE_NAME;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_INTERPRET_ANSI_ESCAPE_SEQUENCES;
import static org.eclipse.ui.console.IAnsiConsoleConstants.P_SHOW_ESCAPE_SEQUENCES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.console.ConsolePlugin;

public class AnsiConsolePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

//	private static final ILog LOGGER = Platform.getLog(FrameworkUtil.getBundle(AnsiConsolePreferencePage.class));
	private ComboFieldEditor paletteSelector;
	private ColorFieldEditor[] colorSelectors;

	public AnsiConsolePreferencePage() {
		super(GRID);
		setPreferenceStore(ConsolePlugin.getDefault().getPreferenceStore());
		setDescription("ANSI Console settings."); //$NON-NLS-1$
	}

	public static class GroupFieldEditor extends FieldEditor {

		private String name;
		private Collection<FieldEditor> members = new ArrayList<>();
		private int numcolumns;
		private Group group;
		private Composite parent;

		/**
		 * The gap outside, between the group-frame and the widgets around the group
		 */
		private static final int GROUP_PADDING = 5; // px

		/**
		 * The inside-distance creates a new boolean field editor
		 */
		protected GroupFieldEditor() {
		}

		/**
		 * Creates a Group of {@link FieldEditor} objects
		 *
		 * @param name              - name
		 * @param fieldEditorParent - parent
		 */
		public GroupFieldEditor(String name, Composite fieldEditorParent) {
			this.name = name;

			// the parent is a Composite, which is contained inside of the preference page.
			// Initially it
			// does not have any layout.
			this.parent = fieldEditorParent;
			this.group = new Group(parent, SWT.DEFAULT);
			this.group.setText(this.name);
		}

		/**
		 * The parent for all the FieldEditors inside of this Group.
		 *
		 * @return - the parent
		 */
		public Composite getFieldEditorParent() {
			return group;
		}

		/**
		 * Sets the FieldeditorChildren for this {@link GroupFieldEditor}
		 *
		 * @param membersParam
		 */
		public void setFieldEditors(Collection<FieldEditor> membersParam) {
			this.members = membersParam;
			doFillIntoGrid(getFieldEditorParent(), numcolumns);
		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor.
		 */
		@Override
		protected void adjustForNumColumns(int numColumns) {
			this.numcolumns = numColumns;
		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor.
		 */
		@Override
		protected void doFillIntoGrid(Composite parentParam, int numColumns) {

			final var gridLayout = new GridLayout();
			gridLayout.marginLeft = GROUP_PADDING;
			gridLayout.marginRight = GROUP_PADDING;
			gridLayout.marginTop = GROUP_PADDING;
			gridLayout.marginBottom = GROUP_PADDING;
			gridLayout.numColumns = 16;
			this.group.setLayout(gridLayout);

			this.parent.layout();
			this.parent.redraw();

			for (final FieldEditor editor : members) {
				editor.fillIntoGrid(getFieldEditorParent(), 2);
			}

		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor. Loads the value from the
		 * preference store and sets it to the check box.
		 */
		@Override
		protected void doLoad() {

			for (final FieldEditor editor : members) {
				editor.load();
			}

		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor. Loads the default value from
		 * the preference store and sets it to the check box.
		 */
		@Override
		protected void doLoadDefault() {

			for (final FieldEditor editor : members) {
				editor.loadDefault();
			}

		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor.
		 */
		@Override
		protected void doStore() {

			for (final FieldEditor editor : members) {
				editor.store();
			}

		}

		@Override
		public void store() {
			doStore();
		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor.
		 */
		@Override
		public int getNumberOfControls() {
			return 1;
		}

		/*
		 * (non-Javadoc) Method declared on FieldEditor.
		 */
		@Override
		public void setFocus() {
			if (!members.isEmpty()) {
				members.iterator().next().setFocus();
			}
		}

		/*
		 * @see FieldEditor.setEnabled
		 */
		@Override
		public void setEnabled(boolean enabled, Composite parentParam) {

			for (final FieldEditor editor : members) {
				editor.setEnabled(enabled, parentParam);
			}

		}

		@Override
		public void setPreferenceStore(IPreferenceStore store) {
			super.setPreferenceStore(store);

			for (final FieldEditor editor : members) {
				editor.setPreferenceStore(store);
			}

		}

	}

	private static class ComboFieldEditor extends FieldEditor {

		/**
		 * The <code>Combo</code> widget.
		 */
		private Combo fCombo;

		/**
		 * The value (not the name) of the currently selected item in the Combo widget.
		 */
		private String fValue;

		public String getValue() {
			return fValue;
		}

		/**
		 * The names (labels) and underlying values to populate the combo widget. These
		 * should be arranged as: { {name1, value1}, {name2, value2}, ...}
		 */
		private final String[][] fEntryNamesAndValues;

		/**
		 * Create the combo box field editor.
		 *
		 *
		 * @param parent the parent composite
		 */
		public ComboFieldEditor(Composite parent) {
			final String[][] entryNamesAndValues = { { "VGA", AnsiConsoleColorPalette.PALETTE_VGA }, //$NON-NLS-1$
					{ "Windows XP", AnsiConsoleColorPalette.PALETTE_WINXP }, //$NON-NLS-1$
					{ "Windows 10", AnsiConsoleColorPalette.PALETTE_WIN10 }, //$NON-NLS-1$
					{ "Mac OS X", AnsiConsoleColorPalette.PALETTE_MAC }, //$NON-NLS-1$
					{ "PuTTY", AnsiConsoleColorPalette.PALETTE_PUTTY }, //$NON-NLS-1$
					{ "XTerm", AnsiConsoleColorPalette.PALETTE_XTERM }, //$NON-NLS-1$
					{ "mIRC", AnsiConsoleColorPalette.PALETTE_MIRC }, //$NON-NLS-1$
					{ "Ubuntu", AnsiConsoleColorPalette.PALETTE_UBUNTU }, //$NON-NLS-1$
					{ "Tango", AnsiConsoleColorPalette.PALETTE_TANGO }, //$NON-NLS-1$
					{ "Rxvt", AnsiConsoleColorPalette.PALETTE_RXVT }, //$NON-NLS-1$
					{ "Custom", AnsiConsoleColorPalette.PALETTE_CUSTOM } //$NON-NLS-1$
			};
			super.init(P_COLOR_PALETTE_NAME, "&Built-in schemes:");//$NON-NLS-1$

			Assert.isTrue(checkArray(entryNamesAndValues));
			fEntryNamesAndValues = entryNamesAndValues;
			createControl(parent);
		}

		/**
		 * Checks whether given <code>String[][]</code> contains sub arrays with minimum
		 * size 2
		 *
		 * @return <code>true</code> if it is ok, and <code>false</code> otherwise
		 */
		private static boolean checkArray(String[][] table) {
			if (table == null) {
				return false;
			}
			for (final String[] array : table) {
				if (array == null || array.length < 2) {
					return false;
				}
			}
			return true;
		}

		@Override
		protected void adjustForNumColumns(int numColumns) {
			if (numColumns > 1) {
				final Control control = getLabelControl();
				var left = numColumns;
				if (control != null) {
					((GridData) control.getLayoutData()).horizontalSpan = 1;
					left = left - 1;
				}
				((GridData) fCombo.getLayoutData()).horizontalSpan = left;
			} else {
				final Control control = getLabelControl();
				if (control != null) {
					((GridData) control.getLayoutData()).horizontalSpan = 1;
				}
				((GridData) fCombo.getLayoutData()).horizontalSpan = 1;
			}
		}

		@Override
		protected void doFillIntoGrid(Composite parent, int numColumns) {
			var comboC = 1;
			if (numColumns > 1) {
				comboC = numColumns - 1;
			}
			Control control = getLabelControl(parent);
			var gd = new GridData();
			gd.horizontalSpan = 1;
			control.setLayoutData(gd);
			control = getComboBoxControl(parent);
			gd = new GridData();
			gd.horizontalSpan = comboC;
			gd.horizontalAlignment = GridData.FILL;
			control.setLayoutData(gd);
			control.setFont(parent.getFont());
		}

		@Override
		protected void doLoad() {
			updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
		}

		@Override
		protected void doLoadDefault() {
			final var oldValue = fValue;
			updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
			valueChanged(oldValue, fValue);
		}

		@Override
		protected void doStore() {
			if (fValue == null) {
				getPreferenceStore().setToDefault(getPreferenceName());
				return;
			}
			getPreferenceStore().setValue(getPreferenceName(), fValue);
		}

		@Override
		public int getNumberOfControls() {
			return 2;
		}

		/*
		 * Lazily create and return the Combo control.
		 */
		private Combo getComboBoxControl(Composite parent) {
			if (fCombo == null) {
				fCombo = new Combo(parent, SWT.READ_ONLY);
				fCombo.setFont(parent.getFont());
				for (var i = 0; i < fEntryNamesAndValues.length; i++) {
					fCombo.add(fEntryNamesAndValues[i][0], i);
				}

				fCombo.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						final var oldValue = fValue;
						final var name = fCombo.getText();
						fValue = getValueForName(name);
						setPresentsDefaultValue(false);
						valueChanged(oldValue, fValue);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// ignore
					}
				});
			}
			return fCombo;
		}

		/*
		 * Given the name (label) of an entry, return the corresponding value.
		 */
		private String getValueForName(String name) {
			for (final String[] entry : fEntryNamesAndValues) {
				if (name.equals(entry[0])) {
					return entry[1];
				}
			}
			return fEntryNamesAndValues[0][0];
		}

		/*
		 * Set the name in the combo widget to match the specified value.
		 */
		public void updateComboForValue(String value) {
			fValue = value;
			for (final String[] fEntryNamesAndValue : fEntryNamesAndValues) {
				if (value.equals(fEntryNamesAndValue[1])) {
					fCombo.setText(fEntryNamesAndValue[0]);
					return;
				}
			}
			if (fEntryNamesAndValues.length > 0) {
				fValue = fEntryNamesAndValues[0][1];
				fCombo.setText(fEntryNamesAndValues[0][0]);
			}
		}

		/**
		 * Informs this field editor's listener, if it has one, about a change to the
		 * value (<code>VALUE</code> property) provided that the old and new values are
		 * different.
		 *
		 * @param oldValue the old value
		 * @param newValue the new value
		 * @since 3.18
		 */
		protected void valueChanged(String oldValue, String newValue) {
			// Only fire event if old and new values are different.
			if (oldValue != null && !oldValue.equals(newValue) || newValue != null) {
				fireValueChanged(VALUE, oldValue, newValue);
			}
		}

		@Override
		public void setEnabled(boolean enabled, Composite parent) {
			super.setEnabled(enabled, parent);
			getComboBoxControl(parent).setEnabled(enabled);
		}
	}

	@Override
	public void createFieldEditors() {
		final var parent = getFieldEditorParent();

		addField(new BooleanFieldEditor(P_INTERPRET_ANSI_ESCAPE_SEQUENCES, "Interpret ANSI escape sequences", parent)); //$NON-NLS-1$

		addField(new BooleanFieldEditor(P_SHOW_ESCAPE_SEQUENCES, "&Show the escape sequences", parent));//$NON-NLS-1$

		// addField(new BooleanFieldEditor(PREF_PUT_RTF_IN_CLIPBOARD,
		// "Put &RTF in Clipboard. You will be able to paste styled text in some
		// applications.", parent));

		final var palette = new Label(parent, SWT.NONE);
		palette.setText("Palette");//$NON-NLS-1$

		palette.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		new Label(parent, SWT.NONE);
		paletteSelector = new ComboFieldEditor(parent);

		addField(paletteSelector);

		colorSelectors = new ColorFieldEditor[P_CUSTOM_COLORS.length];

		final var colorPalette = new Label(parent, SWT.NONE);
		colorPalette.setText("Color palette:");//$NON-NLS-1$

		final var group = new GroupFieldEditor("", parent);//$NON-NLS-1$

		addField(group);
		for (var i = 0; i < P_CUSTOM_COLORS.length; ++i) {

			colorSelectors[i] = new ColorFieldEditor(P_CUSTOM_COLORS[i], "", group.getFieldEditorParent());//$NON-NLS-1$

			addField(colorSelectors[i]);
		}
		group.setFieldEditors(Arrays.asList(colorSelectors));

		/*
		 * createLink(parent, true,
		 * "<a href=\"https://github.com/ydaveluy/pretty-console/wiki/\">Wiki</a>:" +
		 * " documentation."); createLink(parent, false,
		 * "<a href=\"https://github.com/ydaveluy/pretty-console/\">GitHub repository</a>:"
		 * + " source code, issues, etc.");
		 */

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		super.propertyChange(event);
		if (paletteSelector == event.getSource()) {

			final var palette = AnsiConsoleColorPalette.getPalette(event.getNewValue());
			if (palette == null) {
				return;
			}
			// load palette values
			for (var i = 0; i < colorSelectors.length; ++i) {
				colorSelectors[i].getColorSelector().setColorValue(palette[i]);
			}
		} else {
			final var paletteName = paletteSelector.getValue();
			final var palette = AnsiConsoleColorPalette.getPalette(paletteName);
			if (palette == null) {
				return;
			}

			for (var i = 0; i < colorSelectors.length; ++i) {

				if (colorSelectors[i] == event.getSource()) {

					if (!palette[i].equals(event.getNewValue())) {
						paletteSelector.updateComboForValue(AnsiConsoleColorPalette.PALETTE_CUSTOM);

					}
					break;
				}
			}
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do, but we are forced to implement it for IWorkbenchPreferencePage
	}

	/*
	 * private void createLink(Composite parent, boolean fillGap, String text) {
	 * final Link link = new Link(parent, SWT.WRAP); link.setText(text);
	 *
	 * final GridData gridData = new GridData(); gridData.horizontalSpan = 2;
	 * gridData.grabExcessVerticalSpace = fillGap; gridData.verticalAlignment =
	 * SWT.BOTTOM; link.setLayoutData(gridData);
	 *
	 * link.addListener(SWT.Selection, event -> { try {
	 * PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(
	 * new URL(event.text)); } catch (final Exception e) { LOGGER.log(new
	 * Status(IStatus.ERROR, PrettyConsoleActivator.PLUGIN_ID,
	 * "Cannot open url in browser." + "URL: " + event.text + " : " +
	 * e.getMessage()));
	 *
	 * } }); }
	 */
	/*
	 * @Override public boolean performOk() { final boolean result =
	 * super.performOk(); final IHandlerService handlerService =
	 * PlatformUI.getWorkbench().getService(IHandlerService.class); try {
	 * handlerService.executeCommand(EnableDisableHandler.COMMAND_ID, new Event());
	 * } catch (final Exception e) { LOGGER.log(new Status(IStatus.ERROR,
	 * PrettyConsoleActivator.PLUGIN_ID, "Command '" +
	 * EnableDisableHandler.COMMAND_ID + "' not found" + " : " + e.getMessage())); }
	 * return result; }
	 */

}
