package org.innovation;

import org.innovation.config.RepoConfig;
import org.innovation.util.CatalogLoaderUtil;
import org.innovation.service.GitHandlerService;
import org.innovation.util.GitHubPRUtil;
import org.innovation.util.OpenRewriteRunnerUtil;

public class Main {
    public static void main(String[] args) throws Exception {
        var repos = CatalogLoaderUtil.loadCatalog("catalog.yml");
        for (RepoConfig repo : repos) {
            var git = new GitHandlerService(repo);
            git.cloneRepo();
            git.checkoutAndCreateBranchFrom("develop", "upgrade-springboot-java");

            OpenRewriteRunnerUtil.runRewrite(repo);

            git.commitAndPushChanges();
            GitHubPRUtil.createPullRequest(repo);
        }
    }
}