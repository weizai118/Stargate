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

package edu.arizona.cs.stargate.gatekeeper.restful.client;

import edu.arizona.cs.stargate.gatekeeper.GateKeeperClientConfiguration;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import edu.arizona.cs.stargate.common.PathUtils;
import edu.arizona.cs.stargate.gatekeeper.restful.RestfulResponse;
import edu.arizona.cs.stargate.gatekeeper.restful.api.AGateKeeperRestfulAPI;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class GateKeeperRestfulClient extends AGateKeeperRestfulAPI {
    
    private static final Log LOG = LogFactory.getLog(GateKeeperRestfulClient.class);
    
    private GateKeeperClientConfiguration config;
    
    private ClientConfig clientConfig;
    private Client client;
    
    private ClusterManagerRestfulClient clusterManagerClient;
    private DataExportManagerClient dataExportManagerClient;
    private RecipeManagerRestfulClient recipeManagerClient;
    private InterClusterDataTransferRestfulClient interClusterDataTransferClient;
    private FileSystemRestfulClient filesystemClient;

    public GateKeeperRestfulClient(GateKeeperClientConfiguration conf) {
        this.config = conf;
        
        this.clientConfig = new DefaultClientConfig();
        this.clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        this.clientConfig.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, conf.getMaxRPCThreads());

        this.client = Client.create(this.clientConfig);
        
        this.clusterManagerClient = new ClusterManagerRestfulClient(this);
        this.dataExportManagerClient = new DataExportManagerClient(this);
        this.recipeManagerClient = new RecipeManagerRestfulClient(this);
        this.interClusterDataTransferClient = new InterClusterDataTransferRestfulClient(this);
        this.filesystemClient = new FileSystemRestfulClient(this);
    }
    
    public void start() {
    }
    
    public void stop() {
        this.client.getExecutorService().shutdownNow();
        this.client.destroy();
    }
    
    public ClusterManagerRestfulClient getClusterManagerClient() {
        return this.clusterManagerClient;
    }
    
    public DataExportManagerClient getDataExportManagerClient() {
        return this.dataExportManagerClient;
    }
    
    public RecipeManagerRestfulClient getRecipeManagerClient() {
        return this.recipeManagerClient;
    }
    
    public InterClusterDataTransferRestfulClient getInterClusterDataTransferClient() {
        return this.interClusterDataTransferClient;
    }
    
    public FileSystemRestfulClient getFileSystemClient() {
        return this.filesystemClient;
    }
    
    public Object post(String path, Object request, GenericType<?> generic) throws IOException {
        URI absURI = this.config.getServiceURI().resolve(path);
        
        AsyncWebResource webResource = this.client.asyncResource(absURI);
        Future<ClientResponse> future = (Future<ClientResponse>) webResource.accept("application/json").type("application/json").post(ClientResponse.class, request);
        
        // wait for completition
        try {
            ClientResponse response = future.get();
            if(response.getStatus() != 200) {
                throw new IOException("HTTP error code : " + response.getStatus());
            }

            return response.getEntity(generic);
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public Object get(String path, GenericType<?> generic) throws IOException {
        URI absURI = this.config.getServiceURI().resolve(path);
        
        AsyncWebResource webResource = this.client.asyncResource(absURI);
        Future<ClientResponse> future = (Future<ClientResponse>) webResource.accept("application/json").type("application/json").get(ClientResponse.class);
        
        // wait for completition
        try {
            ClientResponse response = future.get();
            if(response.getStatus() != 200) {
                throw new IOException("HTTP error code : " + response.getStatus());
            }

            return response.getEntity(generic);
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public Object delete(String path, GenericType<?> generic) throws IOException {
        URI absURI = this.config.getServiceURI().resolve(path);
        
        AsyncWebResource webResource = this.client.asyncResource(absURI);
        Future<ClientResponse> future = (Future<ClientResponse>) webResource.accept("application/json").type("application/json").delete(ClientResponse.class);
        
        // wait for completition
        try {
            ClientResponse response = future.get();
            if(response.getStatus() != 200) {
                throw new IOException("HTTP error code : " + response.getStatus());
            }

            return response.getEntity(generic);
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public InputStream download(String path) throws IOException {
        URI absURI = this.config.getServiceURI().resolve(path);
        
        AsyncWebResource webResource = this.client.asyncResource(absURI);
        Future<ClientResponse> future = (Future<ClientResponse>) webResource.accept("application/octet-stream").type("application/json").get(ClientResponse.class);
        
        // wait for completition
        try {
            ClientResponse response = future.get();
            if(response.getStatus() != 200) {
                throw new IOException("HTTP error code : " + response.getStatus());
            }

            return response.getEntityInputStream();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public String getResourcePath(String path) {
        return PathUtils.concatPath(AGateKeeperRestfulAPI.BASE_PATH, path);
    }

    @Override
    public boolean checkLive() {
        RestfulResponse<Boolean> response;
        try {
            String url = getResourcePath(AGateKeeperRestfulAPI.LIVENESS_PATH);
            response = (RestfulResponse<Boolean>) get(url, new GenericType<RestfulResponse<Boolean>>(){});
        } catch (IOException ex) {
            LOG.error(ex);
            return false;
        }
        
        if(response.getException() != null) {
            return false;
        } else {
            return response.getResponse().booleanValue();
        }
    }
}
