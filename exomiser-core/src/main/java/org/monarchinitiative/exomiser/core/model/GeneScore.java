/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2021 Queen Mary University of London.
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

package org.monarchinitiative.exomiser.core.model;

import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import org.monarchinitiative.exomiser.core.phenotype.ModelPhenotypeMatch;
import org.monarchinitiative.exomiser.core.prioritisers.model.Disease;

import java.util.List;
import java.util.Objects;

/**
 * Immutable class for collecting information about a gene's score under a particular inheritance mode and the alleles
 * contributing to the score.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 * @since 10.0.0
 */
public final class GeneScore implements Comparable<GeneScore> {

    private static final GeneScore EMPTY = new Builder().build();

    private final GeneIdentifier geneIdentifier;
    private final ModeOfInheritance modeOfInheritance;
    private final double combinedScore;
    private final double phenotypeScore;
    private final double variantScore;
    private final List<VariantEvaluation> contributingVariants;
    private final List<ModelPhenotypeMatch<Disease>> compatibleDiseaseMatches;

    private GeneScore(Builder builder) {
        this.geneIdentifier = builder.geneIdentifier;
        this.modeOfInheritance = builder.modeOfInheritance;
        this.combinedScore = builder.combinedScore;
        this.phenotypeScore = builder.phenotypeScore;
        this.variantScore = builder.variantScore;
        this.contributingVariants = List.copyOf(builder.contributingVariants);
        this.compatibleDiseaseMatches = List.copyOf(builder.compatibleDiseaseMatches);
    }

    public GeneIdentifier getGeneIdentifier() {
        return geneIdentifier;
    }

    public ModeOfInheritance getModeOfInheritance() {
        return modeOfInheritance;
    }

    public double getCombinedScore() {
        return combinedScore;
    }

    public double getPhenotypeScore() {
        return phenotypeScore;
    }

    public double getVariantScore() {
        return variantScore;
    }

    public List<VariantEvaluation> getContributingVariants() {
        return contributingVariants;
    }

    /**
     * Whether or not the {@code GeneScore} has any contributing variants.
     *
     * @return whether or not the {@code GeneScore} has any contributing variants.
     * @since 10.1.0
     */
    public boolean hasContributingVariants() {
        return !contributingVariants.isEmpty();
    }

    public List<ModelPhenotypeMatch<Disease>> getCompatibleDiseaseMatches() {
        return compatibleDiseaseMatches;
    }

    public boolean hasCompatibleDiseaseMatches() {
        return !compatibleDiseaseMatches.isEmpty();
    }

    /**
     * Compares the combined score of two {@code GeneScore} objects. Will return the {@code GeneScore} with the highest
     * numerical value or the first if equal.
     *
     * @param s1 the first {@code GeneScore}
     * @param s2 the other {@code GeneScore}
     * @return the {@code GeneScore} with the highest combined score.
     */
    public static GeneScore max(GeneScore s1, GeneScore s2) {
        return (Double.compare(s1.getCombinedScore(), s2.getCombinedScore()) >= 0) ? s1 : s2;
    }

    /**
     * Compares two specified {@code GeneScore} objects. The natural ordering of these objects is the reverse numerical
     * ordering of their {@code combinedScore} or if this value is equal to 0, the natural order of their {@code GeneSymbol}.
     *
     * @param anotherGeneScore the other {@code GeneScore} against which this is compared
     * @return a value less than {@code 0} if this {@code combinedScore} is numerically greater than
     *          {@code anotherGeneScore.combinedScore}; and a value greater than {@code 0} if this {@code combinedScore} is numerically
     *          less than {@code anotherGeneScore.combinedScore}. Should {@code combinedScore} be numerically equal to
     *          {@code anotherGeneScore.combinedScore} the return value will be equivalent to the comparison of the {@code GeneIdentifier}.
     * @throws NullPointerException if an argument is null
     */
    public int compareTo(GeneScore anotherGeneScore) {
        return GeneScore.compare(this, anotherGeneScore);
    }

