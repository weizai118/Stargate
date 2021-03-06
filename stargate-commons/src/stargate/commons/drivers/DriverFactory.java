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
package stargate.commons.drivers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class DriverFactory {
    
    private static final Log LOG = LogFactory.getLog(DriverFactory.class);
    
    public static ADriver createDriver(DriverSetting driverSetting) throws DriverFailedToLoadException {
        if(driverSetting == null) {
            throw new IllegalArgumentException("driverSetting is null");
        }
        
        return createDriver(driverSetting.getDriverClass(), driverSetting.getDriverConfiguration());
    }
    
    public static ADriver createDriver(Class driverClass, ADriverConfiguration driverConfiguration) throws DriverFailedToLoadException {
        if(driverClass == null) {
            throw new IllegalArgumentException("driverClass is null");
        }

        if(driverConfiguration == null) {
            throw new IllegalArgumentException("driverConfiguration is null");
        }
        
        Method method = null;

        try {
            // find getinstance function
            Class[] argTypes = new Class[] { ADriverConfiguration.class };
            method = driverClass.getDeclaredMethod("getInstance", argTypes);
        } catch(NoSuchMethodException ex) {
            // no getinstance static function
        }
        
        if(method != null) {
            try {
                return (ADriver) method.invoke(null, (Object)driverConfiguration);
            } catch (IllegalAccessException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (IllegalArgumentException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (InvocationTargetException ex) {
                throw new DriverFailedToLoadException(ex);
            }
        } else {
            try {
                Constructor constructor = driverClass.getConstructor(ADriverConfiguration.class);
                return (ADriver) constructor.newInstance(driverConfiguration);
            } catch (InstantiationException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (IllegalAccessException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (IllegalArgumentException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (InvocationTargetException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (NoSuchMethodException ex) {
                throw new DriverFailedToLoadException(ex);
            } catch (SecurityException ex) {
                throw new DriverFailedToLoadException(ex);
            }
        }
    }
}
