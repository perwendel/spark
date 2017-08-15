/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark;

/**
 * Model And View class is used to set the name of the view and the model object
 * to be rendered.
 *
 * @author alex
 */
public class ModelAndView {

    /**
     * Model object.
     */
    private Object model;
    /**
     * View name used to render output.
     */
    private String viewName;

    /**
     * Constructs an instance with the provided model and view name
     *
     * @param model    the model
     * @param viewName the view name
     */
    public ModelAndView(Object model, String viewName) {
        super();
        this.model = model;
        this.viewName = viewName;
    }

    /**
     * @return the model object
     */
    public Object getModel() {
        return model;
    }

    /**
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }

}
