package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configuration {
	
	private List<IncompletitudeFieldDescriptor> fDescriptors;

	public List<IncompletitudeFieldDescriptor> getfDescriptors() {
		
		List<IncompletitudeFieldDescriptor> fDescs = new ArrayList<IncompletitudeFieldDescriptor>();
		
		for (Iterator <IncompletitudeFieldDescriptor>iterator = fDescriptors.iterator(); iterator.hasNext();) {
			IncompletitudeFieldDescriptor incompletitudeFieldDescriptor = iterator.next();
			fDescs.add(incompletitudeFieldDescriptor.clone());
		}
		
		return fDescs;
	}
	
	

}
