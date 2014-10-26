/**
 * Copyright 2014 Mikhail Shugay (mikhail.shugay@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antigenomics.vdjtools.join;

import com.antigenomics.vdjtools.Clonotype;

import java.util.LinkedList;
import java.util.List;

public class JointClonotype implements Comparable<JointClonotype> {
    private final JointSample parent;
    private final List[] variantsBySample;
    private final int[] counts;
    private static final double JITTER = 1e-9; // upper limit on current precision of RepSeq
    private int peak = -1;
    private Clonotype representative = null;
    private double meanFreq = -1;

    public JointClonotype(JointSample parent) {
        this.parent = parent;
        this.variantsBySample = new List[parent.getNumberOfSamples()];
        this.counts = new int[parent.getNumberOfSamples()];
    }

    void addVariant(Clonotype variant, int sampleIndex) {
        List<Clonotype> variants = variantsBySample[sampleIndex];
        if (variants == null)
            variantsBySample[sampleIndex] = (variants = new LinkedList());
        variants.add(variant);
        counts[sampleIndex] += variant.getCount();
    }

    public JointSample getParent() {
        return parent;
    }

    public int getPeak() {
        if (peak < 0) {
            int peakCount = -1;
            for (int i = 0; i < counts.length; i++) {
                if (counts[i] > peakCount) {
                    peak = i;
                    peakCount = counts[i];
                }
            }
        }
        return peak;
    }

    public Clonotype getRepresentative() {
        if (representative == null) {
            int max = 0;
            for (Object o : variantsBySample[getPeak()]) {
                Clonotype clonotype = (Clonotype) o;
                if (clonotype.getCount() > max) {
                    max = clonotype.getCount();
                    representative = clonotype;
                }
            }
        }
        return representative;
    }

    public int getNumberOfVariants(int sampleIndex) {
        return variantsBySample[sampleIndex].size();
    }

    public int getCount(int sampleIndex) {
        return counts[sampleIndex];
    }

    /**
     * Gets clonotype frequency in a given sample
     *
     * @param sampleIndex
     * @return
     */
    public double getFreq(int sampleIndex) {
        return getCount(sampleIndex) / (double) parent.getSample(sampleIndex).getCount();
    }

    /**
     * Gets clonotype frequency at a given sample relative to intersection size of this sample
     *
     * @param sampleIndex
     * @return
     */
    public double getFreqWithinIntersection(int sampleIndex) {
        return getFreq(sampleIndex) / parent.getIntersectionFreq(sampleIndex);
    }

    /**
     * Gets a representative (geometric mean) frequency of a given joint clonotype
     *
     * @return
     */
    double getGeomeanFreq() {
        if (meanFreq < 0) {
            meanFreq = 1;
            for (int i = 0; i < parent.getNumberOfSamples(); i++) {
                meanFreq *= (getFreq(i) + JITTER);
            }
            meanFreq = Math.pow(meanFreq, 1.0 / (double) parent.getNumberOfSamples());
        }
        return meanFreq;
    }

    /**
     * Gets a representative (geometric mean) frequency of a given joint clonotype.
     * The frequency is normalized so that the total mean frequency will sum to 1.0 in JointSample,
     * thus allowing to use it during sample output
     *
     * @return
     */
    public double getFreq() {
        return getGeomeanFreq() / parent.getTotalMeanFreq();
    }

    /**
     * Gets a representative count of a given joint clonotype, which is proportional to representative
     * (geometric mean) frequency. The count is scaled so it is equal to 1 for the smallest JointClonotype,
     * thus allowing to use it during sample output
     *
     * @return
     */
    public int getCount() {
        return (int) (getFreq() / parent.getMinMeanFreq());
    }

    public boolean present(int sampleIndex) {
        return counts[sampleIndex] > 0;
    }

    @Override
    public int compareTo(JointClonotype o) {
        return -Double.compare(this.getGeomeanFreq(), o.getGeomeanFreq());
    }
}
