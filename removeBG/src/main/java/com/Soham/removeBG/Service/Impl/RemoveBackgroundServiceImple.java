package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.Client.ClipdropCliend;
import com.Soham.removeBG.Service.RemoveBackgroundService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor

public class RemoveBackgroundServiceImple implements RemoveBackgroundService {
    @Value("${clipdrop_api}")
    private String key;
    private final ClipdropCliend clipdropCliend;

    @Override
    public byte[] removeBackground(MultipartFile file){
        return clipdropCliend.removeBackground(file,key);

    }

}
