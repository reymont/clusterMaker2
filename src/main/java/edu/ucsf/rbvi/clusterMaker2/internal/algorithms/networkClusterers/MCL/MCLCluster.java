package edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.MCL;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;


import org.cytoscape.model.CyColumn;
//Cytoscape imports
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyRow;
//import cytoscape.Cytoscape;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableHandler;
import org.cytoscape.work.TaskMonitor;


import edu.ucsf.rbvi.clusterMaker2.ClusterResults;
import edu.ucsf.rbvi.clusterMaker2.ClusterAlgorithm;
import edu.ucsf.rbvi.clusterMaker2.ClusterViz;

import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.networkClusterers.AbstractNetworkClusterer;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.AbstractClusterResults;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.DistanceMatrix;//import clusterMaker.algorithms.DistanceMatrix;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.NodeCluster;//import clusterMaker.algorithms.NodeCluster;
import edu.ucsf.rbvi.clusterMaker2.internal.algorithms.edgeConverters.EdgeAttributeHandler;
// import clusterMaker.ui.ClusterViz;
// import clusterMaker.ui.NewNetworkView;

public class MCLCluster extends AbstractNetworkClusterer   {

	
	RunMCL runMCL = null;
	
	@ContainsTunables
	public MCLContext context = null;
	
	public MCLCluster() {
		super();
	}
	
	public String getShortName() {return "mcl";};
	public String getName() {return "MCL cluster";};

	public ClusterViz getVisualizer() {
		// return new NewNetworkView(true);
		return null;
	}

	// TODO: all of our tunables need to be split
	// and and put into a separate context object
	public Object getContext() {
		if (context == null)
			context = new MCLContext();
		return context;
	}
	
	public void doCluster(CyNetwork network, TaskMonitor monitor) {
		this.monitor = monitor;
		
		DistanceMatrix matrix = context.edgeAttributeHandler.getMatrix();
		if (matrix == null) {
			monitor.showMessage(TaskMonitor.Level.ERROR,"Can't get distance matrix: no attribute value?");
			return;
		}

		if (canceled) return;

		//Cluster the nodes
		runMCL = new RunMCL(matrix, context.inflation_parameter, context.iterations, 
		                    context.clusteringThresh, context.maxResidual, context.maxThreads, monitor);

		runMCL.setDebug(debug);

		if (canceled) return;

		monitor.showMessage(TaskMonitor.Level.INFO,"Clustering...");

		// results = runMCL.run(monitor);
		List<NodeCluster> clusters = runMCL.run(network, monitor);
		if (clusters == null) return; // Canceled?

		monitor.showMessage(TaskMonitor.Level.INFO,"Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(network);

		monitor.showMessage(TaskMonitor.Level.INFO,"Creating groups");

		params = new ArrayList<String>();
		context.edgeAttributeHandler.setParams(params);

		List<List<CyNode>> nodeClusters = createGroups(network, clusters);

		results = new AbstractClusterResults(network, nodeClusters);
		monitor.showMessage(TaskMonitor.Level.INFO, "Done.  MCL results:\n"+results);

	}

	public void halt() {
		canceled = true;
		if (runMCL != null)
			runMCL.halt();
	}
}
	
	



