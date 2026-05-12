package es.codeurjc.proyecto_dws_grupo2.service;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Service;
import es.codeurjc.proyecto_dws_grupo2.model.Image;
import es.codeurjc.proyecto_dws_grupo2.repository.ImageRepository;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // 1. Get entity
    public Image getImage(long id) {
        return imageRepository.findById(id).orElseThrow(); 
    }

    // 2. Create and save the bytes
    public Image createImage(InputStream inputStream) throws IOException {
        Image image = new Image();
        image.setImageFile(inputStream.readAllBytes()); 
        return imageRepository.save(image);
    }

    // 3. Give the bytes to download
    public byte[] getImageFile(long id) {
        Image image = getImage(id);
        return image.getImageFile();
    }

    // 4. Delete
    public void deleteImage(long id) {
        imageRepository.deleteById(id);
    }
}