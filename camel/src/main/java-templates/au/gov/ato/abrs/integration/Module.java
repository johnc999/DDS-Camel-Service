package au.gov.ato.abrs.integration;

import au.gov.ato.abrs.integration.cdi.interfaces.ModuleInformation;

import au.gov.ato.abrs.integration.cdi.qualifier.ModuleApiVersion;
import au.gov.ato.abrs.integration.cdi.qualifier.ModuleBuildInformation;
import au.gov.ato.abrs.integration.cdi.qualifier.ModuleName;
import au.gov.ato.abrs.integration.cdi.qualifier.ModuleVersion;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

// @Author: Johnathan Ingram (johnathan.ingram@ato.gov.au)
@ApplicationScoped
public class Module extends ModuleInformation {
    // Note: Populated by Maven plugin using default and custom properties defined within the POM
    public static final String NAME = "${service.name}";

    // Maven Properties
    public static final String POM_NAME="${project.name}";

    public static final String VERSION="${project.version}";

    public static final String PARENT_ARTIFACT_FULL="${project.parent.artifact}";
    public static final String PARENT_ARTIFACT_ID="${project.parent.artifactId}";
    public static final String PARENT_ARTIFACT_GROUP_ID="${project.parent.groupId}";

    public static final String ARTIFACT_FULL="${project.artifact}";
    public static final String ARTIFACT_ID="${project.artifactId}";
    public static final String ARTIFACT_GROUP_ID="${project.groupId}";

    public static final String BUILD_TIMESTAMP="${build.timestamp}";

    public static final String OS_NAME="${os.name}";
    public static final String OS_ARCH="${os.arch}";
    public static final String OS_VERSION="${os.version}";

    public static final String JAVA_VERSION="${java.version}";
    public static final String JAVA_VENDOR="${java.vendor}";  


    @Produces
    @ModuleName    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getPomName() {
        return POM_NAME;
    }

    @Produces
    @ModuleVersion    
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getParentArtifactFull() {
        return PARENT_ARTIFACT_FULL;
    }

    @Override
    public String getParentArtifactId() {
        return PARENT_ARTIFACT_ID;
    }

    @Override
    public String getParentArtifactGroupId() {
        return PARENT_ARTIFACT_GROUP_ID;
    }

    @Override
    public String getArtifactFull() {
        return ARTIFACT_FULL;
    }

    @Override
    public String getArtifactId() {
        return ARTIFACT_ID;
    }

    @Override
    public String getArtifactGroupId() {
        return ARTIFACT_GROUP_ID;
    }

    @Override
    public String getBuildTimestamp() {
        return BUILD_TIMESTAMP;
    }

    @Override
    public String getOsName() {
        return OS_NAME;
    }

    @Override
    public String getOsArch() {
        return OS_ARCH;
    }
    
    @Override
    public String getOsVersion() {
        return OS_VERSION;
    }

    @Override
    public String getJavaVersion() {
        return JAVA_VERSION;
    }

    @Override
    public String getJavaVendor() {
        return JAVA_VENDOR;
    }

    @Produces
    @ModuleBuildInformation
    public String getBuildInformation() {
        String info = getBuildTimestamp() + "UTC using " + getJavaVendor() + " Java "
                    + getJavaVersion() + " (" + getOsName() + " " + getOsArch() + " " +getOsVersion() + ")";
        return info;
    }

    @Produces
    @ModuleApiVersion    
    public String getApiVersion() {
        return translateToApiVersion(getVersion());
    }      
}
