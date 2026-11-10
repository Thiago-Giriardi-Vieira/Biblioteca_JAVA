package br.com.biblioteca.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Set;

public final class ImagemService {
    private static final Set<String> TIPOS = Set.of("image/jpeg", "image/png", "image/webp");

    private ImagemService() {
    }

    public static byte[] validarEExtrair(MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            return null;
        }
        if (!TIPOS.contains(imagem.getContentType())) {
            throw new RegraNegocioException("A imagem deve ser JPG, PNG ou WEBP.");
        }
        if (imagem.getSize() > 3 * 1024 * 1024) {
            throw new RegraNegocioException("A imagem pode ter no máximo 3 MB.");
        }
        try {
            return imagem.getBytes();
        } catch (IOException erro) {
            throw new RegraNegocioException("Não foi possível carregar a imagem.");
        }
    }
}
