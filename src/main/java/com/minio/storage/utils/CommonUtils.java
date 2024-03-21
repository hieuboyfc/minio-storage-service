package com.minio.storage.utils;

import com.minio.storage.dto.FileAttributeDTO;
import com.minio.storage.utils.constant.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonUtils {


    /* How can I convert byte size into a human-readable format
                                  SI     BINARY

                       0:        0 B        0 B
                      27:       27 B       27 B
                     999:      999 B      999 B
                    1000:     1.0 kB     1000 B
                    1023:     1.0 kB     1023 B
                    1024:     1.0 kB    1.0 KiB
                    1728:     1.7 kB    1.7 KiB
                  110592:   110.6 kB  108.0 KiB
                 7077888:     7.1 MB    6.8 MiB
               452984832:   453.0 MB  432.0 MiB
             28991029248:    29.0 GB   27.0 GiB
           1855425871872:     1.9 TB    1.7 TiB
     9223372036854775807:     9.2 EB    8.0 EiB   (Long.MAX_VALUE)
     ==============================================================*/

    public static StringBuffer getRealPath(String root, FileAttributeDTO fileAttribute) throws IOException {
        StringBuffer realPath = new StringBuffer(root);
        realPath.append(File.separator).append(fileAttribute.getCompanyId().toString());
        realPath.append(File.separator).append(DateTimeUtils.format(fileAttribute.getCreateDate(), Constants.DATE_DEFAULT));
        realPath.append(File.separator).append(fileAttribute.getCreateBy());
        Path path = Paths.get(realPath.toString());
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        realPath.append(File.separator).append(fileAttribute.getId());
        return realPath;
    }

}
