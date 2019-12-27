package com.storyboard.modProtector.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IConfigFile {
    void load(InputStream is) throws IOException;
    void save(OutputStream os) throws IOException;
}
