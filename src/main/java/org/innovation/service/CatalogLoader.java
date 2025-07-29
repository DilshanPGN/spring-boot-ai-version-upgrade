package org.innovation.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.innovation.config.RepoConfig;
import org.yaml.snakeyaml.Yaml;

public class CatalogLoader {
    public static List<RepoConfig> loadCatalog(String resourceName) throws Exception {
        Yaml yaml = new Yaml();
        List<RepoConfig> repos = new ArrayList<>();
        try (InputStream in = CatalogLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalArgumentException(resourceName + " not found in resources");
            }
            Map<String, Object> obj = yaml.load(in);
            List<Map<String, String>> repoList = (List<Map<String, String>>) obj.get("repositories");
            for (Map<String, String> map : repoList) {
                repos.add(new RepoConfig(map.get("name"), map.get("url")));
            }
        }
        return repos;
    }
}