package org.innovation.util;

import lombok.experimental.UtilityClass;
import org.innovation.config.RepoConfig;
import org.kohsuke.github.*;

import java.io.IOException;

@UtilityClass
public class GitHubPRUtil {
    public static void createPullRequest(RepoConfig repo) throws IOException {
        var token = System.getenv("GITHUB_TOKEN");
        var github = new GitHubBuilder().withOAuthToken(token).build();
        var ghRepo = github.getRepository(repo.getUrl().replace("https://github.com/", "").replace(".git", ""));

        ghRepo.createPullRequest(
            "Upgrade Spring Boot and Java",
            "upgrade-springboot-java",
            "develop",
            "This PR upgrades Spring Boot and Java using OpenRewrite."
        );
    }
}