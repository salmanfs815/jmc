/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The contents of this file are subject to the terms of either the Universal Permissive License
 * v 1.0 as shown at http://oss.oracle.com/licenses/upl
 *
 * or the following license:
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjdk.jmc.flightrecorder.ui;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.openjdk.jmc.common.item.IItem;
import org.openjdk.jmc.common.item.IType;
import org.openjdk.jmc.flightrecorder.ui.EventTypeFolderNode.EventTypeNode;
import org.openjdk.jmc.flightrecorder.ui.JfrOutlinePage.TriFunction;
import org.openjdk.jmc.flightrecorder.ui.common.TypeFilterBuilder;
import org.openjdk.jmc.flightrecorder.ui.messages.internal.Messages;
import org.openjdk.jmc.flightrecorder.ui.pages.itemhandler.ItemHandlerPage.ItemHandlerUiStandIn;
import org.openjdk.jmc.ui.misc.DisplayToolkit;
import org.openjdk.jmc.ui.wizards.IPerformFinishable;
import org.openjdk.jmc.ui.wizards.OnePageWizardDialog;

public class TypeSelectorWizardPage extends WizardPage implements IPerformFinishable {

	private final EventTypeFolderNode root;
	private final TriFunction<Set<IType<IItem>>, String, ImageDescriptor> onTypesSelected;
	private TypeFilterBuilder typeSelector;
	private ItemHandlerUiStandIn itemHandlerUiStandIn;
	private Text nameTextBox;
	private Label imageLabel;
	public static String newPageName = Messages.ItemHandlerPage_DEFAULT_PAGE_NAME;

	TypeSelectorWizardPage(EventTypeFolderNode root, TriFunction<Set<IType<IItem>>, String, ImageDescriptor> onTypesSelected, String title,
			String description) {
		super("TypeSelectorWizardPage"); //$NON-NLS-1$
		this.root = root;
		this.onTypesSelected = onTypesSelected;
		setTitle(title);
		setDescription(description);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().create());
		typeSelector = new TypeFilterBuilder(composite,
				() -> setPageComplete(typeSelector.getCheckedTypeIds().findAny().isPresent()));
		typeSelector.setInput(root);
		setControl(typeSelector.getControl());

		new Label(composite, SWT.NONE).setText("Page Name:");
		nameTextBox = new Text(composite, SWT.NONE);
		nameTextBox.setEditable(true);
		nameTextBox.setText(Messages.ItemHandlerPage_DEFAULT_PAGE_NAME);

//		Button button = new Button(composite, SWT.NONE);
//		button.setText("Choose page icon");
//
//		button.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				chooseImageFileDialog();
//			}
//		});
	}

	private void chooseImageFileDialog() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		String[] filterNames = new String[] {"Image Files", "All Files (*)"}; //$NON-NLS-1$ //$NON-NLS-2$
		String[] filterExtensions = new String[] {"*.gif;*.png;*.xpm;*.jpg;*.jpeg;*.tiff", "*"}; //$NON-NLS-1$ //$NON-NLS-2$
		fileDialog.setFilterNames(filterNames);
		fileDialog.setFilterExtensions(filterExtensions);
		String filename = fileDialog.open();
		if (filename == null) {
			// Dialog was cancelled. Bail out early to avoid handling that case later. Premature?
			return;
		}
		try (InputStream fis = new FileInputStream(filename)) {
			ImageData imageData = new ImageData(fis);
//			ImageData imageData = new ImageData(filename);
			// Validate image data
			if (imageData.width != 16 || imageData.height != 16) {
				imageData = resizeImage(imageData, 16, 16);
			}
			DisplayToolkit.dispose(imageLabel.getImage());
			imageLabel.setImage(new Image(getShell().getDisplay(), imageData));
			imageLabel.getParent().layout();
			setPageComplete(isPageComplete());
		} catch (Exception e) {
			// FIXME: Add proper logging
			e.printStackTrace();
		}
	}

	private ImageData resizeImage(ImageData imageData, int width, int height) {
		Image original = ImageDescriptor.createFromImageData(imageData).createImage();
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(original, 0, 0, imageData.width, imageData.height, 0, 0, width, height);
		gc.dispose();
		original.dispose();
		ImageData scaledData = scaled.getImageData();
		scaled.dispose();
		return scaledData;
	}

	@Override
	public boolean performFinish() {
		newPageName = nameTextBox.getText();
		onTypesSelected.apply(typeSelector.getSelectedTypes().map(EventTypeNode::getType).collect(Collectors.toSet()),
				newPageName, null);
//		newPageName = Messages.ItemHandlerPage_DEFAULT_PAGE_NAME;
		return true;
	}

	static void openDialog(
		EventTypeFolderNode root, TriFunction<Set<IType<IItem>>, String, ImageDescriptor> onTypesSelected, String title, String description) {
		OnePageWizardDialog.open(new TypeSelectorWizardPage(root, onTypesSelected, title, description), 500, 600);
	}
}
