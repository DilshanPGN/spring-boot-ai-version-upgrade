package org.innovation.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.innovation.config.RepoConfig;

import java.io.File;

public class GitHandler {
    private final RepoConfig repo;
    private final UsernamePasswordCredentialsProvider credentials;

    public GitHandler(RepoConfig repo) {
        this.repo = repo;
        String token = System.getenv("GITHUB_TOKEN");
        this.credentials = new UsernamePasswordCredentialsProvider("spring-boot-version-upgrade-app", token);
    }

    public void cloneRepo() throws GitAPIException {
        Git.cloneRepository()
            .setURI(repo.url)
            .setDirectory(new File(repo.localDir))
            .setCredentialsProvider(credentials)
            .call();
    }

    public void createAndCheckoutBranch(String branchName) throws Exception {
        Git git = Git.open(new File(repo.localDir));
        git.checkout()
            .setCreateBranch(true)
            .setName(branchName)
            .call();
    }
    public void checkoutAndCreateBranchFrom(String baseBranch, String newBranch) throws Exception {
        Git git = Git.open(new File(repo.localDir));

        // Fetch all remote branches
        git.fetch()
                .setCredentialsProvider(credentials)
                .setRemote("origin")
                .call();

        // Checkout the base branch (e.g., develop) from origin
        git.checkout()
                .setName(baseBranch)
                .setStartPoint("origin/" + baseBranch)
                .setCreateBranch(true)
                .call();

        // Create and checkout the new feature branch
        git.checkout()
                .setCreateBranch(true)
                .setStartPoint(baseBranch)
                .setName(newBranch)
                .call();
    }


    public void commitAndPushChanges() throws Exception {
        Git git = Git.open(new File(repo.localDir));
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Upgrade Spring Boot and Java using OpenRewrite").call();
        git.push().setCredentialsProvider(credentials).call();
    }
}