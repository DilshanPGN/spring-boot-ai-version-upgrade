package org.innovation.service;

import org.innovation.config.RepoConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class OpenRewriteRunner {
    public static void runRewrite(RepoConfig repo) throws Exception {
        Path pomPath = Path.of(repo.localDir, "pom.xml");
        List<String> lines = Files.readAllLines(pomPath);

        String javaVersion = "21"; // default
        for (String line : lines) {
            if (line.contains("<java.version>")) {
                javaVersion = line.replaceAll("\\D+", "");
                if (javaVersion.equals("18")) javaVersion = "1.8";
                if (javaVersion.equals("11")) javaVersion = "11";
                if (javaVersion.equals("17")) javaVersion = "17";
                if (javaVersion.equals("21")) javaVersion = "21";
                break;
            }
        }

        Map<String, String> javaHomeMap = Map.of(
                "1.8", "C:\\Java\\Temurin\\jdk-8",
                "11", "C:\\Java\\Temurin\\jdk-11",
                "17", "C:\\Java\\Temurin\\jdk-17",
                "21", "C:\\Java\\Temurin\\jdk-21"
        );

        String javaHome = javaHomeMap.getOrDefault(javaVersion, System.getenv("JAVA_HOME"));
        if (javaHome == null) throw new RuntimeException("JAVA_HOME not set or unknown Java version");

        boolean pluginExists = lines.stream().anyMatch(line -> line.contains("rewrite-maven-plugin"));

        if (!pluginExists) {
            int pluginsIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains("</plugins>")) {
                    pluginsIndex = i;
                    break;
                }
            }

            if (pluginsIndex == -1) {
                throw new RuntimeException("<plugins> section not found in pom.xml for " + repo.name);
            }

            String plugin = """
                <plugin>
                    <groupId>org.openrewrite.maven</groupId>
                    <artifactId>rewrite-maven-plugin</artifactId>
                    <version>6.15.0</version>
                    <configuration>
                        <exportDatatables>true</exportDatatables>
                        <activeRecipes>
                            <recipe>org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_1</recipe>
                            <recipe>org.openrewrite.java.migrate.UpgradeToJava21</recipe>
                        </activeRecipes>
                    </configuration>
                    <dependencies>
                        <dependency>
                            <groupId>org.openrewrite.recipe</groupId>
                            <artifactId>rewrite-migrate-java</artifactId>
                            <version>3.14.1</version>
                        </dependency>
                        <dependency>
                            <groupId>org.openrewrite.recipe</groupId>
                            <artifactId>rewrite-spring</artifactId>
                            <version>6.11.1</version>
                        </dependency>
                    </dependencies>
                </plugin>
            """.stripIndent();

            lines.add(pluginsIndex, plugin);
            Files.write(pomPath, lines);
        }

        ProcessBuilder pb = new ProcessBuilder()
                .directory(new File(repo.localDir))
                .command(System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn", "rewrite:run");

        pb.environment().put("JAVA_HOME", javaHome);
        pb.environment().put("PATH", javaHome + File.separator + "bin" + File.pathSeparator + System.getenv("PATH"));

        pb.inheritIO();
        Process p = pb.start();
        if (p.waitFor() != 0) {
            throw new RuntimeException("OpenRewrite failed for " + repo.name + " using Java " + javaVersion);
        }
    }
}