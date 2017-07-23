package com.noteanalyzer.mvc.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.noteanalyzer.mvc.model.CityModel;
import com.noteanalyzer.mvc.model.NoteDetailModel;
import com.noteanalyzer.mvc.model.NoteInputFormModel;
import com.noteanalyzer.mvc.model.NoteSummaryModel;
import com.noteanalyzer.mvc.model.NoteTypeModel;
import com.noteanalyzer.mvc.model.PropertyTypeModel;
import com.noteanalyzer.mvc.model.StateModel;
import com.noteanalyzer.mvc.service.NoteService;
import com.noteanalyzer.utility.NoteUtility;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

@RestController
public class NoteRestController {

	@Autowired
	NoteService noteService;

	/**
	 * @return the noteService
	 */
	public NoteService getNoteService() {
		return noteService;
	}

	/**
	 * @param noteService
	 *            the noteService to set
	 */
	public void setNoteService(NoteService noteService) {
		this.noteService = noteService;
	}

	@RequestMapping(value = "/analyzeNote", method = RequestMethod.POST)
	public ResponseEntity<NoteInputFormModel> analyzeNote(@RequestBody NoteInputFormModel noteInputFormModel)  {
		String zipCode = noteInputFormModel.getAddress().getZipCode();
		if (StringUtils.isEmpty(zipCode)) {
			return new ResponseEntity<NoteInputFormModel>(HttpStatus.NOT_FOUND);
		}


		Optional<List<NoteTypeModel>> noteTypeModelList = noteService.getNoteType();
		if (noteTypeModelList.isPresent()) {
			noteInputFormModel.setNoteTypeList(noteTypeModelList.get());
		}
		
		Optional<List<PropertyTypeModel>> propTypeModelList = noteService.getPropertyType();
		if (propTypeModelList.isPresent()) {
			noteInputFormModel.setPropTypeList(propTypeModelList.get());
		}
		
		return new ResponseEntity<NoteInputFormModel>(noteInputFormModel, HttpStatus.OK);

	}

