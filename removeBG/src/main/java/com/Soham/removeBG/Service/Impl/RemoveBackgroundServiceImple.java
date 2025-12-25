package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.Client.ClipdropCliend;
import com.Soham.removeBG.Service.RemoveBackgroundService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j

public class RemoveBackgroundServiceImple implements RemoveBackgroundService {
    @Value("${clipdrop_api}")
    private String key;
    private final ClipdropCliend clipdropCliend;


    @Override
    public byte[] removeBackground(MultipartFile file){
        log.info("Received request to remove background. fileName={}, fileSize={}",
                file.getOriginalFilename(), file.getSize());
        log.info("Calling ClipDrop API to remove background");
        return clipdropCliend.removeBackground(file,key);


    }

}
