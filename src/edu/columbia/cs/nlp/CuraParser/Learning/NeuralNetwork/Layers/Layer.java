package edu.columbia.cs.nlp.CuraParser.Learning.NeuralNetwork.Layers;

import edu.columbia.cs.nlp.CuraParser.Accessories.Utils;
import edu.columbia.cs.nlp.CuraParser.Learning.Activation.Activation;
import edu.columbia.cs.nlp.CuraParser.Learning.NeuralNetwork.MLPNetwork;
import edu.columbia.cs.nlp.CuraParser.Learning.WeightInit.FixInit;
import edu.columbia.cs.nlp.CuraParser.Learning.WeightInit.Initializer;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 8/18/16
 * Time: 3:43 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */
public class Layer implements Serializable {
    protected Activation activation;

    protected double[][] w;
    protected double[] b;
    protected boolean useBias;

    public Layer(Activation activation, int nIn, int nOut, Initializer initializer) {
        this(activation, nIn, nOut, initializer, new FixInit(0));
    }

    public Layer(Activation activation, int nIn, int nOut, Initializer initializer, Initializer biasInitializer) {
        this(activation, nIn, nOut, initializer, biasInitializer, true);
    }

    public Layer(Activation activation, int nIn, int nOut, Initializer initializer, Initializer biasInitializer, boolean useBias) {
        this.activation = activation;
        this.useBias = useBias;
        w = new double[nOut][nIn];
        initializer.init(w);

        if (useBias) {
            b = new double[nOut];
            biasInitializer.init(b);
        }
    }

    public double[] forward(double[] i, HashSet<Integer> wIndexToUse, HashSet<Integer> inputToUse) {
        assert i.length == w[0].length;
        return Utils.sum(Utils.dot(w, i, wIndexToUse, inputToUse), b, wIndexToUse);
    }

    public double[] forward(double[] i, HashSet<Integer> wIndexToUse) {
        assert i.length == w[0].length;
        return Utils.sum(Utils.dot(w, i, wIndexToUse), b, wIndexToUse);
    }

    public double[] forward(double[] i) {
        assert i.length == w[0].length;
        return Utils.sum(Utils.dot(w, i), b);
    }

    public double[] activate(double[] i) {
        return activation.activate(i);
    }

    /**
     * Use it in the last layer
     *
     * @param i      input from the previous layer
     * @param labels <0 means not allowed, 1 means gold, other values>=0 is ok.
     * @return
     */
    public double[] forward(double[] i, double[] labels, boolean takeLog) {
        assert i.length == w[0].length;
        return activation.activate(Utils.sum4Output(Utils.dot4Output(w, i, labels), b, labels), labels, takeLog);
    }

    public double[] backward(final double[] delta, int layerIndex, double[] hInput, double[] prevH,
                             HashSet<Integer>[] seenFeatures, double[][][] savedGradients, MLPNetwork network) {
        assert layerIndex < network.numLayers() - 1;
        final double[][] nextW = network.layer(layerIndex + 1).getW();
        assert delta.length == nextW.length;
        assert nextW[0].length == w.length;
        assert hInput.length == w.length;
        assert prevH.length == w[0].length;

        double[] newDelta = activation.gradient(hInput, Utils.dotTranspose(nextW, delta));
        Utils.sumi(b, newDelta);
        Utils.sumi(w, Utils.dotTranspose(newDelta, prevH));

        return newDelta;
    }

    public void modifyW(int i, int j, double change) {
        w[i][j] += change;
    }

    public void modifyB(int i, double change) {
        if (b != null)
            b[i] += change;
    }

    public final double[][] getW() {
        return w;
    }

    public void setW(double[][] w) {
        this.w = w;
    }

    public final double[] getB() {
        return b;
    }

    public void setB(double[] b) {
        this.b = b;
    }

    public int nOut() {
        return w.length;
    }

    public int nIn() {
        return w[0].length;
    }

    public double w(int i, int j) {
        return w[i][j];
    }

    public double b(int i) {
        if (b == null) return 0;
        return b[i];
    }

    public Layer copy(boolean zeroOut, boolean deepCopy) {
        Layer layer = new Layer(activation, w[0].length, w.length, new FixInit(0), new FixInit(0), useBias);
        if (!zeroOut) {
            layer.setW(deepCopy ? Utils.clone(w) : w);
            layer.setB(deepCopy ? Utils.clone(b) : b);
        }
        return layer;
    }

    public void mergeInPlace(Layer anotherLayer) {
        Utils.sumi(w, anotherLayer.getW());
        Utils.sumi(b, anotherLayer.getB());
    }
}