	@RequestMapping(value = "/api/createNote", method = RequestMethod.POST)
	public ResponseEntity<String> createNote(@RequestBody NoteInputFormModel noteInputFormModel) {

		System.out.println("Inside POST with ALL value " + noteInputFormModel);
		try {
			noteService.createNote(noteInputFormModel);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/templateDownload", method = RequestMethod.GET)
	public void downloadFile(HttpServletResponse response) throws IOException {

		File file = null;

		// if(type.equalsIgnoreCase("internal")){
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		file = new File(classloader.getResource("noteTemplate.csv").getFile());
		// }else{
		// file = new File(EXTERNAL_FILE_PATH);
		// }

		/*
		 * if(!file.exists()){ String errorMessage =
		 * "Sorry. The file you are looking for does not exist";
		 * System.out.println(errorMessage); OutputStream outputStream =
		 * response.getOutputStream();
		 * outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		 * outputStream.close(); return; }
		 */
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			System.out.println("mimetype is not detectable, will take default");
			mimeType = "application/octet-stream";
		}

		System.out.println("mimetype : " + mimeType);

		response.setContentType(mimeType);

		/*
		 * "Content-Disposition : inline" will show viewable types [like
		 * images/text/pdf/anything viewable by browser] right on browser while
		 * others(zip e.g) will be directly downloaded [may provide save as
		 * popup, based on your browser setting.]
		 */
		// response.setHeader("Content-Disposition", String.format("inline;
		// filename=\"" + file.getName() +"\""));

		/*
		 * "Content-Disposition : attachment" will be directly download, may
		 * provide save as popup, based on your browser setting
		 */
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		// Copy bytes from source to destination(outputstream in this example),
		// closes both streams.
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	}

	@RequestMapping(value = "/api/noteUpload", method = RequestMethod.POST)
	public ResponseEntity<List<NoteInputFormModel>> multiFileUpload(MultipartHttpServletRequest request,
			RedirectAttributes redirectAttributes) throws IOException {

		Iterator<String> iterator = request.getFileNames();
		MultipartFile multipart = null;
		List<NoteInputFormModel> responseList = new ArrayList<>();
		while (iterator.hasNext()) {
			multipart = request.getFile(iterator.next());
			// do something with the file.....
		}
		System.out.println("Index controler for filr upload called>>" + multipart);
		if (multipart == null) {
			System.out.println("Empty uploaded file");
			return new ResponseEntity<List<NoteInputFormModel>>(responseList, HttpStatus.BAD_REQUEST);
		} else {
			System.out.println("Fetching files" + multipart);

			ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
			strat.setType(NoteInputFormModel.class);
			String[] columns = new String[] { "typeOfNote", "address", "property", "dateOfNote", "upb", "rate",
					"pdiPayment", "tdiPayment", "remainingTerm" }; // the fields
																	// to bind
																	// do in
																	// your
																	// JavaBean
			strat.setColumnMapping(columns);
			File file = NoteUtility.convert(multipart);
			CsvToBean csv = new CsvToBean();
			FileReader fr = new FileReader(file);
			CSVReader csvReader = new CSVReader(new FileReader(file));

			List list = csv.parse(strat, csvReader);
			for (Object object : list) {
				NoteInputFormModel note = (NoteInputFormModel) object;
				System.out.println(note);
				responseList.add(note);
			}
			return new ResponseEntity<List<NoteInputFormModel>>(responseList, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/api/fetchAllNotes", method = RequestMethod.GET)
	public ResponseEntity<List<NoteSummaryModel>> listAllNotes() {
		String loggedInUserName = NoteUtility.getLoggedInUserName();
		System.out.println("Inside Arvind listAllNotes loggedInUserName" + loggedInUserName);
		
		Optional<List<NoteSummaryModel>> notesList = noteService.getAllNotes(loggedInUserName);
		
		/*//List<NoteSummaryModel> notesList = new ArrayList<>();
		NoteSummaryModel note1 = new NoteSummaryModel("http://cdn.flaticon.com/png/256/70689.png", "ad", "ad", "adsad",
				"adafeae", "asfasd", "asda");
		NoteSummaryModel note2 = new NoteSummaryModel("http://cdn.flaticon.com/png/256/70689.png", "ad", "ad", "adsad",
				"adafeae", "asfasd", "asda");
		note1.setNoteId("1");
		note2.setNoteId("2");
		notesList.add(note1);
		notesList.add(note2);*/

		if (!notesList.isPresent()) {
			return new ResponseEntity<List<NoteSummaryModel>>(HttpStatus.NO_CONTENT);// You
		}																						// many
		return new ResponseEntity<List<NoteSummaryModel>>(notesList.get(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/editNote", method = RequestMethod.POST)
	public ResponseEntity<String> editNote(@RequestBody NoteDetailModel noteDetailModel) {

		System.out.println("Inside POST with editNote noteDetailModel value " + noteDetailModel);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/api/deleteNote/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteNote(@PathVariable String noteId) {
		if (StringUtils.isEmpty(noteId)) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		System.out.println("Inside DELETE with deleteNote noteDetailModel value " + noteId);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getNoteDetail/{noteId}", method = RequestMethod.GET)
	public ResponseEntity<String> getNoteDetail(@PathVariable String noteId) {
		if (StringUtils.isEmpty(noteId)) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		System.out.println("Inside Get Note Details with  noteDetailModel value " + noteId);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getNoteType", method = RequestMethod.GET)
	public ResponseEntity<List<NoteTypeModel>> getNoteType() {
		Optional<List<NoteTypeModel>> noteTypeModelList = noteService.getNoteType();
		if (noteTypeModelList.isPresent()) {
			return new ResponseEntity<List<NoteTypeModel>>(noteTypeModelList.get(), HttpStatus.OK);
		}
		return new ResponseEntity<List<NoteTypeModel>>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/api/getPropertyType", method = RequestMethod.GET)
	public ResponseEntity<List<PropertyTypeModel>> getPropertyType() {

		Optional<List<PropertyTypeModel>> propTypeModelList = noteService.getPropertyType();
		if (propTypeModelList.isPresent()) {
			return new ResponseEntity<List<PropertyTypeModel>>(propTypeModelList.get(), HttpStatus.OK);
		}
		return new ResponseEntity<List<PropertyTypeModel>>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/getStateCityList", method = RequestMethod.GET)
	public ResponseEntity<List<StateModel>> getStateCityList() {
		
		CityModel cityModel = new CityModel();
		cityModel.setId("1");
		cityModel.setCityDisplayName("First City");
		CityModel cityModel2 = new CityModel();
		cityModel2.setId("2");
		cityModel2.setCityDisplayName("Second City");
		StateModel stateModel =  new StateModel();
		stateModel.setId("1");
		stateModel.setStateDisplayName("First State");
		List<CityModel> cityList = new ArrayList<>();
		cityList.add(cityModel);
		List<CityModel> cityList2 = new ArrayList<>();
		cityList2.add(cityModel2);
		stateModel.setCityList(cityList);
		StateModel stateModel2 =  new StateModel();
		stateModel2.setId("1");
		stateModel2.setStateDisplayName("Second State");
		stateModel2.setCityList(cityList2);
		
		List<StateModel> stateList = new ArrayList<>();
		stateList.add(stateModel2);
		stateList.add(stateModel);
		return new ResponseEntity<List<StateModel>>(stateList,HttpStatus.OK);
	}
	
}