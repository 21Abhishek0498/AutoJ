package com.auto.gen.junit.autoj.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for handling Maven dependencies and related operations.
 */
@Service
@Slf4j
public class ServiceImpl {

    @Autowired
    private Environment env;

    /**
     * Retrieves the latest version of a Maven artifact from the Maven Central Repository.
     *
     * @param groupId    The group ID of the artifact.
     * @param artifactId The artifact ID.
     * @return The latest version of the artifact.
     * @throws IOException If an error occurs during the HTTP request.
     */
    public static String getLatestVersion(String groupId, String artifactId) throws IOException {
        String apiUrl = "https://search.maven.org/solrsearch/select?q=g:%22" + groupId + "%22+AND+a:%22" + artifactId + "%22&core=gav&rows=1&wt=json";

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString();
            int versionStartIndex = json.indexOf("\"v\":\"") + 5;
            int versionEndIndex = json.indexOf("\"", versionStartIndex);
            return json.substring(versionStartIndex, versionEndIndex);
        }
    }

    /**
     * Retrieves dependency strings from the environment property "custom.dependencies".
     *
     * @return List of dependency strings.
     */
    private List<String> getDependencyStrings() {
        String dependenciesProperty = env.getProperty("custom.dependencies");

        if (dependenciesProperty != null) {
            return Arrays.asList(dependenciesProperty.split(","));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Converts a list of dependency strings into a list of Dependency objects.
     *
     * @return List of Dependency objects.
     */
    public List<Dependency> getDependencies() {
        return convertStringsToDependencies(getDependencyStrings());
    }

    /**
     * Checks for missing dependencies in the provided POM file and adds them if necessary.
     *
     * @param pomFilePath The path to the POM file.
     * @return A message indicating the status of the operation.
     * @throws Exception If an error occurs during the operation.
     */
    public String checkAndAdd(String pomFilePath) throws Exception {
        Model model = readModel(pomFilePath);
        List<Dependency> requiredDependencies = getDependencies();
        List<Dependency> dependenciesPresent = model.getDependencies();
        List<Dependency> missingDependencies = new ArrayList<>();

        for (Dependency requiredDependency : requiredDependencies) {
            boolean isPresent = dependenciesPresent.stream().anyMatch(dep -> dep.getArtifactId().equals(requiredDependency.getArtifactId()));
            if (!isPresent) {
                missingDependencies.add(requiredDependency);
            }
        }

        if (!missingDependencies.isEmpty()) {
            log.info("The following dependencies are missing in the pom.xml file:");
            for (Dependency missingDependency : missingDependencies) {
                addDependencyToPom(missingDependency, pomFilePath, model);
            }

            mavenBuild(pomFilePath);
        } else {
            return "All required dependencies are present in the pom.xml file.";
        }
        return "Added Missing Dependencies";
    }

    /**
     * Adds a dependency to the POM model and writes the updated model to the specified file.
     *
     * @param dependency The Dependency object to be added.
     * @param path       The path to the POM file.
     * @param model      The POM model to be updated.
     * @throws Exception If an error occurs during the operation.
     */
    public void addDependencyToPom(Dependency dependency, String path, Model model) throws Exception {
        if (StringUtils.isNotEmpty(dependency.getVersion())) {
            dependency.setVersion(getLatestVersion(dependency.getGroupId(), dependency.getArtifactId()));
        }
        model.addDependency(dependency);
        writeModel(model, path);
    }

    /**
     * Retrieves Java and Spring Boot versions from the specified POM file.
     *
     * @param pathOfFile The path to the POM file.
     * @return A map containing Java and Spring Boot versions.
     * @throws Exception If an error occurs during the operation.
     */
    public Map<String, String> getJavaVersionAndSpringVersion(String pathOfFile) throws Exception {
        Model model = readModel(pathOfFile);
        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("Java Version", model.getProperties().getProperty("java.version"));
        dependencies.put("Spring Boot Version", model.getParent().getVersion());
        return dependencies;
    }

    /**
     * Reads a POM model from the specified file.
     *
     * @param path The path to the POM file.
     * @return The POM model read from the file.
     * @throws Exception If an error occurs during the operation.
     */
    private Model readModel(String path) throws Exception {
        try (FileReader fileReader = new FileReader(path)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileReader);
        }
    }

    /**
     * Writes a POM model to the specified file.
     *
     * @param model The POM model to be written.
     * @param path  The path to the POM file.
     * @throws Exception If an error occurs during the operation.
     */
    private void writeModel(Model model, String path) throws Exception {
        try (FileWriter fileWriter = new FileWriter(path)) {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fileWriter, model);
        }
    }

    /**
     * Builds a Maven project using the provided POM file.
     *
     * @param path The path to the POM file.
     * @throws Exception If the Maven build fails.
     */
    public void mavenBuild(String path) throws Exception {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(path));
        request.setGoals(Arrays.asList("clean", "dependency:resolve", "compile"));

        Invoker invoker = new DefaultInvoker();
        // Check if Maven is present in the system
        String mavenHomePath = System.getenv("M2_HOME");
        if (mavenHomePath != null) {
            invoker.setMavenHome(new File(mavenHomePath));
        } else {
            // Manually set the Maven home path if not present in the system environment variables
            invoker.setMavenHome(new File("C:/apache-maven-3.9.6"));
        }
        InvocationResult result = invoker.execute(request);

        if (result.getExitCode() != 0) {
            throw new MavenInvocationException("Maven build failed with exit code: " + result.getExitCode(), result.getExecutionException());
        } else {
            log.info("Maven build completed successfully.");

        }
    }

    /**
     * Converts a list of dependency strings into a list of Dependency objects.
     *
     * @param dependencyStrings List of dependency strings.
     * @return List of Dependency objects.
     */
    private List<Dependency> convertStringsToDependencies(List<String> dependencyStrings) {
        return dependencyStrings.stream().map(this::convertStringToDependency).collect(Collectors.toList());
    }


    /**
     * Converts a dependency string into a Dependency object.
     *
     * @param dependencyString The dependency string in the format "groupId:artifactId:version".
     * @return The corresponding Dependency object.
     * @throws IllegalArgumentException If the dependency string is invalid.
     */
    private Dependency convertStringToDependency(String dependencyString) {
        String[] parts = dependencyString.split(":");
        Dependency dependency = new Dependency();
        dependency.setGroupId(parts[0]);
        dependency.setArtifactId(parts[1]);
        dependency.setVersion(StringUtils.defaultIfBlank(parts[2], null));
        dependency.setScope(StringUtils.defaultIfBlank(parts[3], null));

        return dependency;
    }

}