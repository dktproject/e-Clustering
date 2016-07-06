package de.dkt.eservices.eweka;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.eservices.eweka.modules.DataLoader;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;
import eu.freme.common.rest.BaseRestController;
//import junit.framework.Test;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

@RestController
public class EWekaServiceStandAlone extends BaseRestController{
    
	Logger logger = Logger.getLogger(EWekaServiceStandAlone.class);

	@Autowired
	EWekaService service;
		
	@RequestMapping(value = "/e-weka/testURL", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> testURL(
			@RequestParam(value = "preffix", required = false) String preffix,
			@RequestBody(required = false) String postBody) throws Exception {

	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.add("Content-Type", "text/plain");
	    ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller is working properly", responseHeaders, HttpStatus.OK);
	    return response;
	}
	
	@RequestMapping(value = "/e-clustering/generateClusters", method = {RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> documentClustering(
			HttpServletRequest request, 
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
            @RequestParam Map<String, String> allParams,

			@RequestParam(value = "option", required = false) String option,
			@RequestParam(value = "encodingG", required = false) String encoding,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "algorithm", required = false) String algorithm,
			@RequestBody(required = false) String postBody) throws Exception {
		
//		System.err.println(postBody);
		try {
	        MultipartFile file1 = null;
        	String text ="";
			if (request instanceof MultipartHttpServletRequest){
		           MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		           file1 = multipartRequest.getFile("inputFile");
		   		if(file1==null){
					logger.error("No file received in request");
					throw new BadRequestException("No file received in request");
				}
		        if (!file1.isEmpty()) {
	        		String fileContent = "";
		        	try {
		        		BufferedReader br = new BufferedReader(new InputStreamReader(file1.getInputStream(), "UTF-8"));
		        		String line = br.readLine();
		        		while(line!=null){
		        			fileContent += line+"\n";
		        			line = br.readLine();
		        		}
		        		br.close();
		        	} catch (Exception e) {
		        		logger.error("Fail at reading input file.");
		        		throw new BadRequestException("Fail at reading input file.");
		        	}
		        	text = fileContent;
		        } else {
		        	logger.error("The given file was empty.");
		        	throw new BadRequestException("The given file was empty.");
		        }
	        }
			else{
				if(input!=null){
					text = input;
				}
				else if(postBody!=null){
					text = postBody;
				}
				else{
					throw new BadRequestException("No input found: nor file, neither input, neither body content.");
				}
//				text = new String(bytes, "UTF-8");
			}
//	   		//File tmpFile = FileFactory.generateOrCreateFileInstance(tmpFolder + tmpFileName);
//	   		File tmpFile = File.createTempFile("temp", Long.toString(System.nanoTime())+".arff");
//	   		//System.out.println("DEBUG: "+tmpFile.getAbsolutePath());
//
//	   		
//        	try {
//        		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tmpFile));
//        		stream.write(bytes);
//        		stream.close();
//        	} catch (Exception e) {
//        		throw new BadRequestException("Fail at uploading the file.");
//        	}
//        	String path = tmpFile.getAbsolutePath();
            JSONObject outObject;
            outObject = service.generateClusters("content", text, algorithm, language);
            
            String result = outObject.toString(1);
//            if(option.equalsIgnoreCase("normal")){
//                outObject = service.generateClusters("content", text, algorithm, language);
//            	result = outObject.toString();
//            }
//            else if(option.equalsIgnoreCase("loopback")){
//            	result = text;
//            }
//            else if(option.equalsIgnoreCase("testDataLoader")){
////    			Instances isTrainingSet = DataLoader.loadDataFromString(text);
//    			InputStream is = null;
//    			if(encoding!=null){
//    				is = new ByteArrayInputStream( text.getBytes(encoding) );
//    			}
//    			else{
//    				is = new ByteArrayInputStream( text.getBytes() );
//    			}
//    			Instances data1 = DataSource.read(is);
//    			for (int j = 0; j < data1.numInstances(); j++) {
//    				result += "label: "+data1.attribute(j).name()+"\n";
//				}
//            }
//            else if(option.equalsIgnoreCase("testDataLoader2")){
////    			Instances isTrainingSet = DataLoader.loadDataFromString(text);
//    			InputStream is = null;
//    			BufferedReader br = null;
//    			if(encoding!=null){
//    				is = new ByteArrayInputStream( text.getBytes(encoding) );
//    				br = new BufferedReader(new InputStreamReader(is,encoding));
//    			}
//    			else{
//    				is = new ByteArrayInputStream( text.getBytes() );
//    				br = new BufferedReader(new InputStreamReader(is));
//    			}
//    			Instances data1 = new Instances(br);
//    			for (int j = 0; j < data1.numInstances(); j++) {
//    				result += "label: "+data1.attribute(j).name()+"\n";
//				}
//            }
//            else if(option.equalsIgnoreCase("inputstream")){
//    			InputStream is = null;
//    			if(encoding!=null){
//    				is = new ByteArrayInputStream( text.getBytes(encoding) );
//    			}
//    			else{
//    				is = new ByteArrayInputStream( text.getBytes() );
//    			}
//    			byte[] bb = new byte[50000];
//    			is.read(bb, 0, 50000);
//    			result = new String(bb);
//            }
//            else if(option.equalsIgnoreCase("inputstream2")){
//    			InputStream is = null;
//    			BufferedReader br = null;
//    			if(encoding!=null){
//    				is = new ByteArrayInputStream( text.getBytes(encoding) );
//    				br = new BufferedReader(new InputStreamReader(is,encoding));
//    			}
//    			else{
//    				is = new ByteArrayInputStream( text.getBytes() );
//    				br = new BufferedReader(new InputStreamReader(is));
//    			}
//    			String line = br.readLine();
//    			while(line!=null){
//    				result += line + "\n";
//    				line = br.readLine();
//    			}
//            }
            
            HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
			ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
			return response;
		} catch (BadRequestException e) {
			logger.error("EXCEPTION: "+e.getMessage());
			throw e;
		} catch (ExternalServiceFailedException e) {
			logger.error("EXCEPTION: "+e.getMessage());
			throw e;
		}
	}
	
//	@RequestMapping(value = "/e-weka/trainModel", method = {RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> trainModel(
//			HttpServletRequest request,
//			@RequestParam(value = "trainDataFile", required = false) String trainDataFile,
//			@RequestParam(value = "language", required = false) String language,
//			@RequestParam(value = "modelName", required = false) String modelName,
//			@RequestParam(value = "classIndex", required = false) int classIndex,
//			@RequestParam(value = "clacluType", required = false) String clacluType,
//			@RequestParam(value = "modelPath", required = false) String modelPath,
//			@RequestParam(value = "analysis", required = false) String analysis,
//			@RequestBody(required = false) String postBody) throws Exception {
//		try {
////			ParameterChecker.checkNotNullOrEmpty(trainDataFile, "trainDataFile");
//			ParameterChecker.checkInList(analysis, "topicmodelling;classification", "analysis", logger);
//			ParameterChecker.checkNotNullOrEmpty(modelName, "modelName", logger);
//			ParameterChecker.checkNotNullOrEmpty(language, "language", logger);
//			
//	        MultipartFile file = null;//= multipartRequest.getFile("file");
//			if (request instanceof MultipartHttpServletRequest){
//		           MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		           file = multipartRequest.getFile("trainingFile");
//		   		if(file==null){
//					logger.error("No file received in request");
//					throw new BadRequestException("No file received in request");
//				}
//		        if (!file.isEmpty()) {
//			        trainDataFile="tmpFiles" + File.separator+file.getOriginalFilename();
//	        		File tmpFile;
//		        	byte[] bytes;
//		        	try {
//		        		tmpFile = FileFactory.generateOrCreateFileInstance(trainDataFile);
//		        	} catch (Exception e) {
////		        		e.printStackTrace();
//		        		logger.error("Fail at generating temporary file.");
//		        		throw new BadRequestException("Fail at generating temporary file.");
//		        	}
//		        	try {
//		        		bytes = file.getBytes();
//		        	} catch (Exception e) {
//		        		logger.error("Fail at reading input file.");
//		        		throw new BadRequestException("Fail at reading input file.");
//		        	}
//		        	try {
//		        	    FileOutputStream fos = new FileOutputStream(tmpFile); 
//		        	    fos.write(bytes);
//		        	    fos.close();
//		        	} catch (Exception e) {
//		        		logger.error("Fail at writting temporary file.");
//		        		throw new BadRequestException("Fail at writting temporary file.");
//		        	}
//		        } else {
//		        	logger.error("The given file was empty.");
//		        	throw new BadRequestException("The given file was empty.");
//		        }
//	        }
//			else{
//	        	logger.error("No training file provided: HTTPREQUEST is no multipart.");
//	        	throw new BadRequestException("No training file provided: HTTPREQUEST is no multipart.");
//			}
//            
//			ParameterChecker.checkNotNullOrEmpty(trainDataFile, "trainingDataFile", logger);
//			String outString;
//			if(analysis.equalsIgnoreCase("clustering")){
//				outString = service.generateClusters(trainDataFile, classIndex, clacluType, modelPath, modelName, language);
//			}
//			else if(analysis.equalsIgnoreCase("classification")){
//				outString = service.trainClassification(trainDataFile, classIndex, clacluType, modelPath, modelName, language);
//			}
//			else{
//				logger.error("The input analysis ["+analysis+"] is not supported. Only topicmodelling/classification are available.");
//				throw new BadRequestException("The input analysis ["+analysis+"] is not supported. Only topicmodelling/classification are available.");
//			}
//            HttpHeaders responseHeaders = new HttpHeaders();
//            responseHeaders.add("Content-Type", "text/plain");
//    		ResponseEntity<String> response = new ResponseEntity<String>(outString, responseHeaders, HttpStatus.OK);
//    		return response;
//		} catch (BadRequestException e) {
//			logger.error(e.getMessage());
//			throw e;
//		} catch (ExternalServiceFailedException e) {
//			logger.error(e.getMessage());
//			throw e;
//		}
//	}
	
//	@RequestMapping(value = "/e-classification/models", method = { RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> documentclassificationModels(
//			@RequestBody(required = false) String postBody) throws Exception {
//
//		File f = FileFactory.generateOrCreateDirectoryInstance("trainedModels" + File.separator + "documentClassification" + File.separator);
//		if(f.isDirectory()){
//			
//			String sFiles = "";
//			File [] files = f.listFiles();
//			for (File ff: files) {
//				sFiles += ff.getName().replaceAll("\\.EXT$", "").substring(ff.getName().indexOf('-')+1) + "\n";
//				
//			}
//			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", "text/plain");
//			ResponseEntity<String> response = new ResponseEntity<String>(sFiles, responseHeaders, HttpStatus.OK);
//			return response;
//		}
//		return null;
//	}
//
//	@RequestMapping(value = "/e-clustering/models", method = { RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> topicModellingModels(
//			@RequestBody(required = false) String postBody) throws Exception {
//		File f = FileFactory.generateOrCreateDirectoryInstance("trainedModels" + File.separator + "topicModelling" + File.separator);
//		if(f.isDirectory()){
//			String sFiles = "";
//			File [] files = f.listFiles();
//			for (File ff: files) {
//				if(!ff.getName().contains("Alphabet")){
//					sFiles += ff.getName().replaceAll("_TOPIC\\.EXT$", "").substring(ff.getName().indexOf('-')+1) + "\n";
//				}
//				
//			}
//			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", "text/plain");
//			ResponseEntity<String> response = new ResponseEntity<String>(sFiles, responseHeaders, HttpStatus.OK);
//			return response;
//		}
//		return null;
//	}
//	
//	@RequestMapping(value = "/e-clustering", method = {RequestMethod.POST, RequestMethod.GET })
//	public ResponseEntity<String> topicModelling(
//			@RequestParam(value = "input", required = false) String input,
//			@RequestParam(value = "i", required = false) String i,
//			@RequestParam(value = "informat", required = false) String informat,
//			@RequestParam(value = "f", required = false) String f,
//			@RequestParam(value = "outformat", required = false) String outformat,
//			@RequestParam(value = "o", required = false) String o,
//			@RequestParam(value = "prefix", required = false) String prefix,
//			@RequestParam(value = "p", required = false) String p,
//			@RequestHeader(value = "Accept", required = false) String acceptHeader,
//			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
//            @RequestParam Map<String, String> allParams,
//
//			@RequestParam(value = "language", required = false) String language,
//			@RequestParam(value = "modelName", required = false) String modelName,
//			@RequestParam(value = "modelPath", required = false) String modelPath,
//			@RequestBody(required = false) String postBody) throws Exception {
//		try {
//			ParameterChecker.checkNotNullOrEmpty(modelName, "modelName",logger);
//			ParameterChecker.checkNotNullOrEmpty(language, "language",logger);
//
//			NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
//            String textForProcessing = null;
//
//            if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
//                textForProcessing = nifParameters.getInput();
//    			//rdfConversionService.plaintextToRDF(inModel, textForProcessing,language, nifParameters.getPrefix());
//            } else {
//            	
//            	if(nifParameters.getInput()!=null){
//                    textForProcessing = nifParameters.getInput();
//            	}
//            	else if(postBody!=null){
//                    textForProcessing = postBody;
//            	}
//                if (textForProcessing == null) {
//                    logger.error("BADREQUEST: No text to process.");
//                    throw new BadRequestException("No text to process.");
//                }
//            }
//            Model outModel;
//			outModel = service.analyzeText(textForProcessing, "topicmodelling", modelPath, modelName, language, informat, outformat);
//			return createSuccessResponse(outModel, nifParameters.getOutformat());
//		} catch (BadRequestException e) {
//			logger.error("EXCEPTION: "+e.getMessage());
//			throw e;
//		} catch (ExternalServiceFailedException e) {
//			logger.error("EXCEPTION: "+e.getMessage());
//			throw e;
//		}
//	}

}
