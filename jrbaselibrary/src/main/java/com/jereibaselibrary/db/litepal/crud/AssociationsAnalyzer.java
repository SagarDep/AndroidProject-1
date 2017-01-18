/*
 * Copyright (C)  Tony Green, Litepal Framework Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jereibaselibrary.db.litepal.crud;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.jereibaselibrary.db.litepal.LitePalBase;
import com.jereibaselibrary.db.litepal.crud.*;
import com.jereibaselibrary.db.litepal.crud.DataSupport;
import com.jereibaselibrary.db.litepal.crud.model.AssociationsInfo;
import com.jereibaselibrary.db.litepal.exceptions.DataSupportException;
import com.jereibaselibrary.db.litepal.util.DBUtility;

/**
 * Base class of associations analyzer.
 * 
 * @author Tony Green
 * @since 1.1
 */
abstract class AssociationsAnalyzer extends com.jereibaselibrary.db.litepal.crud.DataHandler {

	/**
	 * Get the associated models collection of associated model. Used for
	 * reverse searching associations.
	 * 
	 * @param associatedModel
	 *            The associated model of baseObj.
	 * @param associationInfo
	 *            To get reverse associated models collection.
	 * @return The associated models collection of associated model by analyzing
	 *         associationInfo.
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	protected Collection<com.jereibaselibrary.db.litepal.crud.DataSupport> getReverseAssociatedModels(com.jereibaselibrary.db.litepal.crud.DataSupport associatedModel,
																				  AssociationsInfo associationInfo) throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return (Collection<com.jereibaselibrary.db.litepal.crud.DataSupport>) takeGetMethodValueByField(associatedModel,
				associationInfo.getAssociateSelfFromOtherModel());
	}

	/**
	 * Set the associated models collection of associated model. Break quote of
	 * source collection.
	 * 
	 * @param associatedModel
	 *            The associated model of baseObj.
	 * @param associationInfo
	 *            To get reverse associated models collection.
	 * @param associatedModelCollection
	 *            The new associated models collection with same data as source
	 *            collection but different quote.
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void setReverseAssociatedModels(com.jereibaselibrary.db.litepal.crud.DataSupport associatedModel,
											  AssociationsInfo associationInfo, Collection<com.jereibaselibrary.db.litepal.crud.DataSupport> associatedModelCollection)
			throws SecurityException, IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		putSetMethodValueByField(associatedModel, associationInfo.getAssociateSelfFromOtherModel(),
				associatedModelCollection);
	}

	/**
	 * Check the associated model collection. If the associated model collection
	 * is null, try to initialize the associated model collection by the given
	 * associated field. If the associated field is subclass of List, make an
	 * instance of ArrayList for associated model collection. If the associated
	 * field is subclass of Set, make an instance of HashSet for associated
	 * model collection. If the associated model collection is not null, doing
	 * nothing.
	 * 
	 * @param associatedModelCollection
	 *            The associated model collection to check null and initialize.
	 * @param associatedField
	 *            The field to decide which type to initialize for associated
	 *            model collection.
	 * @throws DataSupportException
	 */
	protected Collection<com.jereibaselibrary.db.litepal.crud.DataSupport> checkAssociatedModelCollection(
			Collection<com.jereibaselibrary.db.litepal.crud.DataSupport> associatedModelCollection, Field associatedField) {
		Collection<com.jereibaselibrary.db.litepal.crud.DataSupport> collection = null;
		if (isList(associatedField.getType())) {
			collection = new ArrayList<com.jereibaselibrary.db.litepal.crud.DataSupport>();
		} else if (isSet(associatedField.getType())) {
			collection = new HashSet<com.jereibaselibrary.db.litepal.crud.DataSupport>();
		} else {
			throw new DataSupportException(DataSupportException.WRONG_FIELD_TYPE_FOR_ASSOCIATIONS);
		}
		if (associatedModelCollection != null) {
			collection.addAll(associatedModelCollection);
		}
		return collection;
	}

	/**
	 * Build the bidirectional association by setting the baseObj instance to
	 * the associated model.
	 * 
	 * @param baseObj
	 *            The instance of self model.
	 * @param associatedModel
	 *            The associated model.
	 * @param associationInfo
	 *            The association info to get the association.
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void buildBidirectionalAssociations(com.jereibaselibrary.db.litepal.crud.DataSupport baseObj, com.jereibaselibrary.db.litepal.crud.DataSupport associatedModel,
												  AssociationsInfo associationInfo) throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		putSetMethodValueByField(associatedModel, associationInfo.getAssociateSelfFromOtherModel(),
				baseObj);
	}

	/**
	 * If the associated model is saved, add its' name and id to baseObj by
	 * calling {@link com.jereibaselibrary.db.litepal.crud.DataSupport#addAssociatedModelWithFK(String, long)}. Or if
	 * the baseObj is saved, add its' name and id to associated model by calling
	 * {@link com.jereibaselibrary.db.litepal.crud.DataSupport#addAssociatedModelWithoutFK(String, long)}.
	 * 
	 * @param baseObj
	 *            The baseObj currently want to persist.
	 * @param associatedModel
	 *            The associated model.
	 */
	protected void dealsAssociationsOnTheSideWithoutFK(com.jereibaselibrary.db.litepal.crud.DataSupport baseObj,
													   com.jereibaselibrary.db.litepal.crud.DataSupport associatedModel) {
		if (associatedModel != null) {
			if (associatedModel.isSaved()) {
				baseObj.addAssociatedModelWithFK(associatedModel.getTableName(),
						associatedModel.getBaseObjId());
			} else {
				if (baseObj.isSaved()) {
					associatedModel.addAssociatedModelWithoutFK(baseObj.getTableName(),
							baseObj.getBaseObjId());
				}
			}
		}
	}

	/**
	 * If the associated model of self model is null, the FK value in database
	 * should be cleared if it exists when updating.
	 * 
	 * @param baseObj
	 *            The baseObj currently want to persist or update.
	 * @param associationInfo
	 *            The associated info analyzed by
	 *            {@link LitePalBase#getAssociationInfo(String)}.
	 */
	protected void mightClearFKValue(DataSupport baseObj, AssociationsInfo associationInfo) {
		baseObj.addFKNameToClearSelf(getForeignKeyName(associationInfo));
	}

	/**
	 * Get foreign key name by {@link AssociationsInfo}.
	 * 
	 * @param associationInfo
	 *            To get foreign key name from.
	 * @return The foreign key name.
	 */
	private String getForeignKeyName(AssociationsInfo associationInfo) {
		return getForeignKeyColumnName(DBUtility.getTableNameByClassName(associationInfo
				.getAssociatedClassName()));
	}
}