    /**
     * Compares two specified {@code GeneScore} objects. The natural ordering of these objects is the reverse numerical
     * ordering of their {@code combinedScore} or if this value is equal to 0, the natural order of their {@code GeneSymbol}.
     *
     * @param s1 the fist score to be compared.
     * @param s2 the second score to be compared.
     * @return  a value less than {@code 0} if this {@code s1.combinedScore} is numerically greater than
     *          {@code s2.combinedScore}; and a value greater than {@code 0} if {@code s1.combinedScore} is numerically
     *          less than {@code s2.combinedScore}. Should {@code s1.combinedScore} be numerically equal to
     *          {@code s2.combinedScore} the return value will be equivalent to the comparison of the {@code GeneIdentifier}.
     * @throws NullPointerException if an argument is null
     */
    public static int compare(GeneScore s1, GeneScore s2) {
        double s1CombinedScore = s1.getCombinedScore();
        double s2CombinedScore = s2.getCombinedScore();
        if (s1CombinedScore < s2CombinedScore) {
            return 1;
        }
        if (s1CombinedScore > s2CombinedScore) {
            return -1;
        }
        //if the scores are equal then return an alphabetised list based on gene symbol
        return GeneIdentifier.compare(s1.getGeneIdentifier(), s2.getGeneIdentifier());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneScore geneScore = (GeneScore) o;
        return Double.compare(geneScore.combinedScore, combinedScore) == 0 &&
                Double.compare(geneScore.phenotypeScore, phenotypeScore) == 0 &&
                Double.compare(geneScore.variantScore, variantScore) == 0 &&
                Objects.equals(geneIdentifier, geneScore.geneIdentifier) &&
                modeOfInheritance == geneScore.modeOfInheritance &&
                Objects.equals(contributingVariants, geneScore.contributingVariants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geneIdentifier, modeOfInheritance, combinedScore, phenotypeScore, variantScore, contributingVariants);
    }

    @Override
    public String toString() {
        return "GeneScore{" +
                "geneIdentifier=" + geneIdentifier +
                ", modeOfInheritance=" + modeOfInheritance +
                ", combinedScore=" + combinedScore +
                ", phenotypeScore=" + phenotypeScore +
                ", variantScore=" + variantScore +
                ", contributingVariants=" + contributingVariants +
                '}';
    }

    public static GeneScore empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private GeneIdentifier geneIdentifier = GeneIdentifier.builder().build();
        private ModeOfInheritance modeOfInheritance = ModeOfInheritance.ANY;
        private double combinedScore;
        private double phenotypeScore;
        private double variantScore;
        private List<VariantEvaluation> contributingVariants = List.of();
        private List<ModelPhenotypeMatch<Disease>> compatibleDiseaseMatches = List.of();

        public Builder geneIdentifier(GeneIdentifier geneIdentifier) {
            this.geneIdentifier = geneIdentifier;
            return this;
        }

        public Builder modeOfInheritance(ModeOfInheritance modeOfInheritance) {
            this.modeOfInheritance = modeOfInheritance;
            return this;
        }

        public Builder combinedScore(double combinedScore) {
            this.combinedScore = combinedScore;
            return this;
        }

        public Builder phenotypeScore(double phenotypeScore) {
            this.phenotypeScore = phenotypeScore;
            return this;
        }

        public Builder variantScore(double variantScore) {
            this.variantScore = variantScore;
            return this;
        }

        public Builder contributingVariants(List<VariantEvaluation> contributingVariants) {
            this.contributingVariants = contributingVariants;
            return this;
        }

        public Builder compatibleDiseaseMatches(List<ModelPhenotypeMatch<Disease>> compatibleDiseaseMatches) {
            this.compatibleDiseaseMatches = compatibleDiseaseMatches;
            return this;
        }

        public GeneScore build() {
            return new GeneScore(this);
        }
    }
}
