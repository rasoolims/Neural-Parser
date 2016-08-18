package edu.columbia.cs.nlp.YaraParser.Learning.WeightInit;

import java.util.Random;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 8/18/16
 * Time: 3:50 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class FixInit extends Initializer {
    double initVal;

    public FixInit(Random random, int nIn, int nOut, double initVal) {
        super(random, nIn, nOut);
        this.initVal = initVal;
    }

    @Override
    public double next() {
        return initVal;
    }
}
