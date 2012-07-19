package mbfi.textPreProcessor.files;
import java.util.ArrayList;
import java.util.List;

import mbfi.textPreProcessor.extractor.RegExpDescritor;


public class FileSource {

	private String inputFilePath;
	private List <FileInformation> files;
	private List <RegExpDescritor> regExp;
	private String outputFilepath;
	
	public FileSource(String inputFilePath, List<FileInformation> files,
			String outputFilepath, List <RegExpDescritor> regExp) {
		super();
		this.inputFilePath = inputFilePath;
		this.files = files;
		this.outputFilepath = outputFilepath;
		this.regExp = regExp;
	}

	public List <RegExpDescritor> getRegExp() {
		return new ArrayList<RegExpDescritor>(regExp);
	}

	public String getOutputFilepath() {
		return outputFilepath;
	}
	
	public String getInputFilepath() {
		return inputFilePath;
	}

	public List<FileInformation> getFiles() {
		return files;
	}
	
}
