package de.juliansauer;

import io.gaiapipeline.javasdk.Handler;
import io.gaiapipeline.javasdk.Javasdk;
import io.gaiapipeline.javasdk.PipelineJob;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class Pipeline {

    private static final Logger LOGGER = Logger.getLogger(Pipeline.class.getName());

    private static GradleConnector connector;

    public Pipeline() {
        connector = GradleConnector.newConnector();
        File projectDir = new File(System.getProperty("user.dir"));
        System.out.println();
        connector.forProjectDirectory(projectDir);
    }

    private static Handler jobHandler = (gaiaArgs) -> {
        LOGGER.info("Starting gradle build");
        ProjectConnection connection = connector.connect();
        BuildLauncher build = connection.newBuild();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        build.setStandardOutput(outputStream);
        build.setStandardError(errorStream);

        build.forTasks("run");
        build.run();
        connection.close();

        LOGGER.info(outputStream.toString());
        LOGGER.severe(errorStream.toString());
        LOGGER.info("Gradle build done");
    };

    public static void main(String[] args) {
        PipelineJob job = new PipelineJob();
        job.setTitle("Compile");
        job.setDescription("Builds jar");
        job.setHandler(jobHandler);

        Javasdk sdk = new Javasdk();
        try {
            sdk.Serve(new ArrayList<>(Arrays.asList(job)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
