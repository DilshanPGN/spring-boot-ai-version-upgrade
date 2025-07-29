package org.innovation.config;

import lombok.Getter;

@Getter
public class RepoConfig {
    private final String name;
    private final String url;
    private final String localDir;

    public RepoConfig(String name, String url) {
        this.name = name;
        this.url = url;
        this.localDir = "repos/" + name;
    }
}
