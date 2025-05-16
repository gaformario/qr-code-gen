package com.gaformario.generate.qrcode.ports;

public interface StoragePort {

    String uploadFile(byte[] fileData, String fileName, String contentType);
}
