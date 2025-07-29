package org.innovation.config;

public class RepoConfig {
    public String name;
    public String url;
    public String localDir;

    public RepoConfig(String name, String url) {
        this.name = name;
        this.url = url;
        this.localDir = "repos/" + name;
    }
}
