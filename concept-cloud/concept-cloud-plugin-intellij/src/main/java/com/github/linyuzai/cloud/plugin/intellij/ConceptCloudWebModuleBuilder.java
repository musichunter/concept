package com.github.linyuzai.cloud.plugin.intellij;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.icons.AllIcons;
import com.intellij.ide.starters.remote.*;
import com.intellij.ide.starters.shared.StarterLanguage;
import com.intellij.ide.starters.shared.StarterProjectType;
import com.intellij.ide.starters.shared.StarterSettings;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.Url;
import com.intellij.util.Urls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("all")
public class ConceptCloudWebModuleBuilder extends WebStarterModuleBuilder {

    @NotNull
    @Override
    protected Url composeGeneratorUrl(@NotNull String s, @NotNull WebStarterContext webStarterContext) {
        String url;
        String path = s.replace("https://", "").replace("http://", "");
        if (path.endsWith("/")) {
            url = path + "java.zip";
        } else {
            url = path + "/java.zip";
        }
        return Urls.newUrl("https", "", url);
    }

    @Override
    protected void extractGeneratorResult(@NotNull File download, @NotNull File parent) {
        //Decompressor.Zip
        try (ZipFile zf = new ZipFile(download)) {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            List<ZipEntry> files = new ArrayList<>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    String dirName = transform(entry.getName());
                    String s = dirName.replaceAll("\\.", "/");
                    createDir(s, parent);
                } else {
                    files.add(entry);
                }
            }
            for (ZipEntry file : files) {
                String fileName = transform(file.getName());
                File create = createFile(fileName, parent);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(file), StandardCharsets.UTF_8))) {
                    String collect = br.lines().collect(Collectors.joining("\n"));
                    FileUtil.writeToFile(create, transform(collect));
                }
            }
        } catch (Throwable e) {
            Messages.showErrorDialog("Unzip error: " + e.getMessage(), getPresentableName());
        }
        /*try (ZipInputStream zis = new ZipInputStream(new FileInputStream(download), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {

                } else {

                }
            }
        } catch (Throwable e) {

        }*/
        //ZipUtil.extract();
    }

    private void createDir(String name, File parent) {
        File file = new File(parent, name);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private File createFile(String name, File parent) throws IOException {
        File file = new File(parent, name);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private String transform(String content) {
        String group = getStarterContext().getGroup();
        String artifact = getStarterContext().getArtifact();
        String pkg = group.toLowerCase() + "." + artifact.toLowerCase();
        String cls = artifact.substring(0, 1).toUpperCase() + artifact.substring(1);
        return content.replaceAll("\\$GROUP\\$", group)
                .replaceAll("\\$ARTIFACT\\$", artifact)
                .replaceAll("\\$PACKAGE\\$", pkg)
                .replaceAll("\\$CLASS\\$", cls);
    }

    @NotNull
    @Override
    public String getBuilderId() {
        return "concept-cloud";
    }

    @NotNull
    @Override
    public String getDefaultServerUrl() {
        return "https://raw.githubusercontent.com/Linyuzai/concept/master/concept-cloud/plugin";
        //return "https://cdn.jsdelivr.net/gh/Linyuzai/concept/concept-cloud/plugin";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Generate a project based on Gradle that supports both Spring Cloud and Spring Boot";
    }

    @NotNull
    @Override
    protected List<StarterLanguage> getLanguages() {
        return Collections.singletonList(StarterSettings.getJAVA_STARTER_LANGUAGE());
    }

    @Nullable
    @Override
    public Icon getNodeIcon() {
        return AllIcons.Nodes.Module;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Concept Cloud";
    }

    @NotNull
    @Override
    protected List<StarterProjectType> getProjectTypes() {
        return Collections.singletonList(StarterSettings.getGRADLE_PROJECT());
    }

    @NotNull
    @Override
    protected WebStarterServerOptions loadServerOptions(@NotNull String s) {
        String url;
        if (s.endsWith("/")) {
            url = s + "starter.json";
        } else {
            url = s + "/starter.json";
        }
        JsonElement json = loadJsonData(url, null);
        JsonObject object = json.getAsJsonObject();
        List<WebStarterFrameworkVersion> frameworkVersions = new ArrayList<>();
        JsonArray cloudArray = object.get("cloud").getAsJsonArray();
        for (JsonElement e : cloudArray) {
            JsonObject cloudObject = e.getAsJsonObject();
            String cloudVersion = cloudObject.get("version").getAsString();
            frameworkVersions.add(new WebStarterFrameworkVersion(cloudVersion, "Concept Cloud v" + cloudVersion, false));
        }
        List<WebStarterDependencyCategory> dependencyCategories = new ArrayList<>();
        JsonObject dependencyObject = object.get("dependency").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : dependencyObject.entrySet()) {
            JsonArray dependencyArray = entry.getValue().getAsJsonArray();
            List<WebStarterDependency> dependencies = new ArrayList<>();
            for (JsonElement dependency : dependencyArray) {
                String string = dependency.getAsString();
                dependencies.add(new WebStarterDependency(string, string, null, Collections.emptyList(), false, false));
            }
            dependencyCategories.add(new WebStarterDependencyCategory(entry.getKey(), dependencies));
        }
        return new WebStarterServerOptions(frameworkVersions, dependencyCategories);
    }
}
