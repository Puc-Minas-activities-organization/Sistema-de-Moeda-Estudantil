package com.puc.moeda.utils;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validador para fotos em Base64 e URLs
 * Suporta: Base64, URLs HTTP(S)
 */
public class FotoValidator {

    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=.]+$"
    );

    private static final long MAX_BASE64_SIZE = 5_242_880; // 5MB em Base64
    private static final long MAX_URL_SIZE = 2048; // 2KB URL

    /**
     * Valida se a string é Base64 válido ou URL válida
     * @param foto String contendo Base64 ou URL
     * @return true se for válido, false caso contrário
     */
    public static boolean isValid(String foto) {
        if (foto == null || foto.trim().isEmpty()) {
            return false; // Foto não pode estar vazia
        }

        // Verificar se é URL
        if (isValidUrl(foto)) {
            return true;
        }

        // Verificar se é Base64
        if (isValidBase64(foto)) {
            return true;
        }

        return false;
    }

    /**
     * Verifica se é uma URL válida
     */
    public static boolean isValidUrl(String foto) {
        if (foto.length() > MAX_URL_SIZE) {
            return false;
        }
        return URL_PATTERN.matcher(foto).matches();
    }

    /**
     * Verifica se é Base64 válido
     * Base64 válido começa com 'data:image/' (Data URI) ou é string pura
     */
    public static boolean isValidBase64(String foto) {
        if (foto.length() > MAX_BASE64_SIZE) {
            return false;
        }

        try {
            // Se for Data URI (data:image/png;base64,...)
            if (foto.startsWith("data:image/")) {
                String base64Part = foto.split(",", 2)[1];
                Base64.getDecoder().decode(base64Part);
                return true;
            }

            // Se for Base64 puro
            if (foto.matches("^[A-Za-z0-9+/]*={0,2}$")) {
                Base64.getDecoder().decode(foto);
                return true;
            }

            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Sanitiza e normaliza a foto
     * Garante que seja um Data URI válido se for Base64
     */
    public static String normalize(String foto) {
        if (foto == null || foto.trim().isEmpty()) {
            return null;
        }

        // Se já for Data URI, retorna como está
        if (foto.startsWith("data:image/")) {
            return foto;
        }

        // Se for URL, retorna como está
        if (isValidUrl(foto)) {
            return foto;
        }

        // Se for Base64 puro, transforma em Data URI
        if (isValidBase64(foto)) {
            return "data:image/png;base64," + foto;
        }

        return null;
    }
}
