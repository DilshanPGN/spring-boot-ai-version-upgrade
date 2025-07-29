package org.innovation.service;

import org.innovation.config.RepoConfig;
import org.kohsuke.github.*;

public class GitHubPRService {
    public static void createPullRequest(RepoConfig repo) throws Exception {
        String token = System.getenv("GITHUB_TOKEN");
        GitHub github = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository ghRepo = github.getRepository(repo.url.replace("https://github.com/", "").replace(".git", ""));

        ghRepo.createPullRequest(
            "Upgrade Spring Boot and Java",
            "upgrade-springboot-java",
            "develop",
            "This PR upgrades Spring Boot and Java using OpenRewrite."
        );
    }
}