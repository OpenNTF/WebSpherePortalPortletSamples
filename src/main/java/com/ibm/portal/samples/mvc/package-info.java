/*
 * (C) Copyright IBM Corp. 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
/**
 * Base package for the implementation of the {@link com.ibm.portal.samples.mvc.TemplatePortlet} portlet. The basic
 * structure of the portlet consists of the model bean {@link com.ibm.portal.samples.mvc.model.TemplateModel} that
 * provides access to the data model of the portlet. The subclass {@link com.ibm.portal.samples.mvc.model.TemplateActions} represents
 * the set of actions that modify the model and the backend data. The controller layer in {@link com.ibm.portal.samples.mvc.controller.TemplateController}
 * provides the URLs that represent model transitions and the view in {@link com.ibm.portal.samples.mvc.view.TemplateView} convenience
 * functions required to render the model.
 * 
 * The portlet {@link com.ibm.portal.samples.mvc.TemplatePortlet} glues the different beans together.
 * 
 * @author cleue
 */
package com.ibm.portal.samples.mvc;