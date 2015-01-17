/**
 Copyright 2014 Mikhail Shugay (mikhail.shugay@gmail.com)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.antigenomics.vdjtools.intersection

/**
 * An enum that defines the clonotype matching rule. 
 * Used both when collapsing "identical" clonotypes in a sample and when matching clonotypes between samples 
 */
public enum IntersectionType {

    /**
     * Intersection rule. Clonotypes match if their CDR3 nucleotide sequences match 
     */
    /*   */ Nucleotide("nt", false),
    /**
     * Intersection rule. Clonotypes match if both V segments and CDR3 nucleotide sequences match 
     */
            NucleotideV("ntV", false),
    /**
     * Intersection rule. Clonotypes match if both V, J segments and CDR3 nucleotide sequences match 
     */
            NucleotideVJ("ntVJ", false),
    /**
     * Intersection rule. Clonotypes match if their CDR3 amino acid sequences match  
     */
            AminoAcid("aa", true),
    /**
     * Intersection rule. Clonotypes match if both V segments and CDR3 amino acid sequences match 
     */
            AminoAcidV("aaV", true),
    /**
     * Intersection rule. Clonotypes match if both V, J segments and CDR3 amino acid sequences match 
     */
            AminoAcidVJ("aaVJ", true),
    /**
     * Intersection rule. Clonotypes match if their CDR3 amino acid sequences match, 
     * but their CDR3 nucleotide sequences do not match. Contamination-proof matching
     */
            AminoAcidNonNucleotide("aa!nt", true),
    /**
     * Intersection rule. Clonotypes match if both V, J segments, somatic hypermutations and CDR3 nucleotide sequences match
     */
            Strict("strict", false)

    final String shortName
    final boolean aminoAcid

    /**
     *
     * @param shortName acronym for clonotype match rule
     * @param aminoAcid if {@code true} matching relies on amino acid sequences; {@code false} for nucleotide sequences
     */
    IntersectionType(String shortName, boolean aminoAcid) {
        this.shortName = shortName
        this.aminoAcid = aminoAcid
    }

    static IntersectionType byName(String shortName) {
        values().find { it.shortName.toUpperCase() == shortName.toUpperCase() }
    }

    static String allowedNames = values().collect { it.shortName }.join(",")
}
