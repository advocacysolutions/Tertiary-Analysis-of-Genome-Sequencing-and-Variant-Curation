/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2020 Queen Mary University of London.
 * Copyright (c) 2012-2016 Charité Universitätsmedizin Berlin and Genome Research Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monarchinitiative.exomiser.core.writers;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.exomiser.core.analysis.Analysis;
import org.monarchinitiative.exomiser.core.analysis.AnalysisResults;
import org.monarchinitiative.exomiser.core.analysis.sample.Sample;
import org.monarchinitiative.exomiser.core.genome.TestFactory;
import org.monarchinitiative.exomiser.core.model.Gene;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.StringJoiner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class TsvGeneResultsWriterTest {

    private static final String HEADER = new StringJoiner("\t")
            .add("#GENE_SYMBOL")
            .add("ENTREZ_GENE_ID")
            .add("EXOMISER_GENE_PHENO_SCORE")
            .add("EXOMISER_GENE_VARIANT_SCORE")
            .add("EXOMISER_GENE_COMBINED_SCORE")
            .add("HUMAN_PHENO_SCORE")
            .add("MOUSE_PHENO_SCORE")
            .add("FISH_PHENO_SCORE")
            .add("WALKER_SCORE")
            .add("PHIVE_ALL_SPECIES_SCORE")
            .add("OMIM_SCORE")
            .add("MATCHES_CANDIDATE_GENE")
            .add("HUMAN_PHENO_EVIDENCE")
            .add("MOUSE_PHENO_EVIDENCE")
            .add("FISH_PHENO_EVIDENCE")
            .add("HUMAN_PPI_EVIDENCE")
            .add("MOUSE_PPI_EVIDENCE")
            .add("FISH_PPI_EVIDENCE\n")
            .toString();

    private static final String FGFR2_GENE_STRING = "FGFR2	2263	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0	\n";
    private static final String RBM8A_GENE_STRING = "RBM8A	9939	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0.0000	0	\n";

    private final TsvGeneResultsWriter instance = new TsvGeneResultsWriter();

    private AnalysisResults analysisResults;
    private final Analysis analysis = Analysis.builder().build();
    private final Sample sample = Sample.builder().build();

    @BeforeEach
    public void setUp() {
        Gene fgfr2 = TestFactory.newGeneFGFR2();
        fgfr2.setCompatibleInheritanceModes(EnumSet.of(ModeOfInheritance.AUTOSOMAL_DOMINANT));
        Gene rbm8a = TestFactory.newGeneRBM8A();
        rbm8a.setCompatibleInheritanceModes(EnumSet.of(ModeOfInheritance.AUTOSOMAL_DOMINANT));
        analysisResults = AnalysisResults.builder()
                .sample(sample)
                .analysis(analysis)
                .genes(Arrays.asList(fgfr2, rbm8a))
                .build();
    }

    @Test
    public void testWrite() throws Exception {
        Path tempFolder = Files.createTempDirectory("exomiser_test");
        String outPrefix = tempFolder.resolve("testWrite").toString();

        OutputSettings settings = OutputSettings.builder()
                .outputPrefix(outPrefix)
                .outputFormats(EnumSet.of(OutputFormat.TSV_GENE))
                .build();

        instance.writeFile(ModeOfInheritance.AUTOSOMAL_DOMINANT, analysisResults, settings);

        Path outputPath = tempFolder.resolve("testWrite_AD.genes.tsv");
        assertThat(outputPath.toFile().exists(), is(true));
        assertThat(outputPath.toFile().delete(), is(true));
        Files.delete(tempFolder);
    }

    @Test
    public void testWriteString() {
        OutputSettings settings = OutputSettings.builder()
                .outputFormats(EnumSet.of(OutputFormat.TSV_GENE))
                .build();
        String outString = instance.writeString(ModeOfInheritance.AUTOSOMAL_DOMINANT, analysisResults, settings);
        assertThat(outString, equalTo(HEADER + FGFR2_GENE_STRING + RBM8A_GENE_STRING));
    }

    @Test
    public void testWriteStringStartsWithAHeaderLine() {
        OutputSettings settings = OutputSettings.builder()
                .outputFormats(EnumSet.of(OutputFormat.TSV_GENE))
                .build();
        String outString = instance.writeString(ModeOfInheritance.AUTOSOMAL_DOMINANT, analysisResults, settings);
        String[] lines = outString.split("\n");
        assertThat(lines[0] + "\n", equalTo(HEADER));
    }

}
