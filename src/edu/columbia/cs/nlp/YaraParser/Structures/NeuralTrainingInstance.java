package edu.columbia.cs.nlp.YaraParser.Structures;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 7/27/16
 * Time: 11:23 AM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class NeuralTrainingInstance {
    private final double[] features;
    private final double[] label;

    public NeuralTrainingInstance(double[] features, double[] label) {
        this.features = features;
        this.label = label;
    }

    public double[] getFeatures() {
        return features;
    }

    public double[] getLabel() {
        return label;
    }

    public int gold() {
        for (int i = 0; i < label.length; i++)
            if (label[i] == 1)
                return i;
        return -1;
    }
}
