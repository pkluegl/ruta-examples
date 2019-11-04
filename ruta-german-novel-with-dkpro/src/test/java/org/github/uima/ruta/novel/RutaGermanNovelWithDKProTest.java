/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.github.uima.ruta.novel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.github.uima.ruta.novel.Name.FirstName;
import com.github.uima.ruta.novel.Name.NobleTitle;
import com.github.uima.ruta.novel.Name.PersName;

public class RutaGermanNovelWithDKProTest {

  @Test
  public void testSample() throws Exception {

    // write default type system for CAS Editor
    try (OutputStream os = new FileOutputStream(new File("TypeSystem.xml"))) {
      TypeSystemDescriptionFactory.createTypeSystemDescription().toXML(os);
    }

    // initialize CAS
    JCas jcas = JCasFactory.createJCas();
    jcas.setDocumentLanguage("de");
    jcas.setDocumentText(FileUtils.readFileToString(
            new File("src/test/resources/samples/sample.txt"), StandardCharsets.UTF_8));

    File descriptorFile = new File(
            "target/generated-sources/ruta/descriptor/com/github/uima/ruta/novel/MainRutaAnnotator.xml");
    Assert.assertTrue("Generated descriptor file exists", descriptorFile.exists());
    // apply rules (with pos tagging)
    AnalysisEngineDescription aed = AnalysisEngineFactory
            .createEngineDescriptionFromPath(descriptorFile.getPath());
    SimplePipeline.runPipeline(jcas, aed);

    storeJCas(jcas, "output/sample.xmi");

    Collection<PersName> persNames = JCasUtil.select(jcas, PersName.class);
    Assert.assertFalse(persNames.isEmpty());
  }

  @Test
  public void testDictionary() throws Exception {
    JCas jcas = JCasFactory.createJCas();
    jcas.setDocumentLanguage("de");
    jcas.setDocumentText("Baron Adam");

    File descriptorFile = new File(
            "target/generated-sources/ruta/descriptor/com/github/uima/ruta/novel/NameRutaAnnotator.xml");
    Assert.assertTrue("Generated descriptor file exists", descriptorFile.exists());
    AnalysisEngineDescription aed = AnalysisEngineFactory
            .createEngineDescriptionFromPath(descriptorFile.getPath());
    SimplePipeline.runPipeline(jcas, aed);

    storeJCas(jcas, "output/testDictionary.xmi");

    Assert.assertEquals(1, JCasUtil.select(jcas, FirstName.class).size());
    Assert.assertEquals(1, JCasUtil.select(jcas, NobleTitle.class).size());

  }

  private void storeJCas(JCas jcas, String location) throws IOException, FileNotFoundException {
    File outputFile = new File(location);
    outputFile.getParentFile().mkdirs();
    try (OutputStream os = new FileOutputStream(outputFile)) {
      CasIOUtils.save(jcas.getCas(), os, SerialFormat.XMI);
    }
  }

}
