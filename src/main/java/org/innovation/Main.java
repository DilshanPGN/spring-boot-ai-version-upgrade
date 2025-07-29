package org.innovation;

import org.innovation.config.RepoConfig;
import org.innovation.service.CatalogLoader;
import org.innovation.service.GitHandler;
import org.innovation.service.GitHubPRService;
import org.innovation.service.OpenRewriteRunner;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<RepoConfig> repos = CatalogLoader.loadCatalog("catalog.yml");
        for (RepoConfig repo : repos) {
            GitHandler git = new GitHandler(repo);
            git.cloneRepo();
            git.checkoutAndCreateBranchFrom("develop", "upgrade-springboot-java");

            OpenRewriteRunner.runRewrite(repo);

            git.commitAndPushChanges();
            GitHubPRService.createPullRequest(repo);
        }
    }
}