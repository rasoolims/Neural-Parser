/**
 * Copyright 2014-2016, Mohammad Sadegh Rasooli
 * Parts of this code is extracted from the Yara parser.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Parser.Threading;

import edu.columbia.cs.nlp.CuraParser.Learning.NeuralNetwork.MLPNetwork;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Configuration.BeamElement;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Configuration.Configuration;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Configuration.GoldConfiguration;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Configuration.State;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Features.FeatureExtractor;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Parser.Enums.Actions;
import edu.columbia.cs.nlp.CuraParser.TransitionBasedSystem.Parser.Parsers.ShiftReduceParser;

import java.util.ArrayList;
import java.util.concurrent.Callable;


public class PartialTreeBeamScorerThread implements Callable<ArrayList<BeamElement>> {

    boolean isDecode;
    MLPNetwork network;
    Configuration configuration;
    GoldConfiguration goldConfiguration;
    ArrayList<Integer> dependencyRelations;
    int labelNullIndex;
    int b;
    ShiftReduceParser parser;

    public PartialTreeBeamScorerThread(boolean isDecode, MLPNetwork network, GoldConfiguration goldConfiguration,
                                       Configuration configuration, ArrayList<Integer> dependencyRelations, int b,
                                       int labelNullIndex, ShiftReduceParser parser) {
        this.isDecode = isDecode;
        this.network = network;
        this.configuration = configuration;
        this.goldConfiguration = goldConfiguration;
        this.dependencyRelations = dependencyRelations;
        this.b = b;
        this.labelNullIndex = labelNullIndex;
        this.parser = parser;
    }


    public ArrayList<BeamElement> call() throws Exception {
        ArrayList<BeamElement> elements = new ArrayList<BeamElement>(dependencyRelations.size() * 2 + 3);

        boolean isNonProjective = false;
        if (goldConfiguration.isNonprojective()) {
            isNonProjective = true;
        }

        State currentState = configuration.state;
        double prevScore = configuration.score;

        boolean canShift = parser.canDo(Actions.Shift, currentState);
        boolean canReduce = parser.canDo(Actions.Reduce, currentState);
        boolean canRightArc = parser.canDo(Actions.RightArc, currentState);
        boolean canLeftArc = parser.canDo(Actions.LeftArc, currentState);
        double[] labels = new double[network.getNumOutputs()];
        if (!canShift) labels[0] = -1;
        if (!canReduce) labels[1] = -1;
        if (!canRightArc)
            for (int i = 0; i < dependencyRelations.size(); i++)
                labels[2 + i] = -1;
        if (!canLeftArc)
            for (int i = 0; i < dependencyRelations.size(); i++)
                labels[dependencyRelations.size() + 2 + i] = -1;
        double[] features = FeatureExtractor.extractFeatures(configuration, labelNullIndex, parser);
        double[] scores = network.output(features, labels);

        if (canShift) {
            if (isNonProjective || goldConfiguration.actionCost(Actions.Shift, -1, currentState, parser) == 0) {
                double score = scores[0];
                double addedScore = score + prevScore;
                elements.add(new BeamElement(addedScore, b, 0, -1));
            }
        }
        if (canReduce) {
            if (isNonProjective || goldConfiguration.actionCost(Actions.Reduce, -1, currentState, parser) == 0) {
                double score = scores[1];
                double addedScore = score + prevScore;
                elements.add(new BeamElement(addedScore, b, 1, -1));
            }

        }

        if (canRightArc) {
            for (int dependency : dependencyRelations) {
                if (isNonProjective || goldConfiguration.actionCost(Actions.RightArc, dependency, currentState, parser) == 0) {
                    double score = scores[2 + dependency];
                    double addedScore = score + prevScore;
                    elements.add(new BeamElement(addedScore, b, 2, dependency));
                }
            }
        }
        if (canLeftArc) {
            for (int dependency : dependencyRelations) {
                if (isNonProjective || goldConfiguration.actionCost(Actions.LeftArc, dependency, currentState, parser) == 0) {
                    double score = scores[2 + dependencyRelations.size() + dependency];
                    double addedScore = score + prevScore;
                    elements.add(new BeamElement(addedScore, b, 3, dependency));

                }
            }
        }

        if (elements.size() == 0) {
            if (canShift) {
                double score = scores[0];
                double addedScore = score + prevScore;
                elements.add(new BeamElement(addedScore, b, 0, -1));
            }
            if (canReduce) {
                double score = scores[1];
                double addedScore = score + prevScore;
                elements.add(new BeamElement(addedScore, b, 1, -1));
            }

            if (canRightArc) {
                for (int dependency : dependencyRelations) {
                    double score = scores[2 + dependency];
                    double addedScore = score + prevScore;
                    elements.add(new BeamElement(addedScore, b, 2, dependency));
                }
            }
            if (canLeftArc) {
                for (int dependency : dependencyRelations) {
                    double score = scores[2 + dependencyRelations.size() + dependency];
                    double addedScore = score + prevScore;
                    elements.add(new BeamElement(addedScore, b, 3, dependency));
                }
            }
        }

        return elements;
    }
}