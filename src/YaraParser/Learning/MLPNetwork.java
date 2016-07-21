package YaraParser.Learning;

/**
 * Created by Mohammad Sadegh Rasooli.
 * ML-NLP Lab, Department of Computer Science, Columbia University
 * Date Created: 7/21/16
 * Time: 3:39 PM
 * To report any bugs or problems contact rasooli@cs.columbia.edu
 */

import YaraParser.Accessories.Options;
import YaraParser.Structures.IndexMaps;
import YaraParser.Structures.NNInfStruct;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;

/**
 * Manual MLP model
 */
public class MLPNetwork {
    double[][] wordEmbeddings;
    double[][] posEmbeddings;
    double[][] labelEmbeddings;

    double[][] hiddenLayer;
    double[] hiddenLayerBias;

    double[][] softmaxLayer;
    double[] softmaxLayerBias;

    final public IndexMaps maps;
    final public Options options;
    final public ArrayList<Integer> dependencyLabels;

    public MLPNetwork(final NNInfStruct nnInfStruct) {

        ComputationGraph net = nnInfStruct.net;
        dependencyLabels = nnInfStruct.dependencyLabels;
        options = nnInfStruct.options;
        maps = nnInfStruct.maps;

        INDArray wEArr = net.getLayer(0).getParam("W");
        wordEmbeddings = new double[wEArr.rows()][wEArr.columns()];
        for (int i = 0; i < wordEmbeddings.length; i++) {
            for (int j = 0; j < wordEmbeddings[i].length; j++)
                wordEmbeddings[i][j] = wEArr.getRow(i).getColumn(j).getDouble(0);
        }

        INDArray pEArr = net.getLayer(20).getParam("W");
        posEmbeddings = new double[pEArr.rows()][pEArr.columns()];
        for (int i = 0; i < posEmbeddings.length; i++) {
            for (int j = 0; j < posEmbeddings[i].length; j++)
                posEmbeddings[i][j] = pEArr.getRow(i).getColumn(j).getDouble(0);
        }

        INDArray lEArr = net.getLayer(39).getParam("W");
        labelEmbeddings = new double[lEArr.rows()][lEArr.columns()];
        for (int i = 0; i < labelEmbeddings.length; i++) {
            for (int j = 0; j < labelEmbeddings[i].length; j++)
                labelEmbeddings[i][j] = lEArr.getRow(i).getColumn(j).getDouble(0);
        }

        INDArray hW = net.getLayer(49).getParam("W");
        hiddenLayer = new double[hW.rows()][hW.columns()];
        for (int i = 0; i < hiddenLayer.length; i++) {
            for (int j = 0; j < hiddenLayer[i].length; j++)
                hiddenLayer[i][j] = hW.getRow(i).getColumn(j).getDouble(0);
        }

        INDArray hB = net.getLayer(49).getParam("b");
        hiddenLayerBias = new double[hB.columns()];
        for (int i = 0; i < hiddenLayerBias.length; i++) {
            hiddenLayerBias[i] = hB.getColumn(i).getDouble(0);
        }

        INDArray sW = net.getLayer(50).getParam("W");
        softmaxLayer = new double[sW.rows()][sW.columns()];
        for (int i = 0; i < softmaxLayer.length; i++) {
            for (int j = 0; j < softmaxLayer[i].length; j++)
                softmaxLayer[i][j] = sW.getRow(i).getColumn(j).getDouble(0);
        }

        INDArray sB = net.getLayer(50).getParam("b");
        softmaxLayerBias = new double[sB.columns()];
        for (int i = 0; i < softmaxLayerBias.length; i++) {
            softmaxLayerBias[i] = sB.getColumn(i).getDouble(0);
        }
    }

    public double[] output(final int[] feats) {
        double[] hidden = new double[hiddenLayer[0].length];

        int offset = 0;
        for (int j = 0; j < feats.length; j++) {
            int tok = feats[j];
            double[][] embedding = null;
            if (j < 19)
                embedding = wordEmbeddings;
            else if (j < 38)
                embedding = posEmbeddings;
            else embedding = labelEmbeddings;

            for (int i = 0; i < hidden.length; i++) {
                for (int k = 0; k < embedding[0].length; k++) {
                    hidden[i] += hiddenLayer[offset + k][i] * embedding[tok][k];
                }
            }
            offset += embedding[0].length;
        }

        for (int i = 0; i < hidden.length; i++) {
            hidden[i] += hiddenLayerBias[i];
            //relu
            hidden[i] = Math.max(0, hidden[i]);
        }

        double[] probs = new double[softmaxLayerBias.length];
        double sum = 0;
        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < hidden.length; j++) {
                probs[i] += softmaxLayer[j][i] * hidden[j];
            }
            probs[i] += softmaxLayerBias[i];
            probs[i] = Math.exp(probs[i]);
            sum += probs[i];
        }

        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
            probs[i] = Math.log(probs[i]);
        }
        return probs;
    }
}
