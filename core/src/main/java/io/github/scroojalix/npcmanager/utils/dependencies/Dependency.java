package io.github.scroojalix.npcmanager.utils.dependencies;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.github.scroojalix.npcmanager.utils.dependencies.relocation.Relocation;

public enum Dependency {
    ASM(
        "org.ow2.asm",
        "asm",
        "7.1",
        "SrL6K20sycyx6qBeoynEB7R7E+0pFfYvjEuMyWJY1N4="
    ),
    ASM_COMMONS(
        "org.ow2.asm",
        "asm-commons",
        "7.1",
        "5VkEidjxmE2Fv+q9Oxc3TFnCiuCdSOxKDrvQGVns01g="
    ),
    JAR_RELOCATOR(
        "me.lucko",
        "jar-relocator",
        "1.4",
        "1RsiF3BiVztjlfTA+svDCuoDSGFuSpTZYHvUK8yBx8I="
    ),

    MONGODB_DRIVER(
        "org.mongodb",
        "mongo-java-driver",
        "3.12.7",
        "D/zgBJWNb9mzmuetJ37a0X9XtpcfSGsXYpxe6eE8Tao=",
        Relocation.of("mongodb", "com{}mongodb"),
        Relocation.of("bson", "org{}bson")
    );

    private final String mavenRepoPath;
    private final String version;
    private final byte[] checksum;
    private final List<Relocation> relocations;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    Dependency(String groupId, String artifactId, String version, String checksum) {
        this(groupId, artifactId, version, checksum, new Relocation[0]);
    }

    Dependency(String groupId, String artifactId, String version, String checksum, Relocation... relocations) {
        this.mavenRepoPath = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );
        this.version = version;
        this.checksum = Base64.getDecoder().decode(checksum);
        this.relocations
         = ImmutableList.copyOf(relocations);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    public String getFileName() {
        return name().toLowerCase().replace('_', '-') + "-" + this.version;
    }

    String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public byte[] getChecksum() {
        return this.checksum;
    }

    public boolean checksumMatches(byte[] hash) {
        return Arrays.equals(this.checksum, hash);
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
