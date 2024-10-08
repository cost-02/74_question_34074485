package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashVerifier {

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

                String generatedHash = sb.toString();
                String storedHash = "";

                try (BufferedReader br = new BufferedReader(new FileReader(directory2 + "/" + file.getName() + ".hash"))) {
                    storedHash = br.readLine();
                }

                boolean isMatch = generatedHash.equals(storedHash);
                System.out.println(file.getName() + ": " + (isMatch ? "YES" : "NO"));
            }
        }
    }
}
