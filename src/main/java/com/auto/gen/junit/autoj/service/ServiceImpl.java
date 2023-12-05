package com.auto.gen.junit.autoj.service;

import com.auto.gen.junit.autoj.dto.ParsedClassDto;
import com.auto.gen.junit.autoj.generator.Generator;
import com.auto.gen.junit.autoj.mapper.CommonObjectMapper;
import com.auto.gen.junit.autoj.model.ParsedClass;
import com.auto.gen.junit.autoj.repository.ParsedClassRepository;
import com.github.javaparser.ParseProblemException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceImpl {

    @Autowired
    private Generator parseJavaFile;

    @Autowired
    private ParsedClassRepository repo;

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

//    public void checkDedendencyAndBuildTest(String path) throws Exception {
//    try {
//        repo.deleteAll();
//        var directory=new File(path);
//
//        if (directory.isDirectory()) {
//            for (File file : directory.listFiles()) {
//               if (file.getName().endsWith(".java")) {
//
//                    String jsonString = CommonObjectMapper.toJsonString(parseJavaFile.startParsing(file.getName()));
//                    // Save JSON string to MongoDB
//                    ParsedClassDto dto = ParsedClassDto.builder().ClassName(file.getName())
//                            .payload(jsonString).createdDate(new java.util.Date()).version(1L)
//                            .build();
//                    saveToMongoDB(dto);
//
//                }
//            }
//        }
//
//    } catch
//    (ParseProblemException e) {
//
//        e.getProblems().forEach(problem -> System.err.println(
//                "Problem: "
//                        + problem));
//
//// Handle the exception or rethrow if needed
//
//    } catch (Exception e) {
//        // Handle the exception according to your requirements
//        e.printStackTrace();
//        throw new Exception(e.getMessage());
//    }
//
//    }

    private void saveToMongoDB(ParsedClassDto dto) {
        ParsedClass entity = CommonObjectMapper.toEntity(dto);
        repo.save(entity);
        System.out.println(repo.findAll().toString());

    }

    public void checkDedendencyAndBuildTest(String path) throws Exception {
        try {
            repo.deleteAll();
            var directory = new File(path);

            if (directory.isDirectory()) {
                for (File file : directory.listFiles()) {
                    if (file.getName().endsWith(".java")) {

                        String jsonString = CommonObjectMapper.toJsonString(parseJavaFile.generate(directory.toPath().toString()));
                        // Save JSON string to MongoDB
                        ParsedClassDto dto = ParsedClassDto.builder().ClassName(file.getName())
                                .payload(jsonString).createdDate(new java.util.Date()).version(1L)
                                .build();
                        saveToMongoDB(dto);

                    }
                }
            }

        } catch
        (ParseProblemException e) {

            e.getProblems().forEach(problem -> System.err.println(
                    "Problem: "
                            + problem));

        } catch (Exception e) {
            // Handle the exception according to your requirements
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }

}


