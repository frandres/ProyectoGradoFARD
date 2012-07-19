package mbfi.textPreProcessor.files;
public class FileInformation implements Comparable<Object>{

	private String inputFilename;
	private String outputFilename;
	
	public FileInformation(String inputFilename, String outputFilename) {
		super();
		this.inputFilename = inputFilename;
		this.outputFilename = outputFilename;
	}
	
	public String getInputFilename() {
		return inputFilename;
	}
	public String getOutputFilename() {
		return outputFilename;
	}

	@Override
	public int compareTo(Object o) {
		if(!(o instanceof FileInformation)){
			return -1;
		}
		FileInformation fileInfo2 = (FileInformation) o;
		return getOutputFilename().compareTo(fileInfo2.getOutputFilename());
		
	}
	
	
}
