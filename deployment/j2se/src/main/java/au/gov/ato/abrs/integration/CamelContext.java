package au.gov.ato.abrs.integration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.health.HealthCheckRegistry;
import org.apache.camel.impl.health.DefaultHealthCheckRegistry;
import org.apache.camel.impl.health.RoutesHealthCheckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ato.abrs.integration.main.CamelServiceDefaultContext;

// @Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
@ApplicationScoped
public class CamelContext extends CamelServiceDefaultContext {
    
    private static Logger log = LoggerFactory.getLogger(CamelContext.class);

    @PostConstruct
    public void customizeContext() {
        super.initDefaultContext();

        // Add custom context initialization here

        // --------------------------------------
        
        log.info("Initialised " + moduleName + " camel service context");
    }

}

