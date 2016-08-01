package YaraParser.Learning;

import YaraParser.Structures.EmbeddingTypes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 7/29/16
 * Time: 2:34 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

public class NetworkMatrices implements Serializable {
    private double[][] wordEmbedding;
    private double[][] posEmbedding;
    private double[][] labelEmbedding;
    private double[][] hiddenLayer;
    private double[] hiddenLayerBias;
    private double[][] softmaxLayer;
    private double[] softmaxLayerBias;

    public NetworkMatrices(int wSize, int wDim, int pSize, int pDim, int lSize, int lDim, int hDim, int hIntDim, int softDim) {
        wordEmbedding = new double[wSize][wDim];
        posEmbedding = new double[pSize][pDim];
        labelEmbedding = new double[lSize][lDim];
        hiddenLayer = new double[hDim][hIntDim];

        hiddenLayerBias = new double[hDim];
        softmaxLayer = new double[softDim][hDim];
        softmaxLayerBias = new double[softDim];
    }

    public void resetToPretrainedWordEmbeddings(int i, double[] embeddings) {
        this.wordEmbedding[i] = embeddings;
    }

    public void modify(EmbeddingTypes t, int i, int j, double change) throws Exception {
        if (t.equals(EmbeddingTypes.WORD))
            wordEmbedding[i][j] += change;
        else if (t.equals(EmbeddingTypes.POS))
            posEmbedding[i][j] += change;
        else if (t.equals(EmbeddingTypes.DEPENDENCY))
            labelEmbedding[i][j] += change;
        else if (t.equals(EmbeddingTypes.HIDDENLAYER))
            hiddenLayer[i][j] += change;
        else if (t.equals(EmbeddingTypes.HIDDENLAYERBIAS)) {
            assert j == -1;
            hiddenLayerBias[i] += change;
        } else if (t.equals(EmbeddingTypes.SOFTMAX))
            softmaxLayer[i][j] += change;
        else if (t.equals(EmbeddingTypes.SOFTMAXBIAS)) {
            assert j == -1;
            softmaxLayerBias[i] += change;
        } else
            throw new Exception("Embedding type not supported");
    }


    public double[][] getWordEmbedding() {
        return wordEmbedding;
    }

    public double[][] getPosEmbedding() {
        return posEmbedding;
    }

    public double[][] getLabelEmbedding() {
        return labelEmbedding;
    }

    public double[][] getHiddenLayer() {
        return hiddenLayer;
    }

    public double[] getHiddenLayerBias() {
        return hiddenLayerBias;
    }

    public double[][] getSoftmaxLayer() {
        return softmaxLayer;
    }

    public double[] getSoftmaxLayerBias() {
        return softmaxLayerBias;
    }

    public ArrayList<double[][]> getAllMatrices() {
        ArrayList<double[][]> matrices = new ArrayList<>();
        matrices.add(wordEmbedding);
        matrices.add(posEmbedding);
        matrices.add(labelEmbedding);
        matrices.add(hiddenLayer);
        matrices.add(softmaxLayer);
        return matrices;
    }

    public ArrayList<double[]> getAllVectors() {
        ArrayList<double[]> vectors = new ArrayList<>();
        vectors.add(hiddenLayerBias);
        vectors.add(softmaxLayerBias);
        return vectors;
    }


    /**
     * Merges the values by summation and puts everything to the first layer
     *
     * @param matrices
     */
    public void mergeMatricesInPlace(NetworkMatrices matrices) {
        //todo check details

        ArrayList<double[][]> allMatrices = getAllMatrices();
        ArrayList<double[]> allVectors = getAllVectors();

        ArrayList<double[][]> allMatrices2 = matrices.getAllMatrices();
        ArrayList<double[]> allVectors2 = matrices.getAllVectors();

        for (int m = 0; m < allMatrices.size(); m++) {
            double[][] m1 = allMatrices.get(m);
            double[][] m2 = allMatrices2.get(m);

            for (int s1 = 0; s1 < m1.length; s1++) {
                for (int s2 = 0; s2 < m1[s1].length; s2++) {
                    m1[s1][s2] += m2[s1][s2];
                }
            }
        }

        for (int v = 0; v < allVectors.size(); v++) {
            double[] v1 = allVectors.get(v);
            double[] v2 = allVectors2.get(v);

            for (int s1 = 0; s1 < v1.length; s1++) {
                v1[s1] += v2[s1];
            }
        }
    }
}