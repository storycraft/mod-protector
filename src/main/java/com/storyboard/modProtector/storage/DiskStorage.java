package com.storyboard.modProtector.storage;

import java.io.*;

public class DiskStorage extends Storage<byte[]> {

    private File directory;

    public DiskStorage(File configFolder) {
        this.directory = configFolder;
    }

    public File getStorageFolder() {
        return directory;
    }

    public File getFile(String name) {
        return new File(getStorageFolder(), name);
    }
    
    public DiskStorage getSubStorage(String name) {
        return new DiskStorage(getFile(name));
    }

    public boolean createStorageDirectory() {
        if (directory.exists() && directory.isDirectory()) {
            return false;
        }

        return directory.mkdirs();
    }

    @Override
    public boolean saveSync(byte[] data, String name) throws IOException {
        File file = getFile(name);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

        writer.write(data);
        writer.close();

        return true;
    }

    @Override
    public byte[] getSync(String name) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        File file = getFile(name);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

        byte[] readBuffer = new byte[2048];
        int readed;
        while ((readed = input.read(readBuffer, 0, readBuffer.length)) != -1) {
            output.write(readBuffer, 0, readed);
        }

        input.close();
        output.close();

        return output.toByteArray();
    }

}