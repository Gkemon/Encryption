package com.gk.emon.encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Encryptor {
    public static void main(String args[]) throws FileNotFoundException {

        String messageToBeEncrypted = readLines("originalText.txt");
        List<Integer> randoms = RandomGenerator.getRandoms(messageToBeEncrypted.length());
        Encryption encryption = new Encryption("AIB2", "END");
        String encryptedMessage = encryption.encryption(messageToBeEncrypted, randoms);
        encryption.decryption(encryptedMessage, randoms);

        writeIntoFile(encryptedMessage);


    }

    public interface EncryptionDecryption {
        String encryption(String message, List<Integer> randomNumbers);

        String decryption(String encryptedMessage, List<Integer> randoms);
    }


    public static class Encryption implements EncryptionDecryption {
        String header, trailer;
        CircularArrayList<String> alphabets =
                new CircularArrayList<>(Arrays.asList(
                        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                        "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));

        CircularArrayList<String> numbers =
                new CircularArrayList<>(Arrays.asList(
                        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));

        Encryption(String header, String trailer) {
            Encryption.this.header = header;
            Encryption.this.trailer = trailer;
        }

        @Override
        public String encryption(String message, List<Integer> randoms) {

            StringBuilder finalMessage = new StringBuilder();
            for (int i = 0; i < message.length(); i++) {
                String letter = Character.toString(message.charAt(i));
                if (isSpecialCharacter(letter)) {
                    finalMessage.append(letter);
                } else {
                    String tempLetter = letter.toUpperCase();
                    if (alphabets.contains(tempLetter)) {
                        tempLetter = alphabets.get(alphabets.indexOf(tempLetter) + randoms.get(i));
                        if (Character.isUpperCase(letter.charAt(0))) {
                            finalMessage.append(tempLetter);
                        } else finalMessage.append(tempLetter.toLowerCase());
                    }
                    if (numbers.contains(letter)) {
                        String number = numbers.get(Integer.parseInt(letter) + randoms.get(i) - 1);
                        finalMessage.append(number);
                    }
                }
            }

            return header + finalMessage.reverse().append(trailer).toString();
        }

        boolean isSpecialCharacter(String targetString) {
            Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(targetString);
            return m.find();
        }

        @Override
        public String decryption(String encryptedMessage, List<Integer> randoms) {
            StringBuilder finalMessage = new StringBuilder();
            if (encryptedMessage != null) {
                StringBuilder stringBuilder = new StringBuilder(encryptedMessage
                        .replace(header, "")
                        .replace(trailer, ""));

                String finalEncryptedMessage = stringBuilder.reverse().toString();


                for (int i = 0; i < finalEncryptedMessage.length(); i++) {
                    String letter = Character.toString(finalEncryptedMessage.charAt(i));
                    if (isSpecialCharacter(letter)) {
                        finalMessage.append(letter);
                    } else {
                        String tempLetter = letter.toUpperCase();
                        if (alphabets.contains(tempLetter)) {
                            tempLetter = alphabets.get(alphabets.indexOf(tempLetter) - randoms.get(i) % alphabets.size());
                            if (Character.isUpperCase(letter.charAt(0))) {
                                finalMessage.append(tempLetter);
                            } else finalMessage.append(tempLetter.toLowerCase());
                        }
                        if (numbers.contains(letter)) {
                            String number = numbers.get(Integer.parseInt(letter) - ((randoms.get(i) % numbers.size()) - 1));
                            finalMessage.append(number);
                        }
                    }
                }

            }
            return finalMessage.toString();
        }
    }

    public static void writeIntoFile(String encrypted) {

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("encryptedMessage.txt"), "utf-8"))) {
            writer.write(encrypted);
        } catch (IOException ex) {
            // Report
        }
        /*ignore*/
    }

    private static class CircularArrayList<T> extends ArrayList<T> {
        public CircularArrayList(List<T> asList) {
            this.clear();
            this.addAll(asList);
        }

        @Override
        public T get(int index) {
            return super.get(Math.abs(index % size()));
        }
    }

    public static class RandomGenerator {
        public static List<Integer> getRandoms(int length) {
            List<Integer> randoms = new ArrayList<>();
            Random random = new Random(8);
            int max = 99, min = 0;
            for (int i = 0; i < length; i++) {
                randoms.add(random.nextInt((max - min) + 1) + min);
            }
            return randoms;
        }
    }


    public static String readLines(String filename) throws FileNotFoundException {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File is not found.");
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder lines = new StringBuilder();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new FileNotFoundException("File is not found.");
        }

        return lines.toString();
    }
}
