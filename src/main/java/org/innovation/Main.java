package org.innovation;

import org.innovation.config.RepoConfig;
import org.innovation.util.CatalogLoaderUtil;
import org.innovation.service.GitHandlerService;
import org.innovation.util.GitHubPRUtil;
import org.innovation.util.OpenRewriteRunnerUtil;
import org.innovation.util.RepoHandlerUtil;

public class Main {

    private static final String BASE_BRANCH_NAME = "develop";
    private static final String NEW_BRANCH_NAME = "upgrade-springboot-java";
    public static void main(String[] args) throws Exception {

        var repos = CatalogLoaderUtil.loadCatalog("catalog.yml");
        for (RepoConfig repo : repos) {
            var git = new GitHandlerService(repo);
            git.cloneRepo();
            git.deleteLocalBranchIfExists(NEW_BRANCH_NAME);
            git.checkoutAndCreateBranchFrom(BASE_BRANCH_NAME, NEW_BRANCH_NAME);

            OpenRewriteRunnerUtil.runRewrite(repo);

            git.commitAndPushChanges();
            GitHubPRUtil.createPullRequest(repo);

            RepoHandlerUtil.deleteLocalRepo(repo);
        }
    }
}