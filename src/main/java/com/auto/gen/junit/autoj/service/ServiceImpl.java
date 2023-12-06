package com.auto.gen.junit.autoj.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auto.gen.junit.autoj.constants.Constants;
import com.auto.gen.junit.autoj.mapper.CommonObjectMapper;
import com.auto.gen.junit.autoj.parser.ParseJavaFile;

@Service
public class ServiceImpl {

    @Autowired
    private ParseJavaFile parseJavaFile;

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

    public void addDependencyToPom(String groupId, String artifactId, String scope, String path) throws Exception {
        Model model = readModel(path);
        addDependencyToModel(model, groupId, artifactId, scope);
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


/**
* This method is used to add a dependency to the POM file.
 * It takes the groupId, artifactId, and scope as parameters.
 * It creates a new Dependency object, sets the groupId, artifactId, and scope,
 * and adds it to the model's dependencies.
 * Finally, it writes the updated model to the POM file.
 * @param model The Maven model object representing the POM file.
 * @param groupId The Maven group ID of the dependency.
 * @param artifactId The Maven artifact ID of the dependency artifact
* */
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

    public void checkDedendencyAndBuildTest(String path) throws Exception {
    try {
        if (isDependencyPresent(Constants.GROUP_ID, Constants.ARTIFACT_ID, path)) {
            System.out.println("Dependency already present in the POM file. No changes made.");
        } else {
            addDependencyToPom(Constants.GROUP_ID, Constants.ARTIFACT_ID, Constants.SCOPE_ID, path);
            mavenBuild(path);
            System.out.println("Dependency added to POM file. Maven build triggered.");
        }
        parseAndSave(new File(path));

    }
    catch (Exception e) {

    }

    }


    private void parseAndSave(File directory) throws IOException {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    parseAndSave(file);
                } else if (file.getName().endsWith(".java")) {
                    parseJavaFile.startParsing(file.getName());
                    String jsonString = CommonObjectMapper.toJsonString(parseJavaFile.startParsing(file.getName()));
                    // Save JSON string to MongoDB
                   // saveToMongoDB(file.getName(), jsonString, collection);
                }
            }
        }

    }

//    private static void saveToMongoDB(String className, String jsonString, MongoCollection<Document> collection) {
//        Map<String, Object> documentMap = new HashMap<>();
//        documentMap.put("class_name", className);
//        documentMap.put("json_string", jsonString);
//
//        Document document = new Document(documentMap);
//        collection.insertOne(document);
//    }
}


