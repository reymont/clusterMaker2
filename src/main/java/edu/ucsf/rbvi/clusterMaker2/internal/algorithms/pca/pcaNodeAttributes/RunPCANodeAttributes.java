/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsf.rbvi.clusterMaker2.internal.algorithms.pca.pcaNodeAttributes;

import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.attributeClusterers.Matrix;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.pca.ComputationMatrix;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.pca.ResultPanelPCA;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.pca.ScatterPlotPCA;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;

/**
 *
 * @author root
 */
public class RunPCANodeAttributes {    
    private final CyNetwork network;
    private final CyNetworkView networkView;
    private final PCANodeAttributesContext context;
    private final TaskMonitor monitor;
    private final String[] weightAttributes;
    
    public RunPCANodeAttributes(CyNetwork network, CyNetworkView networkView, PCANodeAttributesContext context, TaskMonitor monitor, String[] weightAttributes){
        this.network = network;
        this.networkView = networkView;
        this.context = context;
        this.monitor = monitor;
        this.weightAttributes = weightAttributes;
    }
    
    public void computePCA(){
        Matrix matrix = new Matrix(network, weightAttributes, false, context.ignoreMissing, context.selectedOnly);
        double[][] matrixArray = matrix.toArray();
        ComputationMatrix mat = new ComputationMatrix(matrixArray);

        ComputationMatrix[] components = this.computePCsSorted(mat);

        if(context.pcaResultPanel)
            ResultPanelPCA.createAndShowGui(components, matrix.getNodes(), network, networkView, mat.computeVariance());
        
        if(context.pcaPlot)
            ScatterPlotPCA.createAndShowGui(components, mat.computeVariance());
    }

    public ComputationMatrix[] computePCs(ComputationMatrix matrix){
        ComputationMatrix mat = matrix.centralizeColumns();

        ComputationMatrix C = mat.covariance();

        double[] values = C.eigenValues();
        double[][] vectors = C.eigenVectors();

        ComputationMatrix[] components = new ComputationMatrix[values.length];
        double sum=0;
        for(int j=values.length-1, k=0;j>=0;j--,k++){
            sum += values[j];
            double[] w = new double[vectors.length];
            for(int i=0;i<vectors.length;i++){
                w[i] = vectors[i][j];
            }

            components[k] = ComputationMatrix.multiplyMatrixWithArray(mat, w);

            System.out.println("PC: " + k);
            components[k].printMatrix();
        }
        return components;
    }
    
    public ComputationMatrix[] computePCsSorted(ComputationMatrix matrix){

        ComputationMatrix mat = matrix.centralizeColumns();

        ComputationMatrix C = mat.covariance();

        double[] values = C.eigenValues();
        double[][] vectors = C.eigenVectors();

        double max = Double.MAX_VALUE;

        ComputationMatrix[] components = new ComputationMatrix[values.length];
        for(int j=0;j<values.length;j++){
            double value = values[0];
            int pos = 0;
            for(int i=0; i<values.length; i++){
                if(values[i] >= value && values[i] < max){
                    value = values[i];
                    pos = i;
                }
            }
            double[] w = new double[vectors.length];
            for(int i=0;i<vectors.length;i++){
                w[i] = vectors[i][pos];
            }

            components[j] = ComputationMatrix.multiplyMatrixWithArray(mat, w);
            max = value;
        }

        return components;
    }
}