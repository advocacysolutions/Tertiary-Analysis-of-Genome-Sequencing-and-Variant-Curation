/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2018 Queen Mary University of London.
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

package org.monarchinitiative.exomiser.core.genome.dao;

import org.h2.mvstore.MVStore;
import org.monarchinitiative.exomiser.core.proto.AlleleProto;

import java.util.Map;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
class AllelePropertiesDaoAdapterTest {

    AllelePropertiesDaoAdapter newInstanceWithData(Map<AlleleProto.AlleleKey, AlleleProto.AlleleProperties> value) {
        MVStore mvStore = MvAlleleStoreTestUtil.newMvStoreWithData(value);
        AllelePropertiesDaoMvStore allelePropertiesDao = new AllelePropertiesDaoMvStore(mvStore);
        return new AllelePropertiesDaoAdapter(allelePropertiesDao);
    }

}