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
package stargate.server.sourcefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import stargate.commons.service.ServiceNotStartedException;
import stargate.commons.sourcefs.ASourceFileSystemDriver;
import stargate.commons.sourcefs.SourceFileMetadata;

/**
 *
 * @author iychoi
 */
public class SourceFileSystemManager {
    
    private static final Log LOG = LogFactory.getLog(SourceFileSystemManager.class);
    
    private static SourceFileSystemManager instance;

    private ASourceFileSystemDriver driver;
    
    public static SourceFileSystemManager getInstance(ASourceFileSystemDriver driver) {
        synchronized (SourceFileSystemManager.class) {
            if(instance == null) {
                instance = new SourceFileSystemManager(driver);
            }
            return instance;
        }
    }
    
    public static SourceFileSystemManager getInstance() throws ServiceNotStartedException {
        synchronized (SourceFileSystemManager.class) {
            if(instance == null) {
                throw new ServiceNotStartedException("SourceFileSystemManager is not started");
            }
            return instance;
        }
    }
    
    SourceFileSystemManager(ASourceFileSystemDriver driver) {
        if(driver == null) {
            throw new IllegalArgumentException("driver is null");
        }
        
        this.driver = driver;
    }
    
    public synchronized ASourceFileSystemDriver getDriver() {
        return this.driver;
    }
    
    public synchronized void start() throws IOException {
        this.driver.startDriver();
    }

    public synchronized void stop() throws IOException {
        this.driver.stopDriver();
    }
    
    public synchronized SourceFileMetadata getMetadata(URI path) throws IOException {
        return this.driver.getMetadata(path);
    }
    
    public synchronized InputStream getInputStream(URI path) throws IOException {
        return this.driver.getInputStream(path);
    }
    
    public synchronized InputStream getInputStream(URI path, long offset, int size) throws IOException {
        return this.getInputStream(path, offset, size);
    }
    
    @Override
    public synchronized String toString() {
        return "SourceFileSystemManager";
    }
}
