package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashGenerator {

    public static void main(String[] args) throws Exception {
        String directory1 = args[0];
        String directory2 = args[1];
        File folder = new File(directory1);
        File[] listOfFiles = folder.listFiles();

        String keyString = "s3cr3tkey"; // Chiave segreta per HMAC
        SecretKeySpec key = new SecretKeySpec(keyString.getBytes(), "HmacSHA1");

        for (File file : listOfFiles) {
            if (file.isFile()) {
                Mac mac = Mac.getInstance("HmacSHA1");
                mac.init(key);

                byte[] fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                byte[] hashBytes = mac.doFinal(fileBytes);

                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }

                try (PrintWriter output = new PrintWriter(new FileWriter(directory2 + "/" + file.getName() + ".hash"))) {
                    output.println(sb.toString());
                }
            }
        }
        System.out.println("Hash generation complete.");
    }
}
