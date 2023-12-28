package com.auto.gen.junit.autoj.service;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceImpl {

    public Map<String, String> getJavaVersionAndSpringVersion(String pathOfFile) throws Exception {
        Model model = readModel(pathOfFile);
        Map<String, String> dependencies = new HashMap<>();
        dependencies.put("Java Version", model.getProperties().getProperty("java.version"));
        dependencies.put("Spring Boot Version", model.getParent().getVersion());
        return dependencies;
    }

    private Model readModel(String path) throws Exception {
        try (FileReader fileReader = new FileReader(path)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileReader);
        }
    }

    public boolean isDependencyPresent(String groupId, String artifactId, String path) throws Exception {
        return isDependencyPresentInPom(readModel(path), groupId, artifactId);
    }

    public void addDependencyToPom(String groupId, String artifactId, String version, String path) throws Exception {
        Model model = readModel(path);
        addDependencyToModel(model, groupId, artifactId, version);
        writeModel(model, path);
    }

    public void mavenBuild(String path) throws Exception {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(path, "pom.xml"));
        request.setGoals(Arrays.asList("clean", "dependency:resolve","compile"));

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);

        if (result.getExitCode() != 0) {
            throw new MavenInvocationException("Maven build failed with exit code: " + result.getExitCode(), result.getExecutionException());
        } else {
            System.out.println("Maven build completed successfully.");
            
        }
    }

    private void writeModel(Model model, String path) throws Exception {
        try (FileWriter fileWriter = new FileWriter(path)) {
            MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fileWriter, model);
        }
    }

    private boolean isDependencyPresentInPom(Model model, String groupId, String artifactId) {
        for (Dependency dependency : model.getDependencies()) {
            if (groupId.equals(dependency.getGroupId()) && artifactId.equals(dependency.getArtifactId())) {
                return true;
            }
        }
        return false;
    }

    private void addDependencyToModel(Model model, String groupId, String artifactId, String scope)  {
        Dependency newDependency = new Dependency();
        newDependency.setGroupId(groupId);
        newDependency.setArtifactId(artifactId);
        newDependency.setScope(scope);
        //newDependency.setVersion(getLatestVersion(groupId, artifactId));

        model.addDependency(newDependency);
    }

    public static String getLatestVersion(String groupId, String artifactId) throws IOException {
        String apiUrl = "https://search.maven.org/solrsearch/select?q=g:%22"
                + groupId + "%22+AND+a:%22" + artifactId + "%22&core=gav&rows=1&wt=json";

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


}
