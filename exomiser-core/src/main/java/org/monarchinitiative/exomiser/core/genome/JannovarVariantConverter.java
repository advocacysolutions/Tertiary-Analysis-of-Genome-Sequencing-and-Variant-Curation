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

package org.monarchinitiative.exomiser.core.genome;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.*;
import org.monarchinitiative.svart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.charite.compbio.jannovar.reference.Strand.FWD;
import static de.charite.compbio.jannovar.reference.Strand.REV;
import static org.monarchinitiative.svart.Strand.POSITIVE;

public class JannovarVariantConverter {

    private static final Logger logger = LoggerFactory.getLogger(JannovarVariantConverter.class);

    private final ReferenceDictionary referenceDictionary;

    public JannovarVariantConverter(JannovarData jannovarData) {
        this.referenceDictionary = jannovarData.getRefDict();
    }

    public GenomeVariant toGenomeVariant(Variant variant) {
        if (variant.isSymbolic()) {
            throw new IllegalArgumentException("Cannot create GenomeVariant from symbolic variant " + variant);
        }
        GenomePosition genomePosition = startGenomePosition(variant);
        return new GenomeVariant(genomePosition, variant.ref(), variant.alt());
    }

    public SVGenomeVariant toSvGenomeVariant(Variant variant) {
        // n.b. it is possible to create a SVGenomeVariant from a precise variant, it need not be symbolic.
        GenomePosition start = startGenomePosition(variant);
        ConfidenceInterval startCi = variant.startPosition().confidenceInterval();
        int startCiLower = startCi.lowerBound();
        int startCiUpper = startCi.upperBound();

        // Breakend variants have a left and right Breakend - return the right one if this is a breakend, or the original variant if not.
        GenomicRegion endRegion = variantOrRightBreakend(variant);
        GenomePosition end = endGenomePosition(endRegion);
        ConfidenceInterval endCi = endRegion.endPosition().confidenceInterval();
        int endCiLower = endCi.lowerBound();
        int endCiUpper = endCi.upperBound();

        VariantType svSubType = variant.variantType().subType();
        switch (svSubType) {
            case DEL:
                return new SVDeletion(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
            case DEL_ME:
                return new SVMobileElementDeletion(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
            case DUP:
                return new SVDuplication(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
            case DUP_TANDEM:
                return new SVTandemDuplication(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
            case INS:
                return new SVInsertion(start, startCiLower, startCiUpper);
            case INS_ME:
                return new SVMobileElementInsertion(start, startCiLower, startCiUpper);
            case INV:
                return new SVInversion(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
            case CNV:
                return new SVCopyNumberVariant(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
            case BND:
                return new SVBreakend(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper, variant.ref(), variant.alt(), SVBreakend.Side.LEFT_END);
            default:
                return new SVUnknown(start, end, startCiLower, startCiUpper, endCiLower, endCiUpper);
        }
    }

    private GenomicRegion variantOrRightBreakend(Variant variant) {
        if (variant.isBreakend()) {
            BreakendVariant breakend = (BreakendVariant) variant;
            return breakend.right();
        }
        return variant;
    }

    private GenomePosition startGenomePosition(GenomicRegion genomicRegion) {
        int chr = jannovarContigId(genomicRegion.contig());
        return new GenomePosition(referenceDictionary, genomicRegion.strand() == POSITIVE ? FWD : REV, chr, genomicRegion.startWithCoordinateSystem(CoordinateSystem.LEFT_OPEN), PositionType.ZERO_BASED);
    }

    private GenomePosition endGenomePosition(GenomicRegion genomicRegion) {
        int chr = jannovarContigId(genomicRegion.contig());
        return new GenomePosition(referenceDictionary, genomicRegion.strand() == POSITIVE ? FWD : REV, chr, genomicRegion.endWithCoordinateSystem(CoordinateSystem.LEFT_OPEN), PositionType.ZERO_BASED);
    }

    private int jannovarContigId(Contig contig) {
        return referenceDictionary.getContigNameToID().getOrDefault(contig.name(), Contig.unknown().id());
    }
}
