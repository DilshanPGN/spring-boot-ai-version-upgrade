package org.innovation.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.innovation.config.RepoConfig;
import org.kohsuke.github.*;

import java.io.IOException;

@UtilityClass
@Log4j2
public class GitHubPRUtil {
    public static void createPullRequest(RepoConfig repo) throws IOException {
        var token = System.getenv("GITHUB_TOKEN");
        var github = new GitHubBuilder().withOAuthToken(token).build();
        var ghRepo = github.getRepository(repo.getUrl().replace("https://github.com/", "").replace(".git", ""));
        var timestamp = System.currentTimeMillis();

        try {
            ghRepo.createPullRequest(
                    "Upgrade Spring Boot and Java - " + timestamp,
                    "upgrade-springboot-java",
                    "develop",
                    "This PR upgrades Spring Boot and Java using OpenRewrite."
            );
        } catch (HttpException e) {
            if (e.getResponseCode() == 422) {
                log.error("Pull request already exists for {}", repo.getName());
            } else {
                throw e; // Re-throw other HTTP exceptions
            }
        } catch (IOException e) {
            log.error("Failed to create pull request for {}: {}", repo.getName(), e.getMessage());
            throw e; // Re-throw IOException
        }

    }
}