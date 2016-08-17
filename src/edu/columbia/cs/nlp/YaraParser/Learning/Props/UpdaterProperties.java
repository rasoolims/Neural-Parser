package edu.columbia.cs.nlp.YaraParser.Learning.Props;

import edu.columbia.cs.nlp.YaraParser.Learning.Updater.Enums.SGDType;
import edu.columbia.cs.nlp.YaraParser.Learning.Updater.Enums.UpdaterType;

import java.io.Serializable;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 8/17/16
 * Time: 11:41 AM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class UpdaterProperties implements Serializable {
    public double momentum;
    public SGDType sgdType;
    public double learningRate;
    public UpdaterType updaterType;

    public UpdaterProperties() {
        updaterType = UpdaterType.ADAM;
        sgdType = SGDType.NESTEROV;
        // good for ADAM.
        learningRate = 0.001;
        momentum = 0.9;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("updater: " + updaterType + "\n");
        builder.append("learning rate: " + learningRate + "\n");

        if (updaterType == UpdaterType.SGD) {
            builder.append("momentum: " + momentum + "\n");
            builder.append("sgd type: " + sgdType + "\n");
        }
        return builder.toString();
    }
}