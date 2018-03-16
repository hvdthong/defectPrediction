package org.apache.ivy.plugins.resolver.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileURLLister implements URLLister {
    private File basedir;

    public FileURLLister() {
        this(null);
    }

    public FileURLLister(File baseDir) {
        this.basedir = baseDir;
    }

    public boolean accept(String pattern) {
        return pattern.startsWith("file");
    }

    public List listAll(URL url) throws IOException {
        String path = url.getPath();
        File file = basedir == null ? new File(path) : new File(basedir, path);
        if (file.exists() && file.isDirectory()) {
            String[] files = file.list();
            List ret = new ArrayList(files.length);
            URL context = url.getPath().endsWith("/") ? url : new URL(url.toExternalForm() + "/");
            for (int i = 0; i < files.length; i++) {
                ret.add(new URL(context, files[i]));
            }
            return ret;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public String toString() {
        return "file lister";
    }
}
