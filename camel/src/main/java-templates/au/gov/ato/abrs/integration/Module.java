package au.gov.ato.abrs.integration;

import au.gov.ato.abrs.integration.cdi.qualifier.ModuleApiVersion;
import au.gov.ato.abrs.integration.cdi.qualifier.ModuleName;
import au.gov.ato.abrs.integration.cdi.qualifier.ModuleVersion;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@ApplicationScoped
public class Module {
    @Produces
    @ModuleName    
    public static final String NAME = "DDS";

    // Maven Properties
    public static final String POM_NAME="${project.name}";

    @Produces
    @ModuleVersion
    public static final String VERSION="${project.version}";

    public static final String PARENT_ARTIFACT_FULL="${project.parent.artifact}";
    public static final String PARENT_ARTIFACT_ID="${project.parent.artifactId}";
    public static final String PARENT_ARTIFACT_GROUP_ID="${project.parent.groupId}";

    public static final String ARTIFACT_FULL="${project.artifact}";
    public static final String ARTIFACT_ID="${project.artifactId}";
    public static final String ARTIFACT_GROUP_ID="${project.groupId}";

    public static final String BUILD_TIMESTAMP="${build.timestamp}";

    public static final String OS_NAME="${os.name}";
    public static final String OS_ARC="${os.arch}";
    public static final String OS_VERSION="${os.version}";

    public static final String JAVA_VERSION="${java.version}";
    public static final String JAVA_VENDOR="${java.vendor}";  

    @Produces
    @ModuleApiVersion    
    public static String translateToApiVersion() {
        return translateToApiVersion(VERSION);
    }

    public static String translateToApiVersion(String versionStr) {
        String apiVersion = versionStr.replace("-SNAPSHOT", "");
        apiVersion = apiVersion.toLowerCase();

        // Format: MM.mm.hh[-SNAPSHOT]
         String[] toks = apiVersion.split("\\.");

        // Get to mm.MM  (Hotfix does not replace API version)
        if (toks.length > 2)
            toks = Arrays.copyOf(toks, 2);

        // Strip off trailing 0's for API version and don't have hotfix
        List<String> verTokens = Arrays.asList(toks);
        Collections.reverse(verTokens);

        apiVersion = "";
        for (String tok : verTokens) {
            if (0 != Integer.parseInt(tok)) {
                apiVersion = tok + (!apiVersion.isEmpty() ? "." : "") + apiVersion;
            }
        }

        //apiVersion = apiVersion.replaceAll("\\.", "_");
        apiVersion = apiVersion.replaceAll("-", "_");
        return apiVersion;
    }
}
