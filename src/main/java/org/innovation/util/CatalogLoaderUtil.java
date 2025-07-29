package org.innovation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.experimental.UtilityClass;
import org.innovation.config.RepoConfig;
import org.innovation.exception.CatalogException;
import org.yaml.snakeyaml.Yaml;

@UtilityClass
public class CatalogLoaderUtil {

    public static List<RepoConfig> loadCatalog(String resourceName) throws IOException, CatalogException {
        var yaml = new Yaml();
        var repos = new ArrayList<RepoConfig>();

        try (var in = CatalogLoaderUtil.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new CatalogException(resourceName + " not found in resources");
            }
            Map<String, Object> obj = yaml.load(in);
            var repoObj = obj.get("repositories");
            if (!(repoObj instanceof List<?> repoList)) {
                throw new CatalogException("'repositories' is not a list in " + resourceName);
            }
            for (Object item : repoList) {
                if (!(item instanceof Map<?, ?> map)) {
                    throw new CatalogException("Repository entry is not a map in " + resourceName);
                }
                repos.add(new RepoConfig(
                        String.valueOf(map.get("name")),
                        String.valueOf(map.get("url"))
                ));
            }
        }

        return repos;
    }
}