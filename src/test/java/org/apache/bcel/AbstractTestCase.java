/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.bcel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;
import org.apache.bcel.generic.ElementValuePairGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.SimpleElementValueGen;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase
{
    private static final boolean verbose = false;

    protected static final String PACKAGE_BASE_NAME = AbstractTestCase.class.getPackage().getName();

    // Location of test data
    protected static final File TESTDATA = new File("target", "testdata");

    // package base name in signature format, i.e. with '/' separators instead of '.'
    protected static final String PACKAGE_BASE_SIG = PACKAGE_BASE_NAME.replace('.', '/');

    /**
     * @param name
     * @return Path to file under the TESTDATA directory
     */
    protected File createTestdataFile(final String name)
    {
        return new File(TESTDATA, name);
    }

    protected JavaClass getTestClass(final String name) throws ClassNotFoundException
    {
        return SyntheticRepository.getInstance().loadClass(name);
    }

    protected Method getMethod(final JavaClass cl, final String methodname)
    {
        Method[] methods = cl.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodname))
            {
                return m;
            }
        }
        return null;
    }

    /**
     * Delete a file under the TESTDATA directory
     * @param name
     * @return
     */
    protected boolean wipe(final String name)
    {
        return new File(TESTDATA, name).delete();
    }

    /**
     * Delete a directory and file under the TESTDATA directory
     * @param dir
     * @param name
     * @return true if the file was deleted
     */
    protected boolean wipe(final String dir, final String name)
    {
        // The parameter is relative to the TESTDATA dir
        boolean b = wipe(dir + File.separator + name);
        final File testDir = new File(TESTDATA, dir);
        String[] files = testDir.list();
        if (files == null || files.length == 0)
        {
            if (!testDir.delete()) {
                System.err.println("Failed to remove: " + testDir);
            }
        } else {
            System.err.println("Non-empty directory: " + testDir);
        }
        return b;
    }

    public SyntheticRepository createRepos(final String cpentry)
    {
        ClassPath cp = new ClassPath("target" + File.separator + "testdata"
                + File.separator + cpentry + File.separator);
        return SyntheticRepository.getInstance(cp);
    }

    protected Attribute[] findAttribute(final String name, final JavaClass clazz)
    {
        Attribute[] all = clazz.getAttributes();
        List<Attribute> chosenAttrsList = new ArrayList<>();
        for (Attribute element : all) {
            if (verbose) {
                System.err.println("Attribute: " + element.getName());
            }
            if (element.getName().equals(name)) {
                chosenAttrsList.add(element);
            }
        }
        return chosenAttrsList.toArray(new Attribute[] {});
    }

    protected Attribute findAttribute(final String name, final Attribute[] all)
    {
        List<Attribute> chosenAttrsList = new ArrayList<>();
        for (Attribute element : all) {
            if (verbose) {
                System.err.println("Attribute: " + element.getName());
            }
            if (element.getName().equals(name)) {
                chosenAttrsList.add(element);
            }
        }
        assertTrue("Should be one match: " + chosenAttrsList.size(),
                chosenAttrsList.size() == 1);
        return chosenAttrsList.get(0);
    }

    protected String dumpAttributes(final Attribute[] as)
    {
        StringBuilder result = new StringBuilder();
        result.append("AttributeArray:[");
        for (int i = 0; i < as.length; i++)
        {
            Attribute attr = as[i];
            result.append(attr.toString());
            if (i + 1 < as.length) {
                result.append(",");
            }
        }
        result.append("]");
        return result.toString();
    }

    protected String dumpAnnotationEntries(final AnnotationEntry[] as)
    {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < as.length; i++)
        {
            AnnotationEntry annotation = as[i];
            result.append(annotation.toShortString());
            if (i + 1 < as.length) {
                result.append(",");
            }
        }
        result.append("]");
        return result.toString();
    }

    protected String dumpAnnotationEntries(final AnnotationEntryGen[] as)
    {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < as.length; i++)
        {
            AnnotationEntryGen annotation = as[i];
            result.append(annotation.toShortString());
            if (i + 1 < as.length) {
                result.append(",");
            }
        }
        result.append("]");
        return result.toString();
    }

    public AnnotationEntryGen createFruitAnnotationEntry(final ConstantPoolGen cp,
            final String aFruit, final boolean visibility)
    {
        SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.STRING, cp, aFruit);
        ElementValuePairGen nvGen = new ElementValuePairGen("fruit", evg, cp);
        ObjectType t = new ObjectType("SimpleStringAnnotation");
        List<ElementValuePairGen> elements = new ArrayList<>();
        elements.add(nvGen);
        return new AnnotationEntryGen(t, elements, visibility, cp);
    }
}
