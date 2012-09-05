package mdfi.incompletitudeFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configuration {
	
	private double minimumHitRatio;
	private List<IncompletitudeFieldDescriptor> fDescriptors;
	String extractorConfigFile;
	
	public Configuration(String configFilePath) {
		super();
		XMLReader reader = new XMLReader(configFilePath);
		IncompletitudeFieldDescriptorBuilder builder = new IncompletitudeFieldDescriptorBuilder(reader);
		this.fDescriptors = builder.buildIncompletitudeFieldDescriptors();
		this.minimumHitRatio=reader.getMinimumHitRatio();
		this.extractorConfigFile=reader.getExtractorConfigFile();
	}


	public List<IncompletitudeFieldDescriptor> getfDescriptors() {
		
		List<IncompletitudeFieldDescriptor> fDescs = new ArrayList<IncompletitudeFieldDescriptor>();
		
		for (Iterator <IncompletitudeFieldDescriptor>iterator = fDescriptors.iterator(); iterator.hasNext();) {
			IncompletitudeFieldDescriptor incompletitudeFieldDescriptor = iterator.next();
			fDescs.add(incompletitudeFieldDescriptor.clone());
		}
		
		return fDescs;
	}


	public double getMinimumHitRadio() {
		return minimumHitRatio;
	}


	public String getExtractorFilePath() {
		return extractorConfigFile;
	}
	
	

}
