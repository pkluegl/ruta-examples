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
package org.apache.uima.ruta.novel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.ruta.novel.Name.PersName;
import org.junit.Assert;
import org.junit.Test;

public class RunMainRutaAnnotator {

	@Test
	public void test() throws IOException, UIMAException {

		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText(FileUtils.readFileToString(new File("input/sample.txt"), "UTF-8"));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescriptionFromPath("target/generated-sources/ruta/descriptor/org/apache/uima/ruta/novel/MainRutaAnnotator.xml");
		SimplePipeline.runPipeline(jcas, aed);
		
		Collection<PersName> select = JCasUtil.select(jcas, PersName.class);
		Assert.assertFalse(select.isEmpty());
	}

}
