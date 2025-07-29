package org.innovation.util;

import lombok.experimental.UtilityClass;
import org.innovation.JavaVersionInfo;
import org.innovation.config.RepoConfig;
import org.innovation.exception.OpenRewriteRunnerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@UtilityClass
public class OpenRewriteRunnerUtil {

    private static final Map<String, String> JAVA_HOME_MAP = Map.of(
            "1.8", "C:\\Java\\Temurin\\jdk-8",
            "11", "C:\\Java\\Temurin\\jdk-11",
            "17", "C:\\Java\\Temurin\\jdk-17",
            "21", "C:\\Java\\Temurin\\jdk-21"
    );
    private static final String DEFAULT_JAVA_VERSION = "21";
    private static final String JAVA_VERSION_TAG = "<java.version>";
    private static final String REWRITE_PLUGIN_TAG = "<rewrite-maven-plugin>";
    private static final String REWRITE_PLUGIN_XML = "src/main/resources/ReWritePlugin.xml";
    private static final String POM_XML = "pom.xml";
    private static final String PLUGINS_SECTION_END = "</plugins>";
    private static final String REWRITE_COMMAND = "rewrite:run";
    private static final String MVN_CMD = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
    private static final String ENV_JAVA_HOME = "JAVA_HOME";
    private static final String ENV_PATH = "PATH";

    public static void runRewrite(RepoConfig repo) throws IOException, InterruptedException {
        var pomPath = Path.of(repo.getLocalDir(), POM_XML);
        var lines = Files.readAllLines(pomPath);
        var javaVersionInfo = getJavaVersionInfoFromPom(lines);

        var pluginExists = lines.stream().anyMatch(line -> line.contains(REWRITE_PLUGIN_TAG));

        if (!pluginExists) {
            var pluginsIndex = -1;
            for (var i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains(PLUGINS_SECTION_END)) {
                    pluginsIndex = i;
                    break;
                }
            }

            if (pluginsIndex == -1) {
                throw new OpenRewriteRunnerException("<plugins> section not found in pom.xml for " + repo.getName());
            }

            var pluginXmlPath = Path.of(REWRITE_PLUGIN_XML);
            var plugin = Files.readString(pluginXmlPath).stripIndent();
            lines.add(pluginsIndex, plugin);
            Files.write(pomPath, lines);
        }

        var pb = new ProcessBuilder()
                .directory(new File(repo.getLocalDir()))
                .command(MVN_CMD, REWRITE_COMMAND);

        pb.environment().put(ENV_JAVA_HOME, javaVersionInfo.home());
        pb.environment().put(ENV_PATH, javaVersionInfo.home() + File.separator + "bin" + File.pathSeparator + System.getenv(ENV_PATH));

        pb.inheritIO();
        var p = pb.start();
        if (p.waitFor() != 0) {
            throw new OpenRewriteRunnerException("OpenRewrite failed for " + repo.getName() + " using Java " + javaVersionInfo.version());
        }
    }


    private static JavaVersionInfo getJavaVersionInfoFromPom(List<String> lines) {
        var javaVersion = DEFAULT_JAVA_VERSION;

        var versionLine = lines.stream()
                .filter(line -> line.contains(JAVA_VERSION_TAG))
                .findFirst();

        if (versionLine.isPresent()) {
            javaVersion = versionLine.get().replaceAll("\\D+", "");
            switch (javaVersion) {
                case "18":
                    javaVersion = "1.8";
                    break;
                case "11","17","21":
                    break;
                default:
                    throw new OpenRewriteRunnerException("Unknown java version: " + javaVersion);
            }
        }

        var javaHome = JAVA_HOME_MAP.getOrDefault(javaVersion, System.getenv(ENV_JAVA_HOME));
        if (javaHome == null) {
            throw new OpenRewriteRunnerException("JAVA_HOME not set or unknown Java version: " + javaVersion);
        }

        return new JavaVersionInfo(javaVersion, javaHome);
    }
}