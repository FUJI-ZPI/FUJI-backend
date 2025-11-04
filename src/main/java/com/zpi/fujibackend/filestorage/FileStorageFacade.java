package com.zpi.fujibackend.filestorage;


import java.io.InputStream;

public interface FileStorageFacade {


    InputStream downloadFile(String objectKey);
}
