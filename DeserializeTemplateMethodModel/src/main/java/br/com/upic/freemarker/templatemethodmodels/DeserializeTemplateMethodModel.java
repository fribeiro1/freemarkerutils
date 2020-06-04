/*
 * Copyright 2012 Upic
 * 
 * This file is part of FreeMarker Utils.
 *
 * FreeMarker Utils is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FreeMarker Utils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FreeMarker Utils. If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.upic.freemarker.templatemethodmodels;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import freemarker.ext.beans.ArrayModel;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class DeserializeTemplateMethodModel implements
		TemplateMethodModelEx {

	@SuppressWarnings("rawtypes")
	public Object exec(List arguments) throws TemplateModelException {
		Object result = null;

		if (arguments.size() < 1)
			throw new IllegalArgumentException("The argument is required");

		Object model = arguments.get(0);

		if (model instanceof SimpleSequence) {
			List list = (List) DeepUnwrap.unwrap((TemplateModel) model);

			byte[] arr = new byte[list.size()];

			for (int i = 0; i < arr.length; i++) {
				Object b = list.get(i);

				if (!(b instanceof Byte))
					throw new IllegalArgumentException(
							"The argument must be an array of bytes");

				arr[i] = (Byte) b;
			}

			try {
				ObjectInputStream in = new ObjectInputStream(
						new ByteArrayInputStream(arr));

				result = in.readObject();

				in.close();
			} catch (Exception e) {
				throw new TemplateModelException("Can't deserialize object", e);
			}

		} else if (model instanceof ArrayModel) {
			Object arr = ((ArrayModel) model)
					.getAdaptedObject(Object.class);

			if (!(arr instanceof byte[]))
				throw new IllegalArgumentException(
						"The argument must be an array of bytes");

			try {
				ObjectInputStream in = new ObjectInputStream(
						new ByteArrayInputStream((byte[]) arr));

				result = in.readObject();

				in.close();
			} catch (Exception e) {
				throw new TemplateModelException("Can't deserialize object", e);
			}

		} else {
			throw new IllegalArgumentException("The argument must be an array");
		}

		return result;
	}

}