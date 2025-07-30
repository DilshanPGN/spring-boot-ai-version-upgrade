package org.innovation.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.innovation.config.RepoConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@UtilityClass
@Log4j2
public class RepoHandlerUtil {

    public static void deleteLocalRepo(RepoConfig repo) throws IOException {
        var repoPath = Path.of(repo.getLocalDir());
        if (Files.exists(repoPath)) {
            try (var paths = Files.walk(repoPath).sorted(Comparator.reverseOrder())) {
                paths.forEach(path -> {
                    var writable = path.toFile().setWritable(true);
                    if (!writable) {
                        log.warn("Could not set writable: {}", path);
                    }
                    try {
                        Files.delete(path);
                    } catch (Exception e) {
                        log.error("Failed to delete {}: {}", path, e.getMessage());
                    }
                });
            }
        }
    }
}
