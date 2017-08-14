package com.noteanalyzer.mvc.service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import com.noteanalyzer.appraisal.exceptions.AddressNotAvailableException;
import com.noteanalyzer.entity.appraisal.AppriasalSources;
import com.noteanalyzer.entity.notes.NoteConfiguration;
import com.noteanalyzer.entity.notes.NoteType;
import com.noteanalyzer.entity.notes.Parameters;
import com.noteanalyzer.entity.notes.Property;
import com.noteanalyzer.entity.notes.PropertyType;
import com.noteanalyzer.mvc.model.AddressModel;
import com.noteanalyzer.mvc.model.NoteDetailModel;
import com.noteanalyzer.mvc.model.NoteInputFormModel;
import com.noteanalyzer.mvc.model.NoteDashboardModel;
import com.noteanalyzer.mvc.model.NoteTypeModel;
import com.noteanalyzer.mvc.model.PropertyTypeModel;

import lombok.NonNull;


/**
 * This interfae is responsible for all business logic associated with note releated action.
 * @author Arvind Ray
 *
 */
public interface NoteService {
	
	/**
	 * This method will create a new note with given input model. It will associate a property if found on the basis of given addresss or it will create and new property entry.
	 * @param note
	 * @throws ParseException
	 */
	public void createNote(@NonNull NoteInputFormModel note) throws ParseException;
	
	/**
	 * This method will return all type of notes supported by this application.
	 * @return List of note types.
	 */
	public Optional<List<NoteTypeModel>> getNoteType();
	
	/**
	 * This method will return all type of property supported by this application.
	 * @return List of property Types.
	 */
	public Optional<List<PropertyTypeModel>> getPropertyType();
	
	/**
	 * Fetch the details of note for given note id.
	 * @param noteId
	 * @return
	 */
	public Optional<NoteDetailModel> getNoteDetail(@NonNull Long noteId);

	/**
	 * This method will return all the notes associated with given user id.
	 * @param userId
	 * @return
	 */
	public Optional<List<NoteDashboardModel>> getAllNotes(@NonNull long userId);
	
	/**
	 * This method will return the configuration value for given config code and logged in user.
	 * @param configCode
	 * @return
	 */
	public List<NoteConfiguration> getConfigValue(@NonNull List<String> configCode);

	/**
	 * This method will return the configuration value for given config code and logged in user.
	 * @param parameterName
	 * @param userEmailId
	 * @return
	 */
	Parameters getParameterValue(String parameterName, String userEmailId);
	
	/**
	 * This method will return the list of state and city associated with given zipcode.
	 * @param zipCode
	 * @return
	 */
	public Optional<AddressModel> getZipCodeDetails(String zipCode);

	/**
	 * This method will return the list of all state and city supported by our application.
	 * @return
	 */
	Optional<AddressModel> getAllLocationDetails();

	/**
	 * This will return the property details for given address.
	 * @param noteModel
	 * @return
	 */
	Optional<List<Property>> getPropertyByAddress(NoteInputFormModel noteModel);

	/**
	 * This method will return the note type details for give note type code.
	 * @param noteTypeCode
	 * @return
	 */
	List<NoteType> getNoteTypeByCode(String noteTypeCode);

	/**
	 * This method will return the property type details for give property type code.
	 * @param propertyTypeCode
	 * @return
	 */
	List<PropertyType> getPropertyTypeByCode(String propertyTypeCode);

	/**
	 * This method will update the note value with given note details. It will also update the property details from apprisal source.
	 * @param noteDetailModel
	 * @return
	 */
	Optional<NoteDetailModel> updateNote(NoteDetailModel noteDetailModel);

	/**
	 * This method will delete the note associated with logged in user.
	 * @param noteDetailModel
	 * @param userName
	 * @return
	 */
	boolean deleteNote(NoteDetailModel noteDetailModel, String userName);
	
	/**
	 * This method will return the apprisal source details for given apprisal source detail.
	 * @param apprisalSourceCode
	 * @return
	 */

	Optional<List<AppriasalSources>> getApprisalSourceDetail(String apprisalSourceCode);
	
	
}
