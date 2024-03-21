package com.minio.storage.process;

import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.entities.File;
import com.minio.storage.provider.StorageProvider;
import com.minio.storage.utils.constant.Constants;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MediaProcess {

    public FileAttributeDTO upload(FileAttributeDTO fileAttributeDTO, String location) throws IOException {
        if (fileAttributeDTO.getContentType().startsWith(Constants.IMAGE_TYPE)) {
            // ImageProcess.resizeImageToHD(fileAttributeDTO, location);
        } else if (fileAttributeDTO.getContentType().startsWith(Constants.VIDEO_TYPE)) {
            VideoProcess.upload(fileAttributeDTO, location);
        }
        return fileAttributeDTO;
    }

    public static FileAttributeDTO upload(File file, FileAttributeDTO fileAttributeDTO, String location) throws IOException {
        if (fileAttributeDTO.getContentType().startsWith(Constants.IMAGE_TYPE)) {
            // ImageProcess.resizeImageToHD(file, fileAttributeDTO, location);
        } else if (fileAttributeDTO.getContentType().startsWith(Constants.VIDEO_TYPE)) {
            VideoProcess.upload(file, fileAttributeDTO, location);
        }
        return fileAttributeDTO;
    }

    public FileAttributeDTO upload(FileAttributeDTO fileAttributeDTO, StorageProvider provider) throws IOException {
        if (fileAttributeDTO.getContentType().startsWith(Constants.IMAGE_TYPE)) {
            // ImageProcess.resizeImageToHD(fileAttributeDTO, provider);
        } else if (fileAttributeDTO.getContentType().startsWith(Constants.VIDEO_TYPE)) {
            // @@@ FIXME: need clear to make thumbnail and symbolic link to broadcast video with minio
            VideoProcess.upload(fileAttributeDTO, provider);
        }
        return fileAttributeDTO;
    }

}
