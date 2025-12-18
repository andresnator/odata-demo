package com.example.odata.config;

import com.example.odata.service.DemoEdmProvider;
import com.example.odata.service.DemoEntityCollectionProcessor;
import com.example.odata.service.Storage;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Configuration
public class ODataConfig {

    @Autowired
    private Storage storage;
    @Autowired
    private com.example.odata.service.BrandStorage brandStorage;

    @Bean
    public ServletRegistrationBean<HttpServlet> odataServlet() {
        return new ServletRegistrationBean<>(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                try {
                    // Create OData instance
                    OData odata = OData.newInstance();

                    // Configure EDM Provider
                    ServiceMetadata edm = odata.createServiceMetadata(new DemoEdmProvider(), new ArrayList<>());

                    // Create Handler
                    ODataHttpHandler handler = odata.createHandler(edm);

                    // Register Processors
                    handler.register(new DemoEntityCollectionProcessor(storage, brandStorage));

                    // Execute
                    handler.process(req, resp);
                } catch (RuntimeException e) {
                    throw new ServletException(e);
                }
            }
        }, "/OData.svc/*");
    }
}
