/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.service;

/**
 * Thrown if there is no service associated to a given key or alias.
 */
public class UnsupportedServiceException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public UnsupportedServiceException(String msg) {
        super(msg);
    }

	public UnsupportedServiceException() {
		super();
	}

	public UnsupportedServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedServiceException(Throwable cause) {
		super(cause);
	}
}