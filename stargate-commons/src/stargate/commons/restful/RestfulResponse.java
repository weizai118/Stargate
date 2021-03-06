/*
 * The MIT License
 *
 * Copyright 2015 iychoi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package stargate.commons.restful;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.ClassUtils;

/**
 *
 * @author iychoi
 */
public class RestfulResponse<T> {
    private T response;
    private Class exceptionClass;
    private RestfulOperationException exception;
    
    public RestfulResponse() {
        
    }
    
    public RestfulResponse(T response) {
        this.response = response;
    }
    
    public RestfulResponse(Exception ex) {
        if(ex == null) {
            throw new IllegalArgumentException("ex is null");
        }
        
        this.exceptionClass = ex.getClass();
        this.exception = new RestfulOperationException(ex);
    }
    
    @JsonProperty("response")
    public void setResponse(T response) {
        this.response = response;
    }
    
    @JsonProperty("response")
    public T getResponse() {
        return this.response;
    }
    
    @JsonProperty("exception_class")
    public void setExceptionClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            this.exceptionClass = null;
            return;
        }
        
        this.exceptionClass = ClassUtils.findClass(clazz);
    }
    
    @JsonIgnore
    public void setExceptionClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        this.exceptionClass = clazz;
    }
    
    @JsonProperty("exception_class")
    public String getExceptionClassString() {
        if(this.exceptionClass == null) {
            return null;
        }
        return this.exceptionClass.getCanonicalName();
    }
    
    @JsonIgnore
    public Class getExceptionClass() {
        return this.exceptionClass;
    }
    
    @JsonProperty("exception")
    public void setException(RestfulOperationException ex) {
        this.exception = ex;
    }
    
    @JsonProperty("exception")
    public Exception getException() {
        return this.exception;
    }
}
