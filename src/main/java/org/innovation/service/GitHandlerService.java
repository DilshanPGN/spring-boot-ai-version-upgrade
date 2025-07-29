package org.innovation.service;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.innovation.config.RepoConfig;

import java.io.File;
import java.io.IOException;

@Log4j2
public class GitHandlerService {
    private final RepoConfig repo;
    private final UsernamePasswordCredentialsProvider credentials;


    public GitHandlerService(RepoConfig repo) {
        this.repo = repo;
        var token = System.getenv("GITHUB_TOKEN");
        this.credentials = new UsernamePasswordCredentialsProvider("spring-boot-version-upgrade-app", token);
    }

    public void cloneRepo() throws GitAPIException {
        try (var ignored = Git.cloneRepository()
                .setURI(repo.getUrl())
                .setDirectory(new File(repo.getLocalDir()))
                .setCredentialsProvider(credentials)
                .call()
        ) {
            log.info("Cloned repository: {}", repo.getName());
        }
    }

    public void checkoutAndCreateBranchFrom(String baseBranch, String newBranch) throws Exception {
        try (Git git = Git.open(new File(repo.getLocalDir()))) {

            log.debug("Fetching remote branches for repository: {}", repo.getName());
            git.fetch()
                    .setCredentialsProvider(credentials)
                    .setRemote("origin")
                    .call();


            log.debug("Checkout the base branch from origin");
            git.checkout()
                    .setName(baseBranch)
                    .setStartPoint("origin/" + baseBranch)
                    .setCreateBranch(true)
                    .call();

            log.debug("Creating and checking out new branch: {}", newBranch);
            git.checkout()
                    .setCreateBranch(true)
                    .setStartPoint(baseBranch)
                    .setName(newBranch)
                    .call();
        }
    }


    public void commitAndPushChanges() throws IOException, GitAPIException {
        try (var git = Git.open(new File(repo.getLocalDir()))) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Upgrade Spring Boot and Java using OpenRewrite").call();
            git.push().setCredentialsProvider(credentials).call();
        }
    }
}