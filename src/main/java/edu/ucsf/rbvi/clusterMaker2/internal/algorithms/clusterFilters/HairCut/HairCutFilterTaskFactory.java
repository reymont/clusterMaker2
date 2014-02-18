package edu.ucsf.rbvi.clusterMaker2.internal.algorithms.clusterFilters.HairCut;

import java.util.Collections;
import java.util.List;

//Cytoscape imports
import org.cytoscape.work.TaskIterator;


import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterManager;
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterTaskFactory;
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterTaskFactory.ClusterType;
import edu.ucsf.rbvi.clusterMaker2.internal.api.ClusterViz;

public class HairCutFilterTaskFactory implements ClusterTaskFactory   {
	ClusterManager clusterManager;
	HairCutContext context = null;
	
	public HairCutFilterTaskFactory(ClusterManager clusterManager) {
		context = new HairCutContext();
		this.clusterManager = clusterManager;
	}
	
	public String getShortName() {return HairCutFilter.SHORTNAME;};
	public String getName() {return HairCutFilter.NAME;};

	public ClusterViz getVisualizer() {
		// return new NewNetworkView(true);
		return null;
	}

	public boolean isReady() {
		return true;
	}

	public boolean isAvailable() {
		return false;
	}

	public List<ClusterType> getTypeList() {
		return Collections.singletonList(ClusterType.FILTER); 
	}

	public TaskIterator createTaskIterator() {
		// Not sure why we need to do this, but it looks like
		// the tunable stuff "remembers" objects that it's already
		// processed this tunable.  So, we use a copy constructor
		return new TaskIterator(new HairCutFilter(context, clusterManager));
	}
	
}
	
	


