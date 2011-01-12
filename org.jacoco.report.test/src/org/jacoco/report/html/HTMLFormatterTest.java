/*******************************************************************************
 * Copyright (c) 2009, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.report.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import org.jacoco.report.ILanguageNames;
import org.jacoco.report.MemoryMultiReportOutput;
import org.jacoco.report.ReportStructureTestDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link HTMLFormatter}.
 * 
 * @author Marc R. Hoffmann
 * @version $qualified.bundle.version$
 */
public class HTMLFormatterTest {

	private ReportStructureTestDriver driver;

	private HTMLFormatter formatter;

	private MemoryMultiReportOutput output;

	@Before
	public void setup() {
		driver = new ReportStructureTestDriver();
		formatter = new HTMLFormatter();
		output = new MemoryMultiReportOutput();
		formatter.setReportOutput(output);
	}

	@After
	public void teardown() {
		output.assertAllClosed();
	}

	@Test(expected = IllegalStateException.class)
	public void testNoReportOutput() throws IOException {
		new HTMLFormatter().createReportVisitor(null, null, null);
	}

	@Test
	public void testStructureWithGroup() throws IOException {
		driver.sendGroup(formatter);
		output.assertFile("index.html");
		output.assertFile("bundle/index.html");
		output.assertFile("bundle/org.jacoco.example/index.html");
		output.assertFile("bundle/org.jacoco.example/FooClass.html");
	}

	@Test
	public void testStructureWithBundleOnly() throws IOException {
		driver.sendBundle(formatter);
		output.assertFile("index.html");
		output.assertFile("org.jacoco.example/index.html");
		output.assertFile("org.jacoco.example/FooClass.html");
	}

	@Test
	public void testDefaultEncoding() throws Exception {
		driver.sendBundle(formatter);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				output.getFileAsStream("index.html"), "UTF-8"));
		final String line = reader.readLine();
		assertTrue(line,
				line.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\""));
	}

	@Test
	public void testSetEncoding() throws Exception {
		formatter.setOutputEncoding("UTF-16");
		driver.sendBundle(formatter);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				output.getFileAsStream("index.html"), "UTF-16"));
		final String line = reader.readLine();
		assertTrue(line,
				line.startsWith("<?xml version=\"1.0\" encoding=\"UTF-16\""));
	}

	@Test
	public void testGetLanguageNames() throws Exception {
		ILanguageNames names = new ILanguageNames() {
			public String getPackageName(String vmname) {
				return null;
			}

			public String getQualifiedClassName(String vmname) {
				return null;
			}

			public String getClassName(String vmname, String vmsignature,
					String vmsuperclass, String[] vminterfaces) {
				return null;
			}

			public String getMethodName(String vmclassname,
					String vmmethodname, String vmdesc, String vmsignature) {
				return null;
			}

		};
		formatter.setLanguageNames(names);
		assertSame(names, formatter.getLanguageNames());
	}

	@Test
	public void testGetFooterText() throws Exception {
		formatter.setFooterText("Custom Footer");
		assertEquals("Custom Footer", formatter.getFooterText());
	}

	@Test
	public void testGetLocale() throws Exception {
		formatter.setLocale(Locale.KOREAN);
		assertEquals(Locale.KOREAN, formatter.getLocale());
	}

}
