package au.gov.ato.abrs.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelMain {
	private static Logger log = null;

	public static void main(String[] args) throws Exception {

		defaultConfigurationLocation();
		loadPropertiesFileIntoSystemProperties("loki.properties");
		loadPropertiesFileIntoSystemProperties("otel.properties");

		log = LoggerFactory.getLogger(CamelMain.class);
		printBanner();
		runCDIContainer();
	}

	private static void printBanner() {
		try (InputStream is = CamelMain.class.getResourceAsStream("/banner.txt");
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException ex) {	
			// :( o well
		}
		log.info("Version:     " + Module.VERSION);
		log.info("API Version: v" + Module.translateToApiVersion(Module.VERSION));
		log.info("Built:       " + Module.BUILD_TIMESTAMP + "UTC using " + Module.JAVA_VENDOR + " Java " + Module.JAVA_VERSION + " (" + Module.OS_NAME + " " + Module.OS_ARC + " " + Module.OS_VERSION + ")");
	}

	private static void defaultConfigurationLocation() {
		if (null == System.getProperty("integration-config-path")) {
			try {
				String loc = CamelMain.class.getProtectionDomain().getCodeSource().getLocation()
						.toURI().toString();
				loc = (loc.startsWith("file:")) ? loc.split("file:")[1] : loc.split("!")[0].split("jar:file:")[1];
				File mainJarFile = new File(loc);

				File rootDir = mainJarFile.getParentFile();
				while (null != rootDir && !"target".equalsIgnoreCase(rootDir.getName())) {
					rootDir = rootDir.getParentFile();
				}
				if (null != rootDir) {
					rootDir = rootDir.getParentFile();

					File configDir = new File(rootDir, "/src/main/configuration");
					if (!configDir.exists()) {
						// Try parent
						configDir = new File(rootDir.getParentFile(), "/src/main/configuration");
					}

					System.setProperty("integration-config-path", configDir.getAbsolutePath());
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		}
	}

	private static void runCDIContainer() {
		// CDI Container
		// Will register shutdown hook
		try {
			Weld weld = new Weld();
			WeldContainer container = weld.initialize();
		} catch (Throwable ex) {
			System.err.println("Unable to start Camel CDI");
			ex.printStackTrace(System.err);
			System.exit(2);
		}
		System.out.println();
	}

	private static void loadPropertiesFileIntoSystemProperties(String name) {
		File propertiesFile = new File(System.getProperty("integration-config-path"), name);
		if (propertiesFile.exists()) {
			Properties p = new Properties();
			try (InputStream is = new FileInputStream(propertiesFile)) {
				p.load(is);
				for (Object key : p.keySet()) {
					if (key instanceof String) {
						System.setProperty((String) key, p.getProperty((String) key));
					}
				}
			} catch (Exception ex) {
				System.err.println("Unable to locate properties file '" + propertiesFile.getAbsolutePath() + "'");
				System.exit(2);
			}
		} else {
			System.err.println("Unable to locate properties file '" + propertiesFile.getAbsolutePath() + "'");
			System.exit(2);
		}
	}
}
