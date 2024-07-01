package com.ws.base.util;

import com.ws.base.constant.HttpStatus;
import com.ws.base.exception.BaseException;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class FileUploadUtil {

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    public static final String upload(String baseDir, MultipartFile file, List<String> allowedExtension) {
        // 文件名称长度
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > FileUploadUtil.DEFAULT_FILE_NAME_LENGTH) {
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("file.fileNameLengthError"));
        }
        // 判断文件格式
        assertAllowed(file, allowedExtension);

        String absPath = null;
        try {
            absPath = getAbsoluteFile(baseDir, file.getOriginalFilename()).getAbsolutePath();
            file.transferTo(Paths.get(absPath));
        } catch (IOException e) {
            throw new BaseException(HttpStatus.ERROR,MessageUtil.getMessage("file.fileUploadError"));
        }
        return "/bmFile/"+file.getOriginalFilename();
    }

    public static final void assertAllowed(MultipartFile file, List<String> allowedExtension) {
        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("file.fileLengthError"));
        }

        String extension = getExtension(file);
        if (allowedExtension != null && !allowedExtension.contains(extension)) {
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("file.fileTypeError"));
        }
    }


    /**
     * 获取文件后缀
     * @param file
     * @return
     */
    public static final String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int iIndex = fileName.lastIndexOf(".");
        if (iIndex < 0)
            return "";
        return fileName.substring(iIndex + 1).toLowerCase();
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        int iIndex = fileName.lastIndexOf(".");
        if (iIndex < 0)
            return fileName;
        return fileName.substring(0, iIndex);
    }

    /**
     * 获取文件绝对路径
     * @param uploadDir
     * @param fileName
     * @return
     * @throws IOException
     */
    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);
        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }
}
