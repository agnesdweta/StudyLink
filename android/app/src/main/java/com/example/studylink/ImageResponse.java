package com.example.studylink;

public class ImageResponse {

    private String filename;

    // Mengambil nama file dari server
    public String getFilename() {
        return filename;
    }

    // Mengembalikan URL gambar (misal server simpan di /uploads/)
    public String getImage() {
        if (filename != null && !filename.isEmpty()) {
            return "http://10.0.2.2:3000/uploads/" + filename; // ganti sesuai server
        }
        return null;
    }
}
