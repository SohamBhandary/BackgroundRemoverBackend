package com.Soham.removeBG.Service;

import org.springframework.web.multipart.MultipartFile;

public interface RemoveBackgroundService {
   byte[] removeBackground(MultipartFile file);
}
